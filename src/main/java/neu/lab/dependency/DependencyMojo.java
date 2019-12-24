package neu.lab.dependency;

import neu.lab.container.NodeAdapters;
import neu.lab.util.Config;
import neu.lab.util.MavenUtil;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import java.io.File;
import java.util.List;

public abstract class DependencyMojo extends AbstractMojo {
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    public MavenSession session;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    public MavenProject project;

    @Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
    public List<MavenProject> reactorProjects;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    public List<ArtifactRepository> remoteRepositories;

    @Parameter(defaultValue = "${localRepository}", readonly = true)
    public ArtifactRepository localRepository;

    @Component
    public DependencyTreeBuilder dependencyTreeBuilder;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    public File buildDir;

    @Component
    public ArtifactFactory factory;

    @Component
    public ArtifactHandlerManager artifactHandlerManager;
    @Component
    public ArtifactResolver resolver;
    DependencyNode root;

    @Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
    public List<String> compileSourceRoots;

    @Parameter(property = "ignoreTestScope", defaultValue = "true")
    public boolean ignoreTestScope;

    @Parameter(property = "ignoreProvidedScope", defaultValue = "false")
    public boolean ignoreProvidedScope;

    @Parameter(property = "ignoreRuntimeScope", defaultValue = "false")
    public boolean ignoreRuntimeScope;

    @Parameter(property = "append", defaultValue = "false")
    public boolean append;

    public int systemSize = 0;

    public long systemFileSize = 0;// byte

    @Parameter(property = "maxDependencyDepth", defaultValue = "2")
    public int maxDependencyDepth;

    @Parameter(property = "logFilePath")
    public String logFilePath;

    @Parameter(property = "threads", defaultValue = "5")
    public int nThreads;

    // 初始化全局变量
    protected void initGlobalVar() throws Exception {
        MavenUtil.i().setMojo(this);
        Config.maxDependencyDepth = maxDependencyDepth;
        Config.logFilePath = logFilePath;
        Config.nThreads = nThreads;
        // 初始化NodeAdapters
        NodeAdapters.init(root);
        // 初始化DepJars
//        DepJars.init(NodeAdapters.i());// occur jar in tree
        // 验证系统大小
    }

    @Override
    public void execute() throws MojoExecutionException {
        this.getLog().info("method detect start:");
        long startTime = System.currentTimeMillis();
        String pckType = project.getPackaging(); // 得到项目的打包类型
        if ("jar".equals(pckType) || "war".equals(pckType) || "maven-plugin".equals(pckType)
                || "bundle".equals(pckType)) {
            try {
                // project.
                root = dependencyTreeBuilder.buildDependencyTree(project, localRepository, null);
            } catch (DependencyTreeBuilderException e) {
                throw new MojoExecutionException(e.getMessage());
            }
            try {
                initGlobalVar();
            } catch (Exception e) {
                throw new MojoExecutionException("project size error! : " + e);
            }
            run();

        } else {
            this.getLog().info("this project fail because package type is neither jar nor war:"
                    + project.getGroupId() + ":"
                    + project.getArtifactId() + ":"
                    + project.getVersion() + "@"
                    + project.getFile().getAbsolutePath());
        }
        long endTime = System.currentTimeMillis();
        this.getLog().debug("count time : " + (endTime - startTime) + "s");
        this.getLog().debug("method detect end");

    }


    public abstract void run();
}
