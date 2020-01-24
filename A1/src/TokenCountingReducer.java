import gradingTools.comp533s19.assignment0.AMapReduceTracer;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TokenCountingReducer extends AMapReduceTracer {
    private TokenCountingMapper mappedKeyValue;
    private Map<String, Integer> resultMap;

    public TokenCountingReducer(TokenCountingMapper mappedKeyValue, Map<String,Integer> resultMap) {
        this.mappedKeyValue = mappedKeyValue;
        this.resultMap = resultMap;
    }

    public void printReducer() {
        List<KeyValue<String, Integer>> keyValues = new ArrayList<>();
        for (String token: mappedKeyValue.getTokenSet()) {
            keyValues.add(new KeyValue<>(token, 1));
        }
        this.traceReduce(keyValues, resultMap);
    }
}
