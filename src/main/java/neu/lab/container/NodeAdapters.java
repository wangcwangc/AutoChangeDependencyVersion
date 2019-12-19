package neu.lab.container;

import java.util.ArrayList;
import java.util.List;

import neu.lab.vo.ArtifactNode;
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

        System.out.println(root.toNodeString());

        for (DependencyNode child : root.getChildren()) {
            System.out.println(child.toNodeString());
            ArtifactNode artifactNode = new ArtifactNode(child.getArtifact().getGroupId(), child.getArtifact().getArtifactId());
            NodeAdapters.i().addNodeAapter(artifactNode);
        }
        // add management node
    }

    public List<ArtifactNode> getContainer() {
        return container;
    }

    private List<ArtifactNode> container;

    private NodeAdapters() {
        container = new ArrayList<ArtifactNode>();
    }

    public void addNodeAapter(ArtifactNode artifactNode) {
        container.add(artifactNode);
    }

//    /**
//     * 根据node获得对应的adapter
//     *
//     * @param node
//     */
//    public ArtifactNode getNodeAdapter(DependencyNode node) {
//        for (ArtifactNode artifactNode : container) {
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
