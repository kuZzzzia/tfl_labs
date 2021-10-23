package bmstu.iu9.tfl.lab2.grammarToRegexConversion;

import bmstu.iu9.tfl.lab2.Reader;
import bmstu.iu9.tfl.lab2.systemOfRegularExpressionEquations.Equation;
import bmstu.iu9.tfl.lab2.systemOfRegularExpressionEquations.Solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Converter {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar " +
                    "../gr_to_regex_conv/target/gr_to_regex_conv-1.0-SNAPSHOT.jar " +
                    "path/to/file/from/resources/folder/with/grammar");
            System.exit(-1);
        }
        try {
            Reader grammarReader = new Reader(args[0]);
            Rule[] rules = parseRules(grammarReader);
            FiniteAutomata finiteAutomata = new FiniteAutomata(rules);
            HashMap<Integer, Boolean> loopsEliminatedRules = finiteAutomata.findRegexMembers();
            Equation[] equations = finiteAutomata.generateRegexEquationsSystem(loopsEliminatedRules);
            ArrayList<String> ans = Solver.solveEquationsSystem(equations);
            printAns(ans);
        } catch (IOException | Error e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void printAns(ArrayList<String> ans) {
        for (String s: ans) {
            if (s.startsWith("S")) {
                System.out.println(s);
                break;
            }
        }
    }

    private static Rule[] parseRules(Reader grammarReader) {
        String[] equationsStringValue = grammarReader.getData();
        int amount = equationsStringValue.length;
        Rule[] parsedRules = new Rule[amount];
        for (int i = 0; i < amount; i++) {
            parsedRules[i] = new Rule(equationsStringValue[i]);
        }
        return parsedRules;
    }
}
