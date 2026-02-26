import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final String DATA_DIR = "data";
    private static final String DICTIONARY_FILE = DATA_DIR + "/item_dictionary.txt";

    public static void main(String[] args) {
        printHowToRun();

        try (Scanner scanner = new Scanner(System.in)) {
            String dbChoice = promptDbChoice(scanner);
            double minSupport = promptRange(scanner, "Enter minimum support (0.0 to 1.0): ");
            double minConfidence = promptRange(scanner, "Enter minimum confidence (0.0 to 1.0): ");

            String dbPath = DATA_DIR + "/" + dbChoice + ".txt";
            TransactionDB db = TransactionDB.load(DICTIONARY_FILE, dbPath);

            printHeader(dbChoice, minSupport, minConfidence, db);
            printTransactions(db);

            AprioriMiner aprioriMiner = new AprioriMiner();
            long aStart = TimerUtil.nowNanos();
            AprioriMiner.MiningResult aprioriResult = aprioriMiner.mine(db, minSupport);
            RuleGenerator ruleGenerator = new RuleGenerator();
            List<Rule> aprioriRules = ruleGenerator.generateQualifiedRules(
                    aprioriResult.getFrequentItemsets(),
                    aprioriResult.getSupportCounts(),
                    db,
                    minSupport,
                    minConfidence);
            long aEnd = TimerUtil.nowNanos();

            printFrequentItemsets("Apriori Frequent Itemsets", aprioriResult.getFrequentItemsets(), aprioriResult.getSupportCounts(), db);
            printRules("Apriori Qualified Association Rules", aprioriRules, db.getIdToName());

            BruteForceMiner bruteForceMiner = new BruteForceMiner();
            long bStart = TimerUtil.nowNanos();
            BruteForceMiner.MiningResult bruteResult = bruteForceMiner.mine(db, minSupport);
            List<Rule> bruteRules = ruleGenerator.generateQualifiedRules(
                    bruteResult.getFrequentItemsets(),
                    bruteResult.getSupportCounts(),
                    db,
                    minSupport,
                    minConfidence);
            long bEnd = TimerUtil.nowNanos();

            printRules("Brute Force Qualified Association Rules", bruteRules, db.getIdToName());

            System.out.println("\n=== Timing (wall-clock) ===");
            System.out.printf("Apriori:     %.3f ms%n", TimerUtil.elapsedMillis(aStart, aEnd));
            System.out.printf("Brute force: %.3f ms%n", TimerUtil.elapsedMillis(bStart, bEnd));

            compareRuleSets(aprioriRules, bruteRules, db.getIdToName());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Input/Data error: " + e.getMessage());
        }
    }

    private static void printHowToRun() {
        System.out.println("================ APRIORI MIDTERM PROJECT ================");
        System.out.println("How to run:");
        System.out.println("  1) Compile: javac src/*.java");
        System.out.println("  2) Run:     java -cp src Main");
        System.out.println("  3) Input:   database name (db1..db5), min support, min confidence");
        System.out.println("Example input values:");
        System.out.println("  db1");
        System.out.println("  0.20");
        System.out.println("  0.60");
        System.out.println("==========================================================\n");
    }

    private static String promptDbChoice(Scanner scanner) {
        while (true) {
            System.out.print("Choose database file (db1..db5): ");
            String value = scanner.nextLine().trim().toLowerCase();
            if (value.matches("db[1-5]")) {
                return value;
            }
            System.out.println("Invalid choice. Please type db1, db2, db3, db4, or db5.");
        }
    }

    private static double promptRange(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();
            try {
                double parsed = Double.parseDouble(value);
                if (parsed >= 0.0 && parsed <= 1.0) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
                // Continue prompting
            }
            System.out.println("Invalid number. Enter a value between 0.0 and 1.0.");
        }
    }

    private static void printHeader(String dbFile, double minSupport, double minConfidence, TransactionDB db) {
        System.out.println("\n=== Run Configuration ===");
        System.out.println("Database file: " + dbFile + ".txt");
        System.out.printf("Minimum support: %.3f%n", minSupport);
        System.out.printf("Minimum confidence: %.3f%n", minConfidence);
        System.out.println("Transaction count: " + db.getTransactionCount());
        System.out.println("Item dictionary count: " + db.getDictionarySize());
    }

    private static void printTransactions(TransactionDB db) {
        System.out.println("\n=== Input Transactions ===");
        List<java.util.BitSet> txs = db.getTransactions();
        for (int i = 0; i < txs.size(); i++) {
            System.out.printf("T%-2d %s%n", i + 1, db.transactionToNameString(txs.get(i)));
        }
    }

    private static void printFrequentItemsets(String title, List<Itemset> itemsets, Map<Itemset, Integer> counts, TransactionDB db) {
        System.out.println("\n=== " + title + " ===");
        for (Itemset itemset : itemsets) {
            int count = counts.get(itemset);
            double support = count / (double) db.getTransactionCount();
            System.out.printf("  %-40s count=%2d support=%.3f%n", itemset.toNameString(db.getIdToName()), count, support);
        }
    }

    private static void printRules(String title, List<Rule> rules, Map<Integer, String> idToName) {
        System.out.println("\n=== " + title + " ===");
        if (rules.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        int idx = 1;
        for (Rule rule : rules) {
            System.out.printf("  R%-2d %-55s supportCount=%2d support=%.3f confidence=%.3f%n",
                    idx++,
                    rule.canonicalString(idToName),
                    rule.getSupportCount(),
                    rule.getSupport(),
                    rule.getConfidence());
        }
    }

    private static void compareRuleSets(List<Rule> aprioriRules, List<Rule> bruteRules, Map<Integer, String> idToName) {
        Set<Rule> aSet = new HashSet<>(aprioriRules);
        Set<Rule> bSet = new HashSet<>(bruteRules);

        Set<Rule> onlyA = new HashSet<>(aSet);
        onlyA.removeAll(bSet);

        Set<Rule> onlyB = new HashSet<>(bSet);
        onlyB.removeAll(aSet);

        System.out.println("\n=== Rule Set Comparison ===");
        if (onlyA.isEmpty() && onlyB.isEmpty()) {
            System.out.println("PASS: Apriori and brute force produced identical qualified rule sets.");
        } else {
            System.out.println("FAIL: Rule sets differ.");
        }
        System.out.println("Apriori rule count: " + aSet.size());
        System.out.println("Brute force rule count: " + bSet.size());

        if (!onlyA.isEmpty()) {
            System.out.println("Rules only in Apriori:");
            for (Rule r : sortRules(onlyA, idToName)) {
                System.out.println("  " + r.canonicalString(idToName));
            }
        }

        if (!onlyB.isEmpty()) {
            System.out.println("Rules only in Brute Force:");
            for (Rule r : sortRules(onlyB, idToName)) {
                System.out.println("  " + r.canonicalString(idToName));
            }
        }
    }

    private static List<Rule> sortRules(Set<Rule> rules, Map<Integer, String> idToName) {
        List<Rule> list = new ArrayList<>(rules);
        list.sort((r1, r2) -> r1.canonicalString(idToName).compareTo(r2.canonicalString(idToName)));
        return list;
    }
}
