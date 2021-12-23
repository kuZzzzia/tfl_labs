package bmstu.iu9.tfl.lab5;

public class ReformatEBNFApp {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar ./target/tfl_lab_4-1.0.jar path/to/current/syntax path/to/new/syntax path/to/grammar");
            System.exit(-1);
        }
        try {
            Grammar current_syntax = new Grammar(args[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
