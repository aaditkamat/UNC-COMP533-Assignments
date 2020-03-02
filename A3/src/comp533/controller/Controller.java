package comp533.controller;

import comp533.mvc.RemoteTokenCounter;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

public interface Controller {
    String LABEL = AMapReduceTracer.CONTROLLER;
    void getUserInput(RemoteTokenCounter counter);
}
