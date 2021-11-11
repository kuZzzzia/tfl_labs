package bmstu.iu9.tfl.lab3;

import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckCFGForRegularityApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar ./target/tfl_lab_3-1.0.jar path/to/file/from/resources/directory");
            System.exit(-1);
        }
        try {
            Grammar rules = new Grammar(args[0]);
            List<TreeNode> leftmostDerivationsOfNontermsAchievableFromStartingNonterm = new ArrayList<>();
            for (String nonterm : rules.getNontermsAchievableFromStartingNonterm()) {
                leftmostDerivationsOfNontermsAchievableFromStartingNonterm.add(new TreeNode(nonterm, rules));
            }
        } catch (IOException | Error e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
