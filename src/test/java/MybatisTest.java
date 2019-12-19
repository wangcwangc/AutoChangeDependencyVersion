import neu.lab.data.dao.ArtifactVersionMapper;
import neu.lab.data.dao.MavenArtifactMapper;
import neu.lab.data.po.ArtifactVersion;
import neu.lab.data.po.MavenArtifact;
import neu.lab.util.MavenCrawler;
import neu.lab.util.MybatisUtil;
import neu.lab.vo.DependencyNode;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MybatisTest {
    @Test
    @Ignore
    public void test1() {
        String source = "mybatis-config.xml";
        SqlSession sqlSession = null;

        try {
            //1 获取mybatis-config.xml的输入流
            InputStream is = Resources.getResourceAsStream(source);
            //2 创建一个工厂，完成对配置文件的读取
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
            //3 创建sqlSession，开启工厂
            sqlSession = sqlSessionFactory.openSession();
            //4 根据放入工厂的sql语句执行不同的方法
//            count = sqlSession.selectOne("com.bdqn.dao.TUserMapper.queryCount");
            List<MavenArtifact> list = sqlSession.getMapper(MavenArtifactMapper.class).selectAllMavenArtifact();
            for (MavenArtifact mavenArtifact : list) {
                System.out.println(mavenArtifact.toString());
            }
            System.out.println(sqlSession.getMapper(MavenArtifactMapper.class).isExist("org.slf4j", "slf4j-api"));

            System.out.println(sqlSession.getMapper(MavenArtifactMapper.class).selectMavenArtifact("org.slf4j", "slf4j-api").toString());
//            sqlSession.getMapper(MavenArtifactMapper.class).insertMavenArtifact(new MavenArtifact("1","2"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @Ignore
    public void test2() {
        String source = "mybatis-config.xml";
        SqlSession sqlSession = null;

        try {
            //1 获取mybatis-config.xml的输入流
            InputStream is = Resources.getResourceAsStream(source);
            //2 创建一个工厂，完成对配置文件的读取
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
            //3 创建sqlSession，开启工厂
            sqlSession = sqlSessionFactory.openSession();
            //4 根据放入工厂的sql语句执行不同的方法
            Set<ArtifactVersion> list = sqlSession.getMapper(ArtifactVersionMapper.class).selectAllArtifactVersionByMavenArtifactId(1);
            for (ArtifactVersion artifactVersion : list) {
                System.out.println(artifactVersion.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    @Ignore
    public void test3() {
        SqlSession sqlSession = MybatisUtil.createSqlSession();
        MavenArtifact mavenArtifact = sqlSession.getMapper(MavenArtifactMapper.class).selectMavenArtifact("org.slf4j", "slf4j-api");
        List<String> versionList = MavenCrawler.getVersionList(mavenArtifact.getGroupId(), mavenArtifact.getArtifactId());
        List<ArtifactVersion> artifactVersionList = new ArrayList<>();
        int priority = versionList.size();
        for (String version : versionList) {
            System.out.println(version);
            ArtifactVersion artifactVersion = new ArtifactVersion(version, priority--, mavenArtifact.getId());
            artifactVersionList.add(artifactVersion);
            sqlSession.getMapper(ArtifactVersionMapper.class).insertArtifactVersion(artifactVersion);
        }
        System.out.println(artifactVersionList.size());
//        sqlSession.commit();
        MybatisUtil.closeSqlSession(sqlSession);
    }

    @Test
    @Ignore
    public void test4() {
        DependencyNode dependencyNode = new DependencyNode("org.slf4j", "slf4j-api");
        System.out.println(dependencyNode.toString());
    }
}
