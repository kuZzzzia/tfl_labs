package bmstu.iu9.tfl.lab4;

import bmstu.iu9.tfl.lab2.Solver;

import java.util.ArrayList;

public class AutolexApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar ./target/tfl_lab_4-1.0.jar path/to/file/from/resources/directory");
            System.exit(-1);
        }
        try {
            Grammar rules = new Grammar(args[0]);
            Tokenizer tokenizer = new Tokenizer(rules);
            Solver solver = new Solver(rules, tokenizer);
            solver.printResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        if (args.length == 0)
//            System.err.println("Usage: java -jar ./target/tfl_lab_4-1.0.jar path/to/file/from/resources/directory");
//        } else {
//            System.out.println("error");
//        }
//    }

    private static void printAnswer(ArrayList<String> ans) {
        System.out.println("Answer to system is:");
        for (String s: ans) {
            System.out.println(s);
        }
    }

}
