package comp533.partitioner;

public abstract class Partitioner<K,V> {
    public abstract int getPartition(K key, V value, int numOfPartitions);
}
