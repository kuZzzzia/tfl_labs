package bmstu.tfl.lab1.trs;

import bmstu.tfl.lab1.Reader;

import java.io.*;
import java.util.stream.Stream;

public class TRSReader extends Reader {

    public TRSReader(String path) throws Error, IOException {
        super(path);
        if (getData().length != 4) {
            throw new Error("Invalid input data");
        }
    }

    private String[] getItems(int i) {
        Stream<String> constructorsCandidates = Stream.of(getData()[i].substring(getData()[i].indexOf('=') + 1).trim().split(","));
        return constructorsCandidates.map(String::trim).filter(s -> s.length() != 0).toArray(String[]::new);
    }

    public String[] getConstructors() {
        return getItems(0);
    }

    public String[] getVariables() throws Error {
        String[] variables = getItems(1);
        for (String s: variables) {
            if (s.length() != 1) {
                throw new Error("Invalid variable declaration: " + s);
            }
        }
        return variables;
    }

    private String getTerm(int i) {
        return getData()[i].substring(getData()[i].indexOf('=') + 1).trim();
    }

    public String getFirstTerm() {
        return getTerm(2);
    }

    public String getSecondTerm() {
        return getTerm(3);
    }
}
