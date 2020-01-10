package neu.lab.util;

import neu.lab.vo.DependencyInfo;
import org.apache.commons.exec.*;

import java.io.*;
import java.util.ArrayList;


public class ExecuteCommand {

    public static final String MVN_COMPILE = "mvn compile";
    public static final String MVN_PACKAGE = "mvn package";
    public static final String MVN_TEST = "mvn test";
    public static final String MVN_CLEAN = "mvn clean";

    public static void exeCmd(String mvnCmd) throws ExecuteException, IOException {
        exeCmd(mvnCmd, 0, null);
    }

    public static void exeCmd(String mvnCmd, long timeout, String logPath) throws ExecuteException, IOException {
        CommandLine cmdLine = CommandLine.parse(mvnCmd);
        DefaultExecutor executor = new DefaultExecutor();
        if (timeout != 0) {
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
            executor.setWatchdog(watchdog);
        }
        if (logPath != null) {
            executor.setStreamHandler(new PumpStreamHandler(new FileOutputStream(logPath)));
        }
        executor.execute(cmdLine);
    }

    private static ArrayList<String> lines;

    public static ArrayList<String> exeBatAndGetResult(String batFilePath) {
//        BufferedReader br = null;
//		StringBuilder stringBuilder = new StringBuilder();
        lines = new ArrayList<>();
        try {
            Process p = Runtime.getRuntime().exec(batFilePath);
            p.waitFor();
            MavenUtil.i().getLog().info("execute success");
            String logPath = batFilePath;
            if (batFilePath.contains(" ")) {
                logPath = batFilePath.split(" ")[1];
            }
//            String logFile = logPath.substring(0, logPath.lastIndexOf(Config.FILE_SEPARATOR) + 1) + Config.FILE_SEPARATOR + "log.txt";
//            if (!(new File(logFile).exists())) {
//                return lines;
//            }
//            readFileByLines(logFile);
//            printMessage(p.getErrorStream());
//            printMessage(p.getInputStream());
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                if (line.contains("Tests") || line.contains("OK")) {
//                    lines.add(line);
//                }
////				stringBuilder.append(line + "\n");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void printMessage(final InputStream input) {
        new Thread(new Runnable() {
            public void run() {
                BufferedReader bf = new BufferedReader(new InputStreamReader(input));
                String line = null;
                try {
                    while ((line = bf.readLine()) != null) {
                        if (line.contains("Tests") || line.contains("OK")) {
                            lines.add(line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    private static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            // 一次读入一行，直到读入null为文件结束
            while ((line = reader.readLine()) != null) {
                if (line.contains("Tests") || line.contains("OK")) {
                    lines.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static boolean test() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
//        exec.setStreamHandler(streamHandler);
//        exec.execute(commandline);
//        return(outputStream.toString());
        CommandLine cmdLine = CommandLine.parse("mvn test");
        DefaultExecutor executor = new DefaultExecutor();
        try {
            executor.setStreamHandler(streamHandler);
            executor.execute(cmdLine);

        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(outputStream.toString());
        boolean bulidSuccess = false;
        for (String line : outputStream.toString().split("\\n")) {
            if (line.contains("Tests run:")) {
                System.out.println(line);
                bulidSuccess = true;
            }
        }
        return bulidSuccess;
    }

    public static boolean mvn(String mvnCMD) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        CommandLine cmdLine = CommandLine.parse(mvnCMD);
        DefaultExecutor executor = new DefaultExecutor();
        int exitCode = -1;
        try {
            executor.setStreamHandler(streamHandler);
            exitCode = executor.execute(cmdLine);

        } catch (IOException e) {
            MavenUtil.i().getLog().error("this project execute error : " + e.getMessage());
//            e.printStackTrace();
        }
        return exitCode == 0;
    }


    public static boolean mvnTest(DependencyInfo dependencyInfo) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        CommandLine cmdLine = CommandLine.parse("mvn test -f " + PomOperation.i().POM_PATH_COPY);
        DefaultExecutor executor = new DefaultExecutor();
        int exitCode = -1;
        try {
            executor.setStreamHandler(streamHandler);
            exitCode = executor.execute(cmdLine);

        } catch (IOException e) {
            //执行出错，不需要报错，需要记录log
//            e.printStackTrace();
            try {
                String thisProjectLogFilePath = Config.logFilePath
                        + MavenUtil.i().getProjectCor().replaceAll("\\p{Punct}", "")
                        + "/";
                if (!new File(thisProjectLogFilePath).exists()) {
                    new File(thisProjectLogFilePath).mkdirs();
                }
                FileWriter fileWriter = new FileWriter(thisProjectLogFilePath + dependencyInfo.getLogFileName() + ".txt");
                fileWriter.write("project : " + MavenUtil.i().getProjectInfo() + "\n");
                fileWriter.write(dependencyInfo.toString() + "\n");
                fileWriter.write(outputStream.toString());
                fileWriter.close();
            } catch (IOException iOException) {
                MavenUtil.i().getLog().error(iOException.getMessage());
                return false;
            }
        }
        return exitCode == 0;
    }
//    public static void main(String[] args) throws ExecuteException, IOException {
////		exeCmd("sh /Users/wangchao/eclipse-workspace/Host/sensor_testcase/test_method/getSize/neu/lab/Host/execute.sh");
////        System.out.println(exeBatAndGetResult("sh /Users/wangchao/eclipse-workspace/Host/sensor_testcase/test_method/getSize/neu/lab/Host/execute.sh"));
//        System.out.println("/Users/wangchao/eclipse-workspace/Host/sensor_testcase/test_method/getSize/neu/lab/Host/execute.sh".substring(0, "/Users/wangchao/eclipse-workspace/Host/sensor_testcase/test_method/getSize/neu/lab/Host/execute.sh".lastIndexOf(Config.FILE_SEPARATOR) + 1));
////		String commandStr = Config.getMaven() + " -version";
////		ArrayList<String> results = ExecuteCommand.exeCmdAndGetResult(commandStr);
////		for (String line : results) {
////			if (line.contains("3.6.0"))
////				System.out.println(line);
////		}
////		String sensor_dir = "C:\\Users\\Flipped\\eclipse-workspace\\Host\\" + Config.SENSOR_DIR + "\\";
////		String targetFile = ReadXML.copyPom(sensor_dir);
////		List<DependencyInfo> DependencyInfos = new ArrayList<DependencyInfo>();
////		DependencyInfo dependencyInfo = new DependencyInfo();
////		dependencyInfo.setArtifactId("B");
////		dependencyInfo.setGroupId("neu.lab");
////		dependencyInfo.setVersion("1.0");
////		DependencyInfos.add(dependencyInfo);
////		ReadXML.setCopyDependency(DependencyInfos, targetFile);
////		String mvnCmd = Config.getMaven() + Command.MVN_POM + targetFile + Command.MVN_COPY + sensor_dir + "jar\\";
////		exeCmd(mvnCmd);
//    }
}
