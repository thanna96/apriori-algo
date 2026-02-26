public class Rule {

    private final Itemset antecedent;
    private final Itemset consequent;
    private final double support;
    private final double confidence;

    public Rule(Itemset antecedent, Itemset consequent, double support, double confidence) {
        this.antecedent = antecedent;
        this.consequent = consequent;
        this.support = support;
        this.confidence = confidence;
    }

    public Itemset getAntecedent() {
        return antecedent;
    }

    public Itemset getConsequent() {
        return consequent;
    }

    public double getSupport() {
        return support;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return antecedent + " => " + consequent +
                " (support=" + support + ", confidence=" + confidence + ")";
    }
}
