package bmstu.iu9.tfl.lab5;

public class ReformatEBNFApp {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar ./target/tfl_lab_4-1.0.jar path/to/current/syntax path/to/new/syntax path/to/grammar");
            System.exit(-1);
        }
        try {
            System.out.println("Current:");
            MetaGrammar currentSyntax = new MetaGrammar(args[0]);

            System.out.println("New:");
            MetaGrammar newSyntax = new MetaGrammar(args[1]);

            currentSyntax.convertGrammarToCNF();

            System.out.println();
            System.out.println("Current:");
            currentSyntax.printRules();

            //TODO: eliminate epsilon rules -> remove chain rules -> guard terminals
            //         apply only to current_syntax
            //TODO: check if delimiters are tokens (lab4) ONLY WARNING!!!
            //TODO: use ACT or CYK
            //TODO: parse trees -> new syntax
            // if there is a conflict in converting CNAME or NNAME then throw an error
            // else print grammar in new syntax (all variants)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
