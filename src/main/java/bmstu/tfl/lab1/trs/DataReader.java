package bmstu.tfl.lab1.trs;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DataReader {
    private String[] data;

    public DataReader(String path) throws IOException {
        data = null;
        readFromFile(path);
    }

    public void readFromFile(String path) throws Error, IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new Error("File wasn't found");
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        ArrayList<String> dataCandidate = new ArrayList<>();
        while((line = reader.readLine()) != null) {
            dataCandidate.add(line);
        }

        Stream<String> streamFromData = dataCandidate.stream();
        String[] data = streamFromData.filter(s -> s.length() != 0).toArray(String[]::new);

        if (data.length == 4) {
            set(data);
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
