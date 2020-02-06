package comp533.partitioner;

public class PartitionerFactory {
    private static Partitioner partitioner;

    static {
        partitioner = new Partitioner();
    }

    public static Partitioner getPartitioner() {
        return PartitionerFactory.partitioner;
    }
}
