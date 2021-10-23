package bmstu.iu9.tfl.lab2.grammarToRegexConversion;

public class Rule {
    private char startingNTerm;
    private char letter;
    private char endingNTerm;

    protected static final String ERROR_MESSAGE = "Invalid rule declaration: ";
    private static final String RULE_DELIMITER = "->";

    public Rule(String rule) {
        GrammarParser parser = new GrammarParser(rule);
        int i = 0;
        i = parser.parseCapitalLetter(i, ERROR_MESSAGE);
        setStartingNTerm(rule.charAt(0));
        i = parser.presenceString(RULE_DELIMITER, i, ERROR_MESSAGE);
        i = parser.parseLetter(i);
        setLetter(rule.charAt(i - 1));
        if (i != rule.length()) {
            i = parser.parseCapitalLetter(i, ERROR_MESSAGE);
            setEndingNTerm(rule.charAt(i - 1));
            if (i != rule.length()) {
                throw new Error(ERROR_MESSAGE + rule);
            }
        }
    }

    private void setStartingNTerm(char startingNTerm) {
        this.startingNTerm = startingNTerm;
    }

    private void setLetter(char letter) {
        this.letter = letter;
    }

    private void setEndingNTerm(char endingNTerm) {
        this.endingNTerm = endingNTerm;
    }

    protected char getStartingNTerm() {
        return this.startingNTerm;
    }

    protected char getLetter() {
        return this.letter;
    }

    protected char getEndingNTerm() {
        return this.endingNTerm;
    }
}
