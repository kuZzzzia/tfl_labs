package bmstu.iu9.tfl.lab4;

import java.util.*;

public class Tokenizer {
    private static final int FIRST_SET_INDEX = 0;
    private static final String STARTING_NONTERM = "[S]";
    private static final String FOLLOW_SYMBOL = "$";
    private static final String PRECEDE_SYMBOL = "^";

    private final Grammar grammar;
    private final Set<String> tokens;

    protected Tokenizer(Grammar grammar) {
        this.grammar = grammar;
        tokens = new HashSet<>();
        findTokens();
        System.out.println("Tokens: " + tokens);
    }

    private void findTokens() {
        Map<String, Set<String>> followSet = computeFollowOrPrecedeSet(true);
        Map<String, Set<String>> precedeSet = computeFollowOrPrecedeSet(false);
        System.out.println("Follow:");
        for (String nonterm : followSet.keySet()) {
            System.out.println(nonterm + " " + followSet.get(nonterm));
        }
        System.out.println("Precede:");
        for (String nonterm : precedeSet.keySet()) {
            System.out.println(nonterm + " " + precedeSet.get(nonterm));
        }

        Set<String> regularNonterms = new HashSet<>(grammar.getLeftLinearRegularNonterms());
        regularNonterms.addAll(grammar.getRightLinearRegularNonterms());
        regularNonterms.addAll(grammar.getRegularClosureNonterms());

        for (String nonterm : regularNonterms) {
            Set<String> terms = new HashSet<>(followSet.getOrDefault(nonterm, new HashSet<>()));
            terms.addAll(precedeSet.getOrDefault(nonterm, new HashSet<>()));
            Set<String> nontermLanguage = findNontermLanguage(nonterm, new HashSet<>());
            nontermLanguage.retainAll(terms);
            if (nontermLanguage.size() == 0) {
                tokens.add(nonterm);
            } else {
                System.out.println("Conflict of nonterm " + nonterm + " found");
            }
        }
    }

    private Set<String> findNontermLanguage(String nonterm, Set<String> stackTraceVisited) {
        Set<String> lang = new HashSet<>();
        for (List<String> rewritingRule : grammar.getRules().get(nonterm)) {
            if (rewritingRule.size() == 1) {
                String term = rewritingRule.get(0);
                if (Grammar.isTerm(term)) {
                    lang.add(term);
                } else if (!stackTraceVisited.contains(term)) {
                    stackTraceVisited.add(nonterm);
                    lang.addAll(findNontermLanguage(term, stackTraceVisited));
                }
            }
        }
        return lang;
    }


    private static String getFirstOrLastSetGenerator(final List<String> rewritingRule, boolean first) {
        return first
                ? rewritingRule.get(FIRST_SET_INDEX)
                : rewritingRule.get(rewritingRule.size() - 1);
    }


    private Map<String, Set<String>> computeFirstOrLastSet(boolean first) {
        Map<String, List<List<String>>> rules = grammar.getRules();
        Map<String, Set<String>> resultSet = new HashMap<>();

        for (String nonterm : rules.keySet()) {
            for (List<String> rewritingRule : rules.get(nonterm)) {
                String resultSetGenerator = getFirstOrLastSetGenerator(rewritingRule, first);
                if (Grammar.isTerm(resultSetGenerator)) {
                    if (resultSet.containsKey(nonterm)) {
                        resultSet.get(nonterm).add(resultSetGenerator);
                    } else {
                        resultSet.put(nonterm, new HashSet<>(Collections.singleton(resultSetGenerator)));
                    }
                }
            }
        }

        while (true) {
            boolean updated = false;
            for (String nonterm : rules.keySet()) {
                for (List<String> rewritingRule : rules.get(nonterm)) {
                    String resultSetGenerator = getFirstOrLastSetGenerator(rewritingRule, first);
                    if (Grammar.isNonterm(resultSetGenerator)) {
                        Set<String> newSet = new HashSet<>(resultSet.getOrDefault(nonterm, new HashSet<>()));
                        newSet.addAll(resultSet.getOrDefault(resultSetGenerator, new HashSet<>()));
                        if (newSet.size() > resultSet.getOrDefault(nonterm, new HashSet<>()).size()) {
                            updated = true;
                            resultSet.put(nonterm, newSet);
                        }
                    }
                }
            }
            if (!updated) {
                return resultSet;
            }
        }

    }

    private Map<String, Set<String>> computeFollowOrPrecedeSet(boolean follow) {
        Map<String, List<List<String>>> rules = grammar.getRules();
        Map<String, Set<String>> firstOrLast = computeFirstOrLastSet(follow);
        System.out.println(follow ? "First:" : "Last:");
        for (String nonterm : firstOrLast.keySet()) {
            System.out.println(nonterm + " " + firstOrLast.get(nonterm));
        }
        Map<String, Set<String>> resultSet = new HashMap<>();
        resultSet.put(STARTING_NONTERM, new HashSet<>(Collections.singleton(follow ? FOLLOW_SYMBOL : PRECEDE_SYMBOL)));

        while (true) {
            boolean updated = false;
            for (String nonterm : rules.keySet()) {
                for (List<String> rewritingRule : rules.get(nonterm)) {
                    for (int i = 0; i < rewritingRule.size(); i++) {
                        String currentTerm = rewritingRule.get(i);
                        if (Grammar.isNonterm(currentTerm)) {
                            Set<String> newSet = new HashSet<>(resultSet.getOrDefault(currentTerm, new HashSet<>()));
                            Set<String> joined = new HashSet<>();
                            if ((i == rewritingRule.size() - 1 && follow)
                                    || (i == 0 && !follow)) {
                                joined.addAll(resultSet.getOrDefault(nonterm, new HashSet<>()));
                            } else {
                                String followOrPrecedeGenerator = rewritingRule.get(
                                        follow ? i + 1 : i - 1
                                );
                                if (Grammar.isTerm(followOrPrecedeGenerator)) {
                                    joined.add(followOrPrecedeGenerator);
                                } else {
                                    joined.addAll(firstOrLast.getOrDefault(followOrPrecedeGenerator, new HashSet<>()));
                                }
                            }

                            if (newSet.size() < joined.size()) {
                                updated = true;
                            }
                            newSet.addAll(joined);
                            resultSet.put(currentTerm, newSet);
                        }
                    }
                }
            }
            if (!updated) {
                return resultSet;
            }
        }
    }




}
