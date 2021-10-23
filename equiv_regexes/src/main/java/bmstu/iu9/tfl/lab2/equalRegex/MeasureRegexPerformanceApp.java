package bmstu.iu9.tfl.lab2.equalRegex;

import java.io.*;
import java.util.regex.Matcher;

import java.util.regex.Pattern;

public class MeasureRegexPerformanceApp {
    private static final String ACADEMIC_REGEX = "((((0[A-F])|[1-9])[0-9A-F]*)|0)h";
    private static final String NEGATION_REGEX = "(((([^1-9A-Za-z][^0-9a-zG-Z])|[^0A-Za-z])[^a-zG-z]*)|[^1-9A-Za-z])[^0-9A-Za-gi-z]";
    private static final String LAZY_KLEENE_ITERATION_REGEX = "((((0[A-F])|[1-9])[0-9A-F]*?)|0)h";

    private static final int ACADEMIC_REGEX_INDEX = 0;
    private static final int NEGATION_REGEX_INDEX = 1;
    private static final int LAZY_KLEENE_ITERATION_REGEX_INDEX = 2;

    private static final String TEST_PATH = "test_";
    private static final int TESTS_AMOUNT = 10;
    private static final String TEST_FILE_EXTENSION = ".txt";
    private static final String CSV_FILE_EXTENSION = ".csv";

    private static final String CSV_FIRST_COLUMN_NAME = "Test number";
    private static final String CSV_SECOND_COLUMN_NAME = "Academic";
    private static final String CSV_THIRD_COLUMN_NAME = "Negative";
    private static final String CSV_FORTH_COLUMN_NAME = "Lazy Kleene iteration";


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar " +
                    "./equivalent_regular_expressions/target/equivalent_regular_expressions-1.0-SNAPSHOT.jar " +
                    "path/without/extension/where/to/create/file");
            System.exit(-1);
        }
        Long[][] measures = createArraysForResults();
        try {
            runTests(measures);
            writeDataToCSVFile(measures, args[0]);
        } catch (IOException | Error | NullPointerException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static Long[][] createArraysForResults() {
        Long[][] arrays  = new Long[3][];
        for (int i = 0; i < 3; i++) {
            arrays[i] = new Long[TESTS_AMOUNT];
        }
        return arrays;
    }

    private static void runTests(Long[][] measures) throws IOException {
        Pattern academicRegexPattern = compileRegex(ACADEMIC_REGEX);
        Pattern negationRegexPattern = compileRegex(NEGATION_REGEX);
        Pattern lazyKleeneIterationRegexPattern = compileRegex(LAZY_KLEENE_ITERATION_REGEX);

        for (int i = 0; i < TESTS_AMOUNT; i++) {
            String filepath = TEST_PATH + i + TEST_FILE_EXTENSION;
            TestReader reader = new TestReader(filepath);
            String test = reader.getData();

            int res1 = measureRegexPerformanceOnTest(academicRegexPattern, test, i, measures[ACADEMIC_REGEX_INDEX]);
            int res2 = measureRegexPerformanceOnTest(negationRegexPattern, test, i, measures[NEGATION_REGEX_INDEX]);
            int res3 = measureRegexPerformanceOnTest(lazyKleeneIterationRegexPattern, test, i, measures[LAZY_KLEENE_ITERATION_REGEX_INDEX]);
            if (res1 != res2 || res1 != res3) {
                throw new Error("Different results of matching regexes to test " + i);
            }
        }

    }

    private static int measureRegexPerformanceOnTest(Pattern regexPattern, final String test, final int index, Long[] measures) {
        Matcher regexMatcher = createRegexMatcherToTest(regexPattern, test);
        long start = System.currentTimeMillis();
        int count = 0;
        while (regexMatcher.find()) {
            count++;
        }
        long duration = System.currentTimeMillis() - start;
        measures[index] = duration;
        return count;
    }

    private static Pattern compileRegex(String regex) {
        return Pattern.compile(regex);
    }

    private static Matcher createRegexMatcherToTest(Pattern pattern, String test) {
        return pattern.matcher(test);
    }

    private static void writeDataToCSVFile(Long[][] measures, String path) throws IOException {
        File csvOutputFile = new File(path + CSV_FILE_EXTENSION);
        FileWriter writer = new FileWriter(csvOutputFile);
        writer.write(
                buildCSVString(
                        new String[]{
                                CSV_FIRST_COLUMN_NAME,
                                CSV_SECOND_COLUMN_NAME,
                                CSV_THIRD_COLUMN_NAME,
                                CSV_FORTH_COLUMN_NAME
                        }
                        )
        );
        for (int i = 0; i < TESTS_AMOUNT; i++) {
            writer.write(
                    buildCSVString(
                            new String[]{
                                    String.valueOf(i + 1),
                                    String.valueOf(measures[ACADEMIC_REGEX_INDEX][i]),
                                    String.valueOf(measures[NEGATION_REGEX_INDEX][i]),
                                    String.valueOf(measures[LAZY_KLEENE_ITERATION_REGEX_INDEX][i])
                            }
                    )
            );
        }
        writer.flush();
        writer.close();
    }
    
    private static String buildCSVString(String[] data) {
        return String.join(",", data) + '\n';
    }
}
