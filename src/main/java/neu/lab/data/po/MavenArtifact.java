package neu.lab.data.po;

public class MavenArtifact {
    private int id;
    private String artifactId;
    private String groupId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "MavenArtifact{" +
                "id=" + id +
                ", artifactId='" + artifactId + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }
}
