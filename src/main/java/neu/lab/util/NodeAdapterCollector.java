package neu.lab.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;

import neu.lab.container.NodeAdapters;
import neu.lab.conflict.vo.NodeAdapter;

public class NodeAdapterCollector implements DependencyNodeVisitor {
    private static Set<String> longTimeLib;// lib that takes a long time to get call-graph.要花很长时间才能得到call-graph。

    static {
        longTimeLib = new HashSet<String>();
        longTimeLib.add("org.scala-lang:scala-library");
        longTimeLib.add("org.clojure:clojure");
    }

    private NodeAdapters nodeAdapters;

    public NodeAdapterCollector(NodeAdapters nodeAdapters) {
        this.nodeAdapters = nodeAdapters;
    }

    public boolean visit(DependencyNode node) {
        nodeAdapters.addNodeAapter(new NodeAdapter(node));
        return true;
    }

    public boolean endVisit(DependencyNode node) {
        return true;
    }
}
