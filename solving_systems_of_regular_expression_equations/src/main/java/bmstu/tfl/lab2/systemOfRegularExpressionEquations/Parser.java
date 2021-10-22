package bmstu.tfl.lab2.systemOfRegularExpressionEquations;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private static final String EXPRESSION_REGEX = "^[a-z]$";
    private static final String WHITESPACE_REGEX = "\\s";
    private static final String VARIABLE_REGEX = "^[A-Z]$";

    private final String equation;

    public Parser(String equation) {
        this.equation = equation;
    }

    protected void parseCoefficients(HashMap<Character, ArrayList<String>> variables, int i, int end) {
        while (i != end) {
            i = parseCoefficient(variables, i);
        }
        HashMap<Character, Boolean> usedVariables = new HashMap<>();
        for (char var: variables.keySet()) {
            if (usedVariables.containsKey(var)) {
                Equation.throwInvalidEquationError(equation);
            }
            usedVariables.put(var, true);
        }
    }

    private int parseCoefficient(HashMap<Character, ArrayList<String>> variables, int i) {
        int j = parseRegex(i);
        String varString = equation.substring(j, j + 1);
        if (!varString.matches(VARIABLE_REGEX)) {
            throw new Error("Invalid variable declaration: " + equation);
        }
        char var = equation.charAt(j);
        Equation.usedVariables.put(var, true);
        ArrayList<String> coefficients = new ArrayList<>();
        coefficients.add(equation.substring(i, j++));
        variables.put(var, coefficients);
        j = skipWhitespaces(j);
        return presenceCharacter('+', j);
    }

    protected int parseRegex(int i) {
        if (equation.charAt(i) == '(') {
            i++;
            i = parseExpression(i);
            i = skipWhitespaces(i);
            i = presenceCharacter('|', i);
            i = parseAltRegex(i);
            i = presenceCharacter(')', i);
        } else {
            i = parseExpression(i);
        }
        return i;
    }

    private int parseAltRegex(int i) {
        i = parseExpression(i);
        i = skipWhitespaces(i);
        if (equation.charAt(i) == '|') {
            i++;
            i = skipWhitespaces(i);
            i = parseAltRegex(i);
        }
        return i;
    }

    private int parseExpression(int i) {
        return parseCharactersByRegex(i, EXPRESSION_REGEX);
    }

    protected int presenceCharacter(char c, int i) {
        if (equation.charAt(i) != c) {
            Equation.throwInvalidEquationError(equation);
        }
        i++;
        return skipWhitespaces(i);
    }

    private int parseCharactersByRegex(int i, String regex) {
        while (i < equation.length() && equation.substring(i, i + 1).matches(regex)) {
            i++;
        }
        return i;
    }

    protected int skipWhitespaces( int i) {
        i = parseCharactersByRegex(i, WHITESPACE_REGEX);
        if (i == equation.length()) {
            Equation.throwInvalidEquationError(equation);
        }
        return i;
    }
}
