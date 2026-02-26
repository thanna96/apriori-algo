import java.util.Set;
import java.util.HashSet;

public class Itemset {

    private final Set<Integer> items = new HashSet<>();
    private int supportCount;

    public Itemset() {
    }

    public Itemset(Set<Integer> items) {
        this.items.addAll(items);
    }

    public Set<Integer> getItems() {
        return items;
    }

    public int getSupportCount() {
        return supportCount;
    }

    public void setSupportCount(int supportCount) {
        this.supportCount = supportCount;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
