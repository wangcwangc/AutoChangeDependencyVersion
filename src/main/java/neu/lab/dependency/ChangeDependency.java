package neu.lab.dependency;

import neu.lab.container.ArtifactNodes;
import neu.lab.container.NodeAdapters;
import neu.lab.operation.ChangeDependencyOperation;
import neu.lab.util.MavenUtil;
import neu.lab.util.PomOperation;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.List;

@Mojo(name = "changeDependency", defaultPhase = LifecyclePhase.VALIDATE)
public class ChangeDependency extends DependencyMojo {
    @Override
    public void run() {
        new ChangeDependencyOperation().executeOperation();
    }
}
