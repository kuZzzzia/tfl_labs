package bmstu.iu9.tfl.lab3;


import java.io.IOException;
import java.util.*;

public class Grammar extends Reader {
    private static final String RULE_REGEX = "^[A-Z][0-9]*\\s*->\\s*[a-z](\\s*([A-Z][0-9]*)|[a-z])*$";
    private static final String WHITESPACE_REGEX = "\\s";
    private static final String EMPTY_STRING = "";
    private static final String RULE_SEPARATOR = "->";
    private static final String NONTERM_REGEX = "[A-Z]";
    private static final String TERM_REGEX = "[a-z]";
    private static final String DIGIT_REGEX = "[0-9]";
    private static final String STARTING_NONTERM_REGEX = "S";
    private static final int RULE_SEPARATOR_LENGTH = 2;
    private static final int ENDING_INDEX_OF_PARSED_TERM_OR_NONTERM = 1;
    private static final int STARTING_CHAR_INDEX = 0;


    private final Map<String, RuleRightSide> rules;
    private final Set<String> nontermsUsed;

    protected Grammar(String path) throws IOException {
        super(path);
        nontermsUsed = new HashSet<>();
        rules = new HashMap<>();
        parseRules(super.getData());
    }

    private void parseRules(String[] data) {
        for (String line : data) {
            if (!line.matches(RULE_REGEX)) {
                throw new Error("Invalid rule declaration: " + line);
            }
            line = line.replaceAll(WHITESPACE_REGEX, EMPTY_STRING);
            int indexOfRuleSeparator = line.indexOf(RULE_SEPARATOR);
            String ruleLeftSide = line.substring(STARTING_CHAR_INDEX, indexOfRuleSeparator);
            String ruleRightSideString = line.substring(indexOfRuleSeparator + RULE_SEPARATOR_LENGTH);

            String[] newRightSide = splitRuleRightSideIntoArrayOfNontermsAndTerms(ruleRightSideString)
                    .toArray(new String[0]);

            RuleRightSide rightSide = rules.get(ruleLeftSide);

            if (rightSide != null) {
                rightSide.add(newRightSide);
            } else {
                rules.put(ruleLeftSide, new RuleRightSide(newRightSide));
            }

        }

        if (nontermsUsed.contains(STARTING_NONTERM_REGEX)) {
            throw new Error("Starting terminal S appeared on the right side of a rule");
        } else if (!rules.containsKey(STARTING_NONTERM_REGEX)) {
            throw new Error("No rule for starting terminal S found");
        } else if (nontermsUsed.size() != rules.size() - 1) {
            throw new Error("Incorrect amount of rules");
        }
    }

    private List<String> splitRuleRightSideIntoArrayOfNontermsAndTerms(final String ruleRightSideString) {
        List<String> ruleRightSideTermsAndNonterms = new ArrayList<>();
        StringBuilder ruleRightSide = new StringBuilder(ruleRightSideString);

        while (ruleRightSide.length() != 0) {
            parseTermOrNonterm(
                    ruleRightSide,
                    ruleRightSideTermsAndNonterms
            );
        }

        return ruleRightSideTermsAndNonterms;
    }

    private void parseTermOrNonterm(StringBuilder rule, List<String> ruleRightSideTermsAndNonterms) {
        if (getCharAsString(rule, STARTING_CHAR_INDEX).matches(NONTERM_REGEX)) {
            String nonterm = parse(rule, DIGIT_REGEX);
            ruleRightSideTermsAndNonterms.add(nonterm);
            nontermsUsed.add(nonterm);
            return;
        }
        ruleRightSideTermsAndNonterms.add(parse(rule, TERM_REGEX));
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

    protected Map<String, RuleRightSide> getRules() {
        return rules;
    }
}
