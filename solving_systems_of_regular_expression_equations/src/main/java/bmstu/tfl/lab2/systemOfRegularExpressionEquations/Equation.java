package bmstu.tfl.lab2.systemOfRegularExpressionEquations;

import java.util.ArrayList;
import java.util.HashMap;

public class Equation {
    private static final String VARIABLE_REGEX = "^[A-Z]$";
    protected static HashMap<Character, Boolean> usedVariables = new HashMap<>();
    private static final HashMap<Character, Boolean> leftSideVariables = new HashMap<>();

    private char var;
    private final HashMap<Character, ArrayList<String>> variables;
    private ArrayList<String> regex;

    public Equation(String equation) throws Error {
        this.variables = new HashMap<>();
        Parser parser = new Parser(equation);
        int i = setVar(equation, parser);
        checkVariableNotExists();
        i = parser.presenceCharacter('=', i);
        int lastPlusCharacterIndex = equation.lastIndexOf('+');
        String regex;
        if (lastPlusCharacterIndex == -1) {
            int j = parser.parseRegex(i);
            if (j != equation.length()) {
                throwInvalidEquationError(equation);
            }
            regex = equation.substring(i, j);
        } else {
            lastPlusCharacterIndex = parser.skipWhitespaces(lastPlusCharacterIndex + 1);
            int end = parser.parseRegex(lastPlusCharacterIndex);
            if (end != equation.length()) {
                throwInvalidEquationError(equation);
            }
            regex = equation.substring(lastPlusCharacterIndex, end);
            parser.parseCoefficients(variables, i, lastPlusCharacterIndex);
        }
        this.regex = new ArrayList<>();
        this.regex.add(regex);
    }

    private void checkVariableNotExists() {
        char var = getVar();
        if (Equation.leftSideVariables.containsKey(var)) {
            throw new Error("Equation with " + var + "already exists");
        }
        Equation.leftSideVariables.put(var, true);
    }

    protected static void checkAllVariablesUsed() {
        for (char var: Equation.usedVariables.keySet()) {
            if (!Equation.leftSideVariables.containsKey(var)) {
                throw new Error("Not enough equations. The system cannot be solved");
            }
        }
    }

    protected void reduceVariableFromRightSide() {
        char varName = this.getVar();
        ArrayList<String> coefficients = variables.get(varName);
        if (coefficients != null) {
            String coefficient = makeCoefficient(coefficients);
            variables.remove(varName);
            for (ArrayList<String> var : variables.values()) {
                String variableCoefficient = makeCoefficient(var);
                variableCoefficient = concatRegexes(coefficient, variableCoefficient);
                var.clear();
                var.add(variableCoefficient);
            }
            String regex = getRegex();
            regex = concatRegexes(coefficient, regex);
            setRegex(regex);
        }
    }

    private static String makeCoefficient(ArrayList<String> coefficients) {
        if (coefficients.size() == 1) {
            return coefficients.get(0);
        }
        return "(" + String.join("+", coefficients) + ")";
    }

    private static String concatRegexes(String prefix, String suffix) {
        if (prefix.length() == 1 || (prefix.charAt(0) == '(' && prefix.charAt(prefix.length() - 1) == ')')) {
            suffix = prefix + "*" + suffix;
        } else {
            suffix = "(" + prefix + ")*" + suffix;
        }
        return suffix;
    }

    public void substituteVariableInEquation(Equation equation) {
        char varName = equation.getVar();
        ArrayList<String> coefficients = variables.get(varName);
        if (coefficients != null) {
            String coefficient = makeCoefficient(coefficients);
            variables.remove(varName);
            for (char substVarName: equation.variables.keySet()) {
                String newCoefficient = coefficient + makeCoefficient(equation.variables.get(substVarName));
                ArrayList<String> substVarCoefficients = variables.get(substVarName);
                if (substVarCoefficients == null) {
                    ArrayList<String> newCoefficientArrayList = new ArrayList<>();
                    newCoefficientArrayList.add(newCoefficient);
                    variables.put(substVarName, newCoefficientArrayList);
                } else {
                    substVarCoefficients.add(newCoefficient);
                }
            }
            String regexComponent = coefficient + equation.getRegex();
            addRegex(regexComponent);
        }
    }

    private int setVar(final String equation, Parser parser) throws Error {
        int i = 0;
        String varString = equation.substring(i, i + 1);
        if (!varString.matches(VARIABLE_REGEX)) {
            throw new Error("Invalid variable declaration: " + varString);
        }
        this.var = varString.charAt(i++);
        return parser.skipWhitespaces(i);
    }

    protected void setRegex(String regex) {
        this.regex = new ArrayList<>();
        this.regex.add(regex);
    }

    protected void addRegex(String regex) {
        this.regex.add(regex);
    }

    protected char getVar() {
        return this.var;
    }

    protected String getRegex() {
        if (this.regex.size() == 1) {
            return this.regex.get(0);
        }
        return "(" + String.join("+", this.regex) + ")";
    }

    protected String getAns() {
        return String.join("+", this.regex);
    }

    protected static void throwInvalidEquationError(final String equation) {
        throw new Error("Invalid equation declaration: " + equation);
    }
}
