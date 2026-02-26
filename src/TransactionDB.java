import java.util.List;
import java.util.ArrayList;

public class TransactionDB {

    // You can store transactions as lists of item IDs (integers)
    private final List<List<Integer>> transactions = new ArrayList<>();

    public TransactionDB() {
    }

    // Optionally: constructor that loads from a file path like "data/transactions.txt"
    public TransactionDB(String filePath) {
        // TODO: read file and populate transactions list
    }

    public List<List<Integer>> getTransactions() {
        return transactions;
    }
}
