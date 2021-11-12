package bmstu.iu9.tfl.lab3;

import java.util.HashSet;
import java.util.List;

public class NontermLeftmostDerivationTree {
    private TreeNode node;

    public NontermLeftmostDerivationTree(String nonterm, Grammar rules) {
        node = new TreeNode(nonterm, rules, nonterm, new HashSet<>());
    }
}
