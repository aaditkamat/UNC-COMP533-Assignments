package comp533.view;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TokenCounterView extends AMapReduceTracer implements View, PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        this.tracePropertyChange(event);
    }


    @Override
    public String toString() {
        return AMapReduceTracer.VIEW;
    }
}
