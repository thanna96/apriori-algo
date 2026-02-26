import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RuleGenerator {

    public List<Rule> generateQualifiedRules(
            List<Itemset> frequentItemsets,
            Map<Itemset, Integer> supportCounts,
            TransactionDB db,
            double minSupport,
            double minConfidence) {

        List<Rule> rules = new ArrayList<>();
        int totalTransactions = db.getTransactionCount();

        for (Itemset fullSet : frequentItemsets) {
            if (fullSet.size() < 2) {
                continue;
            }

            int fullSupportCount = getSupportCount(fullSet, supportCounts, db);
            double fullSupport = fullSupportCount / (double) totalTransactions;
            if (fullSupport < minSupport) {
                continue;
            }

            int n = fullSet.size();
            int subsetMaskMax = (1 << n) - 1;
            int[] items = fullSet.getItems();

            for (int mask = 1; mask < subsetMaskMax; mask++) {
                if (mask == subsetMaskMax) {
                    continue;
                }
                if (mask == 0) {
                    continue;
                }

                int lhsSize = Integer.bitCount(mask);
                if (lhsSize == 0 || lhsSize == n) {
                    continue;
                }

                int[] lhsArr = new int[lhsSize];
                int[] rhsArr = new int[n - lhsSize];
                int li = 0;
                int ri = 0;
                for (int i = 0; i < n; i++) {
                    if (((mask >> i) & 1) == 1) {
                        lhsArr[li++] = items[i];
                    } else {
                        rhsArr[ri++] = items[i];
                    }
                }

                Itemset lhs = new Itemset(lhsArr);
                Itemset rhs = new Itemset(rhsArr);
                int lhsSupportCount = getSupportCount(lhs, supportCounts, db);
                if (lhsSupportCount == 0) {
                    continue;
                }
                double confidence = fullSupportCount / (double) lhsSupportCount;

                if (confidence >= minConfidence) {
                    rules.add(new Rule(lhs, rhs, fullSupportCount, fullSupport, confidence));
                }
            }
        }

        Collections.sort(rules, (a, b) -> {
            int cmp = a.canonicalString(db.getIdToName()).compareTo(b.canonicalString(db.getIdToName()));
            if (cmp != 0) {
                return cmp;
            }
            return Double.compare(b.getConfidence(), a.getConfidence());
        });

        Set<Rule> unique = new LinkedHashSet<>(rules);
        return new ArrayList<>(unique);
    }

    private int getSupportCount(Itemset itemset, Map<Itemset, Integer> supportCounts, TransactionDB db) {
        Integer count = supportCounts.get(itemset);
        if (count != null) {
            return count;
        }
        int computed = db.countSupport(itemset);
        supportCounts.put(itemset, computed);
        return computed;
    }
}
