package bmstu.iu9.tfl.lab3;

import java.util.*;

public class TreeNode {
    private static final int TERM_STARTING_INDEX = 0;

    private final String         nodeExpr;

    private final boolean        isNonterm;
    private List<List<TreeNode>> leftNode;
    private List<List<String>>   rightNode;
    private Set<StringBuilder>   words;

    private final boolean        isEndOfDerivation;
    private boolean              rootNontermFound;
    private int                  indexOfRootNontermInLeftNodeList;
    private List<String>         suffixOfRootNontermDerivationTree;
    private StringBuilder  prefixOfRootNontermDerivationTree;
    private final StringBuilder  graphvizRepresentation;


    public TreeNode(String nonterm, Grammar rules, String rootNonterm, boolean rootNontermFound, Set<String> stackNontermsTrace) {
        isNonterm = true;
        nodeExpr = nonterm;
        leftNode = new ArrayList<>();
        rightNode = new ArrayList<>();
        words = new HashSet<>();
        isEndOfDerivation = false;
        this.rootNontermFound = rootNontermFound;

        prefixOfRootNontermDerivationTree = new StringBuilder();
        graphvizRepresentation = new StringBuilder();

        List<String[]> rewritingVariants = rules.getNontermRewritingVariants(nonterm);
        for (String[] rewritingVariant : rewritingVariants) {
            List<TreeNode> rewritingVariantLeftNode = new ArrayList<>();
            rewritingVariantLeftNode.add(new TreeNode(rewritingVariant[TERM_STARTING_INDEX], false));
            leftNode.add(rewritingVariantLeftNode);
            rightNode.add(new ArrayList<>(Arrays.asList(Arrays.copyOfRange(rewritingVariant, 1, rewritingVariant.length))));
        }

        Set<Integer> endedDerivation = new HashSet<>();
        while (endedDerivation.size() != leftNode.size()) {
            iterLeftmostDerivation(rules, rootNonterm, stackNontermsTrace, endedDerivation);
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
        List<Integer> circledDerivation = new ArrayList<>();
        for (int i = 0; i < rightNode.size(); i++) {
            if (!rootNontermFound || i != indexOfRootNontermInLeftNodeList) {
                List<String> rightNodeVariant = rightNode.get(i);
                if (rightNodeVariant.isEmpty()) {
                    Set<StringBuilder> derivationToTermsString = buildShortestDerivationToTermsString(leftNode.get(i));
                    if (derivationToTermsString != null) {
                        words.addAll(derivationToTermsString);
                    }
                    endedDerivation.add(i);
                } else {
                    String expr = rightNodeVariant.get(0);
                    if (expr.matches(RuleRightSide.NONTERM_REGEX)) {
                        if (stackNontermsTrace.contains(expr)) {
                            circledDerivation.add(0, i);
                        } else if (!rootNontermFound && expr.equals(rootNonterm)) {
                            setRootNontermFound(i, endedDerivation);
                            leftNode.get(i).add(new TreeNode(rootNonterm, true));
                        } else {
                            Set<String> newStackNontermsTrace = new HashSet<>(stackNontermsTrace);
                            newStackNontermsTrace.add(expr);
                            TreeNode newNode = new TreeNode(expr, rules, rootNonterm, rootNontermFound, newStackNontermsTrace);
                            if (newNode.leftNode.isEmpty()) {
                                circledDerivation.add(0, i);
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
                    rightNodeVariant.remove(0);
                }
            }
        }
        for (int noRootNontermFoundIndex : circledDerivation) {
            if (rootNontermFound && noRootNontermFoundIndex < indexOfRootNontermInLeftNodeList) {
                indexOfRootNontermInLeftNodeList--;
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
                Set<StringBuilder> wordsOfNodeNonterm = node.words;
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
        indexOfRootNontermInLeftNodeList = i;
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
                for (TreeNode prefixNode : leftNode.get(indexOfRootNontermInLeftNodeList)) {
                    int k = j;
                    j = prefixNode.buildLeftMostDerivationToRootNonterm(j);
                    prefix.append(prefixNode.prefixOfRootNontermDerivationTree);
                    buildRelationshipBtwTwoNodesInGraphvizRepresentation(prefixNode.nodeExpr, i, k);
                    appendNodeToGraphvizRepresentation(prefixNode);
                }
                List<TreeNode> derivation = leftNode.get(indexOfRootNontermInLeftNodeList);

                List<String> suffix = rightNode.get(indexOfRootNontermInLeftNodeList);
                if (derivation.get(derivation.size() - 1).isEndOfDerivation) {
                    suffixOfRootNontermDerivationTree = new ArrayList<>(suffix);
                } else {
                    suffixOfRootNontermDerivationTree = new ArrayList<>(derivation.get(derivation.size() - 1).suffixOfRootNontermDerivationTree);
                    suffixOfRootNontermDerivationTree.addAll(suffix);
                }
                if (!suffix.isEmpty()) {
                    buildRelationshipBtwTwoNodesInGraphvizRepresentation(String.join("", suffix), i, j++);
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
        return j;
    }

    private void buildRelationshipBtwTwoNodesInGraphvizRepresentation(String node, int i, int j) {
        graphvizRepresentation.append("{").append(i).append("[label = \"").append(nodeExpr).append("\"]} -> {").append(j).append("[label = \"").append(node).append("\"]};\n");
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

    protected Set<StringBuilder> getWords() {
        return this.words;
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
