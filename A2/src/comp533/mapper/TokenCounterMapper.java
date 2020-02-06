package comp533.mapper;

import comp533.keyvalue.KeyValue;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public class TokenCounterMapper extends AMapReduceTracer implements Mapper<String, Integer>{
    @Override
    public KeyValue<String, Integer> map(String stringArg) {
        KeyValue<String, Integer> keyValue = new KeyValue<>(stringArg, 1);
        this.traceMap(stringArg, keyValue);
        return keyValue;
    }
}
