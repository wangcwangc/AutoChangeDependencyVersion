package neu.lab.data.dao;

import neu.lab.data.po.ArtifactVersion;
import neu.lab.data.po.MavenArtifact;

import java.util.List;

public interface ArtifactVersionMapper {
    List<ArtifactVersion> selectAllArtifactVersionByMavenArtifactId(int mavenArtifactId);
}
