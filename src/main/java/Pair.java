public record Pair<K, V>(K key, V value) implements Comparable<Pair<K, V>> {

    @Override
    public int compareTo(Pair<K, V> other) {
        if (key instanceof Comparable) {
            Comparable<K> comparableKey = (Comparable<K>) key;
            return comparableKey.compareTo(other.key());
        }
        throw new UnsupportedOperationException("Key does not implement Comparable.");
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}