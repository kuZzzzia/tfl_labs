package bmstu.iu9.tfl.lab2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RegexTestsGenerator {
    private static final String TEST_PATH = "test_";
    private static final int TESTS_AMOUNT = 10;
    private static final String TEST_FILE_EXTENSION = ".txt";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar " +
                    "./equivalent_regular_expressions/target/equivalent_regular_expressions-1.0-SNAPSHOT.jar " + //TODO: update
                    "path/without/extension/where/to/create/file");
            System.exit(-1);
        }
        try {
            generateTests(args[0]);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void generateTests(String path) throws IOException {
        int length = 100;
        for (int i = 0; i < TESTS_AMOUNT; i++) {
            String filepath = path + "/" + TEST_PATH + i + TEST_FILE_EXTENSION;
            File testFile = new File(filepath);
            FileWriter writer = new FileWriter(testFile);
            if (i % 2 == 0) {
                writer.write(getRandomString(length));
            } else {
                writer.write(getHexNumberString(length));
            }
            writer.flush();
            writer.close();
            if (i % 2 == 1) {
                length *= 10;
            }
        }
    }

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++){
            int number = random.nextInt(4);
            long result;
            switch (number) {
                case 0: //A-Z
                    result = Math.round(Math.random()*25+65);
                    stringBuilder.append((char)result);
                    break;
                case 1: //a-z
                    result = Math.round(Math.random()*25+97);
                    stringBuilder.append((char)result);
                    break;
                case 2: //integer
                    int integer = (new Random().nextInt()) & Integer.MAX_VALUE;
                    stringBuilder.append(integer);
                    break;
                case 3:
                    int len = new Random().nextInt(length - i);
                    stringBuilder.append(getRandomHexNumber(len));
                    i += len - 1;
                    break;
            }
        }
        return stringBuilder.toString();
    }

    public static String getHexNumberString(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++){
            int number = random.nextInt(3);
            long result;
            switch (number) {
                case 0:
                    if (i == 0) {
                        stringBuilder.append(0);
                        i++;
                    }
                    result = Math.round(Math.random()*25+65);
                    stringBuilder.append((char)result);
                    break;
                case 1:
                    stringBuilder.append(new Random().nextInt(10));
                    break;
                case 2:
                    stringBuilder.append('h');
                    break;
            }
        }
        stringBuilder.append('h');
        return stringBuilder.toString();
    }


    public static String getRandomHexNumber(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++){
            int number = random.nextInt(2);
            long result;
            switch (number) {
                case 0:
                    if (i == 0) {
                        stringBuilder.append(0);
                        i++;
                    }
                    result = Math.round(Math.random()*25+65);
                    stringBuilder.append((char)result);
                    break;
                case 1:
                    stringBuilder.append(new Random().nextInt(10));
                    break;
            }
        }
        stringBuilder.append('h');
        return stringBuilder.toString();
    }
}
