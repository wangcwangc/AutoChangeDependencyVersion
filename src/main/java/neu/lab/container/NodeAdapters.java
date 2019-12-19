package neu.lab.container;

import java.util.ArrayList;
import java.util.List;

import neu.lab.util.Config;
import neu.lab.util.MavenUtil;
import neu.lab.vo.DependencyInfo;
import org.apache.maven.shared.dependency.tree.DependencyNode;


/**
 * @author asus
 */
public class NodeAdapters {
    private static NodeAdapters instance;

    public static NodeAdapters i() {
        return instance;
    }

    public static void init(DependencyNode root) {
        // if (instance == null) {
        instance = new NodeAdapters();
        // add node in dependency tree
//        NodeAdapterCollector visitor = new NodeAdapterCollector(instance);
//        root.accept(visitor);

//        System.out.println(root.toNodeString());

        for (DependencyNode childDirect : root.getChildren()) {
//            System.out.println(child.toNodeString());
            //过滤test范围的依赖
            if (MavenUtil.i().getMojo().ignoreTestScope && "test".equals(childDirect.getArtifact().getScope())) {
                continue;
            }
            ArtifactNodes artifactNodes = new ArtifactNodes(childDirect.getArtifact().getGroupId(),
                    childDirect.getArtifact().getArtifactId(), childDirect.getArtifact().getVersion());
            NodeAdapters.i().addArtifactNodes(artifactNodes);
            addIndirectArtifactNodes(childDirect, 2);
        }
        // add management node
    }

    private static void addIndirectArtifactNodes(DependencyNode dependencyNode, int depth) {
        if (depth <= Config.maxDependencyDepth) {
            for (DependencyNode child : dependencyNode.getChildren()) {
                if (MavenUtil.i().getMojo().ignoreTestScope && "test".equals(child.getArtifact().getScope())) {
                    continue;
                }
                ArtifactNodes artifactNodes = new ArtifactNodes(child.getArtifact().getGroupId(),
                        child.getArtifact().getArtifactId(), child.getArtifact().getVersion());
                NodeAdapters.i().addArtifactNodes(artifactNodes);
                addIndirectArtifactNodes(child, ++depth);
            }
        }
    }

    public List<ArtifactNodes> getContainer() {
        return container;
    }

    private List<ArtifactNodes> container;

    private NodeAdapters() {
        container = new ArrayList<ArtifactNodes>();
    }

    public void addArtifactNodes(ArtifactNodes artifactNodes) {
        container.add(artifactNodes);
    }

    public ArtifactNodes getArtifactNodes(DependencyInfo dependencyInfo) {
        for (ArtifactNodes artifactNodes : container) {
            if (artifactNodes.isSelf(dependencyInfo.getGroupId(), dependencyInfo.getArtifactId())) {
                return artifactNodes;
            }
        }
        return null;
    }
//    /**
//     * 根据node获得对应的adapter
//     *
//     * @param node
//     */
//    public ArtifactNodes getNodeAdapter(DependencyNode node) {
//        for (ArtifactNodes artifactNode : container) {
//            if (nodeAdapter.isSelf(node))
//                return nodeAdapter;
//        }
//        MavenUtil.i().getLog().warn("cant find nodeAdapter for node:" + node.toNodeString());
//        return null;
//    }

//    public NodeAdapter getNodeAdapter(NodeAdapter entryNodeAdapter) {
//        for (NodeAdapter nodeAdapter : container) {
//            if (nodeAdapter.isSelf(entryNodeAdapter))
//                return nodeAdapter;
//        }
//        MavenUtil.i().getLog().warn("can not find nodeAdapter for management node:" + entryNodeAdapter.toString());
//        return null;
//    }
//
//    public List<NodeAdapter> getAllNodeAdapter() {
//        return container;
//    }
}
