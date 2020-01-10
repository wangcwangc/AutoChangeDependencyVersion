package neu.lab.container;

import neu.lab.data.dao.ArtifactVersionDao;
import neu.lab.data.dao.MavenArtifactDao;
import neu.lab.data.po.ArtifactVersion;
import neu.lab.data.po.MavenArtifact;
import neu.lab.util.MavenCrawler;
import neu.lab.util.MavenUtil;
import neu.lab.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ArtifactNodes {
    private String groupId;
    private String artifactId;
    private String currentVersion;
    private Set<ArtifactVersion> artifactVersions;

    public ArtifactNodes(String groupId, String artifactId, String currentVersion) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.currentVersion = currentVersion;
        artifactVersions = new LinkedHashSet<>();
        try {
            initArtifactVersionSet();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Set<ArtifactVersion> getArtifactVersions() {
        return artifactVersions;
    }

    public void setArtifactVersions(Set<ArtifactVersion> artifactVersions) {
        this.artifactVersions = artifactVersions;
    }

    public void addArtifactVersion(ArtifactVersion artifactVersion) {
        artifactVersions.add(artifactVersion);
    }

    private void initArtifactVersionSet() {
        SqlSession sqlSession = MybatisUtil.createSqlSession();

        //创建mapper
        MavenArtifactDao mavenArtifactMapper = sqlSession.getMapper(MavenArtifactDao.class);
        ArtifactVersionDao artifactVersionMapper = sqlSession.getMapper(ArtifactVersionDao.class);
        MavenUtil.i().getLog().info("get maven artifact for : " + groupId + " " + artifactId);
        MavenArtifact mavenArtifact = mavenArtifactMapper.selectMavenArtifact(groupId, artifactId);

        if (mavenArtifact == null) {
            //先插入到数据库
            mavenArtifactMapper.insertMavenArtifact(new MavenArtifact(groupId, artifactId));
            //再从数据库读取获取id
            mavenArtifact = mavenArtifactMapper.selectMavenArtifact(groupId, artifactId);
            List<String> versionList = MavenCrawler.getVersionList(groupId, artifactId);
            if (versionList.size() > 0) {
                int priority = versionList.size();
                for (String version : versionList) {
                    ArtifactVersion artifactVersion = new ArtifactVersion(version, priority--, mavenArtifact.getId());
                    artifactVersions.add(artifactVersion);
                }
                artifactVersionMapper.insertArtifactVersionSet(artifactVersions);
            } else {
                MybatisUtil.closeSqlSession(sqlSession);
                return;
            }
            sqlSession.commit();
        } else {
            artifactVersions = artifactVersionMapper.selectAllArtifactVersionByMavenArtifactId(mavenArtifact.getId());
        }
        MybatisUtil.closeSqlSession(sqlSession);
    }

    //判断是不是同一个包
    public boolean isSelf(String groupId, String artifactId) {
        return this.groupId.equals(groupId) && this.artifactId.equals(artifactId);
    }

    public String getNextVersion(String version, boolean upgrade) {
        ArtifactVersion nextArtifactVersion = null;
        if (version == null) {
            version = currentVersion;
        }
        if (upgrade) {
            try {
                nextArtifactVersion = getArtifactVersionByPriority(getArtifactVersionByVersion(version).getPriority() + 1);
            } catch (Exception e) {
                MavenUtil.i().getLog().error(e.getMessage());
            }
            if (nextArtifactVersion == null) {
                return null;
            }
            return nextArtifactVersion.getVersion();
        } else {
            try {
                nextArtifactVersion = getArtifactVersionByPriority(getArtifactVersionByVersion(version).getPriority() - 1);
            } catch (Exception e) {
                MavenUtil.i().getLog().error(e.getMessage());
            }
            if (nextArtifactVersion == null) {
                return null;
            }
            return nextArtifactVersion.getVersion();
        }
    }

    private ArtifactVersion getArtifactVersionByPriority(int priority) {
        for (ArtifactVersion artifactVersion : artifactVersions) {
            if (artifactVersion.getPriority() == priority) {
                return artifactVersion;
            }
        }
        return null;
    }

    private ArtifactVersion getArtifactVersionByVersion(String version) {
        for (ArtifactVersion artifactVersion : artifactVersions) {
            if (artifactVersion.getVersion().equals(version)) {
                return artifactVersion;
            }
        }
        return null;
    }


    public boolean canChangeVersion() {
        return artifactVersions.size() > 0;
    }

    public String getSig() {
        return groupId + ":" + artifactId + ":" + currentVersion;
    }

    @Override
    public String toString() {
        return "ArtifactNodes{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", currentVersion='" + currentVersion + '\'' +
                ", artifactVersions=" + artifactVersions +
                '}';
    }
}
