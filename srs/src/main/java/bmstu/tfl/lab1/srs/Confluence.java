package bmstu.tfl.lab1.srs;

import java.io.IOException;

public class Confluence {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar ./target/tfl_lab_1_srs-1.0-SNAPSHOT.jar path/to/test/from/resources");
        } else {
            try {
                SRSReader data = new SRSReader(args[0]);
                matchConfluence(data);
            } catch (Error | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void matchConfluence(SRSReader data) {
        String[] rules = data.getLeftSideOfRules();
        for (int i = 0; i < rules.length; i++) {
            if (rules[i].length() == 0) {
                throw new Error ("Wrong rule declaration: " + rules[i]);
            }
            for (int j = 0; j < rules.length; j++) {
                if (i == j) {
                    if (checkConfluence(rules[i])) {
                        printResult(i, data);
                        return;
                    }
                } else {
                    if (checkConfluence(rules[i], rules[j])) {
                        printResult(i, j, data);
                        return;
                    }
                }
            }
        }
        System.out.println("System is probably confluent");
    }

    public static boolean checkConfluence(String rule) {
        int[] pi = prefix(rule);
        return pi[pi.length - 1] != 0;
    }

    public static boolean checkConfluence(String firstRule, String secondRule) {
        for (int i = 0; i < firstRule.length() && i < secondRule.length(); i++) {
            int ind = KMPSubstr(firstRule.substring(0, i + 1), secondRule);
            if (ind + 1 == secondRule.length() - firstRule.substring(0, i).length()) {
                return true;
            }
        }
        return false;
    }

    public static int[] prefix(String s) {
        int[] pi = new int[s.length()];
        int t = 0;
        pi[0] = t;
        for (int i = 1; i < s.length(); i++) {
            while (t > 0 && s.charAt(t) != s.charAt(i)) {
                t = pi[t-1];
            }
            if (s.charAt(t) == s.charAt(i)) {
                t++;
            }
            pi[i] = t;
        }
        return pi;
    }

    public static int KMPSubstr(String s, String t) {
        int[] pi = prefix(s);
        int q = 0;
        int ans = -1;
        for (int k = 0; k < t.length(); k++) {
            while (q > 0 && s.charAt(q) != t.charAt(k)) {
                q = pi[q-1];
            }
            if (s.charAt(q) == t.charAt(k)) {
                q++;
            }
            if (q == s.length()) {
                q = 0;
                ans = k - s.length() + 1;
            }
        }
        return ans;
    }

    public static void printResult(int i, SRSReader data) {
        System.out.println("System is not confluent");
        System.out.println("Rule number " + i + ": " + ANSI_GREEN + data.getData(i) + ANSI_RESET + " has equal prefix and suffix");
    }

    public static void printResult(int i,int j, SRSReader data) {
        System.out.println("System is not confluent");
        System.out.println("Rule number " + i + ": " + ANSI_GREEN + data.getData(i) + ANSI_RESET + " has prefix equal to suffix of rule number " + j + ": " + ANSI_GREEN + data.getData(j) + ANSI_RESET);
    }

}
