import java.util.ArrayList;
import java.util.List;

class HashEntry<K, V> {
    private K key;
    private V value;

    public HashEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}

class ExtendableHashTable<K, V> {
    private List<List<HashEntry<K, V>>> buckets;
    private int globalDepth;

    public ExtendableHashTable(int initialSize) {
        this.buckets = new ArrayList<>(initialSize);
        this.globalDepth = 1;

        for (int i = 0; i < initialSize; i++) {
            buckets.add(new ArrayList<>());
        }
    }

    public void insert(K key, V value) {
        int hash = key.hashCode();
        int bucketIndex = hash % buckets.size();

        if (buckets.get(bucketIndex).size() >= 2) {
            // If a bucket is full, split it and double the table size if necessary
            if (buckets.get(bucketIndex).size() >= Math.pow(2, globalDepth)) {
                // Double the table size and rehash
                List<List<HashEntry<K, V>>> newBuckets = new ArrayList<>(buckets);
                for (int i = 0; i < buckets.size(); i++) {
                    newBuckets.add(new ArrayList<>());
                }
                buckets = newBuckets;
                globalDepth++;

                // Rehash entries
                for (List<HashEntry<K, V>> oldBucket : buckets) {
                    for (HashEntry<K, V> entry : oldBucket) {
                        int newBucketIndex = entry.getKey().hashCode() % buckets.size();
                        buckets.get(newBucketIndex).add(entry);
                    }
                }
                bucketIndex = hash % buckets.size();
            }
        }

        buckets.get(bucketIndex).add(new HashEntry<>(key, value));
    }

    public V get(K key) {
        int hash = key.hashCode();
        int bucketIndex = hash % buckets.size();

        for (HashEntry<K, V> entry : buckets.get(bucketIndex)) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }

        return null;
    }
}