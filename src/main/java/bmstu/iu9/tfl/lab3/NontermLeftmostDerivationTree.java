package bmstu.iu9.tfl.lab3;

import java.util.*;

public class NontermLeftmostDerivationTree {
    private final TreeNode node;

    public NontermLeftmostDerivationTree(String nonterm, Grammar rules) {
        node = new TreeNode(nonterm, rules, nonterm, false, new HashSet<>());
    }

    protected void categorizeNonterm(
            Map<String, NontermLeftmostDerivationTree> leftmostDerivations,
            Map<String, Set<String>> regularNontermsSubsets,
            Set<String> regularNonterms,
            Set<String> probablyRegularNonterms,
            Set<String> suspiciousNonterms) {
        if (!node.checkRootNontermFound()) {
            if (node.checkNodeIsFinite() && checkRegularSubsetContainsSuffix(new ArrayList<>(Collections.singleton(node.getNodeExpr())), regularNontermsSubsets) != null) {
                regularNonterms.add(node.getNodeExpr());
            } else {
                probablyRegularNonterms.add(node.getNodeExpr());
            }
            return;
        }
        node.buildLeftMostDerivationToRootNonterm(0);
        String prefix = node.getPrefixOfRootNontermDerivationTree();
        List<String> suffix = node.getSuffixOfRootNontermDerivationTree();
        if (suffix.isEmpty()) {
            if (node.checkNodeIsFinite()) {
                regularNonterms.add(node.getNodeExpr());
            } else {
                probablyRegularNonterms.add(node.getNodeExpr());
            }
            return;
        }
        String regularSubsetKey = checkRegularSubsetContainsSuffix(suffix, regularNontermsSubsets);
        if ( regularSubsetKey != null
                && checkWordBelongsToSuffixIterLanguage(leftmostDerivations, new StringBuilder(prefix), suffix)) {
            Set<StringBuilder> words = node.getShortestWords();
            if (words.isEmpty()) {
                suspiciousNonterms.add(node.getNodeExpr());
                return;
            } else {
                for (StringBuilder word : words) {
                    if (!checkWordBelongsToSuffixIterLanguage(leftmostDerivations,
                            new StringBuilder(word.toString()), suffix)
                    ) {
                        suspiciousNonterms.add(node.getNodeExpr());
                        return;
                    }
                }
            }
            if (checkRegularSubsetContainsRootNonterm(regularNontermsSubsets.get(regularSubsetKey))) {
                regularNonterms.add(node.getNodeExpr());
            } else {
                probablyRegularNonterms.add(node.getNodeExpr());
            }
            return;
        }
        suspiciousNonterms.add(node.getNodeExpr());
    }


    private String checkRegularSubsetContainsSuffix(List<String> suffix,
                                                    Map<String, Set<String>> regularNontermsSubsets) {
        for (String key : regularNontermsSubsets.keySet()) {
            Set<String> intersection = new HashSet<>(regularNontermsSubsets.get(key));
            intersection.retainAll(suffix);
            if (intersection.size() == suffix.size()) {
                return key;
            }
        }
        return null;
    }

    private boolean checkWordBelongsToSuffixIterLanguage(Map<String, NontermLeftmostDerivationTree> leftmostDerivations,
                                                         StringBuilder word, List<String> suffix) {
        boolean result;
        while(word.length() != 0) {
            result = checkWordPrefixCanBeRecognisedByRule(leftmostDerivations, word, suffix, 0);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean checkWordPrefixCanBeRecognisedByRule(Map<String, NontermLeftmostDerivationTree> leftmostDerivations,
                                                         StringBuilder word, List<String> suffix, int i) {
        String pattern = suffix.get(i);
        if (pattern.matches(RuleRightSide.NONTERM_REGEX)) {
            if (word.length() == 0) {
                return false;
            }
            for (StringBuilder prefix : leftmostDerivations.get(pattern).node.getShortestWords()) {
                StringBuilder wordCopy = new StringBuilder(word.toString());
                if (checkWordStartsWithPattern(wordCopy, prefix.toString()) && (i + 1 == suffix.size()
                        || checkWordPrefixCanBeRecognisedByRule(leftmostDerivations, wordCopy, suffix, i + 1))) {
                    word.replace(0, word.length(), wordCopy.toString());
                    return true;
                }
            }
            return false;
        }
        return checkWordStartsWithPattern(word, pattern) && (i + 1 == suffix.size()
                || checkWordPrefixCanBeRecognisedByRule(leftmostDerivations, word, suffix, i + 1));
    }

    private boolean checkWordStartsWithPattern(StringBuilder word, String pattern) {
        if (!word.toString().startsWith(pattern)) {
            return false;
        }
        word.delete(0, pattern.length());
        return true;
    }

    protected String getGraphvizRepresentation() {
        return "digraph {\n"
                + node.getGraphvizRepresentation()
                + "}";
    }

    private boolean checkRegularSubsetContainsRootNonterm(Set<String> regularSubset) {
        return regularSubset.contains(node.getNodeExpr());
    }

    protected boolean checkRecursionInDerivationFound() {
        return node.getRootNontermFound();
    }

    protected String getNonterm() {
        return node.getNodeExpr();
    }
}
