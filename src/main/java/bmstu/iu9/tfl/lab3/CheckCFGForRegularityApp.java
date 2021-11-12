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
            if (suspiciousNonterms.isEmpty()) {
                probablyRegularNontermsSetClosure(probablyRegularNonterms, regularNonterms, rules);

            } else {
                //TODO: graphviz
            }
        } catch (IOException | Error e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void probablyRegularNontermsSetClosure(Set<String> probablyRegularNonterms, Set<String> regularNonterms, Grammar rules) {
        Queue<String> newRegularNonterms = new PriorityQueue<>();
        function(probablyRegularNonterms, regularNonterms, rules, newRegularNonterms);
        while (!newRegularNonterms.isEmpty()) {
            regularNonterms.add(newRegularNonterms.poll());
            function(probablyRegularNonterms, regularNonterms, rules, newRegularNonterms);
        }
    }

    private static void function(Set<String> probablyRegularNonterms, Set<String> regularNonterms, Grammar rules, Queue<String> newRegularNonterms) { //TODO: rename
        for (String nonterm : probablyRegularNonterms) {
            if (checkRewritingRulesContainOnlyRegularNonterms(nonterm, regularNonterms, rules)) {
                newRegularNonterms.add(nonterm);
                probablyRegularNonterms.remove(nonterm);
            }
        }
    }

    private static boolean checkRewritingRulesContainOnlyRegularNonterms(String nonterm, Set<String> regularNonterms, Grammar rules) {
        Set<String> nontermFirstLevelDependency = rules.getNontermFirstLevelDependency(nonterm);
        for (String dependNonterm : nontermFirstLevelDependency) {
            if (!regularNonterms.contains(dependNonterm)) {
                return false;
            }
        }
        return true;
    }

}
