package comp533.keyvalue;

import java.io.Serializable;

public interface KeyValue<K, V> extends Serializable {
    K getKey();
    V getValue();
}
