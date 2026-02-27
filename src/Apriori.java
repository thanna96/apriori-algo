import java.util.List;

public class Apriori {

    private final TransactionDB db;
    private final AprioriMiner miner;

    public Apriori(TransactionDB db) {
        this.db = db;
        this.miner = new AprioriMiner();
    }

    // Backward-compatible wrapper around AprioriMiner.
    public List<Itemset> run(double minSupport) {
        AprioriMiner.MiningResult result = miner.mine(db, minSupport);
        return result.getFrequentItemsets();
    }
}
