package comp533.reducer;

import comp533.keyvalue.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TokenCountingReducer implements Reducer<String, Integer>{
    public Map<String, Integer> reduce(List<KeyValue<String, Integer>> argList) {
        Map<String, Integer> map = new HashMap<>();
        for (KeyValue<String, Integer> keyValue: argList) {
            String key = keyValue.getKey();
            Integer value = keyValue.getValue();
            if (map.containsKey(key)) {
                Integer sum = map.get(key);
                sum += value;
                map.put(key, sum);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
}
