package coupledsims;

import gradingTools.comp533s20.assignment7.Assignment7Suite;
import util.trace.Tracer;

public class RunA7Tests {
	public static void main(String[] args) {
		Tracer.showInfo(true);
		Assignment7Suite.main(args);
		Assignment7Suite.setProcessTimeOut(60);
	}
}
