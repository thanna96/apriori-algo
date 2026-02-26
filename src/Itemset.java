import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Itemset {
    private final int[] items; // always sorted ascending

    public Itemset(int[] values) {
        int[] copy = Arrays.copyOf(values, values.length);
        Arrays.sort(copy);
        this.items = copy;
    }

    public Itemset(List<Integer> values) {
        this(toIntArray(values));
    }

    private static int[] toIntArray(List<Integer> values) {
        int[] arr = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            arr[i] = values.get(i);
        }
        return arr;
    }

    public int size() {
        return items.length;
    }

    public int get(int index) {
        return items[index];
    }

    public int[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    public boolean contains(int item) {
        return Arrays.binarySearch(items, item) >= 0;
    }

    public List<Itemset> kMinusOneSubsets() {
        if (items.length <= 1) {
            return Collections.emptyList();
        }
        List<Itemset> subsets = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            int[] subset = new int[items.length - 1];
            int idx = 0;
            for (int j = 0; j < items.length; j++) {
                if (j != i) {
                    subset[idx++] = items[j];
                }
            }
            subsets.add(new Itemset(subset));
        }
        return subsets;
    }

    public String toNameString(Map<Integer, String> idToName) {
        List<String> names = new ArrayList<>();
        for (int id : items) {
            names.add(idToName.get(id));
        }
        return "{" + String.join(", ", names) + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Itemset)) {
            return false;
        }
        Itemset other = (Itemset) obj;
        return Arrays.equals(this.items, other.items);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(items);
    }

    @Override
    public String toString() {
        return Arrays.toString(items);
    }
}
