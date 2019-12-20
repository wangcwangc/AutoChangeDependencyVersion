package neu.lab.operation;

import neu.lab.container.ArtifactNodes;
import neu.lab.container.NodeAdapters;
import neu.lab.data.po.ArtifactVersion;
import neu.lab.util.ExecuteCommand;
import neu.lab.util.MavenUtil;
import neu.lab.util.PomOperation;
import neu.lab.vo.DependencyInfo;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeDependencyOperation {
    //当前pom中显式声明的dependency
//    private List<DependencyInfo> dependencyInfoList = new ArrayList<>();
    private Set<String> dependencyInPom = new HashSet<>();

    public void executeOperation() {
        readPom();
        List<ArtifactNodes> needChangeDependencyList = NodeAdapters.i().getContainer();
        if (needChangeDependencyList.size() == 0) return;
        for (ArtifactNodes artifactNodes : needChangeDependencyList) {
            changeVersion(artifactNodes);
        }
    }

    public void readPom() {
        List<Element> dependencyList = PomOperation.i().readPomDependencies();
        for (Element element : dependencyList) {
//            DependencyInfo dependencyInfo = new DependencyInfo(element.element("groupId").getText(), element.element("artifactId").getText());
            dependencyInPom.add(element.element("groupId").getText() + ":" + element.element("artifactId").getText());
//            dependencyInfoList.add(dependencyInfo);
        }
    }

    private boolean hasInCurrentPom(ArtifactNodes artifactNodes) {
        return dependencyInPom.contains(artifactNodes.getGroupId() + ":" + artifactNodes.getArtifactId());
    }

    private void changeVersion(ArtifactNodes artifactNodes) {
        change(artifactNodes, true);
        change(artifactNodes, false);
    }

    private void change(ArtifactNodes artifactNodes, boolean upgrade) {
//        boolean jumpMajor = false;
        boolean hasError = false;
        ArtifactVersion artifactVersion = null;
        while (!hasError) {
//            artifactVersion = artifactNodes.getNextVersion(artifactVersion, jumpMajor, true);
            artifactVersion = artifactNodes.getNextVersion(artifactVersion, upgrade);
            if (artifactVersion == null) {
                break;
            }
            DependencyInfo dependencyInfo = new DependencyInfo(artifactNodes.getGroupId(), artifactNodes.getArtifactId(), artifactVersion.getVersion());
            PomOperation.i().setDependency(dependencyInfo);
            //TODO exec mvn package and mvn test
            hasError = ExecuteCommand.test();
            //TODO 记录出错log
        }
    }

}
