package bmstu.tfl.lab1.trs;

import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Unification {
    private static Constructor[] constructors;
    private static String[] variables;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -cp ./main/src bmstu.tfl.lab1.trs.Unification path/to/test");
        } else {
            try {
                DataReader data = new DataReader(args[0]);
                setConstructors(data.getConstructors());
                setVariables(data.getVariables());
                Term firstTerm = new Term(data.getFirstTerm());
                Term secondTerm = new Term(data.getSecondTerm());
                printUnification(unify(firstTerm, secondTerm));
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


    public static Pair<Boolean, Integer> checkExistenceOfConstructor(char name) throws Error {
        for (Constructor c: constructors) {
            if (c.getName() == name) {
                return new Pair<>(Boolean.TRUE, c.getArgumentsAmount());
            }
        }
        return new Pair<>(Boolean.FALSE, 0);
    }

    public static boolean checkExistenceOfVariable(String name) throws Error {
        for (String s: variables) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static Pair<String, String[]> unify(Term firstTerm, Term secondTerm) throws Error {
        if (firstTerm.getType() == TermType.VARIABLE) {
            if (secondTerm.getType() == TermType.VARIABLE) {
                if (secondTerm.getName() != firstTerm.getName()) {
                    return new Pair<>(firstTerm.toString(), new String[]{firstTerm + " = " + secondTerm});
                }
                return new Pair<>(firstTerm.toString(), null);
            }
            return new Pair<>(secondTerm.toString(), new String[]{firstTerm + " = " + secondTerm});
        } else if (secondTerm.getType() == TermType.VARIABLE) {
            return new Pair<>(firstTerm.toString(), new String[]{secondTerm + " = " + firstTerm});
        } else if (firstTerm.getName() == secondTerm.getName()) {
            if (firstTerm.getType() == TermType.CONSTANT) {
                return new Pair<>(firstTerm.toString(), null);
            } else {
                Term[] firstTermArguments = firstTerm.getArguments(), secondTermArguments = secondTerm.getArguments();
                StringBuilder unification = new StringBuilder(String.valueOf(firstTerm.getName()));
                ArrayList<String> substitutions = new ArrayList<>();
                unification.append('(');
                for (int i = 0; i < firstTermArguments.length; i++) {
                    Pair<String, String[]> unificator = unify(firstTermArguments[i], secondTermArguments[i]);
                    unification.append(unificator.getKey());
                    if (i != firstTermArguments.length - 1) {
                        unification.append(", ");
                    }
                    if (unificator.getValue() != null) {
                        substitutions.addAll(Arrays.asList(unificator.getValue()));
                    }
                }
                unification.append(')');
                String[] subst = new String[substitutions.size()];
                for (int i = 0; i < substitutions.size(); i++)
                    subst[i] = substitutions.get(i);
                return new Pair<>(unification.toString(), subst);
            }
        } else {
            throw new Error("Unification is not possible");
        }
    }

    private static void printUnification(Pair<String, String[]> answer) {
        if (answer.getValue() != null) {
            System.out.println("Substitutions");
            for (String s: answer.getValue()) {
                System.out.println(s);
            }
        }
        System.out.println("Unification");
        System.out.println(answer.getKey());
    }
}
