package neu.lab.operation;

import neu.lab.container.ArtifactNodes;
import neu.lab.container.NodeAdapters;
import neu.lab.data.po.ArtifactVersion;
import neu.lab.util.Config;
import neu.lab.util.ExecuteCommand;
import neu.lab.util.MavenUtil;
import neu.lab.util.PomOperation;
import neu.lab.vo.DependencyInfo;
import org.dom4j.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChangeDependencyOperation {
    //当前pom中显式声明的dependency
//    private List<DependencyInfo> dependencyInfoList = new ArrayList<>();
    private Set<String> dependencyInPom = new HashSet<>();
    //write change log to file
    private StringBuffer stringBuffer = new StringBuffer();

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
        writeChangeLogToFile();
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
        stringBuffer.append("start to change verion for " + artifactNodes.getSig());
        changeStopWhenError(artifactNodes, true);
        changeStopWhenError(artifactNodes, false);
    }

    private void changeStopWhenError10Times(ArtifactNodes artifactNodes, boolean upgrade) {
//        boolean jumpMajor = false;
//        boolean successMvn = true;
        int errorExecNum = 0;
        String artifactVersion = null;
        while (errorExecNum <= 10) {
            artifactVersion = artifactNodes.getNextVersion(artifactVersion, upgrade);
            if (artifactVersion == null) {
                break;
            }
//            System.out.println(artifactVersion);
            DependencyInfo dependencyInfo = new DependencyInfo(artifactNodes.getGroupId(), artifactNodes.getArtifactId(), artifactVersion);
            stringBuffer.append("change version from " + artifactNodes.getSig() + " to " + dependencyInfo.getName() + "\n");
            if (hasInCurrentPom(dependencyInfo)) {
                PomOperation.i().updateDependencyVersion(dependencyInfo);
                MavenUtil.i().getLog().info("success update dependency version for " + dependencyInfo.getName());
            } else {
                MavenUtil.i().getLog().info("success add dependency for " + dependencyInfo.getName());
                PomOperation.i().addDependency(dependencyInfo);
            }
            MavenUtil.i().getLog().debug("execute mvn clean");
            ExecuteCommand.mvn(ExecuteCommand.MVN_CLEAN);
            MavenUtil.i().getLog().debug("execute mvn test");
            boolean successMvn = ExecuteCommand.mvnTest(dependencyInfo);
            if (!successMvn) {
                errorExecNum++;
            }
//            PomOperation.i().restorePom();
        }
    }

    private void changeStopWhenError(ArtifactNodes artifactNodes, boolean upgrade) {
        boolean successMvn = true;
        String artifactVersion = null;
        while (successMvn) {
            artifactVersion = artifactNodes.getNextVersion(artifactVersion, upgrade);
            if (artifactVersion == null) {
                break;
            }
//            System.out.println(artifactVersion);
            DependencyInfo dependencyInfo = new DependencyInfo(artifactNodes.getGroupId(), artifactNodes.getArtifactId(), artifactVersion);
            stringBuffer.append("change version from " + artifactNodes.getSig() + " to " + dependencyInfo.getName() + "\n");
            if (hasInCurrentPom(dependencyInfo)) {
                PomOperation.i().updateDependencyVersion(dependencyInfo);
                MavenUtil.i().getLog().info("success update dependency version for " + dependencyInfo.getName());
            } else {
                MavenUtil.i().getLog().info("success add dependency for " + dependencyInfo.getName());
                PomOperation.i().addDependency(dependencyInfo);
            }
            MavenUtil.i().getLog().debug("execute mvn clean");
            ExecuteCommand.mvn(ExecuteCommand.MVN_CLEAN);
            MavenUtil.i().getLog().debug("execute mvn test");
            successMvn = ExecuteCommand.mvnTest(dependencyInfo);
        }
    }

    private void writeChangeLogToFile() {
        String thisProjectLogFilePath = Config.logFilePath
                + MavenUtil.i().getProjectCor().replaceAll("\\p{Punct}", "")
                + "/";
        if (!new File(thisProjectLogFilePath).exists()) {
            new File(thisProjectLogFilePath).mkdirs();
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(thisProjectLogFilePath + "changeLog.txt");
            fileWriter.write(stringBuffer.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
