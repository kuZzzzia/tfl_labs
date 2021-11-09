package bmstu.iu9.tfl.lab3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleRightSide {
    List<String[]> rewritingVariants;
    boolean  regular;

    public RuleRightSide(String[] rewritingVariant) {
        rewritingVariants = new ArrayList<>(Collections.singleton(rewritingVariant));
    }

    protected void add(String[] newRightSide) {
        rewritingVariants.add(newRightSide);
    }
}
