package bmstu.tfl.lab1.trs;

import javafx.util.Pair;

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
        if (term.length() == 0) {
            throw new Error("Empty term");
        }
        Pair<Boolean, Integer> args = Unification.checkExistenceOfConstructor(term.charAt(0));
        if (args.getKey()) {
            name = term.charAt(0);
            if (args.getValue() == 0) {
                type = TermType.CONSTANT;
                if (term.length() != 1) {
                    throw new Error("Invalid constructor usage" + term);
                }
            } else {
                type = TermType.CONSTRUCTOR;
                String[] argsCandidates = getTermArguments(term, args.getValue());
                arguments = new Term[args.getValue()];
                for (int i = 0; i < argsCandidates.length; i++) {
                    arguments[i] = new Term(argsCandidates[i]);
                }
            }
        } else if (Unification.checkExistenceOfVariable(String.valueOf(term.charAt(0)))) {
            name = term.charAt(0);
            type = TermType.VARIABLE;
        } else {
            throw new Error("No declared constructors or variables found: " + term);
        }
    }

    private String[] getTermArguments(String term, int amountOfArguments) {
        String[] args = new String[amountOfArguments];
        int count = 0;
        if (term.indexOf('(') == 1 && term.lastIndexOf(')') == term.length() - 1) {
            StringBuilder argument = new StringBuilder();
            int balancingBracket = 0;
            for (int i = 2; i < term.length() - 1; i++) {
                if (term.charAt(i) == ',') {
                    if (balancingBracket == 0) {
                        if (argument.length() == 0) {
                            throw new Error("Empty argument: " + term);
                        } else {
                            args[count++] = argument.toString().trim();
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
                args[count++] = argument.toString().trim();
            }
            if (count != amountOfArguments) {
                throw new Error("Invalid amount of constructor arguments: " + term);
            }
            return args;
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
            for (Term t : getArguments()) {
                stringBuilder.append(t.toString());
                stringBuilder.append(", ");
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            stringBuilder.append(')');
        }
        return stringBuilder.toString();
    }

}
