package comp533.mapper;

import comp533.keyvalue.KeyValue;

public interface Mapper<K, V> {
    public KeyValue<K, V> map(String stringArg);
}
