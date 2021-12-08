package bmstu.iu9.tfl.lab2;

import bmstu.iu9.tfl.lab4.Grammar;
import bmstu.iu9.tfl.lab4.Tokenizer;

import java.util.*;

public class Solver {
    private static final String NEW_STATE = "[$Q$]";

    Map<String, String> result;

    public Solver(Grammar rules, Tokenizer tokenizer) {
        this.result = new HashMap<>();

        Set<String> finiteNonterms = findFiniteNonterms(rules.getRules());
        Set<String> rightLinear = new HashSet<>(rules.getRightLinearRegularNonterms());
        rightLinear.retainAll(finiteNonterms);
        Map<String, String> result = solveEquationsSystem(
          generateRegexEquationsSystem(
                  rules.getRules(),
                  rightLinear
          )
        );
        for (String nonterm : result.keySet()) {
            if (finiteNonterms.contains(nonterm)) {
                this.result.put(nonterm, result.get(nonterm));
            }
        }
        Set<String> notComputedLeftLinear = new HashSet<>(rules.getLeftLinearRegularNonterms());
        notComputedLeftLinear.removeAll(rules.getRightLinearRegularNonterms());
        for (String nonterm : notComputedLeftLinear) {
            Map<String, List<List<String>>> newGrammar = new HashMap<>();
            convertLeftToRightRegular(nonterm, nonterm, new HashSet<>(), rules.getRules(), newGrammar);
            Set<String> finite = findFiniteNonterms(newGrammar);
            Set<String> regexLeftLinear = new HashSet<>(newGrammar.keySet());
            regexLeftLinear.retainAll(finite);
            Map<String, String> generatedRegex = solveEquationsSystem(
                    generateRegexEquationsSystem(
                            newGrammar,
                            regexLeftLinear
                    )
            );
            this.result.put(nonterm, generatedRegex.get(NEW_STATE));
        }
        for (String nonterm : rules.getRegularClosureNonterms()) {
            if (finiteNonterms.contains(nonterm)) {
                if (!this.result.containsKey(nonterm)) {
                    generateRegexForClosureNonterms(nonterm, rules.getRules(), finiteNonterms);
                }
            }
        }
        Set<String> notTokens = new HashSet<>(this.result.keySet());
        notTokens.removeAll(tokenizer.getTokens());
        for (String notToken : notTokens) {
            this.result.remove(notToken);
        }
    }

    private void convertLeftToRightRegular(String nonterm, String root, Set<String> stackTraceVisited, Map<String, List<List<String>>> baseRules, Map<String, List<List<String>>> newGrammar) {
        if (stackTraceVisited.contains(nonterm)) {
            return;
        }
        for (List<String> rewritingRule: baseRules.get(nonterm)) {
            if (rewritingRule.size() != 1) {
                stackTraceVisited.add(nonterm);
                convertLeftToRightRegular(rewritingRule.get(0), root, stackTraceVisited, baseRules, newGrammar);
                stackTraceVisited.remove(nonterm);
                if (!newGrammar.containsKey(rewritingRule.get(0))) {
                    newGrammar.put(rewritingRule.get(0), new ArrayList<>());
                }
                newGrammar.get(rewritingRule.get(0)).add(Arrays.asList(rewritingRule.get(1), nonterm));
                if (nonterm.equals(root)) {
                    newGrammar.get(rewritingRule.get(0)).add(Collections.singletonList(rewritingRule.get(1)));
                }
            } else {
                String term = rewritingRule.get(0);
                if (Grammar.isTerm(term)) {
                    if (!newGrammar.containsKey(NEW_STATE)) {
                        newGrammar.put(rewritingRule.get(0), new ArrayList<>());
                    }
                    newGrammar.get(NEW_STATE).add(Arrays.asList(term, nonterm));
                    if (nonterm.equals(root)) {
                        newGrammar.get(NEW_STATE).add(Collections.singletonList(term));
                    }
                } else {
                    stackTraceVisited.add(nonterm);
                    convertLeftToRightRegular(term, root, stackTraceVisited, baseRules, newGrammar);
                    stackTraceVisited.remove(nonterm);
                    if (!newGrammar.containsKey(term)) {
                        newGrammar.put(term, new ArrayList<>());
                    }
                    newGrammar.get(term).add(Collections.singletonList(nonterm));
                }
            }
        }
    }

    private void generateRegexForClosureNonterms(String nonterm, Map<String, List<List<String>>> rules, Set<String> finiteNonterms) {
        String regex = "";
        for (List<String> rewritingRule : rules.get(nonterm)) {
            StringBuilder ruleRegex = new StringBuilder();
            for (String term : rewritingRule) {
                if (Grammar.isTerm(term)) {
                    ruleRegex.append(term);
                } else if (finiteNonterms.contains(term)) {
                    if (!result.containsKey(term)) {
                        generateRegexForClosureNonterms(term, rules, finiteNonterms);
                    }
                    ruleRegex.append(result.get(term));
                } else {
                    ruleRegex = null;
                    break;
                }
            }
            if (ruleRegex != null) {
                regex = regex.equals("") ? ("(" + ruleRegex + ")") : "(" + regex + "+" + "(" + ruleRegex + "))";
            }
        }
        result.put(nonterm, regex);
    }

    protected Equation[] generateRegexEquationsSystem(Map<String, List<List<String>>> rules, Set<String> regexNTerms) {
        ArrayList<Equation> system = new ArrayList<>();
        for (String nonterm : rules.keySet()) {
            if (regexNTerms.contains(nonterm)) {
                ArrayList<String> regex = new ArrayList<>();
                HashMap<String, List<String>> variables = new HashMap<>();
                for (List<String> rewritingRule : rules.get(nonterm)) {
                    if (rewritingRule.size() == 1) {
                        String term = rewritingRule.get(0);
                        if (Grammar.isTerm(term)) {
                            regex.add(term);
                        } else if (regexNTerms.contains(term)){
                            if (variables.containsKey(term)) {
                                variables.get(term).add("");
                            } else {
                                variables.put(term, new ArrayList<>(Collections.singleton("")));
                            }
                        }
                    } else {
                        String rewritingNonterm = rewritingRule.get(1);
                        String rewritingTerm = rewritingRule.get(0);
                        if (regexNTerms.contains(rewritingNonterm)) {
                            if (variables.containsKey(rewritingNonterm)) {
                                variables.get(rewritingNonterm).add(rewritingTerm);
                            } else {
                                variables.put(rewritingNonterm, new ArrayList<>(Collections.singleton(rewritingTerm)));
                            }
                        }
                    }
                }
                system.add(new Equation(nonterm, variables, regex));
            }
        }
        return system.toArray(new Equation[0]);
    }


    private Set<String> findFiniteNonterms(Map<String, List<List<String>>> rules) {
        Set<String> finiteNonterms = new HashSet<>();

        while(true) {
            boolean found = false;
            for (String nonterm : rules.keySet()) {
                for (List<String> rewritingRule : rules.get(nonterm)) {
                    boolean ind = false;
                    for (String term : rewritingRule) {
                        if (!Grammar.isTerm(term) && !finiteNonterms.contains(term)) {
                            ind = true;
                            break;
                        }
                    }
                    if (!ind && !finiteNonterms.contains(nonterm)) {
                        found = true;
                        finiteNonterms.add(nonterm);
                    }
                }
            }
            if (!found) {
                return finiteNonterms;
            }
        }
    }


    public static Map<String, String> solveEquationsSystem(Equation[] equations) {
        int amount = equations.length;
        if (amount != 1) {
            for (int i = 0; i < amount - 1; i++) {
                equations[i].reduceVariableFromRightSide();
                for (int j = i + 1; j < amount; j++) {
                    equations[j].substituteVariableInEquation(equations[i]);
                }
            }
            for (int i = amount - 1; i > 0; i--) {
                equations[i].reduceVariableFromRightSide();
                for (int j = i - 1; j > -1; j--) {
                    equations[j].substituteVariableInEquation(equations[i]);
                }
            }
        } else {
            equations[0].reduceVariableFromRightSide();
        }
        Map<String, String> ans = new HashMap<>();
        for (Equation equation : equations) {
            ans.put(equation.getVar(), equation.getAns());
        }
        return ans;
    }

    public void printResult() {
        System.out.println("Answer: ");
        for (String token : result.keySet()) {
            System.out.println(token + " " + result.get(token));
        }
    }
}
