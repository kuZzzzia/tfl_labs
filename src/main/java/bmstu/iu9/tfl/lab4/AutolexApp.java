package bmstu.iu9.tfl.lab4;

import java.util.ArrayList;
import java.util.List;

public class AutolexApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar ./target/tfl_lab_4-1.0.jar path/to/file/from/resources/directory");
            System.exit(-1);
        }
        try {
            Grammar rules = new Grammar(args[0]);
            System.out.println(rules);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> solveEquationsSystem(Equation[] equations) {
        int amount = equations.length;
        if (amount != 1) {
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
        } else {
            equations[0].reduceVariableFromRightSide();
        }
        List<String> ans = new ArrayList<>();
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
