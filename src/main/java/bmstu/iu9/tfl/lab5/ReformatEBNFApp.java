package bmstu.iu9.tfl.lab5;

public class ReformatEBNFApp {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java -jar ./target/tfl_lab_4-1.0.jar path/to/current/syntax path/to/new/syntax path/to/grammar");
            System.exit(-1);
        }
        try {
            MetaGrammar currentSyntax = new MetaGrammar(args[0], true);
            currentSyntax.transformCurrentMeta(true);

            MetaGrammar currentSyntaxWithoutProtectionOfTerms = new MetaGrammar(args[0], false);
            currentSyntaxWithoutProtectionOfTerms.transformCurrentMeta(false);

//            System.out.println("\nCurrent:");
//            currentSyntax.printRules();

//            System.out.println("\nNew:");
            MetaGrammar newSyntax = new MetaGrammar(args[1], false);
//            newSyntax.printRules();

            TreeBuilder treeBuilder = new TreeBuilder(args[2], currentSyntax, newSyntax);

            //TODO: use ACT or CYK
            //TODO: parse trees -> new syntax
            // if there is a conflict in converting CNAME or NNAME then throw an error
            // else print grammar in new syntax (all variants)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
