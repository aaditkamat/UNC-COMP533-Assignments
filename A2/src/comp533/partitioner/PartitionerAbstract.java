package comp533.partitioner;

public abstract class PartitionerAbstract<K,V> {
    public abstract int getPartition(K key, V value, int numOfPartitions);
}
