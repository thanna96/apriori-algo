import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AprioriMiner {
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

        System.out.println("\n=== Apriori Intermediate Steps ===");
        System.out.println("Minimum support count threshold = " + minCount + " out of " + txCount);

        Map<Itemset, Integer> allSupportCounts = new HashMap<>();
        List<Itemset> allFrequent = new ArrayList<>();

        Map<Itemset, Integer> l1Counts = generateL1(db, minCount);
        printLevel("L1", l1Counts, db);

        List<Itemset> lk = sortedItemsets(l1Counts.keySet());
        allFrequent.addAll(lk);
        allSupportCounts.putAll(l1Counts);

        int k = 2;
        while (!lk.isEmpty()) {
            List<Itemset> ck = generateCandidates(lk, k);
            if (ck.isEmpty()) {
                break;
            }
            Map<Itemset, Integer> ckCounts = countCandidates(db, ck);
            printLevel("C" + k, ckCounts, db);

            Map<Itemset, Integer> lkCounts = filterFrequent(ckCounts, minCount);
            printLevel("L" + k, lkCounts, db);

            if (lkCounts.isEmpty()) {
                break;
            }

            lk = sortedItemsets(lkCounts.keySet());
            allFrequent.addAll(lk);
            allSupportCounts.putAll(lkCounts);
            k++;
        }

        return new MiningResult(allFrequent, allSupportCounts);
    }

    private Map<Itemset, Integer> generateL1(TransactionDB db, int minCount) {
        Map<Itemset, Integer> freq = new LinkedHashMap<>();
        for (Integer item : db.getActiveItemsSorted()) {
            Itemset singleton = new Itemset(new int[] { item });
            int count = db.countSupport(singleton);
            if (count >= minCount) {
                freq.put(singleton, count);
            }
        }
        return freq;
    }

    private List<Itemset> generateCandidates(List<Itemset> prevFrequent, int k) {
        Set<Itemset> candidates = new LinkedHashSet<>();
        Set<Itemset> prevSet = new LinkedHashSet<>(prevFrequent);

        for (int i = 0; i < prevFrequent.size(); i++) {
            for (int j = i + 1; j < prevFrequent.size(); j++) {
                Itemset a = prevFrequent.get(i);
                Itemset b = prevFrequent.get(j);
                if (!canJoin(a, b, k - 1)) {
                    continue;
                }
                Itemset joined = join(a, b);
                if (joined.size() != k) {
                    continue;
                }
                if (allSubsetsFrequent(joined, prevSet)) {
                    candidates.add(joined);
                }
            }
        }

        List<Itemset> out = new ArrayList<>(candidates);
        sortItemsetsInPlace(out);
        return out;
    }

    private boolean canJoin(Itemset a, Itemset b, int prevSize) {
        if (a.size() != prevSize || b.size() != prevSize) {
            return false;
        }
        for (int i = 0; i < prevSize - 1; i++) {
            if (a.get(i) != b.get(i)) {
                return false;
            }
        }
        return a.get(prevSize - 1) != b.get(prevSize - 1);
    }

    private Itemset join(Itemset a, Itemset b) {
        int[] ai = a.getItems();
        int[] bi = b.getItems();
        int[] combined = new int[ai.length + 1];
        System.arraycopy(ai, 0, combined, 0, ai.length);
        combined[ai.length] = bi[bi.length - 1];
        return new Itemset(combined);
    }

    private boolean allSubsetsFrequent(Itemset candidate, Set<Itemset> prevFrequent) {
        for (Itemset subset : candidate.kMinusOneSubsets()) {
            if (!prevFrequent.contains(subset)) {
                return false;
            }
        }
        return true;
    }

    private Map<Itemset, Integer> countCandidates(TransactionDB db, List<Itemset> candidates) {
        Map<Itemset, Integer> counts = new LinkedHashMap<>();
        for (Itemset candidate : candidates) {
            counts.put(candidate, db.countSupport(candidate));
        }
        return counts;
    }

    private Map<Itemset, Integer> filterFrequent(Map<Itemset, Integer> counts, int minCount) {
        Map<Itemset, Integer> frequent = new LinkedHashMap<>();
        for (Map.Entry<Itemset, Integer> e : counts.entrySet()) {
            if (e.getValue() >= minCount) {
                frequent.put(e.getKey(), e.getValue());
            }
        }
        return frequent;
    }

    private void printLevel(String levelName, Map<Itemset, Integer> counts, TransactionDB db) {
        System.out.println("\n" + levelName + ":");
        if (counts.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        List<Itemset> ordered = sortedItemsets(counts.keySet());
        int txCount = db.getTransactionCount();
        for (Itemset itemset : ordered) {
            int c = counts.get(itemset);
            double support = c / (double) txCount;
            System.out.printf("  %-40s count=%2d support=%.3f%n", itemset.toNameString(db.getIdToName()), c, support);
        }
    }

    private List<Itemset> sortedItemsets(Set<Itemset> itemsets) {
        List<Itemset> list = new ArrayList<>(itemsets);
        sortItemsetsInPlace(list);
        return list;
    }

    private void sortItemsetsInPlace(List<Itemset> list) {
        Collections.sort(list, (x, y) -> {
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
    }
}
