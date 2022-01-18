package bmstu.tfl.lab1.trs;

import java.io.IOException;
import java.util.ArrayList;

public class Unification {
    private static Constructor[] constructors;
    private static String[] variables;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar ./trs/target/trs-1.0-SNAPSHOT.jar path/to/test/from/resources");
        } else {
            try {
                TRSReader data = new TRSReader(args[0]);
                setConstructors(data.getConstructors());
                setVariables(data.getVariables());
                Term firstTerm = new Term(data.getFirstTerm());
                Term secondTerm = new Term(data.getSecondTerm());
                ArrayList<String> firstTermSubstitutions = new ArrayList<>(), secondTermSubstitutions = new ArrayList<>();
                printUnification(unify(firstTerm, secondTerm, firstTermSubstitutions, secondTermSubstitutions), firstTerm, secondTerm, firstTermSubstitutions, secondTermSubstitutions);
            } catch (Error | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void setConstructors(String[] input) {
        constructors = new Constructor[input.length];
        for (int i = 0; i < constructors.length; i++) {
            constructors[i] = new Constructor(input[i]);
        }
    }

    private static void setVariables(String[] input) {
        variables = new String[input.length];
        System.arraycopy(input, 0, variables, 0, input.length);
    }


    protected static Constructor checkExistenceOfConstructor(char name) {
        for (Constructor c: constructors) {
            if (c.getName() == name) {
                return c;
            }
        }
        return null;
    }

    protected static boolean checkExistenceOfVariable(String name) {
        for (String s: variables) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static String unify(Term firstTerm, Term secondTerm, ArrayList<String> firstTermSubstitutions, ArrayList<String> secondTermSubstitutions) throws Error {
        if (firstTerm.getType() == TermType.VARIABLE) {
            if (secondTerm.getType() == TermType.VARIABLE) {
                if (secondTerm.getName() != firstTerm.getName()) {
                    secondTermSubstitutions.add(secondTerm + " = " + firstTerm);
                }
                return firstTerm.toString();
            }
            firstTermSubstitutions.add(firstTerm + " = " + secondTerm);
            return secondTerm.toString();
        } else if (secondTerm.getType() == TermType.VARIABLE) {
            secondTermSubstitutions.add(secondTerm + " = " + firstTerm);
            return firstTerm.toString();
        } else if (firstTerm.getName() == secondTerm.getName()) {
            if (firstTerm.getType() == TermType.CONSTANT) {
                return firstTerm.toString();
            } else {
                Term[] firstTermArguments = firstTerm.getArguments(), secondTermArguments = secondTerm.getArguments();
                StringBuilder unification = new StringBuilder(String.valueOf(firstTerm.getName()));
                unification.append('(');
                for (int i = 0; i < firstTermArguments.length; i++) {
                    unification.append(unify(firstTermArguments[i], secondTermArguments[i], firstTermSubstitutions, secondTermSubstitutions));
                    if (i != firstTermArguments.length - 1) {
                        unification.append(", ");
                    }
                }
                unification.append(')');
                return unification.toString();
            }
        } else {
            throw new Error("Unification is not possible");
        }
    }

    private static void printUnification(String answer, Term firstTerm, Term secondTerm, ArrayList<String> firstTermSubstitutions, ArrayList<String> secondTermSubstitutions) {
        printTermSubstitutions(firstTerm, firstTermSubstitutions);
        printTermSubstitutions(secondTerm, secondTermSubstitutions);
        System.out.println("Unification:");
        System.out.println("\t" + answer);
    }

    private static void printTermSubstitutions(Term term, ArrayList<String> termSubstitutions) {
        if (termSubstitutions.size() != 0) {
            System.out.println("Substitutions for " + ANSI_GREEN + term.toString() + ANSI_RESET + " term:");
            for (String s: termSubstitutions) {
                System.out.println("\t" + s);
            }
        }
    }
}
