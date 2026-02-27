import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssociationRules {

    private final TransactionDB db;
    private final RuleGenerator ruleGenerator;

    public AssociationRules(TransactionDB db) {
        this.db = db;
        this.ruleGenerator = new RuleGenerator();
    }

    // Generates all rules that pass minConfidence from the provided frequent itemsets.
    public List<Rule> generateRules(List<Itemset> frequentItemsets, double minConfidence) {
        Map<Itemset, Integer> supportCounts = new HashMap<>();
        for (Itemset itemset : frequentItemsets) {
            supportCounts.put(itemset, db.countSupport(itemset));
        }
        return ruleGenerator.generateQualifiedRules(frequentItemsets, supportCounts, db, 0.0, minConfidence);
    }
}
