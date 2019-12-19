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
        //当前pom中显式声明的dependency
        List<DependencyInfo> dependencyInfoList = new ArrayList<>();

        for (Element element : dependencyList) {
            DependencyInfo dependencyInfo = new DependencyInfo(element.element("groupId").getText(), element.element("artifactId").getText());
            dependencyInfoList.add(dependencyInfo);
        }

//        NodeAdapters.i().getArtifactNodes()
    }
}
