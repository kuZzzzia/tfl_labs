package bmstu.iu9.tfl.lab3;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Reader {
    private String[] data;

    protected Reader(String path) throws IOException {
        data = null;
        readFromFile(path);
    }

    private void readFromFile(String path) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new Error("File wasn't found");
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        ArrayList<String> dataLines = new ArrayList<>();
        while((line = reader.readLine()) != null) {
            dataLines.add(line.trim());
        }

        set(
                dataLines
                        .stream()
                        .filter(
                                s -> !s.isEmpty()
                        )
                        .toArray(
                                String[]::new
                        )
        );
    }

    private void set(String[] data) {
        if (data.length == 0) {
            throw new Error("Empty file");
        }
        this.data = new String[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public String[] getData() {
        return this.data;
    }
}

