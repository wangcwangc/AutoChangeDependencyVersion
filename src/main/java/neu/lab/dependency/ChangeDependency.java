package neu.lab.dependency;

import neu.lab.container.ArtifactNodes;
import neu.lab.container.NodeAdapters;
import neu.lab.operation.ChangeDependencyOperation;
import neu.lab.util.MavenUtil;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.List;

@Mojo(name = "changeDependency", defaultPhase = LifecyclePhase.VALIDATE)
public class ChangeDependency extends DependencyMojo {
    @Override
    public void run() {
        List<ArtifactNodes> con = NodeAdapters.i().getContainer();
        for (ArtifactNodes artifactNode : con) {
            System.out.println(artifactNode.getArtifactId());
        }
        System.out.println(MavenUtil.i().getProjectPom());
        new ChangeDependencyOperation().readPom();
    }
}
