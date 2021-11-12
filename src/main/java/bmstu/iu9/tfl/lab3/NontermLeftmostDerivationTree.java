package bmstu.iu9.tfl.lab3;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NontermLeftmostDerivationTree {

    private final TreeNode node;

    public NontermLeftmostDerivationTree(String nonterm, Grammar rules) {
        node = new TreeNode(nonterm, rules, nonterm, false, new HashSet<>());
    }

    protected void function(Map<String, NontermLeftmostDerivationTree> leftmostDerivations,
                            Map<String, Set<String>> regularNontermsSubsets,
                            Set<String> regularNonterms,
                            Set<String> probablyRegularNonterms,
                            Set<String> suspiciousNonterms) {
        if (!node.checkRootNontermFound()) {
            probablyRegularNonterms.add(node.getNodeExpr());
            return;
        }
        String prefix = node.buildLeftMostDerivationToRootNonterm(node.getNodeExpr());
        List<String> suffix = node.getNontermsAndTermsSuffixOfRootNontermDerivationTree();
        if (suffix.isEmpty()) {
            probablyRegularNonterms.add(node.getNodeExpr());
            return;
        }
        String regularSubsetKey = checkRegularSubsetContainsSuffix(suffix, regularNontermsSubsets);
        if ( regularSubsetKey != null && checkWordBelongsToSuffixIterLanguage(leftmostDerivations, new StringBuilder(prefix), suffix)) {
            Set<StringBuilder> words = node.getWords();
            if (words.isEmpty()) {
                suspiciousNonterms.add(node.getNodeExpr());
                return;
            } else {
                for (StringBuilder word : words) {
                    if (!checkWordBelongsToSuffixIterLanguage(leftmostDerivations, new StringBuilder(word.toString()), suffix)) {
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
        //recursiveClosureOfAllNonterms that were created by NontermLeftmostDerivationTree
        // if (all nonterms are regular) then it is regular
        // if (all terms are regular and probably_regular) then it is probably_regular
        // if (all terms are probably_regular) then it is probably_regular
        // if it has all non-terms that are probably_regular then language regularity can not be determined
    }


    private String checkRegularSubsetContainsSuffix(List<String> suffix, Map<String, Set<String>> regularNontermsSubsets) {
        for (String key : regularNontermsSubsets.keySet()) {
            Set<String> intersection = new HashSet<>(regularNontermsSubsets.get(key));
            intersection.retainAll(suffix);
            if (intersection.size() == suffix.size()) {
                return key;
            }
        }
        return null;
    }

    private boolean checkWordBelongsToSuffixIterLanguage(Map<String, NontermLeftmostDerivationTree> leftmostDerivations, StringBuilder word, List<String> suffix) {
        boolean result;
        while(word.length() != 0) {
            result = checkWordPrefixCanBeRecognisedByRule(leftmostDerivations, word, suffix, 0);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean checkWordPrefixCanBeRecognisedByRule(Map<String, NontermLeftmostDerivationTree> leftmostDerivations, StringBuilder word, List<String> suffix, int i) {
        if (suffix.size() == i) {
            return true;
        }

        String pattern = suffix.get(i);
        if (pattern.matches(RuleRightSide.NONTERM_REGEX)) {
            if (word.length() == 0) {
                return false;
            }
            for (StringBuilder prefix : leftmostDerivations.get(pattern).node.getWords()) {
                StringBuilder wordCopy = new StringBuilder(word.toString());
                if (checkWordPrefixCanBeRecognisedByRule(leftmostDerivations, wordCopy, suffix, i + 1)) {
                    return true;
                }
            }
            return false;
        }
        return checkWordStartsWithPattern(word, pattern) && checkWordPrefixCanBeRecognisedByRule(leftmostDerivations, word, suffix, i + 1);
    }

    private boolean checkWordStartsWithPattern(StringBuilder word, String pattern) {
        if (!word.toString().startsWith(pattern)) {
            return false;
        }
        word.delete(0, pattern.length());
        return true;
    }

    private boolean checkRegularSubsetContainsRootNonterm(Set<String> regularSubset) {
        return regularSubset.contains(node.getNodeExpr());
    }

}
