package neu.lab.operation;

import neu.lab.util.MavenUtil;
import neu.lab.util.ReadXML;
import org.dom4j.Element;

import java.util.List;

public class ChangeDependencyOperation {

    public void executeOperation() {

    }

    public void readPom() {
        List<Element> dependencyList = ReadXML.readPomDependencies(MavenUtil.i().getProjectPom());
        for (Element element : dependencyList) {
            System.out.println(element.element("groupId").getText());
            System.out.println(element.element("artifactId").getText());
            System.out.println(element.element("version").getText());
        }
    }
}
