package bmstu.iu9.tfl.lab5;

import java.io.IOException;
import java.util.*;

public class Grammar extends Reader {
    private static final String RULE = "RULE";
    private static final String EXP = "EXP";
    private static final String NTERM = "NTERM";
    private static final String ALT = "ALT";
    private static final String NEXTALT = "NEXTALT";
    private static final String ITER = "ITER";
    private static final String CONST = "CONST";

    private final Map<String, List<List<String>>> rules;



    public Grammar(String path) throws IOException {
        super(path);

        rules = new HashMap<>();
        rules.put(RULE, new ArrayList<>(Collections.singletonList(Arrays.asList(NTERM, EXP)))); //TODO: change
        rules.put(EXP, new ArrayList<>(Arrays.asList(
                Collections.singletonList(ALT),
                Arrays.asList(ITER, EXP),
                Arrays.asList(NTERM, EXP),
                Arrays.asList(CONST, EXP),
                Arrays.asList(EXP, EXP),
                new ArrayList<>())
        ));
        rules.put(NTERM, new ArrayList<>(Collections.singletonList(Arrays.asList(NNAME))));
        rules.put(ALT, new ArrayList<>(Collections.singletonList(Arrays.asList(EXP, NEXTALT))));
        rules.put(NEXTALT, new ArrayList<>(Arrays.asList(
                Arrays.asList(EXP, NEXTALT),
                Arrays.asList(EXP, NEXTALT)
        )));
        rules.put(ITER, new ArrayList<>(Collections.singletonList(Arrays.asList(EXP))));
        rules.put(CONST, new ArrayList<>(Collections.singletonList(Arrays.asList(EXP))));


//        parseRules(super.getData());
    }

}
