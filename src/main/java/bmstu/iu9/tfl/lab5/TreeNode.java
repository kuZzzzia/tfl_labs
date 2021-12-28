package bmstu.iu9.tfl.lab5;

import java.util.List;

public class TreeNode {
    enum NodeType {
        TERMINAL,
        ITER
    }

    private NodeType type;
    private String nonterm;
    private String term;
    private TreeNode left, right;

    public TreeNode(String nonterm, TreeNode left, TreeNode right) {
        this.nonterm = nonterm;
        this.left = left;
        this.right = right;
        type = NodeType.ITER;
    }

    public TreeNode(String nonterm, String term) {
        this.nonterm = nonterm;
        this.term = term;
        type = NodeType.TERMINAL;
    }

    public String getNonterm() {
        return nonterm;
    }
}
