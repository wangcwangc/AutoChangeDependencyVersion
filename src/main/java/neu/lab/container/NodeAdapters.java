package neu.lab.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.util.MavenUtil;
import neu.lab.util.NodeAdapterCollector;
import neu.lab.vo.NodeAdapter;
import org.apache.maven.shared.dependency.tree.DependencyNode;

import neu.lab.vo.NodeAdapter;

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
        NodeAdapterCollector visitor = new NodeAdapterCollector(instance);
        root.accept(visitor);
        // add management node
    }

    private List<NodeAdapter> container;

    private NodeAdapters() {
        container = new ArrayList<NodeAdapter>();
    }

    public void addNodeAapter(NodeAdapter nodeAdapter) {
        container.add(nodeAdapter);
    }

    /**
     * 根据node获得对应的adapter
     *
     * @param node
     */
    public NodeAdapter getNodeAdapter(DependencyNode node) {
        for (NodeAdapter nodeAdapter : container) {
            if (nodeAdapter.isSelf(node))
                return nodeAdapter;
        }
        MavenUtil.i().getLog().warn("cant find nodeAdapter for node:" + node.toNodeString());
        return null;
    }

    public NodeAdapter getNodeAdapter(NodeAdapter entryNodeAdapter) {
        for (NodeAdapter nodeAdapter : container) {
            if (nodeAdapter.isSelf(entryNodeAdapter))
                return nodeAdapter;
        }
        MavenUtil.i().getLog().warn("can not find nodeAdapter for management node:" + entryNodeAdapter.toString());
        return null;
    }

    public List<NodeAdapter> getAllNodeAdapter() {
        return container;
    }

}
