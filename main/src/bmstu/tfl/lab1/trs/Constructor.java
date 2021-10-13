package bmstu.tfl.lab1.trs;


public class Constructor {
    private final char name;
    private final int argumentsAmount;

    public Constructor(String input) throws Error {
        name = parseName(input);
        argumentsAmount = parseArgumentsAmount(input);
    }

    private static char parseName(String input) throws Error {
        final int nameEndingIndex = input.indexOf('(');
        if (nameEndingIndex != 1) {
            throw new Error("Invalid constructor declaration: " + input);
        }
        char nameCandidate = input.charAt(0);
        if (!((nameCandidate >= 'a' && nameCandidate <= 'z') || (nameCandidate >= 'A' && nameCandidate <= 'Z'))) {
            throw new Error("Invalid constructor name: " + input);
        }
        return nameCandidate;
    }

    private static int parseArgumentsAmount(String input) throws Error {
        final int argumentsAmountStartingIndex = input.indexOf('(') + 1;
        if (input.indexOf(')') != input.length() - 1) {
            throw new Error("Invalid constructor declaration: " + input);
        }
        int argumentsAmountCandidate;
        try {
            argumentsAmountCandidate = Integer.parseInt(input.substring(argumentsAmountStartingIndex, input.length() - 1));
        } catch (NumberFormatException ignored) {
            throw new Error("Constructor must have the amount of arguments in brackets: " + input);
        }
        return argumentsAmountCandidate;
    }

    public char getName() {
        return this.name;
    }

    public int getArgumentsAmount() {
        return this.argumentsAmount;
    }
}
