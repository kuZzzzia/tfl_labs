package bmstu.iu9.tfl.lab3;

import java.util.*;

public class RuleRightSide {
    private static final int        REGULAR_RULE_ONLY_OF_TERMS_LENGTH = 1;
    private static final int        INDEX_OF_TERM_STRING_IN_REGULAR_RULE = 0;
    protected static final String   NONTERM_REGEX = "^[A-Z][0-9]*$";

    private final List<String[]> rewritingVariants;

    private Set<String> dependency;
    private String      regularSubsetKey;

    public RuleRightSide(String[] rewritingVariant) {
        this.rewritingVariants = new ArrayList<>(Collections.singleton(rewritingVariant));
        this.dependency = null;
    }

    protected void add(String[] newRightSide) {
        rewritingVariants.add(newRightSide);
    }

    protected Set<String> getFirstLevelDependency() {
        Set<String> firstLevelDependency = new HashSet<>();
        for (String[] rewritingRule: rewritingVariants) {
            if (rewritingRule.length == REGULAR_RULE_ONLY_OF_TERMS_LENGTH) {
                firstLevelDependency.add(rewritingRule[INDEX_OF_TERM_STRING_IN_REGULAR_RULE]);
            } else {
                for (String expr : rewritingRule) {
                    if (expr.matches(NONTERM_REGEX)) {
                        firstLevelDependency.add(expr);
                    }
                }
            }
        }
        return firstLevelDependency;
    }

    protected void setDependency(Set<String> dependency) {
        this.dependency = dependency;
    }

    protected Set<String> getDependency() {
        return new HashSet<>(dependency);
    }

    protected boolean checkNullDependency() {
         return dependency == null;
    }

    protected boolean checkNontermDependency(String nonterm) {
        return dependency.contains(nonterm);
    }

    protected void setRegularSubsetKey(String regularSubsetKey) {
        this.regularSubsetKey = regularSubsetKey;
    }
}
