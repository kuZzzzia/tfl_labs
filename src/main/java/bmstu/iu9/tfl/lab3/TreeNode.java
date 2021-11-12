package bmstu.iu9.tfl.lab3;

import java.util.*;

public class TreeNode {
    private static final int TERM_STARTING_INDEX = 0;


    private boolean              isNonterm;
    private String               nodeExpr;
    private List<List<TreeNode>> middleNode;
    private List<List<String>>   rightNode;
    private Set<StringBuilder>   words;


    public TreeNode(String nonterm, Grammar rules, String rootNonterm, Set<String> stackNontermsTrace) {
        isNonterm = true;
        nodeExpr = nonterm;
        middleNode = new ArrayList<>();
        rightNode = new ArrayList<>();
        words = new HashSet<>();
        List<String[]> rewritingVariants = rules.getNontermRewritingVariants(nonterm);
        for (String[] rewritingVariant : rewritingVariants) {
            List<TreeNode> rewritingVariantMiddleNode = new ArrayList<>();
            rewritingVariantMiddleNode.add(new TreeNode(rewritingVariant[TERM_STARTING_INDEX]));
            rightNode.add(new ArrayList<>(Arrays.asList(Arrays.copyOfRange(rewritingVariant, 1, rewritingVariant.length))));
        }
        List<Integer> noRootNontermFoundAfterDerivation = new ArrayList<>();
        for (int i = 0; i < rightNode.size(); i++) {
            List<String> rightNodeVariant = rightNode.get(i);
            if (rightNodeVariant.isEmpty()) {
                Set<StringBuilder> derivationToTermsString = buildShortestDerivationToTermsString(middleNode.get(i));
                if (derivationToTermsString != null) {
                    words.addAll(derivationToTermsString);
                }
                noRootNontermFoundAfterDerivation.add(0, i);
            } else {
                String expr = rightNodeVariant.get(0);
                if (expr.matches(RuleRightSide.NONTERM_REGEX)) {
                    Set<String> newStackNontermsTrace = new HashSet<>(stackNontermsTrace);
                    newStackNontermsTrace.add(expr);
                    middleNode.get(i).add(new TreeNode(expr, rules, rootNonterm, newStackNontermsTrace));
                } else {
                    middleNode.get(i).add(new TreeNode(expr));
                }
            }
        }
    }

    public TreeNode(String termsString) {
        isNonterm = false;
        nodeExpr = termsString;
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
}
