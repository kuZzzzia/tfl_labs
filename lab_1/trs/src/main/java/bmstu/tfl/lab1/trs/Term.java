package bmstu.tfl.lab1.trs;

import java.util.ArrayList;

enum TermType {
    CONSTRUCTOR,
    CONSTANT,
    VARIABLE
}

public class Term {
    private final char name;
    private Term[] arguments;
    private final TermType type;

    public Term(String term) throws Error {
        arguments = null;
        if (term.length() == 0) {
            throw new Error("Empty term");
        }
        Constructor constructor = Unification.checkExistenceOfConstructor(term.charAt(0));
        if (constructor != null) {
            name = term.charAt(0);
            if (constructor.getArgumentsAmount() == 0) {
                type = TermType.CONSTANT;
                if (term.length() != 1) {
                    throw new Error("Invalid constructor: " + term);
                }
            } else {
                type = TermType.CONSTRUCTOR;
                String[] argsCandidates = getTermArguments(term, constructor.getArgumentsAmount());
                arguments = new Term[constructor.getArgumentsAmount()];
                for (int i = 0; i < argsCandidates.length; i++) {
                    arguments[i] = new Term(argsCandidates[i]);
                }
            }
        } else if (Unification.checkExistenceOfVariable(String.valueOf(term.charAt(0)))) {
            name = term.charAt(0);
            type = TermType.VARIABLE;
            if (term.length() != 1) {
                throw new Error("Invalid variable: " + term);
            }
        } else {
            throw new Error("No declared constructors or variables found: " + term);
        }
    }

    private String[] getTermArguments(String term, int amountOfArguments) {
        ArrayList<String> argsList = new ArrayList<>();
        if (term.indexOf('(') == 1 && term.lastIndexOf(')') == term.length() - 1) {
            StringBuilder argument = new StringBuilder();
            int balancingBracket = 0;
            for (int i = 2; i < term.length() - 1; i++) {
                if (term.charAt(i) == ',') {
                    if (balancingBracket == 0) {
                        if (argument.length() == 0) {
                            throw new Error("Empty argument: " + term);
                        } else {
                            argsList.add(argument.toString().trim());
                            argument.setLength(0);
                        }
                    } else {
                        argument.append(term.charAt(i));
                    }
                } else {
                    if (term.charAt(i) == '(') {
                        balancingBracket++;
                    } else if (term.charAt(i) == ')') {
                        balancingBracket--;
                    }
                    argument.append(term.charAt(i));
                }
            }
            if (argument.length() == 0) {
                throw new Error("Empty argument: " + term);
            } else if (balancingBracket != 0) {
                throw new Error("Invalid sequence of brackets in term: " + term);
            } else {
                argsList.add(argument.toString().trim());
            }
            if (argsList.size() != amountOfArguments) {
                throw new Error("Invalid amount of constructor arguments: " + term);
            }
            return argsList.toArray(new String[0]);
        }
        throw new Error("Invalid constructor usage: " + term);
    }

    public TermType getType() {
        return this.type;
    }

    public char getName() {
        return this.name;
    }

    public Term[] getArguments() {
        return this.arguments;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(getName()));
        if (getType() == TermType.CONSTRUCTOR) {
            stringBuilder.append('(');
            if (getArguments() != null) {
                for (Term t : getArguments()) {
                    stringBuilder.append(t.toString());
                    stringBuilder.append(", ");
                }
                stringBuilder.setLength(stringBuilder.length() - 2);
            }
            stringBuilder.append(')');
        }
        return stringBuilder.toString();
    }

}
