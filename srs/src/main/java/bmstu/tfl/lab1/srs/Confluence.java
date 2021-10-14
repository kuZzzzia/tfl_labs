package bmstu.tfl.lab1.srs;

import java.io.IOException;

public class Confluence {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar ./target/tfl_lab_1_srs-1.0-SNAPSHOT.jar path/to/test/from/resources");
        } else {
            try {
                SRSReader data = new SRSReader(args[0]);
                matchConfluence(data.getLeftSideOfRules());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void matchConfluence(String[] rules) {
        for (int i = 0; i < rules.length; i++) {
            for (int j = 0; j < rules.length; j++) {
                if (i == j && checkConfluence(rules[i])) {
                    System.out.println("System is probably not confluent");

                    System.out.println("Rule number " + i + ": "+ data.get(i) + "has equal prefix and suffix");
                    return;
                } else if (!rules[i].equals(rules[j])) {
                    if (checkConfluence(rules[i], rules[j])) {
                        System.out.println("System is probably not confluent");

                        System.out.println("Rule number " + i + ": " + data.get(i) + "has prefix equal to suffix of rule number: " + data.get(j));
                        return;
                    } else if (checkConfluence(rules[j], rules[i])) {
                        System.out.println("System is probably not confluent");

                        System.out.println("Rule number " + i + ": " + data.get(i) + "has prefix equal to suffix of rule number: " + data.get(j));
                        return;
                    }
                }
            }
        }
        System.out.println("System is confluent");
    }

    public static boolean checkConfluence(String rule) {
        return true;
    }

    public static boolean checkConfluence(String firstRule, String secondRule) {
        return true;
    }

}
