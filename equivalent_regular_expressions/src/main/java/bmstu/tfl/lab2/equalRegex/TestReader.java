package bmstu.tfl.lab2.equalRegex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TestReader {
    private String data;

    public TestReader(String path) throws Error, IOException {
        data = null;
        readFromFile(path);
    }

    public void readFromFile(String path) throws Error, IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new Error("File " + path + " wasn't found");
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        StringBuilder input = new StringBuilder();
        while((line = reader.readLine()) != null) {
            input.append(line).append(" ");
        }

        set(input.toString().trim());
    }

    protected void set(String data) throws Error {
        if (data.length() == 0) {
            throw new Error("Empty file");
        }
        this.data = data;
    }

    protected String getData() {
        return this.data;
    }

}
