package bmstu.tfl.lab2.systemOfRegularExpressionEquations;

import java.util.ArrayList;
import java.util.HashMap;

import bmstu.tfl.lab2.Parser;

public class RegexEquationParser extends Parser {
    private static final String ERROR_MESSAGE = "Invalid equation declaration: ";

    public RegexEquationParser(String equation) {
        super(equation);
    }

    protected void parseCoefficients(HashMap<Character, ArrayList<String>> variables, int i, int end, String err) {
        while (i != end) {
            i = parseCoefficient(variables, i, err);
        }
        HashMap<Character, Boolean> usedVariables = new HashMap<>();
        for (char var: variables.keySet()) {
            if (usedVariables.containsKey(var)) {
                throw new Error(ERROR_MESSAGE + getData());
            }
            usedVariables.put(var, true);
        }
    }

    private int parseCoefficient(HashMap<Character, ArrayList<String>> variables, int i, String err) {
        int j = parseRegex(i, err);
        String varString = getData().substring(j, j + 1);
        if (!varString.matches(Parser.CAPITAL_LETTERS_REGEX)) {
            throw new Error("Invalid variable declaration: " + getData());
        }
        char var = getData().charAt(j);
        Equation.usedVariables.put(var, true);
        ArrayList<String> coefficients = new ArrayList<>();
        coefficients.add(getData().substring(i, j++));
        variables.put(var, coefficients);
        j = skipWhitespaces(j, err);
        return presenceCharacter('+', j, err);
    }

    protected int parseRegex(int i, String err) {
        if (getData().charAt(i) == '(') {
            i++;
            i = parseExpression(i);
            i = skipWhitespaces(i, err);
            i = presenceCharacter('|', i, err);
            i = parseAltRegex(i, err);
            i = presenceCharacter(')', i, err);
        } else {
            i = parseExpression(i);
        }
        return i;
    }

    private int parseAltRegex(int i, String err) {
        i = parseExpression(i);
        i = skipWhitespaces(i, err);
        if (getData().charAt(i) == '|') {
            i++;
            i = skipWhitespaces(i, err);
            i = parseAltRegex(i, err);
        }
        return i;
    }

    private int parseExpression(int i) {
        return parseCharactersByRegex(i, Parser.LOWERCASE_LETTERS_REGEX);
    }

}
