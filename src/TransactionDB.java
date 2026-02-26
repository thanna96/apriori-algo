import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransactionDB {
    private final Map<String, Integer> nameToId;
    private final Map<Integer, String> idToName;
    private final List<BitSet> transactions;

    private TransactionDB(Map<String, Integer> nameToId, Map<Integer, String> idToName, List<BitSet> transactions) {
        this.nameToId = nameToId;
        this.idToName = idToName;
        this.transactions = transactions;
    }

    public static TransactionDB load(String dictionaryPath, String dbPath) throws IOException {
        Map<String, Integer> n2i = new LinkedHashMap<>();
        Map<Integer, String> i2n = new LinkedHashMap<>();

        List<String> dictLines = Files.readAllLines(Paths.get(dictionaryPath));
        int id = 0;
        for (String raw : dictLines) {
            String item = raw.trim();
            if (item.isEmpty()) {
                continue;
            }
            if (n2i.containsKey(item)) {
                throw new IllegalArgumentException("Duplicate dictionary item: " + item);
            }
            n2i.put(item, id);
            i2n.put(id, item);
            id++;
        }

        List<BitSet> txs = new ArrayList<>();
        List<String> txLines = Files.readAllLines(Paths.get(dbPath));
        for (int lineNo = 0; lineNo < txLines.size(); lineNo++) {
            String line = txLines.get(lineNo).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] tokens = line.split(",");
            BitSet bs = new BitSet(n2i.size());
            for (String token : tokens) {
                String name = token.trim();
                if (!n2i.containsKey(name)) {
                    throw new IllegalArgumentException("Unknown item '" + name + "' in " + dbPath + " line " + (lineNo + 1));
                }
                int itemId = n2i.get(name);
                if (bs.get(itemId)) {
                    throw new IllegalArgumentException("Duplicate item in transaction at " + dbPath + " line " + (lineNo + 1) + ": " + name);
                }
                bs.set(itemId);
            }
            txs.add(bs);
        }

        return new TransactionDB(n2i, i2n, txs);
    }

    public int getTransactionCount() {
        return transactions.size();
    }

    public int getDictionarySize() {
        return idToName.size();
    }

    public Map<Integer, String> getIdToName() {
        return Collections.unmodifiableMap(idToName);
    }

    public List<BitSet> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public String transactionToNameString(BitSet tx) {
        List<String> names = new ArrayList<>();
        for (int item = tx.nextSetBit(0); item >= 0; item = tx.nextSetBit(item + 1)) {
            names.add(idToName.get(item));
        }
        return "{" + String.join(", ", names) + "}";
    }

    public BitSet toBitSet(Itemset itemset) {
        BitSet target = new BitSet(getDictionarySize());
        for (int item : itemset.getItems()) {
            target.set(item);
        }
        return target;
    }

    public boolean transactionContains(BitSet tx, Itemset itemset) {
        for (int item : itemset.getItems()) {
            if (!tx.get(item)) {
                return false;
            }
        }
        return true;
    }

    public int countSupport(Itemset itemset) {
        int count = 0;
        for (BitSet tx : transactions) {
            if (transactionContains(tx, itemset)) {
                count++;
            }
        }
        return count;
    }

    public double support(Itemset itemset) {
        if (transactions.isEmpty()) {
            return 0.0;
        }
        return countSupport(itemset) / (double) transactions.size();
    }

    public List<Integer> getActiveItemsSorted() {
        BitSet active = new BitSet(getDictionarySize());
        for (BitSet tx : transactions) {
            active.or(tx);
        }
        List<Integer> out = new ArrayList<>();
        for (int item = active.nextSetBit(0); item >= 0; item = active.nextSetBit(item + 1)) {
            out.add(item);
        }
        return out;
    }
}
