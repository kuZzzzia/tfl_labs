package bmstu.iu9.tfl.lab5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeBuilder extends Reader {

    private static final int TERMINAL_RULE_LENGTH = 1;
    private static final int ITER_RULE_LENGTH = 2;

    private final Map<String, List<List<String>>> currentSyntax;
    private final Map<String, List<List<String>>> newSyntax;
    private final String grammar;

    public TreeBuilder(String path, MetaGrammar currentSyntax, MetaGrammar newSyntax) throws IOException {
        super(path);
        this.currentSyntax = currentSyntax.getRules();
        this.newSyntax = newSyntax.getRules();
        grammar = String.join("\n", getData());
        AlgoCYK();
    }

    private static String unwrapTerm(String s) {
        return s.substring(1, s.length() - 1);
    }

    private void AlgoCYK() {
        List<List<List<TreeNode>>> tableCYK = initializeTableCYK();

        for (int i = 1; i <= grammar.length(); i++) {
            String inputTerm = String.valueOf(grammar.charAt(i - 1));
            for (String nonterm : currentSyntax.keySet()) {
                if (currentSyntax.get(nonterm).size() == TERMINAL_RULE_LENGTH && currentSyntax.get(nonterm).get(0).size() == 1) {
                    String term = unwrapTerm(currentSyntax.get(nonterm).get(0).get(0));
                        if ( (term.length() > 1 && inputTerm.matches(term) ) || inputTerm.equals(term)) {
                            tableCYK.get(1).get(i).add(new TreeNode(nonterm, inputTerm));
                        }
                }
            }
        }
        for (int l = 2; l <= grammar.length(); l++) {
            for (int i = 1; i <= grammar.length() - l + 1; i++) {
                int j = i + l - 1;
                for (int k = i; k <= j - 1; k++) {
                    for (String nonterm : currentSyntax.keySet()) {
                        for (List<String> rewritingRule : currentSyntax.get(nonterm)) {
                            if (rewritingRule.size() == ITER_RULE_LENGTH) {
                                List<TreeNode> left = checkListContainsNonterm(tableCYK.get(i).get(k), rewritingRule.get(0));
                                List<TreeNode> right = checkListContainsNonterm(tableCYK.get(k+1).get(j), rewritingRule.get(1));
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
        for (TreeNode s : tableCYK.get(1).get(grammar.length())) {
            if (s.getNonterm().equals("[S]")) {
                System.out.println("YES");
            }
        }
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
        List<List<List<TreeNode>>> tableCYK = new ArrayList<>(grammar.length() + 1);
        for (int i = 0; i < grammar.length() + 1; i++) {
            tableCYK.add(new ArrayList<>(grammar.length() + 1));
        }
        for (List<List<TreeNode>> elem : tableCYK) {
            for (int i = 0; i < grammar.length() + 1; i++) {
                elem.add(new ArrayList<>());
            }
        }
        return tableCYK;
    }
}
