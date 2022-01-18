package bmstu.iu9.tfl.lab2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Reader {
    private String[] data;

    public Reader(String path) throws Error, IOException {
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
            dataCandidate.add(line.trim());
        }

        Stream<String> streamFromData = dataCandidate.stream();
        set(streamFromData.filter(s -> s.length() != 0).toArray(String[]::new));
    }

    protected void set(String[] data) throws Error {
        if (data.length == 0) {
            throw new Error("Empty file");
        }
        this.data = new String[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public String[] getData() {
        return this.data;
    }

    public String getData(int i) {
        return this.data[i];
    }
}
