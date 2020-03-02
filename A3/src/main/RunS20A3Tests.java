package main;

import gradingTools.comp533s20.assignment3.Assignment3Suite;
import trace.grader.basics.GraderBasicsTraceUtility;
import util.trace.Tracer;

public class RunS20A3Tests {
    public static void main(String[] args) {
        Tracer.showInfo(true);
        GraderBasicsTraceUtility.setBufferTracedMessages(false);
        Tracer.setMaxTraces(8000);
        Assignment3Suite.main(args);
    }
}