package coupledsims;

import gradingTools.comp533s20.assignment4.Assignment4Suite;
import util.trace.Tracer;

public class RunA4Tests {

	public static void main(String[] args) {
		Tracer.showInfo(true);
		Assignment4Suite.main(args);
		Assignment4Suite.setProcessTimeOut(100);
	}

}
