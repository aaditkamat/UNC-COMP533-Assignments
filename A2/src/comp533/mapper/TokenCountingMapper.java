package comp533.mapper;

import comp533.keyvalue.KeyValue;

public class TokenCountingMapper implements Mapper<String, Integer>{
    @Override
    public KeyValue<String, Integer> map(String stringArg) {
        return new KeyValue<>(stringArg, 1);
    }
}
