package neu.lab.data.dao;

import neu.lab.data.po.ArtifactVersion;

import java.util.LinkedHashSet;
import java.util.Set;

public interface ArtifactVersionMapper {
    LinkedHashSet<ArtifactVersion> selectAllArtifactVersionByMavenArtifactId(int mavenArtifactId);
    void insertArtifactVersion(ArtifactVersion ArtifactVersion);
}
