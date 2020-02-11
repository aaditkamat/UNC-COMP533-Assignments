package comp533.partitioner;

import util.trace.Tracer;

public class TokenCounterPartitioner extends Partitioner<String, Integer> {
    @Override
    public int getPartition(String key, Integer value, int numOfPartitions) {
        //Tracer.userMessage("Key is: " + key);
        char keyFirstChar = key.charAt(0);
        if (!Character.isLetter(keyFirstChar)) {
            return 0;
        }
        char lowercaseKeyFirstChar = Character.toLowerCase(keyFirstChar);
        int offset = lowercaseKeyFirstChar - 'a';
        int maxPartitionSize = (int) Math.ceil((('z' - 'a' + 1) * 1.0) / (numOfPartitions * 1.0));
        return (int) Math.floor(((lowercaseKeyFirstChar - 'a' + 1) * 1.0) / (maxPartitionSize * 1.0));
    }
}
