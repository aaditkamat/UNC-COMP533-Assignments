package main;

import gradingTools.comp533s20.assignment2.Assignment2Suite;
import trace.grader.basics.GraderBasicsTraceUtility;
import util.trace.Tracer;

public class RunS20A1Tests {
    public static void main(String[] args) {
        Tracer.showInfo(true);
        GraderBasicsTraceUtility.setBufferTracedMessages(false);
        Tracer.setMaxTraces(8000);
        Assignment2Suite.main(args);
    }
}