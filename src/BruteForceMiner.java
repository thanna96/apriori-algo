import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BruteForceMiner {
    public static class MiningResult {
        private final List<Itemset> frequentItemsets;
        private final Map<Itemset, Integer> supportCounts;

        public MiningResult(List<Itemset> frequentItemsets, Map<Itemset, Integer> supportCounts) {
            this.frequentItemsets = frequentItemsets;
            this.supportCounts = supportCounts;
        }

        public List<Itemset> getFrequentItemsets() {
            return frequentItemsets;
        }

        public Map<Itemset, Integer> getSupportCounts() {
            return supportCounts;
        }
    }

    public MiningResult mine(TransactionDB db, double minSupport) {
        int txCount = db.getTransactionCount();
        int minCount = (int) Math.ceil(minSupport * txCount);

        System.out.println("\n=== Brute Force Intermediate Summary ===");
        System.out.println("Minimum support count threshold = " + minCount + " out of " + txCount);

        List<Integer> activeItems = db.getActiveItemsSorted();
        List<Itemset> allFrequent = new ArrayList<>();
        Map<Itemset, Integer> supportCounts = new HashMap<>();

        for (int k = 1; k <= activeItems.size(); k++) {
            List<Itemset> candidates = new ArrayList<>();
            buildCombinations(activeItems, k, 0, new int[k], 0, candidates);

            int frequentAtK = 0;
            for (Itemset candidate : candidates) {
                int count = db.countSupport(candidate);
                if (count >= minCount) {
                    supportCounts.put(candidate, count);
                    allFrequent.add(candidate);
                    frequentAtK++;
                }
            }

            System.out.printf("  k=%d -> candidates=%d frequent=%d%n", k, candidates.size(), frequentAtK);
            if (frequentAtK == 0) {
                break;
            }
        }

        Collections.sort(allFrequent, (x, y) -> {
            if (x.size() != y.size()) {
                return Integer.compare(x.size(), y.size());
            }
            int[] a = x.getItems();
            int[] b = y.getItems();
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]) {
                    return Integer.compare(a[i], b[i]);
                }
            }
            return 0;
        });

        return new MiningResult(allFrequent, supportCounts);
    }

    private void buildCombinations(
            List<Integer> items,
            int k,
            int start,
            int[] current,
            int depth,
            List<Itemset> out) {

        if (depth == k) {
            out.add(new Itemset(current));
            return;
        }

        for (int i = start; i <= items.size() - (k - depth); i++) {
            current[depth] = items.get(i);
            buildCombinations(items, k, i + 1, current, depth + 1, out);
        }
    }
}
