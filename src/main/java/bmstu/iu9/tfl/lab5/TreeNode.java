package bmstu.iu9.tfl.lab5;

import java.util.*;

public class TreeNode {
    enum NodeType {
        TERMINAL,
        CNF,
        CFG
    }

    private static final Set<String> NONTERMS = new HashSet<>(Arrays.asList(
            MetaGrammar.RULE,
            MetaGrammar.EXP,
            MetaGrammar.NTERM,
            MetaGrammar.ALT,
            MetaGrammar.NEXTALT,
            MetaGrammar.ITER,
            MetaGrammar.CONST,
            MetaGrammar.NNAME,
            MetaGrammar.CNAME
    ));

    private static final Set<String> SYNTAX = new HashSet<>(NONTERMS);

    static {
        SYNTAX.addAll(MetaGrammar.SYNTAX_TOKENS);
    }

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

    public TreeNode(String nonterm, List<TreeNode> path) {
        this.nonterm = nonterm;
        this.path = path;
        type = NodeType.CFG;
    }

    public TreeNode(String nonterm, String term) {
        this.nonterm = nonterm;
        this.term = term;
        type = NodeType.TERMINAL;
    }

    public String getNonterm() {
        return nonterm;
    }

    public boolean applyNewSyntax(Map<String, String> newSyntax) {
        if (type == NodeType.CFG) {
            path.removeIf(s -> s.nonterm.equals(MetaGrammar.SPACE));
            switch (nonterm) {
                case MetaGrammar.RULE:
                    for (int i = 0; i < path.size(); i++) {
                        TreeNode node = path.get(i);
                        switch (node.nonterm) {
                            case MetaGrammar.RULE:
                                if (!node.applyNewSyntax(newSyntax)) {
                                    return false;
                                }
                                break;
                            case MetaGrammar.BEGIN_RULE:
                            case MetaGrammar.SEP_R:
                            case MetaGrammar.END_RULE:
                                path.remove(i--);
                                break;
                            case MetaGrammar.NTERM:
                                if (!node.applyNewSyntax(newSyntax)) {
                                    return false;
                                }
                                path.add(i++, new TreeNode(MetaGrammar.BEGIN_RULE, newSyntax.get(MetaGrammar.BEGIN_RULE)));
                                break;
                            case MetaGrammar.EXP:
                                if (node.expIsEmpty(node) || !node.applyNewSyntax(newSyntax)) {
                                    return false;
                                }
                                path.add(i++, new TreeNode(MetaGrammar.SEP_R, newSyntax.get(MetaGrammar.SEP_R)));
                                path.add(++i, new TreeNode(MetaGrammar.END_RULE, newSyntax.get(MetaGrammar.END_RULE)));
                                path.add(++i, new TreeNode(MetaGrammar.SPACE, "\n"));
                                break;
                        }
                    }
                    break;
                case MetaGrammar.NTERM:
                    for (int i = 0; i < path.size(); i++) {
                        TreeNode node = path.get(i);
                        switch (node.nonterm) {
                            case MetaGrammar.BEGIN_NTERM:
                            case MetaGrammar.END_NTERM:
                                path.remove(i--);
                                break;
                            case MetaGrammar.NNAME:
                                path.add(i++, new TreeNode(MetaGrammar.BEGIN_NTERM, newSyntax.get(MetaGrammar.END_NTERM)));
                                path.add(++i, new TreeNode(MetaGrammar.END_NTERM, newSyntax.get(MetaGrammar.END_NTERM)));
                                break;
                        }
                    }
                    break;
                case MetaGrammar.ALT:
                    for (int i = 0; i < path.size(); i++) {
                        TreeNode node = path.get(i);
                        switch (node.nonterm) {
                            case MetaGrammar.BEGIN_ALT:
                            case MetaGrammar.SEP_A:
                                path.remove(i--);
                                break;
                            case MetaGrammar.EXP:
                                if (node.expIsEmpty(node) || !node.applyNewSyntax(newSyntax)) {
                                    return false;
                                }
                                path.add(i++, new TreeNode(MetaGrammar.BEGIN_ALT, newSyntax.get(MetaGrammar.BEGIN_ALT)));
                                path.add(++i, new TreeNode(MetaGrammar.SEP_A, newSyntax.get(MetaGrammar.SEP_A)));
                                break;
                            case MetaGrammar.NEXTALT:
                                if (!node.applyNewSyntax(newSyntax)) {
                                    return false;
                                }
                                break;
                        }
                    }
                    break;
                case MetaGrammar.NEXTALT:
                    boolean next = false;
                    for (TreeNode node : path) {
                        if (node.nonterm.equals(MetaGrammar.NEXTALT)) {
                            next = true;
                            break;
                        }
                    }
                    if (next) {
                        for (int i = 0; i < path.size(); i++) {
                            TreeNode node = path.get(i);
                            switch (node.nonterm) {
                                case MetaGrammar.SEP_A:
                                    path.remove(i--);
                                    break;
                                case MetaGrammar.EXP:
                                    if (node.expIsEmpty(node) || !node.applyNewSyntax(newSyntax)) {
                                        return false;
                                    }
                                    path.add(i++, new TreeNode(MetaGrammar.BEGIN_ALT, newSyntax.get(MetaGrammar.BEGIN_ALT)));
                                    path.add(++i, new TreeNode(MetaGrammar.SEP_A, newSyntax.get(MetaGrammar.SEP_A)));
                                    break;
                                case MetaGrammar.NEXTALT:
                                    if (!node.applyNewSyntax(newSyntax)) {
                                        return false;
                                    }
                                    path.add(i++, new TreeNode(MetaGrammar.SEP_A, newSyntax.get(MetaGrammar.SEP_A)));
                                    break;
                            }
                        }
                    } else {
                        boolean expExists = false;
                        for (TreeNode node : path) {
                            if (node.nonterm.equals(MetaGrammar.EXP)) {
                                expExists = true;
                                break;
                            }
                        }
                        if (!expExists) {
                            int j = path.size();
                            if (path.get(path.size() - 1).nonterm.equals(MetaGrammar.END_ALT)) {
                                j--;
                            }
                            List<TreeNode> newNode = new ArrayList<>();
                            for (int i = 0; i < j; i++) {
                                newNode.add(path.get(i));
                            }
                            path.subList(0, j).clear();
                            path.add(0, new TreeNode(MetaGrammar.EXP, newNode));
                        }
                        for (int i = 0; i < path.size(); i++) {
                            TreeNode node = path.get(i);
                            switch (node.nonterm) {
                                case MetaGrammar.END_ALT:
                                    path.remove(i--);
                                    break;
                                case MetaGrammar.EXP:
                                    if (node.expIsEmpty(node) || !node.applyNewSyntax(newSyntax)) {
                                        return false;
                                    }
                                    path.add(++i, new TreeNode(MetaGrammar.END_ALT, newSyntax.get(MetaGrammar.END_ALT)));
                                    break;
                            }
                        }
                    }
                    break;
                case MetaGrammar.ITER:
                    for (int i = 0; i < path.size(); i++) {
                        TreeNode node = path.get(i);
                        switch (node.nonterm) {
                            case MetaGrammar.BEGIN_ITER:
                            case MetaGrammar.END_ITER:
                                path.remove(i--);
                                break;
                            case MetaGrammar.EXP:
                                if (node.expIsEmpty(node) || !node.applyNewSyntax(newSyntax)) {
                                    return false;
                                }
                                path.add(i++, new TreeNode(MetaGrammar.BEGIN_ITER, newSyntax.get(MetaGrammar.BEGIN_ITER)));
                                path.add(++i, new TreeNode(MetaGrammar.END_ITER, newSyntax.get(MetaGrammar.END_ITER)));
                                break;
                        }
                    }
                    break;
                case MetaGrammar.CONST:
                    for (int i = 0; i < path.size(); i++) {
                        TreeNode node = path.get(i);
                        switch (node.nonterm) {
                            case MetaGrammar.BEGIN_CONST:
                            case MetaGrammar.END_CONST:
                                path.remove(i--);
                                break;
                            case MetaGrammar.CNAME:
                                path.add(i++, new TreeNode(MetaGrammar.BEGIN_CONST, newSyntax.get(MetaGrammar.BEGIN_CONST)));
                                path.add(++i, new TreeNode(MetaGrammar.END_CONST, newSyntax.get(MetaGrammar.END_CONST)));
                                break;
                        }
                    }
                    break;
                case MetaGrammar.EXP:
                    boolean lparenAppeared = false;
                    for (int i = 0; i < path.size(); i++) {
                        TreeNode node = path.get(i);
                        switch (node.nonterm) {
                            case MetaGrammar.LPAREN: {
                                lparenAppeared = true;
                                node.term = newSyntax.get(MetaGrammar.LPAREN);
                                int j = i + 1;
                                while (j < path.size() && !path.get(j).nonterm.equals(MetaGrammar.RPAREN)) {
                                    j++;
                                }
                                if (j == path.size()) {
                                    j = i + 1;
                                    while (j < path.size() && !path.get(j).nonterm.equals(MetaGrammar.EXP)) {
                                        j++;
                                    }
                                    if (j == path.size()) {
                                        path.add(new TreeNode(MetaGrammar.RPAREN, newSyntax.get(MetaGrammar.RPAREN)));
                                    } else {
                                        path.add(++j, new TreeNode(MetaGrammar.RPAREN, newSyntax.get(MetaGrammar.RPAREN)));
                                    }
                                }
                                break;
                            }
                            case MetaGrammar.RPAREN:
                                node.term = newSyntax.get(MetaGrammar.RPAREN);
                                if (!lparenAppeared) {
                                    int j = i - 1;
                                    while (j > -1 && !path.get(j).nonterm.equals(MetaGrammar.EXP)) {
                                        j--;
                                    }
                                    if (j == -1) {
                                        path.add(0, new TreeNode(MetaGrammar.LPAREN, newSyntax.get(MetaGrammar.LPAREN)));
                                    } else {
                                        path.add(j, new TreeNode(MetaGrammar.LPAREN, newSyntax.get(MetaGrammar.LPAREN)));
                                    }
                                    i++;
                                }
                                break;
                            case MetaGrammar.NNAME: {
                                List<TreeNode> newNode = new ArrayList<>();
                                newNode.add(new TreeNode(MetaGrammar.BEGIN_NTERM, newSyntax.get(MetaGrammar.BEGIN_NTERM)));
                                newNode.add(node);
                                newNode.add(new TreeNode(MetaGrammar.END_NTERM, newSyntax.get(MetaGrammar.END_NTERM)));
                                int j = findPrev(i - 1, path, MetaGrammar.BEGIN_NTERM);
                                int k = findPost(i + 1, path, MetaGrammar.END_NTERM);
                                path.subList(j + 1, k).clear();
                                i = j + 1;
                                path.add(i, new TreeNode(MetaGrammar.NTERM, newNode));
                                break;
                            }
                            case MetaGrammar.NEXTALT: {
                                List<TreeNode> newNode = new ArrayList<>();
                                newNode.add(new TreeNode(MetaGrammar.BEGIN_ALT, newSyntax.get(MetaGrammar.BEGIN_ALT)));
                                int j = findPrev(i - 1, path, MetaGrammar.EXP);
                                newNode.add(path.get(j));
                                newNode.add(new TreeNode(MetaGrammar.SEP_A, newSyntax.get(MetaGrammar.SEP_A)));
                                newNode.add(path.get(i));
                                j = findPrev(j - 1, path, MetaGrammar.BEGIN_ALT);
                                path.subList(j + 1, i + 1).clear();
                                i = i + 1;
                                path.add(i, new TreeNode(MetaGrammar.ALT, newNode));
                                break;
                            }
                            case MetaGrammar.CNAME: {
                                List<TreeNode> newNode = new ArrayList<>();
                                newNode.add(new TreeNode(MetaGrammar.BEGIN_CONST, newSyntax.get(MetaGrammar.BEGIN_CONST)));
                                newNode.add(node);
                                newNode.add(new TreeNode(MetaGrammar.END_CONST, newSyntax.get(MetaGrammar.END_CONST)));
                                int j = findPrev(i - 1, path, MetaGrammar.BEGIN_CONST);
                                int k = findPost(i + 1, path, MetaGrammar.END_CONST);
                                path.subList(j + 1, k).clear();
                                i = j + 1;
                                path.add(i, new TreeNode(MetaGrammar.CONST, newNode));
                                break;
                            }
                            case MetaGrammar.BEGIN_ITER: {
                                List<TreeNode> newNode = new ArrayList<>();
                                newNode.add(new TreeNode(MetaGrammar.BEGIN_ITER, newSyntax.get(MetaGrammar.BEGIN_ITER)));
                                newNode.add(path.get(i + 1));
                                newNode.add(new TreeNode(MetaGrammar.END_ITER, newSyntax.get(MetaGrammar.END_ITER)));
                                if (i + 2 < path.size()) {
                                    if (path.get(i + 2).nonterm.equals(MetaGrammar.END_ITER)) {
                                        path.subList(i, i + 3).clear();
                                    }
                                } else {
                                    path.subList(i, i + 2).clear();
                                }
                                path.add(i, new TreeNode(MetaGrammar.ITER, newNode));
                                break;
                            }
                            case MetaGrammar.END_ITER: {
                                List<TreeNode> newNode = new ArrayList<>();
                                newNode.add(new TreeNode(MetaGrammar.BEGIN_ITER, newSyntax.get(MetaGrammar.BEGIN_ITER)));
                                newNode.add(path.get(i - 1));
                                newNode.add(new TreeNode(MetaGrammar.END_ITER, newSyntax.get(MetaGrammar.END_ITER)));
                                path.subList(i - 1, i + 1).clear();
                                i--;
                                path.add(i, new TreeNode(MetaGrammar.ITER, newNode));;
                                break;
                            }
                        }
                    }
                    for (TreeNode node : path) {
                        if (NONTERMS.contains(node.nonterm)) {
                            if (!node.applyNewSyntax(newSyntax)) {
                                return false;
                            }
                        }
                    }
                    break;
            }

        }
        return true;
    }

    private static int findPrev(int j, List<TreeNode> path, String nonterm) {
        if (j > -1) {
            if (path.get(j).nonterm.equals(nonterm)) {
                j--;
            } else {
                j++;
            }
        }
        return j;
    }

    private static int findPost(int k, List<TreeNode> path, String nonterm) {
        if (k < path.size()) {
            if (path.get(k).nonterm.equals(nonterm)) {
                k++;
            } else {
                k--;
            }
        }
        return k;
    }

    private boolean expIsEmpty(TreeNode node) {
        return node.print().trim().isEmpty();
    }

    public void foldTree(String newNNameRegex, String newCNameRegex) {
        if (type == NodeType.CNF) {
            if (nonterm.equals(MetaGrammar.STARTING_NONTERM)) {
                nonterm = MetaGrammar.RULE;
            }
            path = new ArrayList<>();

            left.foldSubtree(newNNameRegex, newCNameRegex);
            if (left.type.equals(NodeType.CFG) && !SYNTAX.contains(left.nonterm)) {
                path.addAll(left.path);
            } else {
                path.add(left);
            }

            right.foldSubtree(newNNameRegex, newCNameRegex);
            if (right.type.equals(NodeType.CFG) && !SYNTAX.contains(right.nonterm)) {
                path.addAll(right.path);
            } else {
                path.add(right);
            }
            type = NodeType.CFG;
            left = null;
            right = null;
        }
    }

    public void foldSubtree(String newNNameRegex, String newCNameRegex) {
        if (type == NodeType.CNF) {
            foldTree(newNNameRegex, newCNameRegex);
            if (nonterm.equals(MetaGrammar.CNAME)) {
                checkConflictsInNameDefinition(this, newCNameRegex);
            } else if (nonterm.equals(MetaGrammar.NNAME)) {
                checkConflictsInNameDefinition(this, newNNameRegex);
            }
        }
    }

    public static void checkConflictsInNameDefinition(TreeNode node, String regex) {
        String name = node.print();
        if(!name.matches(regex)) {
            throw new Error("Conflict in name found: " + name);
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
