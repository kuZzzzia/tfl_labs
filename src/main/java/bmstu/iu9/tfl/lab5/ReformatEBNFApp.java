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

            MetaGrammar newSyntax = new MetaGrammar(args[1], false);

            TreeBuilder treeBuilder = new TreeBuilder(args[2], currentSyntax, newSyntax);

            //TODO: list of variants of the grammar in new syntax
            // going by each node substituting core nonterms
            // get new grammar
            // print unique strings

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
