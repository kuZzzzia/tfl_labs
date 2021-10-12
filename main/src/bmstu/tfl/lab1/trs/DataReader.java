package bmstu.tfl.lab1.trs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class DataReader {
    private String[] data;

    public DataReader(String path) throws IOException {
        data = null;
        readFromFile(path);
    }

    public void readFromFile(String path) throws Error, IOException {
        String filename;
        try {
            filename = Objects.requireNonNull(getClass().getClassLoader().getResource(path)).getFile();
        } catch (NullPointerException ignored) {
            throw new Error("File wasn't found");
        }

        File file = new File(filename);

        String input = new String(Files.readAllBytes(file.toPath()));
        final String separator = "\n";
        String[] dataCandidate = input.split(separator);
        Stream<String> streamFromData = Arrays.stream(dataCandidate);
        dataCandidate = streamFromData.filter(s -> s.length() != 0).toArray(String[]::new);

        if (dataCandidate.length == 4) {
            set(dataCandidate);
        } else {
            throw new Error("Invalid input data");
        }
    }

    private void set(String[] data) {
        this.data = new String[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    private String[] getItems(int i) {
        Stream<String> constructorsCandidates = Stream.of(data[i].substring(data[i].indexOf('=') + 1).trim().split(","));
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
        return data[i].substring(data[i].indexOf('=') + 1).trim();
    }

    public String getFirstTerm() {
        return getTerm(2);
    }

    public String getSecondTerm() {
        return getTerm(3);
    }
}
