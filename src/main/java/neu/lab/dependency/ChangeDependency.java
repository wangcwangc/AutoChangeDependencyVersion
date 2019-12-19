package neu.lab.dependency;

import neu.lab.container.NodeAdapters;
import neu.lab.vo.ArtifactNode;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.List;

@Mojo(name = "changeDependency", defaultPhase = LifecyclePhase.VALIDATE)
public class ChangeDependency extends DependencyMojo {
    @Override
    public void run() {
        List<ArtifactNode> con = NodeAdapters.i().getContainer();
        for (ArtifactNode artifactNode : con){
            System.out.println(artifactNode.toString());
        }
    }
}
