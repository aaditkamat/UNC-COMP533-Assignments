import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public class TokenCounterModel extends AMapReduceTracer{
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private String inputString;
    private Map<String, Integer> result;

    public TokenCounterModel() {
        this.inputString = null;
        this.result = new HashMap<String, Integer>();
    }

    public String getInputString() {
        return this.inputString;
    }

    private Map<String, Integer> formMap(String[] tokens) {
        Map<String, Integer> map = new HashMap<>();
        for (String token: tokens) {
            if (!map.containsKey(token)) {
                map.put(token, 1);
            } else {
                int value = map.get(token);
                map.put(token, value + 1);
            }
        }
        return map;
    }

    private void updateResultMVC(TokenCounterView view) {
        Map<String, Integer> oldResult = this.result;
        String[] tokens = this.inputString.split(" ");
        this.result = this.formMap(tokens);
        PropertyChangeEvent updateResultEvent = new PropertyChangeEvent(this, "Result",
                oldResult, this.result);
        view.printNotification(updateResultEvent);
        this.pcs.firePropertyChange(updateResultEvent);
    }

    public void updateResultMapReduce(String inputString) {
        String[] tokens = inputString.split(" ");
        TokenCountingMapper mappedKeyValue = new TokenCountingMapper(tokens);
        mappedKeyValue.printMapper();
        Map<String, Integer> resultMap = this.formMap(tokens);
        TokenCountingReducer reducer = new TokenCountingReducer(mappedKeyValue, resultMap);
        reducer.printReducer();
    }

    public void setInputString(String newInputString, TokenCounterView view) {
        String oldInputString = this.inputString;
        this.inputString = newInputString;
        PropertyChangeEvent updateInputStringEvent = new PropertyChangeEvent(this, "InputString",
                oldInputString, newInputString);
        view.printNotification(updateInputStringEvent);
        this.pcs.firePropertyChange(updateInputStringEvent);
        this.updateResultMVC(view);
        this.updateResultMapReduce(newInputString);
    }

    public Map<String, Integer> getResult() {
        return this.result;
    }

    public void addPropertyChangeListener(PropertyChangeListener aListener) {
        this.pcs.addPropertyChangeListener(aListener);
    }

    @Override
    public String toString() {
        return AMapReduceTracer.MODEL;
    }
}
