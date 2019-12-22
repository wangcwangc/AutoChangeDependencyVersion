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
        MavenUtil.i().getLog().info("first execute mvn test");
        if (!ExecuteCommand.mvn(ExecuteCommand.MVN_TEST)) {
            return;
        }
        boolean backup = PomOperation.i().backupPom();
        if (!backup) {
            return;
        }
        readPom();
        List<ArtifactNodes> needChangeDependencyList = NodeAdapters.i().getContainer();
        if (needChangeDependencyList.size() > 0) {
            for (ArtifactNodes artifactNodes : needChangeDependencyList) {
                if (artifactNodes.canChangeVersion()) {
                    changeVersion(artifactNodes);
                }
//            System.out.println(artifactNodes.getGroupId() + artifactNodes.getArtifactId());
            }
        }
        PomOperation.i().deletePomCopy();
    }

    public void readPom() {
        List<Element> dependencyList = PomOperation.i().readPomDependencies();
        for (Element element : dependencyList) {
//            DependencyInfo dependencyInfo = new DependencyInfo(element.element("groupId").getText(), element.element("artifactId").getText());
            dependencyInPom.add(element.element("groupId").getText() + ":" + element.element("artifactId").getText());
//            dependencyInfoList.add(dependencyInfo);
        }
    }

    private boolean hasInCurrentPom(DependencyInfo dependencyInfo) {
        return dependencyInPom.contains(dependencyInfo.getGroupId() + ":" + dependencyInfo.getArtifactId());
    }

    private void changeVersion(ArtifactNodes artifactNodes) {
        change(artifactNodes, true);
        change(artifactNodes, false);
    }

    private void change(ArtifactNodes artifactNodes, boolean upgrade) {
//        boolean jumpMajor = false;
        boolean successMvn = true;
        String artifactVersion = null;
        while (successMvn) {
            artifactVersion = artifactNodes.getNextVersion(artifactVersion, upgrade);
            if (artifactVersion == null) {
                break;
            }
//            System.out.println(artifactVersion);
            DependencyInfo dependencyInfo = new DependencyInfo(artifactNodes.getGroupId(), artifactNodes.getArtifactId(), artifactVersion);
            if (hasInCurrentPom(dependencyInfo)) {
                PomOperation.i().updateDependencyVersion(dependencyInfo);
                MavenUtil.i().getLog().info("success update dependency version for " + dependencyInfo.getName());
            } else {
                MavenUtil.i().getLog().info("success add dependency for " + dependencyInfo.getName());
                PomOperation.i().addDependency(dependencyInfo);
            }
            successMvn = ExecuteCommand.mvnTest(dependencyInfo);
            PomOperation.i().restorePom();
        }
    }

}
