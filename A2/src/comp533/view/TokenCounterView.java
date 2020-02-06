package comp533.view;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.beans.PropertyChangeEvent;

public class TokenCounterView extends AMapReduceTracer {

    public void printNotification(PropertyChangeEvent event) {
        this.tracePropertyChange(event);
    }

    @Override
    public String toString() {
        return AMapReduceTracer.VIEW;
    }
}
