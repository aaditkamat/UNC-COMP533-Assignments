package coupledsims;

import gradingTools.comp533s20.assignment6.Assignment6Suite;
import util.trace.Tracer;

public class RunA6Tests {
	public static void main(String[] args) {
		Tracer.showInfo(true);
		Assignment6Suite.main(args);
		Assignment6Suite.setProcessTimeOut(60);
	}
}
