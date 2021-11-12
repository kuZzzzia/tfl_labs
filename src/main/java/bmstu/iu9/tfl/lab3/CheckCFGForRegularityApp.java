package bmstu.iu9.tfl.lab3;

import java.io.IOException;
import java.util.*;

public class CheckCFGForRegularityApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar ./target/tfl_lab_3-1.0.jar path/to/file/from/resources/directory");
            System.exit(-1);
        }
        try {
            Grammar rules = new Grammar(args[0]);
            Map<String, NontermLeftmostDerivationTree> leftmostDerivationsOfNontermsAchievableFromStartingNonterm = new HashMap<>();
            for (String nonterm : rules.getNontermsAchievableFromStartingNonterm()) {
                leftmostDerivationsOfNontermsAchievableFromStartingNonterm.put(nonterm, new NontermLeftmostDerivationTree(nonterm, rules));
            }
            Set<String> regularNonterms = new HashSet<>();
            Set<String> probablyRegularNonterms = new HashSet<>();
            Set<String> suspiciousNonterms = new HashSet<>();
            for (NontermLeftmostDerivationTree nontermLeftmostDerivationTree : leftmostDerivationsOfNontermsAchievableFromStartingNonterm.values()) {
                nontermLeftmostDerivationTree.function(
                        leftmostDerivationsOfNontermsAchievableFromStartingNonterm,
                        rules.getRegularNontermsSubsets(),
                        regularNonterms,
                        probablyRegularNonterms,
                        suspiciousNonterms
                );
            }
        } catch (IOException | Error e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
