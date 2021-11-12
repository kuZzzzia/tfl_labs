package bmstu.iu9.tfl.lab3;

import java.util.*;

public class TreeNode {
    private static final int TERM_STARTING_INDEX = 0;

    private final String         nodeExpr;

    private final boolean        isNonterm;
    private List<List<TreeNode>> middleNode;
    private List<List<String>>   rightNode;
    private Set<StringBuilder>   words;

    private final boolean        isEndOfDerivation;
    private boolean              rootNontermFound;
    private int                  indexOfRootNontermInMiddleNodeList;
    private List<String>         nontermsAndTermsSuffixOfRootNontermDerivationTree;


    public TreeNode(String nonterm, Grammar rules, String rootNonterm, boolean rootNontermFound, Set<String> stackNontermsTrace) {
        isNonterm = true;
        nodeExpr = nonterm;
        middleNode = new ArrayList<>();
        rightNode = new ArrayList<>();
        words = new HashSet<>();
        isEndOfDerivation = false;
        this.rootNontermFound = rootNontermFound;

        List<String[]> rewritingVariants = rules.getNontermRewritingVariants(nonterm);
        for (String[] rewritingVariant : rewritingVariants) {
            List<TreeNode> rewritingVariantMiddleNode = new ArrayList<>();
            rewritingVariantMiddleNode.add(new TreeNode(rewritingVariant[TERM_STARTING_INDEX], false));
            middleNode.add(rewritingVariantMiddleNode);
            rightNode.add(new ArrayList<>(Arrays.asList(Arrays.copyOfRange(rewritingVariant, 1, rewritingVariant.length))));
        }

        Set<Integer> endedDerivation = new HashSet<>();
        while (endedDerivation.size() != middleNode.size()) {
            iterLeftmostDerivation(rules, rootNonterm, stackNontermsTrace, endedDerivation);
        }
    }

    public TreeNode(String termsString, boolean isEndOfDerivation) {
        isNonterm = isEndOfDerivation;
        nodeExpr = termsString;
        this.isEndOfDerivation = isEndOfDerivation;
    }

    private void iterLeftmostDerivation(Grammar rules, String rootNonterm, Set<String> stackNontermsTrace, Set<Integer> endedDerivation) {
        List<Integer> circledDerivation = new ArrayList<>();
        for (int i = 0; i < rightNode.size(); i++) {
            if (!rootNontermFound || i != indexOfRootNontermInMiddleNodeList) {
                List<String> rightNodeVariant = rightNode.get(i);
                if (rightNodeVariant.isEmpty()) {
                    Set<StringBuilder> derivationToTermsString = buildShortestDerivationToTermsString(middleNode.get(i));
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
                            middleNode.get(i).add(new TreeNode(rootNonterm, true));
                        } else {
                            Set<String> newStackNontermsTrace = new HashSet<>(stackNontermsTrace);
                            newStackNontermsTrace.add(expr);
                            TreeNode newNode = new TreeNode(expr, rules, rootNonterm, rootNontermFound, newStackNontermsTrace);
                            if (newNode.middleNode.isEmpty()) {
                                circledDerivation.add(0, i);
                            } else {
                                if (newNode.rootNontermFound) {
                                    setRootNontermFound(i, endedDerivation);
                                }
                                middleNode.get(i).add(newNode);
                            }
                        }
                    } else {
                        middleNode.get(i).add(new TreeNode(expr, false));
                    }
                    rightNodeVariant.remove(0);
                }
            }
        }
        for (int noRootNontermFoundIndex : circledDerivation) {
            if (rootNontermFound && noRootNontermFoundIndex < indexOfRootNontermInMiddleNodeList) {
                indexOfRootNontermInMiddleNodeList--;
            }
            middleNode.remove(noRootNontermFoundIndex);
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
        indexOfRootNontermInMiddleNodeList = i;
        endedDerivation.add(i);
    }

    protected boolean checkRootNontermFound() {
        return rootNontermFound;
    }


    protected String buildLeftMostDerivationToRootNonterm(String rootNonterm) {
        if (isNonterm) {
            if (isEndOfDerivation) {
                return "";
            }
            StringBuilder prefix = new StringBuilder();
            if (rootNontermFound) {
                for (TreeNode prefixNode : middleNode.get(indexOfRootNontermInMiddleNodeList)) {
                    prefix.append(prefixNode.buildLeftMostDerivationToRootNonterm(rootNonterm));
                }
                List<TreeNode> derivation = middleNode.get(indexOfRootNontermInMiddleNodeList);
                if (derivation.get(derivation.size() - 1).isEndOfDerivation) {
                    nontermsAndTermsSuffixOfRootNontermDerivationTree = new ArrayList<>(rightNode.get(indexOfRootNontermInMiddleNodeList));
                } else {
                    nontermsAndTermsSuffixOfRootNontermDerivationTree = new ArrayList<>(derivation.get(derivation.size() - 1).nontermsAndTermsSuffixOfRootNontermDerivationTree);
                    nontermsAndTermsSuffixOfRootNontermDerivationTree.addAll(rightNode.get(indexOfRootNontermInMiddleNodeList));
                }
            } else {
                for (TreeNode prefixNode : middleNode.get(0)) {
                    prefix.append(prefixNode.buildLeftMostDerivationToRootNonterm(rootNonterm));
                }
            }
            return prefix.toString();
        }
        return nodeExpr;
    }

    protected List<String> getNontermsAndTermsSuffixOfRootNontermDerivationTree() {
        return nontermsAndTermsSuffixOfRootNontermDerivationTree;
    }

    protected Set<StringBuilder> getWords() {
        return this.words;
    }
}
