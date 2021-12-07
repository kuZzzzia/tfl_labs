package bmstu.iu9.tfl.lab4;

import java.io.IOException;
import java.util.*;

public class Grammar extends Reader {

    private enum GrammarType {
        LEFT_LINEAR,
        RIGHT_LINEAR,
    }

    private static final String RULE_REGEX = "\\[[A-Za-z]+\\]\\s*::=\\s*(([a-z0-9_*+=()$;:]|(\\[[A-Za-z]+\\]))\\s*)+";
    private static final String RULE_DECLARATION_ERROR = "Invalid rule declaration: ";
    private static final String WHITESPACE_REGEX = "\\s";
    private static final String EMPTY_STRING = "";
    private static final String RULE_SEPARATOR = "::=";
    private static final String NONTERM_REGEX = "\\[[A-Za-z]+\\]";
    private static final String TERM_REGEX = "[a-z0-9_*+=()$;:]";

    private static final int    STARTING_CHAR_INDEX = 0;
    private static final int    RULE_SEPARATOR_LENGTH = 3;

    private final Map<String, List<List<String>>> rules;
    private final Set<String> nontermsUsed;
    private final Map<GrammarType, Set<String>> regularNonterms;


    protected Grammar(String path) throws IOException {
        super(path);

        rules = new HashMap<>();
        nontermsUsed = new HashSet<>();
        regularNonterms = new HashMap<>();
        regularNonterms.put(GrammarType.LEFT_LINEAR, new HashSet<>());
        regularNonterms.put(GrammarType.RIGHT_LINEAR, new HashSet<>());

        parseRules(super.getData());
        buildRegularSubsets();

    }

    private void buildRegularSubsets() {
        Map<String, Set<String>> dependency = new HashMap<>();
        for (String nonterm: rules.keySet()) {
            if (!dependency.containsKey(nonterm)) {
                Set<String> stackTrace = new HashSet<>(Collections.singleton(nonterm));
                buildNontermDependency(nonterm, dependency, stackTrace);
            }
        }

    }

    private Set<String> findIrregularNonterms(GrammarType grammarType) {
        Set<String> irregular = new HashSet<>();
        for (String nonterm : rules.keySet()) {
            for (List<String> rewritingRule : rules.get(nonterm)) {
                if (isNotChainRule(rewritingRule) && isNotRegular(grammarType, rewritingRule)) {

                }
            }
        }
    }

    private void buildNontermDependency(String nonterm, Map<String, Set<String>> dependency, Set<String> stackTrace) {
        Set<String> nontermDependency = new HashSet<>();
        for (List<String> rewritingRule : rules.get(nonterm)) {
            for (String term : rewritingRule) {
                if (isNonterm(term)) {
                    if (stackTrace.contains(term)) {
                        nontermDependency.add(term);
                    } else {
                        if (!dependency.containsKey(term)) {
                            stackTrace.add(term);
                            buildNontermDependency(term, dependency, stackTrace);
                            stackTrace.remove(term);
                        }
                        nontermDependency.addAll(dependency.get(term));
                    }
                }
            }
        }
        dependency.put(nonterm, nontermDependency);
    }

    private void parseRules(String[] data) {
        for (String line: data) {
            if (!line.matches(RULE_REGEX)) {
                throw new Error(RULE_DECLARATION_ERROR + line);
            }
            line = line.replaceAll(WHITESPACE_REGEX, EMPTY_STRING);
            int indexOfRuleSeparator = line.indexOf(RULE_SEPARATOR);
            String nonterm = line.substring(STARTING_CHAR_INDEX, indexOfRuleSeparator);
            List<String> parsedRewritingRule = parseRewritingRule(line
                    .substring(indexOfRuleSeparator + RULE_SEPARATOR_LENGTH
                    )
            );

            if (rules.containsKey(nonterm)) {
                rules.get(nonterm).add(parsedRewritingRule);
            } else {
                rules.put(nonterm, new ArrayList<>(Collections.singleton(parsedRewritingRule)));
            }
        }

        for (String nonterm: nontermsUsed) {
            if (!rules.containsKey(nonterm)) {
                throw new Error("No declaration rule for nonterm " + nonterm + " found");
            }
        }
    }

    private List<String> parseRewritingRule(String rewritingRuleString) {
        List<String> parsedTerms = new ArrayList<>();
        StringBuilder rewritingRule = new StringBuilder(rewritingRuleString);
        while (rewritingRule.length() != 0) {
            if (rewritingRule.charAt(0) == '[') {
                int closingBracketIndex = rewritingRule.indexOf("]") + 1;
                String nonterm = rewritingRule.substring(0, closingBracketIndex);
                parsedTerms.add(nonterm);
                nontermsUsed.add(nonterm);
                rewritingRule.delete(0, closingBracketIndex);
            } else {
                parsedTerms.add(rewritingRuleString.substring(0, 1));
                rewritingRule.deleteCharAt(0);
            }
        }
        return parsedTerms;
    }

    private boolean isNonterm(String s) {
        return s.matches(NONTERM_REGEX);
    }

    private boolean isTerm(String s) {
        return s.matches(TERM_REGEX);
    }

    private boolean isNotRegular(GrammarType grammarType, List<String> rewritingRule) {
        switch (rewritingRule.size()) {
            case 1 :
                return !isTerm(rewritingRule.get(0));
            case 2 :
                return (grammarType.equals(GrammarType.RIGHT_LINEAR) && !(isTerm(rewritingRule.get(0)) && isNonterm(rewritingRule.get(1)))) ||
                        (grammarType.equals(GrammarType.LEFT_LINEAR) && !(isTerm(rewritingRule.get(1)) && isNonterm(rewritingRule.get(0))));
            default:
                return true;
        }
    }

    private boolean isNotChainRule(List<String> rewritingRule) {
        return rewritingRule.size() != 1 || !isNonterm(rewritingRule.get(0));
    }
}
