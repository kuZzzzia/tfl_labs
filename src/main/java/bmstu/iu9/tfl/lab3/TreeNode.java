package bmstu.iu9.tfl.lab3;

import java.util.*;

public class TreeNode {
    private static final int    INDEX_FOR_LIFO = 0;
    private static final int    INDEX_OF_FIRST_EXPR_IN_RIGHT_NODE = 0;
    private static final String EMPTY_STRING = "";

    private final String         nodeExpr;

    private final boolean           isNonterm;
    private List<List<TreeNode>>    leftNode;
    private List<List<String>>      rightNode;
    private Set<StringBuilder>      shortestWords;

    private final boolean       isEndOfDerivation;
    private boolean             rootNontermFound;
    private int                 indexOfDerivationWithRootNonterm;
    private StringBuilder       prefixOfRootNontermDerivationTree;
    private List<String>        suffixOfRootNontermDerivationTree;
    private final StringBuilder graphvizRepresentation;


    public TreeNode(String nonterm, Grammar rules, String rootNonterm, boolean rootNontermFound, Set<String> stackNontermsTrace) {
        isNonterm = true;
        nodeExpr = nonterm;
        leftNode = new ArrayList<>();
        rightNode = new ArrayList<>();
        shortestWords = new HashSet<>();
        isEndOfDerivation = false;
        this.rootNontermFound = rootNontermFound;

        prefixOfRootNontermDerivationTree = new StringBuilder();
        graphvizRepresentation = new StringBuilder();

        if (!rootNontermFound) {
            List<String[]> rewritingVariants = rules.getNontermRewritingVariants(nonterm);
            for (String[] rewritingVariant : rewritingVariants) {
                leftNode.add(new ArrayList<>());
                rightNode.add(new ArrayList<>(Arrays.asList(rewritingVariant)));
            }

            Set<Integer> endedDerivation = new HashSet<>();
            while (endedDerivation.size() != leftNode.size()) {
                iterLeftmostDerivation(rules, rootNonterm, stackNontermsTrace, endedDerivation);
            }
        }
    }

    public TreeNode(String termsString, boolean isEndOfDerivation) {
        isNonterm = isEndOfDerivation;
        nodeExpr = termsString;
        this.isEndOfDerivation = isEndOfDerivation;
        prefixOfRootNontermDerivationTree = new StringBuilder();
        graphvizRepresentation = new StringBuilder();
    }

    private void iterLeftmostDerivation(Grammar rules, String rootNonterm, Set<String> stackNontermsTrace, Set<Integer> endedDerivation) {
        List<Integer> recursiveDerivation = new ArrayList<>();
        for (int i = 0; i < rightNode.size(); i++) {
            if (!rootNontermFound || i != indexOfDerivationWithRootNonterm) {
                List<String> rightNodeVariant = rightNode.get(i);
                if (rightNodeVariant.isEmpty()) {
                    Set<StringBuilder> derivationToTermsString = buildShortestDerivationToTermsString(leftNode.get(i));
                    if (derivationToTermsString != null) {
                        shortestWords.addAll(derivationToTermsString);
                    }
                    endedDerivation.add(i);
                } else {
                    String expr = rightNodeVariant.get(INDEX_OF_FIRST_EXPR_IN_RIGHT_NODE);
                    if (expr.matches(RuleRightSide.NONTERM_REGEX)) {
                        if (stackNontermsTrace.contains(expr)) {
                            recursiveDerivation.add(INDEX_FOR_LIFO, i);
                        } else if (!rootNontermFound && expr.equals(rootNonterm)) {
                            setRootNontermFound(i, endedDerivation);
                            stackNontermsTrace.add(rootNonterm);
                            leftNode.get(i).add(new TreeNode(rootNonterm, true));
                        } else {
                            Set<String> newStackNontermsTrace = new HashSet<>(stackNontermsTrace);
                            newStackNontermsTrace.add(expr);
                            TreeNode newNode = new TreeNode(expr, rules, rootNonterm, false, newStackNontermsTrace);
                            if (newNode.leftNode.isEmpty()) {
                                recursiveDerivation.add(INDEX_FOR_LIFO, i);
                            } else {
                                if (newNode.rootNontermFound) {
                                    setRootNontermFound(i, endedDerivation);
                                }
                                leftNode.get(i).add(newNode);
                            }
                        }
                    } else {
                        leftNode.get(i).add(new TreeNode(expr, false));
                    }
                    rightNodeVariant.remove(INDEX_OF_FIRST_EXPR_IN_RIGHT_NODE);
                }
            }
        }
        for (int noRootNontermFoundIndex : recursiveDerivation) {
            if (rootNontermFound && noRootNontermFoundIndex < indexOfDerivationWithRootNonterm) {
                indexOfDerivationWithRootNonterm--;
            }
            leftNode.remove(noRootNontermFoundIndex);
            rightNode.remove(noRootNontermFoundIndex);
        }
    }

    private Set<StringBuilder> buildShortestDerivationToTermsString(List<TreeNode> leftmostDerivationTree) {
        Set<StringBuilder> words = new HashSet<>();
        for (TreeNode node : leftmostDerivationTree) {
            if (node.isNonterm) {
                Set<StringBuilder> concat = new HashSet<>();
                Set<StringBuilder> wordsOfNodeNonterm = node.shortestWords;
                if (wordsOfNodeNonterm == null) {
                    return null;
                }
                for (StringBuilder wordPrefix : words) {
                    for (StringBuilder wordSuffix : wordsOfNodeNonterm) {
                        concat.add(new StringBuilder(wordPrefix.toString().concat(wordSuffix.toString())));
                    }
                }
                words = concat;
            } else {
                String derivationEnd = node.nodeExpr;
                if (words.isEmpty()) {
                    words.add(new StringBuilder(derivationEnd));
                } else {
                    for (StringBuilder word : words) {
                        word.append(derivationEnd);
                    }
                }
            }
        }
        return words;
    }

    private void setRootNontermFound(int i, Set<Integer> endedDerivation) {
        rootNontermFound = true;
        indexOfDerivationWithRootNonterm = i;
        endedDerivation.add(i);
    }

    protected boolean checkRootNontermFound() {
        return rootNontermFound;
    }


    protected int buildLeftMostDerivationToRootNonterm(int i) {
        int j = i + 1;
        if (isNonterm) {
            if (isEndOfDerivation) {
                return j;
            }
            StringBuilder prefix = new StringBuilder();
            if (rootNontermFound) {
                for (TreeNode prefixNode : leftNode.get(indexOfDerivationWithRootNonterm)) {
                    int k = j;
                    j = prefixNode.buildLeftMostDerivationToRootNonterm(j);
                    prefix.append(prefixNode.prefixOfRootNontermDerivationTree);
                    buildRelationshipBtwTwoNodesInGraphvizRepresentation(prefixNode.nodeExpr, i, k);
                    appendNodeToGraphvizRepresentation(prefixNode);
                }
                List<TreeNode> derivation = leftNode.get(indexOfDerivationWithRootNonterm);

                List<String> suffix = rightNode.get(indexOfDerivationWithRootNonterm);
                if (derivation.get(derivation.size() - 1).isEndOfDerivation) {
                    suffixOfRootNontermDerivationTree = new ArrayList<>(suffix);
                } else {
                    suffixOfRootNontermDerivationTree = new ArrayList<>(derivation.get(derivation.size() - 1).suffixOfRootNontermDerivationTree);
                    suffixOfRootNontermDerivationTree.addAll(suffix);
                }
                if (!suffix.isEmpty()) {
                    buildRelationshipBtwTwoNodesInGraphvizRepresentation(String.join(EMPTY_STRING, suffix), i, j++);
                }
            } else {
                for (TreeNode prefixNode : leftNode.get(0)) {
                    int k = j;
                    j = prefixNode.buildLeftMostDerivationToRootNonterm(j);
                    prefix.append(prefixNode.prefixOfRootNontermDerivationTree);
                    buildRelationshipBtwTwoNodesInGraphvizRepresentation(prefixNode.nodeExpr, i, k);
                    appendNodeToGraphvizRepresentation(prefixNode);
                }
            }
            prefixOfRootNontermDerivationTree = prefix;
            return j;
        }
        prefixOfRootNontermDerivationTree = new StringBuilder(nodeExpr);
        return j;
    }

    private void buildRelationshipBtwTwoNodesInGraphvizRepresentation(String node, int i, int j) {
        appendNodeToGraphRelation(graphvizRepresentation, i, nodeExpr);
        graphvizRepresentation.append(" -> ");
        appendNodeToGraphRelation(graphvizRepresentation, j, node);
        graphvizRepresentation.append(";\n");
    }

    private static void appendNodeToGraphRelation(StringBuilder graph, int num, String name) {
        graph.append("{").append(num).append(" [label = \"").append(name).append("\"]}");

    }

    private void appendNodeToGraphvizRepresentation(TreeNode prefixNode) {
        graphvizRepresentation.append(prefixNode.graphvizRepresentation.toString());
    }

    protected List<String> getSuffixOfRootNontermDerivationTree() {
        return suffixOfRootNontermDerivationTree;
    }

    protected String getPrefixOfRootNontermDerivationTree() {
        return prefixOfRootNontermDerivationTree.toString();
    }

    protected Set<StringBuilder> getShortestWords() {
        return this.shortestWords;
    }

    protected String getNodeExpr() {
        return nodeExpr;
    }

    protected boolean getRootNontermFound() {
        return rootNontermFound;
    }

    protected String getGraphvizRepresentation() {
        return graphvizRepresentation.toString();
    }
}
