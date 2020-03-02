package comp533.reducer;

import comp533.keyvalue.KeyValue;

import java.util.List;
import java.util.Map;

public interface Reducer<K, V> {
    public Map<K, V> reduce(List<KeyValue<K, V>> argList);
}
