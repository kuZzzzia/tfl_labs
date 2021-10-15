package bmstu.tfl.lab1.trs;


public class Constructor {
    private final char name;
    private final int argumentsAmount;

    public Constructor(String input) throws Error {
        final String pattern = "[a-zA-z]\\(\\d*\\)";
        if (!input.matches(pattern)) {
            throw new Error("Invalid constructor declaration: " + input);
        }
        name = input.charAt(0);
        argumentsAmount = parseArgumentsAmount(input.substring(2, input.length() - 1));
    }

    private static int parseArgumentsAmount(String input) throws Error {
        int argumentsAmountCandidate;
        try {
            argumentsAmountCandidate = Integer.parseInt(input);
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
