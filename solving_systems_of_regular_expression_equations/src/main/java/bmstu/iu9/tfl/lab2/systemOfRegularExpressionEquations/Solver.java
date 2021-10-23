package bmstu.iu9.tfl.lab2.systemOfRegularExpressionEquations;

import bmstu.iu9.tfl.lab2.Reader;

import java.io.IOException;
import java.util.ArrayList;

public class Solver {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar " +
                    "./solving_... " + //TODO: update
                    "path/to/file/from/resources/folder/with/system/of/equations");
            System.exit(-1);
        }
        try {
            Reader equationsSystemReader = new Reader(args[0]);
            Equation[] equations = parseEquations(equationsSystemReader);
            Equation.checkAllVariablesUsed();
            ArrayList<String> ans = solveEquationsSystem(equations);
            printAnswer(ans);
        } catch (IOException | Error e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static Equation[] parseEquations(Reader equationsSystemReader) {
        String[] equationsStringValue = equationsSystemReader.getData();
        int amount = equationsStringValue.length;
        Equation[] parsedEquations = new Equation[amount];
        for (int i = 0; i < amount; i++) {
            parsedEquations[i] = new Equation(equationsStringValue[i]);
        }
        return parsedEquations;
    }

    public static ArrayList<String> solveEquationsSystem(Equation[] equations) {
        int amount = equations.length;
        for (int i = 0; i < amount - 1; i++) {
            equations[i].reduceVariableFromRightSide();
            for (int j = i + 1; j < amount; j++) {
                equations[j].substituteVariableInEquation(equations[i]);
            }
        }
        for (int i = amount - 1; i > 0; i--) {
            equations[i].reduceVariableFromRightSide();
            for (int j = i - 1; j > -1; j--) {
                equations[j].substituteVariableInEquation(equations[i]);
            }
        }
        ArrayList<String> ans = new ArrayList<>();
        for (Equation equation : equations) {
            ans.add(equation.getVar() + " = " + equation.getAns());
        }
        return ans;
    }

    private static void printAnswer(ArrayList<String> ans) {
        System.out.println("Answer to system is:");
        for (String s: ans) {
            System.out.println(s);
        }
    }
}
