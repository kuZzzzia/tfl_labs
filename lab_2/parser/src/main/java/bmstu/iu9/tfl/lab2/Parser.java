package bmstu.iu9.tfl.lab2;

public class Parser {
    public static final String LOWERCASE_LETTERS_REGEX = "^[a-z]$";
    public static final String WHITESPACE_REGEX = "\\s";
    public static final String CAPITAL_LETTERS_REGEX = "^[A-Z]$";

    private final String data;

    public Parser(String data) {
        this.data = data;
    }

    protected String getData() {
        return this.data;
    }

    public int presenceCharacter(char c, int i, String err) {
        if (data.charAt(i) != c) {
            throw new Error(err + data);
        }
        i++;
        return skipWhitespaces(i, err);
    }

    public int presenceString(String s, int i, String err) {
        if (!data.substring(i).startsWith(s)) {
            throw new Error(err + data);
        }
        i += s.length();
        return skipWhitespaces(i, err);
    }

    public int parseCharactersByRegex(int i, String regex) {
        while (i < data.length() && data.substring(i, i + 1).matches(regex)) {
            i++;
        }
        return i;
    }

    public int parseCapitalLetter(int i, String err) throws Error {
        String varString = getData().substring(i, i + 1);
        if (!varString.matches(Parser.CAPITAL_LETTERS_REGEX)) {
            throw new Error(err + varString);
        }
        i++;
        return skipWhitespaces(i, err);
    }

    public int skipWhitespaces(int i, String err) {
        i = parseCharactersByRegex(i, WHITESPACE_REGEX);
        if (i == data.length()) {
            throw new Error(err + data);
        }
        return i;
    }
}
