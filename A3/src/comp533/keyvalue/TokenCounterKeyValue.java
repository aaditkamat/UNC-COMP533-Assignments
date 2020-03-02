package comp533.keyvalue;

public class TokenCounterKeyValue<K, V> implements KeyValue<K, V> {
    private K key;
    private V value;

    public TokenCounterKeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + this.key + "," + this.value + ")";
    }
}
