package bmstu.iu9.tfl.lab2.grammarToRegexConversion;

import bmstu.iu9.tfl.lab2.Parser;

import java.util.HashMap;

public class Rule {
    private char startingNTerm;
    private char letter;
    private char endingNTerm = '0';

    private static final HashMap<Character, Boolean> nTermsUsed = new HashMap<>();

    protected static final String ERROR_MESSAGE = "Invalid rule declaration: ";
    private static final String RULE_DELIMITER = "->";

    public Rule(String rule) {
        GrammarParser parser = new GrammarParser(rule);
        int i = 0;
        i = parser.parseCapitalLetter(i, ERROR_MESSAGE);
        setStartingNTerm(rule.charAt(0));
        addTermToUsed(getStartingNTerm());
        i = parser.presenceString(RULE_DELIMITER, i, ERROR_MESSAGE);
        i = parser.parseLetter(i, Parser.LOWERCASE_LETTERS_REGEX);
        setLetter(rule.charAt(i - 1));
        if (i != rule.length()) {
            i = parser.parseLetter(i, Parser.CAPITAL_LETTERS_REGEX);
            setEndingNTerm(rule.charAt(i - 1));
            addTermToUsed(getEndingNTerm());
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

    private void addTermToUsed(char term) {
        Rule.nTermsUsed.put(term, true);
    }

    protected char getStartingNTerm() {
        return this.startingNTerm;
    }

    protected static int getAmountOfUsedTerms() {
        return Rule.nTermsUsed.size();
    }

    protected char getLetter() {
        return this.letter;
    }

    protected char getEndingNTerm() {
        return this.endingNTerm;
    }
}
