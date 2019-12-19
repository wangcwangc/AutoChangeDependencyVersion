package neu.lab.vo;

import neu.lab.data.dao.ArtifactVersionDao;
import neu.lab.data.dao.MavenArtifactDao;
import neu.lab.data.po.ArtifactVersion;
import neu.lab.data.po.MavenArtifact;
import neu.lab.util.MavenCrawler;
import neu.lab.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DependencyNode {
    private String groupId;
    private String artifactId;
    private Set<ArtifactVersion> artifactVersions;

    public DependencyNode(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        artifactVersions = new LinkedHashSet<>();
        initArtifactVersionSet();
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
                    artifactVersionMapper.insertArtifactVersion(artifactVersion);
                }
            }
            sqlSession.commit();
        } else {
            artifactVersions = artifactVersionMapper.selectAllArtifactVersionByMavenArtifactId(mavenArtifact.getId());
        }
        MybatisUtil.closeSqlSession(sqlSession);
    }

    @Override
    public String toString() {
        return "DependencyNode{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", artifactVersions=" + artifactVersions +
                '}';
    }
}
