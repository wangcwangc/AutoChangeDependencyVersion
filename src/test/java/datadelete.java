import neu.lab.data.dao.ArtifactVersionDao;
import neu.lab.data.dao.MavenArtifactDao;
import neu.lab.data.po.MavenArtifact;
import neu.lab.dependency.ChangeDependency;
import neu.lab.util.MavenUtil;
import neu.lab.util.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class datadelete {
    @Test
    @Ignore
    public void test() throws IOException {
        SqlSession sqlSession = MybatisUtil.createSqlSession();
        Set<String> dataset = new HashSet<>();
        //创建mapper
        MavenArtifactDao mavenArtifactMapper = sqlSession.getMapper(MavenArtifactDao.class);
        ArtifactVersionDao artifactVersionMapper = sqlSession.getMapper(ArtifactVersionDao.class);
        List<MavenArtifact> mavenArtifactList = mavenArtifactMapper.selectAllMavenArtifact();
        FileWriter filewriter = new FileWriter("nullartifact.txt");
        int num = 0;
        for (MavenArtifact mavenArtifact : mavenArtifactList) {
            if (artifactVersionMapper.isExist(mavenArtifact.getId()) == 0) {
                System.out.println(mavenArtifact.toString());
                dataset.add(mavenArtifact.getGroupId() + ":" + mavenArtifact.getArtifactId());
            }
            num++;
            System.out.println(num);
            if (num > 3000) {
                break;
            }
        }
        for (String data : dataset) {
            filewriter.write(data + "\n");

        }


        filewriter.close();
        MybatisUtil.closeSqlSession(sqlSession);
    }

    @Test
    @Ignore
    public void test1() throws IOException {
        SqlSession sqlSession = MybatisUtil.createSqlSession();

        //创建mapper
        MavenArtifactDao mavenArtifactMapper = sqlSession.getMapper(MavenArtifactDao.class);

        MavenUtil.i().setMojo(new ChangeDependency());
        BufferedReader reader = new BufferedReader(new FileReader("nullartifact.txt"));
        String line = reader.readLine();
        int num = 0;
        while (line != null && !line.equals("")) {
            try {
                String[] projectName = line.split(":");
                String groupId = projectName[0];
                String artifactId = projectName[1];
                System.out.println(groupId + " : " + artifactId);
                mavenArtifactMapper.deleteMavenArtifact(groupId, artifactId);
                System.out.println("delete success");
            } catch (Exception e) {
                System.out.println(e);
            }
            line = reader.readLine();
            System.out.println(num);
            num++;
        }
        System.out.println(num);
        reader.close();
        sqlSession.commit();
        MybatisUtil.closeSqlSession(sqlSession);
    }
}
