package bmstu.iu9.tfl.lab2.grammarToRegexConversion;

import bmstu.iu9.tfl.lab2.Parser;

public class GrammarParser extends Parser {
    public GrammarParser(String rule) {
        super(rule);
    }

    protected int parseLetter(int i, String regex) {
        String varString = getData().substring(i, i + 1);
        if (!varString.matches(regex)) {
            throw new Error(Rule.ERROR_MESSAGE + varString);
        }
        return i + 1;
    }
}
