package neu.lab.data.dao;

import neu.lab.data.po.MavenArtifact;

import java.util.List;

public interface MavenArtifactMapper {
    List<MavenArtifact> selectAllMavenArtifact();
    int isExist(String artifactId,String groupId);
}
