package comp533.controller;

import comp533.model.TokenCounterModel;
import comp533.view.TokenCounterView;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.util.Scanner;

public class TokenCounterController extends AMapReduceTracer {
    public void getUserInput(TokenCounterModel model, TokenCounterView view) {
        Scanner inputHandler = new Scanner(System.in);
        this.traceThreadPrompt();
        int numThreads = inputHandler.nextInt();
        model.setNumThreads(numThreads, view);
        while (true) {
            this.traceNumbersPrompt();
            String line = inputHandler.next();
            if (line.equals(AMapReduceTracer.QUIT)) {
                break;
            }
            model.setInputString(line, view);
        }
    }

    @Override
    public String toString() {
        return AMapReduceTracer.CONTROLLER;
    }
}
