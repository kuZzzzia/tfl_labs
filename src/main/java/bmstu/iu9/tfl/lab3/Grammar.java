package bmstu.iu9.tfl.lab3;


import java.io.IOException;
import java.util.*;

public class Grammar extends Reader {
    private static final String RULE_REGEX = "^[A-Z][0-9]*\\s*->\\s*[a-z](\\s*([A-Z][0-9]*)|[a-z])*$";
    private static final String WHITESPACE_REGEX = "\\s";
    private static final String EMPTY_STRING = "";
    private static final String RULE_SEPARATOR = "->";
    private static final String NONTERM_REGEX = "[A-Z]";
    private static final String DIGIT_REGEX = "[0-9]";
    private static final String STARTING_NONTERM_REGEX = "S";
    private static final int RULE_SEPARATOR_LENGTH = 2;
    private static final int ENDING_INDEX_OF_PARSED_TERM_OR_NONTERM = 1;
    private static final int STARTING_CHAR_INDEX = 0;


    private final Map<String, List<String[]>> rules;
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

            List<String> ruleRightSide = splitRuleRightSideIntoArrayOfNontermsAndTerms(ruleRightSideString);

            String[] ruleRightSideArray = ruleRightSide.toArray(new String[0]);
            List<String[]> rightSides = rules.get(ruleLeftSide);

            if (rightSides != null) {
                rightSides.add(ruleRightSideArray);
            } else {
                rightSides = new ArrayList<>(Collections.singletonList(ruleRightSideArray));
                rules.put(ruleLeftSide, rightSides);
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

    private List<String> splitRuleRightSideIntoArrayOfNontermsAndTerms(String ruleRightSideString) {
        List<String> ruleRightSideTermsAndNonterms = new ArrayList<>();
        while (!ruleRightSideString.isEmpty()) {
            ruleRightSideString = ruleRightSideString
                    .substring(
                            parseTermOrNonterm(
                                    ruleRightSideString,
                                    ruleRightSideTermsAndNonterms
                            )
                    );
        }

        return ruleRightSideTermsAndNonterms;
    }

    private int parseTermOrNonterm(final String rule, List<String> ruleRightSideTermsAndNonterms) {
        if (getCharAsString(rule, STARTING_CHAR_INDEX).matches(NONTERM_REGEX)) {
            int endIndexOfNonterm = ENDING_INDEX_OF_PARSED_TERM_OR_NONTERM;
            if (rule.length() != ENDING_INDEX_OF_PARSED_TERM_OR_NONTERM) {
                while (getCharAsString(rule, endIndexOfNonterm).matches(DIGIT_REGEX)){
                    endIndexOfNonterm++;
                }
            }
            String nonterm = rule.substring(STARTING_CHAR_INDEX, endIndexOfNonterm);
            ruleRightSideTermsAndNonterms.add(nonterm);
            nontermsUsed.add(nonterm);
            return endIndexOfNonterm;
        }
        ruleRightSideTermsAndNonterms.add(getCharAsString(rule, STARTING_CHAR_INDEX));
        return ENDING_INDEX_OF_PARSED_TERM_OR_NONTERM;
    }

    private static String getCharAsString(String s, int index) {
        return String.valueOf(s.charAt(index));
    }
}
