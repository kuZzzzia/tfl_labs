package bmstu.iu9.tfl.lab3;


import java.io.IOException;
import java.util.*;

public class Grammar extends Reader {
    private static final String RULE_REGEX = "^[A-Z][0-9]*\\s*->\\s*[a-z](\\s*([A-Z][0-9]*)|[a-z])*$";
    private static final String WHITESPACE_REGEX = "\\s";
    private static final String EMPTY_STRING = "";
    private static final String RULE_SEPARATOR = "->";
    private static final String STARTING_NONTERM_REGEX = "S";
    private static final int    RULE_SEPARATOR_LENGTH = 2;
    private static final int    STARTING_CHAR_INDEX = 0;


    private final Map<String, RuleRightSide>    rules;
    private final Set<String>                   nontermsUsed;
    private final Map<String, Set<String>>      regularNontermsSubsets;
    private final Set<String>                   notRegularNonterms;

    protected Grammar(String path) throws IOException {
        super(path);
        nontermsUsed = new HashSet<>();
        rules = new HashMap<>();
        regularNontermsSubsets = new HashMap<>();
        notRegularNonterms = new HashSet<>();
        parseRules(super.getData());
        makeRegularSubsets();
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

            String[] newRightSide = new Parser(ruleRightSideString, nontermsUsed).getParsedRuleRightSide();

            if (newRightSide.length > 2) {
                notRegularNonterms.add(ruleLeftSide);
            }

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

    private void makeRegularSubsets() {
        Set<String> stackBuildDependency = new HashSet<>();
        Queue<String> newNotRegularNonterms = new PriorityQueue<>(rules.size());

        for (String nonterm : rules.keySet()) {
            if (rules.get(nonterm).checkNullDependency()) {
                buildDependency(nonterm, stackBuildDependency, newNotRegularNonterms);
            }
        }

        while (!newNotRegularNonterms.isEmpty()) {
            String notRegularNonterm = newNotRegularNonterms.poll();
            for (String nonterm : rules.keySet()) {
                if (!notRegularNonterms.contains(nonterm)
                        && !nonterm.equals(notRegularNonterm)
                        && rules.get(nonterm).checkNontermDependency(notRegularNonterm)) {
                    newNotRegularNonterms.add(nonterm);
                }
            }
            notRegularNonterms.add(notRegularNonterm);
        }

        for (String nonterm : rules.keySet()) {
            if (!notRegularNonterms.contains(nonterm)) {
                regularNontermsSubsets.put(nonterm, rules.get(nonterm).getDependency());
                rules.get(nonterm).setRegularSubsetKey(nonterm);
            }
        }
        Set<String> regularNonterms = new HashSet<>(regularNontermsSubsets.keySet());

        for (String nonterm1 : regularNonterms) {
            if (regularNontermsSubsets.containsKey(nonterm1)) {
                for (String nonterm2 : regularNonterms) {
                    if (!nonterm1.equals(nonterm2) && regularNontermsSubsets.containsKey(nonterm2)
                            && regularNontermsSubsets.get(nonterm1).contains(nonterm2)) {
                        regularNontermsSubsets.remove(nonterm2);
                        rules.get(nonterm2).setRegularSubsetKey(nonterm1);
                    }
                }
            }
        }
    }

    private void buildDependency(String nonterm, Set<String> stackBuildDependency, Queue<String> newNotRegularRules) {
        RuleRightSide ruleRightSide = rules.get(nonterm);
        stackBuildDependency.add(nonterm);
        Set<String> firstLevelDependency = ruleRightSide.getFirstLevelDependency();
        Set<String> fullDependency = new HashSet<>(firstLevelDependency);
        for (String expr : firstLevelDependency) {
            if (expr.matches(RuleRightSide.NONTERM_REGEX) && !stackBuildDependency.contains(expr)) {
                if (rules.get(expr).checkNullDependency()) {
                    buildDependency(expr, stackBuildDependency, newNotRegularRules);
                }
                fullDependency.addAll(rules.get(expr).getDependency());
                if (notRegularNonterms.contains(expr)) {
                    newNotRegularRules.add(nonterm);
                }
            }
        }
        ruleRightSide.setDependency(fullDependency);
        stackBuildDependency.remove(nonterm);
    }

    protected Set<String> getNontermsAchievableFromStartingNonterm() {
        Set<String> nontermsAchievableFromStartingNonterm = new HashSet<>();
        for (String expr : rules.get(STARTING_NONTERM_REGEX).getDependency()) {
            if (expr.matches(RuleRightSide.NONTERM_REGEX)) {
                nontermsAchievableFromStartingNonterm.add(expr);
            }
        }
        return nontermsAchievableFromStartingNonterm;
    }

    protected List<String[]> getNontermRewritingVariants(String nonterm) {
        return rules.get(nonterm).getRewritingVariants();
    }
}
