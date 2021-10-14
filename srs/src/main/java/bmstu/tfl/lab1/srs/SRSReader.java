package bmstu.tfl.lab1.srs;

import bmstu.tfl.lab1.Reader;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class SRSReader extends Reader {
    private String[] leftSidesOfRules;

    public SRSReader(String path) throws Error, IOException {
        super(path);
        fillLeftSidesOfRules();
    }

    private void fillLeftSidesOfRules() {
        Stream<String> streamFromRules = Arrays.stream(super.getData());
        String[] leftSidesOfRules = streamFromRules.map(s -> s.substring(0, s.indexOf("->"))).toArray(String[]::new);
        System.arraycopy(leftSidesOfRules, 0, this.leftSidesOfRules, 0, leftSidesOfRules.length);
    }

    public String[] getLeftSideOfRules() {
        return this.leftSidesOfRules;
    }
}
