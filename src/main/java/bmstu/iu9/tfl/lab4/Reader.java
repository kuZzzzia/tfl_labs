package bmstu.iu9.tfl.lab4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Reader {
    private static final String FILE_NOT_FOUND_ERROR = "File wasn't found";
    private static final String EMPTY_FILE_ERROR = "Empty file";

    private String[] data;

    protected Reader(String path) throws IOException {
        data = null;
        readFromFile(path);
    }

    private void readFromFile(String path) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new Error(FILE_NOT_FOUND_ERROR);
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        List<String> dataLines = new ArrayList<>();
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
            throw new Error(EMPTY_FILE_ERROR);
        }
        this.data = new String[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public String[] getData() {
        return this.data;
    }
}
