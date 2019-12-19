package neu.lab.data.po;

public class ArtifactVersion {
    private int id;
    private String version;
    private String major;
    private String minor;
    private String patch;
    private int priority;
    private int mavenArtifactId;

    public ArtifactVersion() {
    }

    public ArtifactVersion(String version, int priority, int mavenArtifactId) {
        this.version = version;
        String[] versionSplit = version.split("\\.");
        int length = versionSplit.length;
        switch (length) {
            case 1:
                major = versionSplit[0];
                break;
            case 2:
                major = versionSplit[0];
                minor = versionSplit[1];
                break;
            default:
                major = versionSplit[0];
                minor = versionSplit[1];
                patch = versionSplit[2];
        }
        this.priority = priority;
        this.mavenArtifactId = mavenArtifactId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getPatch() {
        return patch;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getMavenArtifactId() {
        return mavenArtifactId;
    }

    public void setMavenArtifactId(int mavenArtifactId) {
        this.mavenArtifactId = mavenArtifactId;
    }

    @Override
    public String toString() {
        return "ArtifactVersion{" +
                "id=" + id +
                ", version='" + version + '\'' +
                ", major='" + major + '\'' +
                ", minor='" + minor + '\'' +
                ", patch='" + patch + '\'' +
                ", priority=" + priority +
                ", mavenArtifactId=" + mavenArtifactId +
                '}';
    }
}
