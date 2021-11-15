package bmstu.iu9.tfl.lab3;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            String dirPath = "./derives";
            createDirectoryForDerivations(dirPath);
            for (NontermLeftmostDerivationTree nontermLeftmostDerivationTree : leftmostDerivationsOfNontermsAchievableFromStartingNonterm.values()) {
                nontermLeftmostDerivationTree.categorizeNonterm(
                        leftmostDerivationsOfNontermsAchievableFromStartingNonterm,
                        rules.getRegularNontermsSubsets(),
                        regularNonterms,
                        probablyRegularNonterms,
                        suspiciousNonterms
                );
                if (nontermLeftmostDerivationTree.checkRecursionInDerivationFound() && rules.checkNontermIsNotRegular(nontermLeftmostDerivationTree.getNonterm())) {
                    renderNontermDerivationDigraph(nontermLeftmostDerivationTree, dirPath);
                }
            }

            if (suspiciousNonterms.isEmpty()) {
                probablyRegularNontermsSetClosure(probablyRegularNonterms, regularNonterms, rules);
                printNontermSets(probablyRegularNonterms, regularNonterms, suspiciousNonterms);
                if (regularNonterms.isEmpty()) {
                    System.out.println("Can not determine language regularity");
                } else if (probablyRegularNonterms.isEmpty()) {
                    System.out.println("Language is regular");
                } else {
                    System.out.println("Language is probably regular");
                }
            } else {
                printNontermSets(probablyRegularNonterms, regularNonterms, suspiciousNonterms);
                System.out.println("Language is not regular");
            }
        } catch (Error | Exception e) {
            System.err.println(e);
            System.exit(-1);
        }
    }

    private static void createDirectoryForDerivations(String path) throws Exception {
        File dir = new File(path);
        if (dir.exists()) {
            if (!deleteDirectory(dir)) {
                throw new Exception("Error with deleting the directory");
            }
        }
        if (!dir.mkdirs()) {
            throw new Exception("Directory was not created");
        }
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    private static void renderNontermDerivationDigraph(NontermLeftmostDerivationTree nontermLeftmostDerivationTree, String dirPath) throws IOException {
        String graph = nontermLeftmostDerivationTree.getGraphvizRepresentation();
        System.out.println(graph);
        String fileName = dirPath + "/" + nontermLeftmostDerivationTree.getNonterm();
        Path dotFile = Paths.get(fileName + ".dot");
        Files.write(dotFile, Collections.singleton(graph), StandardCharsets.UTF_8);
        String command = "dot -Tsvg " + fileName + ".dot" + " -o" + fileName + ".svg";
        Runtime rt = Runtime.getRuntime();
        rt.exec(command);
    }

    private static void probablyRegularNontermsSetClosure(Set<String> probablyRegularNonterms, Set<String> regularNonterms, Grammar rules) {
        Queue<String> newRegularNonterms = new PriorityQueue<>();
        closeSet(probablyRegularNonterms, regularNonterms, rules, newRegularNonterms);
        while (!newRegularNonterms.isEmpty()) {
            regularNonterms.add(newRegularNonterms.poll());
            closeSet(probablyRegularNonterms, regularNonterms, rules, newRegularNonterms);
        }
    }

    private static void closeSet(Set<String> probablyRegularNonterms, Set<String> regularNonterms, Grammar rules, Queue<String> newRegularNonterms) {
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

    private static void printNontermSets(Set<String> probablyRegularNonterms, Set<String> regularNonterms, Set<String> suspiciousNonterms) {
        System.out.print("Regular nonterms: ");
        System.out.println(regularNonterms);
        System.out.print("Probably regular nonterms: ");
        System.out.println(probablyRegularNonterms);
        System.out.print("Suspicious nonterms: ");
        System.out.println(suspiciousNonterms);
    }

}
