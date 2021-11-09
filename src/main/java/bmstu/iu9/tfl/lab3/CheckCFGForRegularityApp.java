package bmstu.iu9.tfl.lab3;

import java.io.IOException;

public class CheckCFGForRegularityApp {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar ./target/tfl_lab_3-1.0.jar path/to/file/from/resources/directory");
            System.exit(-1);
        }
        try {
            Grammar rules = new Grammar(args[0]);
        } catch (IOException | Error e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
