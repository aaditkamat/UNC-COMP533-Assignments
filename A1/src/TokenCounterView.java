import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.beans.PropertyChangeEvent;

public class TokenCounterView extends AMapReduceTracer {

    public void printNotification(PropertyChangeEvent event) {
        this.trace(event.toString());
    }

    @Override
    public String toString() {
        return AMapReduceTracer.VIEW;
    }
}
