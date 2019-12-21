package neu.lab.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import neu.lab.container.NodeAdapters;
import neu.lab.vo.DependencyInfo;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class PomOperation {
    //    public static String COPY_CONFLICT = "copyConflictDependency.xml";
//    public static String COPY_JUNIT = "copyJunit.xml";
//    public static String COYT_EVOSUITE = "copyEvosuiteRuntime.xml";
    private String POM_PATH = MavenUtil.i().getProjectPom();
    private String POM_PATH_COPY = MavenUtil.i().getBaseDir().getAbsolutePath() + "/pom-copy.xml";

    private static PomOperation instance;

    public static PomOperation i() {
        if (instance == null) {
            instance = new PomOperation();
        }
        return instance;
    }

    private PomOperation() {
    }

    /**
     * add dependency to new empty
     *
     * @param dependencyInfo
     */
    public void setDependency(DependencyInfo dependencyInfo) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(POM_PATH);
            Element rootElement = document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            Element dependency = dependencies.addElement("dependency");
            dependencyInfo.addDependencyElement(dependency);
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileWriter(POM_PATH + dependencyInfo.getArtifactId()), outputFormat);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
    }

    public List<Element> readPomDependencies() {
        SAXReader reader = new SAXReader();
        List<Element> dependencyList = new ArrayList<>();

        try {
            Document document = reader.read(POM_PATH);
            Element rootElement = document.getRootElement();
            Element dependencies = rootElement.element("dependencies");
            if (dependencies != null) {
                dependencyList = dependencies.elements("dependency");
//                while (elementIterator.hasNext()) {
//                    dependencyList.add((Element) elementIterator.next());
//                }
            }
//            Element dependency = dependencies.addElement("dependency");
//            dependencyInfo.addDependencyElement(dependency);
//            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
//            outputFormat.setEncoding("UTF-8");
//            XMLWriter writer = new XMLWriter(new FileWriter(xmlFilePath), outputFormat);
//            writer.write(document);
//            writer.close();
        } catch (Exception e) {
            MavenUtil.i().getLog().error(e.getMessage());
        }
        return dependencyList;
    }

    public boolean backupPom() {
        MavenUtil.i().getLog().info("backup pom.xml");
        try {
            Files.copy(new File(POM_PATH).toPath(), new File(POM_PATH_COPY).toPath());
        } catch (IOException e) {
            MavenUtil.i().getLog().error("backup pom.xml error");
            MavenUtil.i().getLog().error(e.getMessage());
            return false;
        }
        MavenUtil.i().getLog().info("success backup");
        return true;
    }

    public boolean restorePom() {
        if (new File(POM_PATH).exists()) {
            new File(POM_PATH).delete();
        }
        try {
            Files.copy(new File(POM_PATH_COPY).toPath(), new File(POM_PATH).toPath());
        } catch (IOException e) {
            MavenUtil.i().getLog().error("restore pom.xml error");
            MavenUtil.i().getLog().error(e.getMessage());
            return false;
        }
        if (new File(POM_PATH_COPY).exists()) {
            new File(POM_PATH_COPY).delete();
        }
        MavenUtil.i().getLog().info("success restore");
        return true;
    }
//    /**
//     * copy empty dependency xml to target path
//     *
//     * @return XMLName.xml path
//     */
//    public static String copyPom(String XMLName) {
//        InputStream fileInputStream = ReadXML.class.getResourceAsStream("/" + XMLName);
//        String xmlFileName = System.getProperty("user.dir") + Config.FILE_SEPARATOR + Config.SENSOR_DIR + Config.FILE_SEPARATOR + XMLName;
//        byte[] buffer;
//        try {
//            buffer = new byte[fileInputStream.available()];
//            fileInputStream.read(buffer);
//            Files.write(buffer, new File(xmlFileName));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return xmlFileName;
//    }

    /**
     * execute Maven dependency:copy-dependencies
     *
     * @param xmlFileName
     * @param dir         for dependency jar
     */
//    public static void executeMavenCopy(String xmlFileName, String dir) {
////        String mvnCmd = Config.getMaven() + Command.MVN_POM + xmlFileName + Command.MVN_COPY + dir;
//        try {
//            ExecuteCommand.exeCmd(mvnCmd);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
