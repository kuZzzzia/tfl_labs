package bmstu.iu9.tfl.lab2.systemOfRegularExpressionEquations;

import java.util.ArrayList;
import java.util.HashMap;

public class Equation {
    protected static final String ERROR_MESSAGE = "Invalid equation declaration: ";

    protected static HashMap<Character, Boolean> usedVariables = new HashMap<>();
    private static final HashMap<Character, Boolean> leftSideVariables = new HashMap<>();

    private char var;
    private final HashMap<Character, ArrayList<String>> variables;
    private ArrayList<String> regex;

    public Equation(String equation) throws Error {
        this.variables = new HashMap<>();
        RegexEquationParser parser = new RegexEquationParser(equation);
        int i = 0;
        i = parser.parseCapitalLetter(i, ERROR_MESSAGE);
        setVar(equation.charAt(0));
        checkVariableNotExists();
        i = parser.presenceCharacter('=', i, ERROR_MESSAGE);
        int lastPlusCharacterIndex = equation.lastIndexOf('+');
        String regex;
        if (lastPlusCharacterIndex == -1) {
            int j = parser.parseRegex(i, ERROR_MESSAGE);
            if (j != equation.length()) {
                throw new Error(ERROR_MESSAGE + equation);
            }
            regex = equation.substring(i, j);
        } else {
            lastPlusCharacterIndex = parser.skipWhitespaces(lastPlusCharacterIndex + 1, ERROR_MESSAGE);
            int end = parser.parseRegex(lastPlusCharacterIndex, ERROR_MESSAGE);
            if (end != equation.length()) {
                throw new Error(ERROR_MESSAGE + equation);
            }
            regex = equation.substring(lastPlusCharacterIndex, end);
            parser.parseCoefficients(variables, i, lastPlusCharacterIndex, ERROR_MESSAGE);
        }
        this.regex = new ArrayList<>();
        this.regex.add(regex);
    }

    public Equation(char var, HashMap<Character, ArrayList<String>> variables, ArrayList<String> regex) {
        this.var = var;
        this.variables = variables;
        this.regex = regex;
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


    protected static String concatRegexes(String prefix, String suffix) {
        if (prefix.length() == 1 || (prefix.charAt(0) == '(' && prefix.charAt(prefix.length() - 1) == ')')) {
            suffix = prefix + "*" + suffix;
        } else {
            suffix = "(" + prefix + ")*" + suffix;
        }
        return suffix;
    }

    private static String makeCoefficient(ArrayList<String> coefficients) {
        if (coefficients.size() == 1) {
            return coefficients.get(0);
        }
        return "(" + String.join("+", coefficients) + ")";
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

    private void setVar(char var) {
        this.var = var;
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
}
