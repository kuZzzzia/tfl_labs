package bmstu.iu9.tfl.lab5;

import java.io.IOException;
import java.util.*;

public class MetaGrammar extends Reader {
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
    private static final String LPAREN_DEFAULT = "(";
    private static final String RPAREN_DEFAULT = ")";
    private static final String SEP_R_DEFAULT = "=";
    private static final String SEP_A_DEFAULT = "|";
    private static final String END_ITER_DEFAULT = "*";

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
                Arrays.asList(alias.get(BEGIN_RULE), NTERM, alias.get(SEP_R), EXP, alias.get(END_RULE))
        )));
        rules.put(EXP, new ArrayList<>(Arrays.asList(
                Collections.singletonList(ALT),
                Arrays.asList(ITER, EXP),
                Arrays.asList(NTERM, EXP),
                Arrays.asList(CONST, EXP),
                Arrays.asList(alias.get(LPAREN), EXP, alias.get(RPAREN), EXP),
                new ArrayList<>(Collections.singleton(EMPTY_DEFAULT))
        )));
        rules.put(NTERM, new ArrayList<>(Collections.singletonList(
                Arrays.asList(alias.get(BEGIN_NTERM), NNAME, alias.get(END_NTERM))
        )));
        rules.put(ALT, new ArrayList<>(Collections.singletonList(
                Arrays.asList(alias.get(BEGIN_ALT), EXP, alias.get(SEP_A), NEXTALT)
        )));
        rules.put(NEXTALT, new ArrayList<>(Arrays.asList(
                Arrays.asList(EXP, alias.get(SEP_A), NEXTALT),
                Arrays.asList(EXP, alias.get(END_ALT))
        )));
        rules.put(ITER, new ArrayList<>(Collections.singletonList(
                Arrays.asList(alias.get(BEGIN_ITER), EXP, alias.get(END_ITER))
        )));
        rules.put(CONST, new ArrayList<>(Collections.singletonList(
                Arrays.asList(alias.get(BEGIN_CONST), EXP, alias.get(END_CONST))
        )));

        rules.putAll(generateGrammarFromRegex(alias.get(CNAME), CNAME));

        rules.putAll(generateGrammarFromRegex(alias.get(NNAME), NNAME));

        for (String key : rules.keySet()) {
            System.out.println(key + ":" + rules.get(key));
        }
    }

    private Map<String, List<List<String>>> generateGrammarFromRegex(String regex, String startingNonterm) { //TODO: implement
        Map<String, List<List<String>>> regularGrammar = new HashMap<>();
        return regularGrammar;
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
                alias.replace(key, value);
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
