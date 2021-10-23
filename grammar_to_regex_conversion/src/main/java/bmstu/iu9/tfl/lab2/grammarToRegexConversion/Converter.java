package bmstu.iu9.tfl.lab2.grammarToRegexConversion;

import bmstu.iu9.tfl.lab2.Reader;

import java.io.IOException;


public class Converter {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar " +
                    "./grammar_... " + //TODO: update
                    "path/to/file/from/resources/folder/with/grammar");
            System.exit(-1);
        }
        try {
            Reader grammarReader = new Reader(args[0]);
            Rule[] rules = parseRules(grammarReader);
        } catch (IOException | Error e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        /*
        пройтись по всем правым и левым частям и пос
        Построить автомат
        Построить по автомату систему уравнений регулярных выражений

        Вывести регулярку на экран
        ASSOCIATIVITY = FALSE
         */
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
