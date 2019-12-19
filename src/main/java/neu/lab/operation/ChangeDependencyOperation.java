package neu.lab.operation;

import neu.lab.container.NodeAdapters;
import neu.lab.util.MavenUtil;
import neu.lab.util.ReadXML;
import neu.lab.vo.DependencyInfo;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class ChangeDependencyOperation {

    public void executeOperation() {
        readPom();
    }

    public void readPom() {
        List<Element> dependencyList = ReadXML.readPomDependencies(MavenUtil.i().getProjectPom());
        List<DependencyInfo> dependencyInfoList = new ArrayList<>();

        for (Element element : dependencyList) {
            DependencyInfo dependencyInfo = new DependencyInfo();
            System.out.println(element.element("groupId").getText());
            System.out.println(element.element("artifactId").getText());
            System.out.println(element.element("version").getText());
        }
//        NodeAdapters.i().getArtifactNodes()
    }
}
