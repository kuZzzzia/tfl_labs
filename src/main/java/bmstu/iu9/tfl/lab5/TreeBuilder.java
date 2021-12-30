package bmstu.iu9.tfl.lab5;

import java.io.IOException;
import java.util.*;

public class TreeBuilder extends Reader {
    private static final String INPUT_GRAMMAR_ERROR = "Input grammar can not be parsed with current syntax";

    private static final int TERMINAL_RULE_LENGTH = 1;

    private final Map<String, List<List<String>>> currentSyntax;
    private final String grammar;

    private final List<String> newGrammars;

    public TreeBuilder(String path, MetaGrammar currentSyntax, MetaGrammar newSyntax) throws IOException {
        super(path);
        this.currentSyntax = currentSyntax.getRules();

        grammar = String.join("\n", getData());
        List<TreeNode> parsingTrees = AlgoCYK();
        System.out.println("CYK parsing ended");
        if (parsingTrees.isEmpty()) {
            throw new Error(INPUT_GRAMMAR_ERROR);
        }
        newGrammars = new ArrayList<>();
        String newNNameRegex = newSyntax.getNNameRegex();
        String newCNameRegex = newSyntax.getCNameRegex();
        for (int i = 0; i < parsingTrees.size(); i++) {
            parsingTrees.get(i).foldTree(newNNameRegex, newCNameRegex);
            if (!parsingTrees.get(i).applyNewSyntax(newSyntax.getAlias())) {
                parsingTrees.remove(i--);
            } else {
                String newGrammar = parsingTrees.get(i).print();
                if (!newGrammars.contains(newGrammar)) {
                    newGrammars.add(newGrammar);
                }
            }
        }
    }

    private static String unwrapTerm(String s) {
        return s.substring(1, s.length() - 1);
    }

    private List<TreeNode> AlgoCYK() {
        List<List<List<TreeNode>>> tableCYK = initializeTableCYK();

        for (int i = 0; i < grammar.length(); i++) {
            String inputTerm = String.valueOf(grammar.charAt(i));
            for (String nonterm : currentSyntax.keySet()) {
                for (List<String> rewritingRule : currentSyntax.get(nonterm)) {
                    if (rewritingRule.size() == TERMINAL_RULE_LENGTH) {
                        String term = unwrapTerm(rewritingRule.get(0));
                        if ((term.length() > 1 && inputTerm.matches(term)) || inputTerm.equals(term)) {
                            tableCYK.get(i).get(0).add(new TreeNode(nonterm, inputTerm));
                        }
                    }
                }
            }
        }
        for (int j = 1; j < grammar.length(); j++) {
            for (int i = 0; i < grammar.length() - j; i++) {
                for (int k = 0; k < j; k++) {
                    for (String nonterm : currentSyntax.keySet()) {
                        for (List<String> rewritingRule : currentSyntax.get(nonterm)) {
                            if (rewritingRule.size() == 2) {
                                List<TreeNode> left = checkListContainsNonterm(tableCYK.get(i).get(k), rewritingRule.get(0));
                                List<TreeNode> right = checkListContainsNonterm(tableCYK.get(i + k + 1).get(j - k - 1), rewritingRule.get(1));
                                if (left.size() != 0 && right.size() != 0) {
                                    for (TreeNode leftNode : left) {
                                        for (TreeNode rightNode : right) {
                                            tableCYK.get(i).get(j).add(new TreeNode(nonterm, leftNode, rightNode));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        List<TreeNode> parsingTrees = new ArrayList<>();
        for (TreeNode s : tableCYK.get(0).get(grammar.length() - 1)) {
            if (s.getNonterm().equals(MetaGrammar.STARTING_NONTERM)) {
                parsingTrees.add(s);
            }
        }
        return parsingTrees;
    }

    private static List<TreeNode> checkListContainsNonterm(List<TreeNode> list, String nonterm) {
        List<TreeNode> ans = new ArrayList<>();
        for (TreeNode node : list) {
            if (node.getNonterm().equals(nonterm)) {
                ans.add(node);
            }
        }
        return ans;
    }

    private List<List<List<TreeNode>>> initializeTableCYK() {
        List<List<List<TreeNode>>> tableCYK = new ArrayList<>(grammar.length());
        for (int i = 0; i < grammar.length(); i++) {
            tableCYK.add(new ArrayList<>(grammar.length()));
        }
        for (List<List<TreeNode>> elem : tableCYK) {
            for (int i = 0; i < grammar.length(); i++) {
                elem.add(new ArrayList<>());
            }
        }
        return tableCYK;
    }

    public void printNewGrammars() {
        for (String g : newGrammars) {
            System.out.println(g);
        }
    }
}
