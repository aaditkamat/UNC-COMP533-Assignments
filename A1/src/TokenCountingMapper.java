import gradingTools.comp533s19.assignment0.AMapReduceTracer;


import java.util.HashSet;
import java.util.Set;

public class TokenCountingMapper extends AMapReduceTracer {
    private Set<String> tokenSet;

    public TokenCountingMapper(String[] tokens) {
        this.tokenSet = new HashSet<>();
        for (String token: tokens) {
            tokenSet.add(token);
        }
    }

    public Set<String> getTokenSet() {
        return tokenSet;
    }

    public void printMapper() {
        for (String token: this.tokenSet) {
            this.traceMap(token, new KeyValue<String, Integer>(token, 1));
        }
    }
}
