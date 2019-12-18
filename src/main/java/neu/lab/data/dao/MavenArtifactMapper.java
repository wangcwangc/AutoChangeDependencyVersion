package neu.lab.data.dao;

import neu.lab.data.po.MavenArtifact;

import java.util.List;

public interface MavenArtifactMapper {
    List<MavenArtifact> selectAllMavenArtifact();
    int isExist(String groupId, String artifactId);
    void insertMavenArtifact(MavenArtifact mavenArtifact);
    MavenArtifact selectMavenArtifact(String groupId, String artifactId);
}
