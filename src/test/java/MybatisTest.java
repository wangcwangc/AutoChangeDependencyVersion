import neu.lab.data.dao.ArtifactVersionMapper;
import neu.lab.data.dao.MavenArtifactMapper;
import neu.lab.data.po.ArtifactVersion;
import neu.lab.data.po.MavenArtifact;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class MybatisTest {
    @Test
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
            List<ArtifactVersion> list = sqlSession.getMapper(ArtifactVersionMapper.class).selectAllArtifactVersionByMavenArtifactId(1);
            for (ArtifactVersion artifactVersion : list) {
                System.out.println(artifactVersion.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
