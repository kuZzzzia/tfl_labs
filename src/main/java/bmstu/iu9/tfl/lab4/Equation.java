package bmstu.iu9.tfl.lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Equation {
    private final String var;
    private final Map<String, List<String>> variables;
    private List<String> regex;

    public Equation(String var, Map<String, List<String>> variables, List<String> regex) {
        this.var = var;
        this.variables = variables;
        this.regex = regex;
    }

    protected void reduceVariableFromRightSide() {
        List<String> coefficients = variables.get(var);
        if (coefficients != null) {
            String coefficient = makeCoefficient(coefficients);
            variables.remove(var);
            for (List<String> var : variables.values()) {
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

    private static String makeCoefficient(List<String> coefficients) { //TODO: check size == 0
        if (coefficients.size() == 1) {
            return coefficients.get(0);
        }
        return "(" + String.join("|", coefficients) + ")";
    }

    public void substituteVariableInEquation(Equation equation) {
        String varName = equation.getVar();
        List<String> coefficients = variables.get(varName);
        if (coefficients != null) {
            String coefficient = makeCoefficient(coefficients);
            variables.remove(varName);
            for (String substVarName: equation.variables.keySet()) {
                String newCoefficient = coefficient + makeCoefficient(equation.variables.get(substVarName));
                List<String> substVarCoefficients = variables.get(substVarName);
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

    protected void setRegex(String regex) {
        this.regex = new ArrayList<>();
        this.regex.add(regex);
    }

    protected void addRegex(String regex) {
        this.regex.add(regex);
    }

    protected String getVar() {
        return var;
    }

    protected String getRegex() {
        if (this.regex.size() == 1) {
            return this.regex.get(0);
        }
        return "(" + String.join("|", this.regex) + ")";
    }

    protected String getAns() {
        return String.join("|", this.regex);
    }
}
