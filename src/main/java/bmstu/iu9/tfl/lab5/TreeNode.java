package bmstu.iu9.tfl.lab5;

import org.omg.CORBA.TRANSACTION_MODE;

import java.util.*;

public class TreeNode {
    enum NodeType {
        TERMINAL,
        CNF,
        CFG
    }

    private static final Set<String> NONTERMS = new HashSet<>(Arrays.asList(
            "RULE",
            "EXP",
            "NTERM",
            "ALT",
            "NEXTALT",
            "ITER",
            "CONST",
            "NNAME",
            "CNAME"
    ));

    private NodeType type;
    private String nonterm;
    private String term;
    private TreeNode left, right;
    private List<TreeNode> path;

    public TreeNode(String nonterm, TreeNode left, TreeNode right) {
        this.nonterm = nonterm;
        this.left = left;
        this.right = right;
        type = NodeType.CNF;
    }

    public TreeNode(String nonterm, String term) {
        this.nonterm = nonterm;
        this.term = term;
        type = NodeType.TERMINAL;
    }

    public String getNonterm() {
        return nonterm;
    }

    public void foldTree(String newNNameRegex, String newCNameRegex) {
        if (type == NodeType.CNF) {
            if (nonterm.equals(MetaGrammar.NEW_STARTING_NONTERM) || nonterm.equals(MetaGrammar.STARTING_NONTERM)) {
                nonterm = MetaGrammar.RULE;
            }
            path = new ArrayList<>();
            type = NodeType.CFG;
            foldSubtree(left, newNNameRegex, newCNameRegex);
            foldSubtree(right, newNNameRegex, newCNameRegex);
            left = null;
            right = null;
        }
    }

    public void foldSubtree(TreeNode node, String newNNameRegex, String newCNameRegex) {
        if (node.type == NodeType.CNF) {
            if (node.nonterm.equals(MetaGrammar.CNAME)) {
                checkConflictsInNameDefinition(node, newCNameRegex);
            } else if (node.nonterm.equals(MetaGrammar.NNAME)) {
                checkConflictsInNameDefinition(node, newNNameRegex);
            } else {
                left.foldTree(newNNameRegex, newCNameRegex);
            }
            if (!NONTERMS.contains(left.nonterm)) {
                path.addAll(left.path);
            } else {
                path.add(left);
            }
        } else {
            path.add(left);
        }
    }

    public static void checkConflictsInNameDefinition(TreeNode node, String regex) {
        String name = node.print();
        if(!name.matches(regex)) {
            throw new Error("Conflict in name found: " + name); //TODO: const
        }
    }

    public String print() {
        if (type == NodeType.TERMINAL) {
            return term;
        }
        StringBuilder s = new StringBuilder();
        for (TreeNode node : path) {
            s.append(node.print());
        }
        return s.toString();
    }
}
