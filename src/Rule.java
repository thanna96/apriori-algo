import java.util.Map;

public class Rule {
    private final Itemset lhs;
    private final Itemset rhs;
    private final int supportCount;
    private final double support;
    private final double confidence;

    public Rule(Itemset lhs, Itemset rhs, int supportCount, double support, double confidence) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.supportCount = supportCount;
        this.support = support;
        this.confidence = confidence;
    }

    public Itemset getLhs() {
        return lhs;
    }

    public Itemset getRhs() {
        return rhs;
    }

    public int getSupportCount() {
        return supportCount;
    }

    public double getSupport() {
        return support;
    }

    public double getConfidence() {
        return confidence;
    }

    public String canonicalString(Map<Integer, String> idToName) {
        return lhs.toNameString(idToName) + " -> " + rhs.toNameString(idToName);
    }

    @Override
    public String toString() {
        return lhs + " -> " + rhs + " (sup=" + support + ", conf=" + confidence + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rule)) {
            return false;
        }
        Rule other = (Rule) obj;
        return lhs.equals(other.lhs) && rhs.equals(other.rhs);
    }

    @Override
    public int hashCode() {
        int result = lhs.hashCode();
        result = 31 * result + rhs.hashCode();
        return result;
    }
}
