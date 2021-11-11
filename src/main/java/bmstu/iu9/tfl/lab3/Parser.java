package bmstu.iu9.tfl.lab3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser {
    private static final String NONTERM_CHAR_REGEX = "[A-Z]";
    private static final String TERM_CHAR_REGEX = "[a-z]";
    private static final String DIGIT_REGEX = "[0-9]";
    private static final int    ENDING_INDEX_OF_PARSED_TERM_OR_NONTERM = 1;
    private static final int    STARTING_CHAR_INDEX = 0;

    private final String[] parsedRuleRightSide;

    public Parser(String ruleRightSide, Set<String> nontermsUsed) {
        parsedRuleRightSide = splitRuleRightSideIntoArrayOfNontermsAndTerms(ruleRightSide, nontermsUsed)
                .toArray(new String[0]);
    }

    private List<String> splitRuleRightSideIntoArrayOfNontermsAndTerms(final String ruleRightSideString, Set<String> nontermsUsed) {
        List<String> ruleRightSideTermsAndNonterms = new ArrayList<>();
        StringBuilder ruleRightSide = new StringBuilder(ruleRightSideString);

        while (ruleRightSide.length() != 0) {
            parseTermOrNonterm(
                    ruleRightSide,
                    ruleRightSideTermsAndNonterms,
                    nontermsUsed
            );
        }

        return ruleRightSideTermsAndNonterms;
    }

    private void parseTermOrNonterm(StringBuilder rule, List<String> ruleRightSideTermsAndNonterms, Set<String> nontermsUsed) {
        if (getCharAsString(rule, STARTING_CHAR_INDEX).matches(NONTERM_CHAR_REGEX)) {
            String nonterm = parse(rule, DIGIT_REGEX);
            ruleRightSideTermsAndNonterms.add(nonterm);
            nontermsUsed.add(nonterm);
            return;
        }
        ruleRightSideTermsAndNonterms.add(parse(rule, TERM_CHAR_REGEX));
    }

    private String parse(final StringBuilder rule, final String regex) {
        int endIndexOfUnit = ENDING_INDEX_OF_PARSED_TERM_OR_NONTERM;
        while (endIndexOfUnit < rule.length()
                && getCharAsString(rule, endIndexOfUnit).matches(regex)) {
            endIndexOfUnit++;
        }
        String unit = rule.substring(STARTING_CHAR_INDEX, endIndexOfUnit);
        rule.delete(STARTING_CHAR_INDEX, endIndexOfUnit);

        return unit;
    }

    private static String getCharAsString(final StringBuilder s, final int index) {
        return String.valueOf(s.charAt(index));
    }

    protected String[] getParsedRuleRightSide() {
        return this.parsedRuleRightSide;
    }
}
