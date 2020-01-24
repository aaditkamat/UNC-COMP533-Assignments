import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.beans.PropertyChangeEvent;
import java.util.Scanner;

public class TokenCounterController extends AMapReduceTracer {
    public void getUserInput(Scanner inputHandler, TokenCounterModel model, TokenCounterView view) {
        this.traceNumbersPrompt();
        String line = inputHandler.nextLine();
        boolean hasQuit = line.equals(AMapReduceTracer.QUIT);
        while (!hasQuit) {
            model.setInputString(line, view);
        }
    }

    @Override
    public String toString() {
        return AMapReduceTracer.CONTROLLER;
    }
}
