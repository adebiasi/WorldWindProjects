package it.graphitech.objects;


import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        return Integer.signum((int)(o2.nodeMagnitude-o1.nodeMagnitude));
    }
}
