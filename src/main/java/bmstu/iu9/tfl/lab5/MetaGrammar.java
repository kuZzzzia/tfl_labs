package bmstu.iu9.tfl.lab5;

import java.io.IOException;
import java.util.*;

public class MetaGrammar extends Reader {
    private static final String SPECIAL_SYMBOL = "$";

    private static final String CONST_FILE_SEPARATOR = "=";
    private static final String SYNTAX_DEFINITION_ERROR = "Error in defining syntax: ";
    private static final String NO_CNAME_OR_NNAME_DEFINITION_ERROR = "No NNAME or CNAME definition found";

    private static final String RULE = "RULE";
    private static final String EXP = "EXP";
    private static final String NTERM = "NTERM";
    private static final String ALT = "ALT";
    private static final String NEXTALT = "NEXTALT";
    private static final String ITER = "ITER";
    private static final String CONST = "CONST";
    private static final String BEGIN_RULE = "BEGIN_RULE";
    private static final String SEP_R = "SEP_R";
    private static final String END_RULE = "END_RULE";
    private static final String LPAREN = "LPAREN";
    private static final String RPAREN = "RPAREN";
    private static final String BEGIN_NTERM = "BEGIN_NTERM";
    private static final String END_NTERM = "END_NTERM";
    private static final String BEGIN_ALT = "BEGIN_ALT";
    private static final String SEP_A = "SEP_A";
    private static final String END_ALT = "END_ALT";
    private static final String BEGIN_ITER = "BEGIN_ITER";
    private static final String END_ITER = "END_ITER";
    private static final String BEGIN_CONST = "BEGIN_CONST";
    private static final String END_CONST = "END_CONST";
    private static final String NNAME = "NNAME";
    private static final String CNAME = "CNAME";

    private static final String EMPTY_DEFAULT = "";
    private static final String LPAREN_DEFAULT = "$($";
    private static final String RPAREN_DEFAULT = "$)$";
    private static final String SEP_R_DEFAULT = "$=$";
    private static final String SEP_A_DEFAULT = "$|$";
    private static final String END_ITER_DEFAULT = "$*$";

    private static final String CAPITAL_LETTER_REGEX = "[A-Z]";
    private static final String SMALL_LETTER_REGEX = "[a-z]";
    private static final String DIGIT_REGEX = "[0-9]";
    private static final String NOT_WHITESPACE_SYMBOL_REGEX = ".";
    private static final String WHITESPACE_SYMBOL_REGEX = "!blank!";
    private static final String SYMBOL_REGEX = "[^\\[\\].!\\s]";
    private static final String WHITESPACE_REGEX = "\\s";
    private static final String NOT_WHITESPACE_REGEX = "\\S";
    private static final char OPENING_PARENTHESIS = '(';
    private static final char CLOSING_PARENTHESIS = ')';
    private static final char REGEX_ITER_SYMBOL = '+';
    private static final char REGEX_CLOSURE_SYMBOL = '*';

    private static final String INVALID_TERM_WHILE_PARSING = "Invalid term";
    private static final String NO_TERM_FOUND_WHILE_PARSING = "Term expected, but not found";
    private static final String GENERATING_GRAMMAR_FROM_REGEX_ERROR = " grammar error: ";
    private static final String NO_CLOSING_PARENTHESIS_FOUND = "Expected ')' after term parsed";
    private static final String NO_REGEX_ITER_OR_CLOSURE_SYMBOL_FOUND = "Expected '*' or '+'";
    private static final int EMPTY_STRING_LENGTH = 0;
    private static final int POSITION_OF_SYMBOL_TO_PARSE = 0;

    private static final Set<String> SYNTAX_TOKENS = new HashSet<>(Arrays.asList(
            BEGIN_RULE,
            SEP_R,
            END_RULE,
            LPAREN,
            RPAREN,
            BEGIN_NTERM,
            END_NTERM,
            BEGIN_ALT,
            SEP_A,
            END_ALT,
            BEGIN_ITER,
            END_ITER,
            BEGIN_CONST,
            END_CONST
    ));

    private final Map<String, List<List<String>>> rules;

    public MetaGrammar(String path) throws IOException {
        super(path);
        Map<String, String> alias = getSyntaxDefinition();

        rules = new HashMap<>();
        rules.put(RULE, new ArrayList<>(Collections.singletonList(
                new ArrayList<>(Arrays.asList(alias.get(BEGIN_RULE), NTERM, alias.get(SEP_R), EXP, alias.get(END_RULE)))
        )));
        rules.put(EXP, new ArrayList<>(new ArrayList<>(Arrays.asList(
                new ArrayList<>(Collections.singletonList(ALT)),
                new ArrayList<>(Arrays.asList(ITER, EXP)),
                new ArrayList<>(Arrays.asList(NTERM, EXP)),
                new ArrayList<>(Arrays.asList(CONST, EXP)),
                new ArrayList<>(Arrays.asList(alias.get(LPAREN), EXP, alias.get(RPAREN), EXP)),
                new ArrayList<>(Collections.singleton(EMPTY_DEFAULT))
        ))));
        rules.put(NTERM, new ArrayList<>(Collections.singletonList(
                new ArrayList<>(Arrays.asList(alias.get(BEGIN_NTERM), NNAME, alias.get(END_NTERM)))
        )));
        rules.put(ALT, new ArrayList<>(Collections.singletonList(
                new ArrayList<>(Arrays.asList(alias.get(BEGIN_ALT), EXP, alias.get(SEP_A), NEXTALT))
        )));
        rules.put(NEXTALT, new ArrayList<>(new ArrayList<>(Arrays.asList(
                new ArrayList<>(Arrays.asList(EXP, alias.get(SEP_A), NEXTALT)),
                new ArrayList<>(Arrays.asList(EXP, alias.get(END_ALT)))
        ))));
        rules.put(ITER, new ArrayList<>(Collections.singletonList(
                new ArrayList<>(Arrays.asList(alias.get(BEGIN_ITER), EXP, alias.get(END_ITER)))
        )));
        rules.put(CONST, new ArrayList<>(Collections.singletonList(
                new ArrayList<>(Arrays.asList(alias.get(BEGIN_CONST), EXP, alias.get(END_CONST)))
        )));

        rules.putAll(generateGrammarFromRegex(new StringBuilder(alias.get(CNAME)), CNAME));

        rules.putAll(generateGrammarFromRegex(new StringBuilder(alias.get(NNAME)), NNAME));

        printRules();
    }

    protected void printRules() {
        for (String key : rules.keySet()) {
            System.out.println(key + ":" + rules.get(key));
        }
        System.out.println();
    }

    protected void transformCurrentMeta() {
        eliminateEpsilonRules();
        for (String nonterm : rules.keySet()) {
            for (List<String> rewritingRule : rules.get(nonterm)) {
                if (!nonterm.equals(EXP) || rewritingRule.size() != 1){
                    rewritingRule.removeIf(String::isEmpty);
                }
            }
        }
        eliminateChainRules();
        protectTerms();
        convertGrammarToCNF();
        printRules();
    }

    private void convertGrammarToCNF() {
        int newNontermCount = 0;
        Map<String, List<List<String>>> newRules = new HashMap<>();
        Set<String> nontermsBeforeTransformation = new HashSet<>(rules.keySet());
        for (String nonterm : nontermsBeforeTransformation) {
            for (List<String> rewritingRule : rules.get(nonterm)) {
                List<String> ruleTransforming = rewritingRule;
                while (ruleTransforming.size() > 2) {
                    String newNonterm = generateNewNonterm(nonterm, newNontermCount++);
                    List<List<String>> newRewritingRule = new ArrayList<>();
                    newRewritingRule.add(new ArrayList<>(ruleTransforming));
                    newRewritingRule.get(0).remove(0);
                    while (ruleTransforming.size() > 1) {
                        ruleTransforming.remove(1);
                    }
                    ruleTransforming.add(newNonterm);
                    rules.put(newNonterm, newRewritingRule);
                    ruleTransforming = newRewritingRule.get(0);
                }
            }
        }
    };

    private void protectTerms() {
        Map<String, String> protectedTerms = new HashMap<>();
        for (String nonterm : rules.keySet()) {
                for (List<String> rewritingRule : rules.get(nonterm)) {
                    if (rewritingRule.size() > 1) {
                        for (int i = 0; i < rewritingRule.size(); i++) {
                            String term = rewritingRule.get(i);
                            if (isTerm(term)) {
                                Optional<String> protectingNonterm = Optional.ofNullable(protectedTerms.get(term));
                                if (protectingNonterm.isPresent()) {
                                    rewritingRule.set(i, protectingNonterm.get());
                                } else {
                                    String newProtectingNonterm = "PROTECTED_" + term.substring(1, term.length() - 1);
                                    protectedTerms.put(term, newProtectingNonterm);
                                    rewritingRule.set(i, newProtectingNonterm);
                                }
                            }
                        }
                    }
                }
        }
        for (String protectedTerm : protectedTerms.keySet()) {
            String newProtectingNonterm = protectedTerms.get(protectedTerm);
            List<List<String>> newRewritingRule = new ArrayList<>();
            newRewritingRule.add(new ArrayList<>(Collections.singleton(protectedTerm)));
            rules.put(newProtectingNonterm, newRewritingRule);
        }
    }

    private static boolean isTerm(String candidate) {
        return (candidate.length() > 2 && candidate.startsWith(SPECIAL_SYMBOL) && candidate.endsWith(SPECIAL_SYMBOL));
    }

    private void eliminateChainRules() {
        for (String nontermLeft : rules.keySet()) {
            boolean updated = true;
            while (updated) {
                updated = false;
                for (String nontermRight : rules.keySet()) {
                    int i = 0;
                    List<List<String>> rewritingRules = rules.get(nontermLeft);
                    int length = rewritingRules.size();
                    while (i < length && !(rewritingRules.get(i).size() == 1
                            && rewritingRules.get(i).get(0).equals(nontermRight))) {
                        i++;
                    }
                    if (i != length) {
                        rules.get(nontermLeft).remove(i);
                        for (List<String> newRewritingRule : rules.get(nontermRight)) {
                            boolean exists = false;
                            for (List<String> rewritingRule : rules.get(nontermLeft)) {
                                if (rewritingRule.equals(newRewritingRule)) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                rules.get(nontermLeft).add(new ArrayList<>(newRewritingRule));
                                updated = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void eliminateEpsilonRules() {
        Set<String> nullable = new HashSet<>();
        Queue<String> nullableBuffer = new PriorityQueue<>();
        for (String nonterm : rules.keySet()) {
            for (List<String> rewritingRule : rules.get(nonterm)) {
                if (rewritingRule.size() == 1 && rewritingRule.get(0).equals(EMPTY_DEFAULT)) {
                    nullable.add(nonterm);
                    break;
                }
            }
        }
        findCollapsingNonterms(nullable, nullableBuffer);
        while (!nullableBuffer.isEmpty()) {
            nullable.addAll(nullableBuffer);
            nullableBuffer.clear();
            findCollapsingNonterms(nullable, nullableBuffer);
        }
        boolean updated = true;
        while (updated) {
            updated = false;
            for (String nonterm : rules.keySet()) {
                Set<List<String>> newRulesBuffer = new HashSet<>();
                for (List<String> rewritingRule : rules.get(nonterm)) {
                    if (rewritingRule.size() > 1) {
                        for (int i = 0; i < rewritingRule.size(); i++) {
                            String term = rewritingRule.get(i);
                            if (nullable.contains(term)) {
                                List<String> newRule = new ArrayList<>(rewritingRule);
                                newRule.remove(i);
                                if (!term.equals(EXP) || nonterm.equals(EXP)) {
                                    addNewRuleIfUnique(nonterm, newRule, newRulesBuffer);
                                }
                            }
                        }
                    }
                }
                if (!newRulesBuffer.isEmpty()) {
                    updated = true;
                    for (List<String> newRule : newRulesBuffer) {
                        rules.get(nonterm).add(newRule);
                    }
                }
            }
        }
        Queue<String> nontermsToDelete = new PriorityQueue<>();
        for (String nonterm : rules.keySet()) {
            List<List<String>> rewritingRules = rules.get(nonterm);
            for (int i = 0; i < rewritingRules.size(); i++) {
                if (rewritingRules.get(i).get(0).equals(EMPTY_DEFAULT) && rewritingRules.get(i).size() == 1) {
                    if (rewritingRules.size() == 1) {
                        nontermsToDelete.add(nonterm);
                    }
                    rewritingRules.remove(i--);
                }
            }
        }
        while (!nontermsToDelete.isEmpty()) {
            String nontermToDelete = nontermsToDelete.poll();
            rules.remove(nontermToDelete);
            for (String nonterm : rules.keySet()) {
                List<List<String>> rewritingRules = rules.get(nonterm);
                for (int i = 0; i < rewritingRules.size(); i++) {
                    boolean delete = false;
                    for (String term : rewritingRules.get(i)) {
                        if (term.equals(nontermToDelete)) {
                            delete = true;
                            break;
                        }
                    }
                    if (delete) {
                        rewritingRules.remove(i--);
                        if (rewritingRules.size() == 0) {
                            nontermsToDelete.add(nonterm);
                        }
                    }
                }
            }
        }
    }

    private void addNewRuleIfUnique(String nonterm, List<String> newRule, Set<List<String>> newRulesBuffer) {
        for (List<String> rewritingRule : rules.get(nonterm)) {
            if (rewritingRule.equals(newRule)) {
                return;
            }
        }
        newRulesBuffer.add(newRule);
    }

    private void findCollapsingNonterms(Set<String> nullable, Queue<String> nullableBuffer) {
        for (String nonterm : rules.keySet()) {
            if (!(nullable.contains(nonterm) || nullableBuffer.contains(nonterm))) {
                for (List<String> rewritingRule : rules.get(nonterm)) {
                    boolean isCollapsing = true;
                    for (String term : rewritingRule) {
                        if (!nullable.contains(term)) {
                            isCollapsing = false;
                            break;
                        }
                    }
                    if (isCollapsing) {
                        nullableBuffer.add(nonterm);
                    }
                }
            }
        }
    }

    private Map<String, List<List<String>>> generateGrammarFromRegex(StringBuilder regex, String startingNonterm) {
        Map<String, List<List<String>>> regularGrammar = new HashMap<>();
        if (regex.length() == EMPTY_STRING_LENGTH) {
            throw new Error();
        }
        String currentNonterm = startingNonterm;
        int nontermCount = 0;
        while(regex.length() != EMPTY_STRING_LENGTH) {
            if (regex.charAt(POSITION_OF_SYMBOL_TO_PARSE) == OPENING_PARENTHESIS) {
                regex.deleteCharAt(POSITION_OF_SYMBOL_TO_PARSE);
                String term;
                try {
                    term = parseTerm(regex);
                } catch (Error e) {
                    throw new Error(startingNonterm + GENERATING_GRAMMAR_FROM_REGEX_ERROR + e);
                }
                if (regex.length() == EMPTY_STRING_LENGTH || regex.charAt(POSITION_OF_SYMBOL_TO_PARSE) != CLOSING_PARENTHESIS) {
                    throw new Error(startingNonterm + GENERATING_GRAMMAR_FROM_REGEX_ERROR + NO_CLOSING_PARENTHESIS_FOUND);
                }
                regex.deleteCharAt(POSITION_OF_SYMBOL_TO_PARSE);
                if (regex.length() == EMPTY_STRING_LENGTH) {
                    throw new Error(startingNonterm + GENERATING_GRAMMAR_FROM_REGEX_ERROR + NO_REGEX_ITER_OR_CLOSURE_SYMBOL_FOUND);
                }
                if (regex.charAt(POSITION_OF_SYMBOL_TO_PARSE) == REGEX_ITER_SYMBOL) {
                    String newNonterm = generateNewNonterm(startingNonterm, nontermCount++);
                    regularGrammar.put(currentNonterm, new ArrayList<>(Arrays.asList(Arrays.asList(term, currentNonterm), Arrays.asList(term, newNonterm))));
                    currentNonterm = newNonterm;
                } else if (regex.charAt(POSITION_OF_SYMBOL_TO_PARSE) == REGEX_CLOSURE_SYMBOL) {
                    String newNonterm = generateNewNonterm(startingNonterm, nontermCount++);
                    regularGrammar.put(currentNonterm, new ArrayList<>(Arrays.asList(Arrays.asList(term, currentNonterm), Collections.singletonList(newNonterm))));
                    currentNonterm = newNonterm;
                } else {
                    throw new Error(startingNonterm + GENERATING_GRAMMAR_FROM_REGEX_ERROR + NO_REGEX_ITER_OR_CLOSURE_SYMBOL_FOUND);
                }
                regex.deleteCharAt(POSITION_OF_SYMBOL_TO_PARSE);
            } else {
                String term;
                try {
                    term = parseTerm(regex);
                } catch (Error e) {
                    throw new Error(startingNonterm + GENERATING_GRAMMAR_FROM_REGEX_ERROR + e);
                }
                String newNonterm = generateNewNonterm(startingNonterm, nontermCount++);
                regularGrammar.put(currentNonterm, new ArrayList<>(Collections.singletonList(Arrays.asList(term, newNonterm))));
                currentNonterm = newNonterm;
            }
        }
        regularGrammar.put(currentNonterm, new ArrayList<>(Collections.singletonList(Collections.singletonList(EMPTY_DEFAULT))));
        return regularGrammar;
    }

    private static String generateNewNonterm(String name, int number) {
        return name + "_" + number;
    }

    private String parseTerm(StringBuilder regex) {
        String regexString = regex.toString();
        if (regexString.isEmpty()) {
            throw new Error(NO_TERM_FOUND_WHILE_PARSING);
        }
        int lengthOfPrefixToDelete = 0;
        String terminal = "";
        if (regexString.startsWith(CAPITAL_LETTER_REGEX)) {
            terminal = wrapWithSpecialSymbol(CAPITAL_LETTER_REGEX);
            lengthOfPrefixToDelete = CAPITAL_LETTER_REGEX.length();
        } else if (regexString.startsWith(SMALL_LETTER_REGEX)) {
            terminal = wrapWithSpecialSymbol(SMALL_LETTER_REGEX);
            lengthOfPrefixToDelete = SMALL_LETTER_REGEX.length();
        } else if (regexString.startsWith(DIGIT_REGEX)) {
            terminal = wrapWithSpecialSymbol(DIGIT_REGEX);
            lengthOfPrefixToDelete = DIGIT_REGEX.length();
        } else if (regexString.substring(POSITION_OF_SYMBOL_TO_PARSE, 1).matches(SYMBOL_REGEX)){
            terminal = wrapWithSpecialSymbol(regexString.substring(POSITION_OF_SYMBOL_TO_PARSE, 1));
            lengthOfPrefixToDelete = terminal.length();
        } else if (regexString.startsWith(NOT_WHITESPACE_SYMBOL_REGEX)) {
            terminal = wrapWithSpecialSymbol(NOT_WHITESPACE_REGEX);
            lengthOfPrefixToDelete = NOT_WHITESPACE_SYMBOL_REGEX.length();
        } else if (regexString.startsWith(WHITESPACE_SYMBOL_REGEX)) {
            terminal = wrapWithSpecialSymbol(WHITESPACE_REGEX);
            lengthOfPrefixToDelete = WHITESPACE_SYMBOL_REGEX.length();
        } else {
            throw new Error(INVALID_TERM_WHILE_PARSING);
        }
        regex.delete(POSITION_OF_SYMBOL_TO_PARSE, lengthOfPrefixToDelete);
        return terminal;
    }

    private static String wrapWithSpecialSymbol(String s) {
        return SPECIAL_SYMBOL + s + SPECIAL_SYMBOL;
    }

    private Map<String, String> getSyntaxDefinition() {
        Map<String, String> alias = new HashMap<>();
        alias.put(BEGIN_RULE, EMPTY_DEFAULT);
        alias.put(END_RULE, EMPTY_DEFAULT);
        alias.put(BEGIN_NTERM, EMPTY_DEFAULT);
        alias.put(END_NTERM, EMPTY_DEFAULT);
        alias.put(BEGIN_ALT, EMPTY_DEFAULT);
        alias.put(END_ALT, EMPTY_DEFAULT);
        alias.put(BEGIN_ITER, EMPTY_DEFAULT);
        alias.put(BEGIN_CONST, EMPTY_DEFAULT);
        alias.put(END_CONST, EMPTY_DEFAULT);

        alias.put(LPAREN, LPAREN_DEFAULT);
        alias.put(RPAREN, RPAREN_DEFAULT);
        alias.put(SEP_R, SEP_R_DEFAULT);
        alias.put(SEP_A, SEP_A_DEFAULT);
        alias.put(END_ITER, END_ITER_DEFAULT);


        for (String line : this.getData()) {
            String[] split = line.split(CONST_FILE_SEPARATOR, 2);
            if (split.length != 2) {
                throw new Error(SYNTAX_DEFINITION_ERROR + line);
            }
            String key = split[0].trim(), value = split[1].trim();
            if (SYNTAX_TOKENS.contains(key)) {
                alias.replace(key, wrapWithSpecialSymbol(value));
            } else if (key.equals(NNAME) || key.equals(CNAME)) {
                alias.put(key, value);
            }
        }
        if (!(alias.containsKey(NNAME) && alias.containsKey(CNAME))) {
            throw new Error(NO_CNAME_OR_NNAME_DEFINITION_ERROR);
        }
        return alias;
    }

}
