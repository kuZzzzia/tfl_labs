package bmstu.iu9.tfl.lab4;

import bmstu.iu9.tfl.lab5.MetaGrammar;

import java.util.*;

import static bmstu.iu9.tfl.lab5.MetaGrammar.isTerm;

public class Tokenizer {

    private enum GrammarType {
        RIGHT_LINEAR,
        LEFT_LINEAR
    }

    private static final int FIRST_SET_INDEX = 0;
    private static final String STARTING_NONTERM = "RULE";
    private static final String FOLLOW_SYMBOL = "$";
    private static final String PRECEDE_SYMBOL = "^";

    private final MetaGrammar grammar;
    private final Set<String> tokens;

    private Set<String> rightLinearRegularNonterms;
    private Set<String> leftLinearRegularNonterms;
    private Set<String> regularClosureNonterms;

    public Tokenizer(MetaGrammar grammar) {
        this.grammar = grammar;
        tokens = new HashSet<>();
        buildRegularSubsets();
        findTokens();
    }

    private void findTokens() {
        Map<String, Set<String>> followSet = computeFollowOrPrecedeSet(true);
        Map<String, Set<String>> precedeSet = computeFollowOrPrecedeSet(false);

        Set<String> regularNonterms = new HashSet<>(leftLinearRegularNonterms);
        regularNonterms.addAll(rightLinearRegularNonterms);
        regularNonterms.addAll(regularClosureNonterms);

        for (String nonterm : regularNonterms) {
            Set<String> terms = new HashSet<>(followSet.getOrDefault(nonterm, new HashSet<>()));
            terms.addAll(precedeSet.getOrDefault(nonterm, new HashSet<>()));
            Set<String> nontermLanguage = findNontermLanguage(nonterm, new HashSet<>());
            nontermLanguage.retainAll(terms);
            if (nontermLanguage.size() == 0) {
                tokens.add(nonterm);
            }
        }
    }

    private Set<String> findNontermLanguage(String nonterm, Set<String> stackTraceVisited) {
        Set<String> lang = new HashSet<>();
        for (List<String> rewritingRule : grammar.getRules().get(nonterm)) {
            if (rewritingRule.size() == 1) {
                String term = rewritingRule.get(0);
                if (isTerm(term)) {
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
                if (isTerm(resultSetGenerator)) {
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
                    if (!isTerm(resultSetGenerator)) {
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
        Map<String, Set<String>> resultSet = new HashMap<>();
        resultSet.put(STARTING_NONTERM, new HashSet<>(Collections.singleton(follow ? FOLLOW_SYMBOL : PRECEDE_SYMBOL)));

        while (true) {
            boolean updated = false;
            for (String nonterm : rules.keySet()) {
                for (List<String> rewritingRule : rules.get(nonterm)) {
                    for (int i = 0; i < rewritingRule.size(); i++) {
                        String currentTerm = rewritingRule.get(i);
                        if (!isTerm(currentTerm)) {
                            Set<String> newSet = new HashSet<>(resultSet.getOrDefault(currentTerm, new HashSet<>()));
                            Set<String> joined = new HashSet<>();
                            if ((i == rewritingRule.size() - 1 && follow)
                                    || (i == 0 && !follow)) {
                                joined.addAll(resultSet.getOrDefault(nonterm, new HashSet<>()));
                            } else {
                                String followOrPrecedeGenerator = rewritingRule.get(
                                        follow ? i + 1 : i - 1
                                );
                                if (isTerm(followOrPrecedeGenerator)) {
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

    public Set<String> getTokens() {
        return tokens;
    }

    private void buildRegularSubsets() {
        Map<String, Set<String>> dependency = new HashMap<>();
        for (String nonterm: grammar.getRules().keySet()) {
            if (!dependency.containsKey(nonterm)) {
                Set<String> stackTrace = new HashSet<>(Collections.singleton(nonterm));
                buildNontermDependency(nonterm, dependency, stackTrace);
            }
        }
        leftLinearRegularNonterms = getRegularNonterms(GrammarType.LEFT_LINEAR, dependency);
        rightLinearRegularNonterms = getRegularNonterms(GrammarType.RIGHT_LINEAR, dependency);
        buildRegularGrammarsClosure(dependency);
    }

    private void buildRegularGrammarsClosure(final Map<String, Set<String>> dependency) {
        regularClosureNonterms = new HashSet<>();

        while (true) {
            boolean changed = false;
            for (String nonterm : grammar.getRules().keySet()) {
                if (!checkNontermIsRegular(nonterm)) {
                    boolean regular = true;
                    for (String rewritingNonterm : dependency.get(nonterm)) {
                        if (!checkNontermIsRegular(rewritingNonterm)) {
                            regular = false;
                        }
                    }
                    if (regular) {
                        regularClosureNonterms.add(nonterm);
                        changed = true;
                    }
                }
            }
            if (!changed) {
                return;
            }
        }
    }

    protected boolean checkNontermIsRegular(String nonterm) {
        return leftLinearRegularNonterms.contains(nonterm)
                || rightLinearRegularNonterms.contains(nonterm)
                || regularClosureNonterms.contains(nonterm);
    }

    private Set<String> getRegularNonterms(final GrammarType grammarType, final Map<String, Set<String>> dependency) {
        Set<String> regularNonterms = new HashSet<>();
        Set<String> notRegularNonterms = findNotRegularNonterms(grammarType, dependency);
        for (String nonterm : grammar.getRules().keySet()) {
            if (!notRegularNonterms.contains(nonterm)) {
                regularNonterms.add(nonterm);
            }
        }
        return  regularNonterms;
    }

    private Set<String> findNotRegularNonterms(final GrammarType grammarType, final Map<String, Set<String>> dependency) {
        Set<String> notRegularNonterms = new HashSet<>();
        Queue<String> newNotRegularNonterms = new PriorityQueue<>(grammar.getRules().size());
        for (String nonterm : grammar.getRules().keySet()) {
            for (List<String> rewritingRule : grammar.getRules().get(nonterm)) {
                if (isNotRegular(grammarType, rewritingRule)) {
                    newNotRegularNonterms.add(nonterm);
                }
            }
        }

        while (!newNotRegularNonterms.isEmpty()) {
            String notRegularNonterm = newNotRegularNonterms.poll();
            for (String nonterm: grammar.getRules().keySet()) {
                if (!notRegularNonterms.contains(nonterm) && !newNotRegularNonterms.contains(nonterm) && dependency.get(nonterm).contains(notRegularNonterm)) {
                    newNotRegularNonterms.add(nonterm);
                }
            }
            notRegularNonterms.add(notRegularNonterm);
        }

        return notRegularNonterms;
    }

    private void buildNontermDependency(String nonterm, Map<String, Set<String>> dependency, Set<String> stackTrace) {
        Set<String> nontermDependency = new HashSet<>();
        for (List<String> rewritingRule : grammar.getRules().get(nonterm)) {
            for (String term : rewritingRule) {
                if (!isTerm(term)) {
                    nontermDependency.add(term);
                    if (!stackTrace.contains(term)) {
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

    private boolean isNotRegular(GrammarType grammarType, List<String> rewritingRule) {
        switch (rewritingRule.size()) {
            case 1 :
                return !isTerm(rewritingRule.get(0));
            case 2 :
                return (grammarType.equals(GrammarType.RIGHT_LINEAR) && !(isTerm(rewritingRule.get(0)) && !isTerm(rewritingRule.get(1)))) ||
                        (grammarType.equals(GrammarType.LEFT_LINEAR) && !(isTerm(rewritingRule.get(1)) && !isTerm(rewritingRule.get(0))));
            default:
                return true;
        }
    }
}

