package comp533.controller;

import comp533.mvc.RemoteTokenCounter;
import comp533.view.View;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.rmi.RemoteException;
import java.util.Scanner;

public class TokenCounterController extends AMapReduceTracer implements Controller {
    private Scanner inputHandler;
    private RemoteTokenCounter counter;
    private View view;

    public TokenCounterController(RemoteTokenCounter counter, View view) {
        this.inputHandler = new Scanner(System.in);
        this.counter = counter;
        this.view = view;
    }

    public void getUserInput(RemoteTokenCounter counter) {
        this.traceThreadPrompt();
        int numThreads = this.inputHandler.nextInt();
        this.inputHandler.nextLine();
        try {
            counter.setNumThreads(numThreads, this.view);
            while (true) {
                this.traceNumbersPrompt();
                String line = inputHandler.nextLine();
                if (line.equals(AMapReduceTracer.QUIT)) {
                    counter.interruptThreads();
                    counter.callClientQuit();
                    this.traceQuit();
                    break;
                }
                counter.setInputString(line, this.view);
            }
        } catch (RemoteException ex) {
            ex.getStackTrace();
        }
    }


    @Override
    public String toString() {
        return Controller.LABEL;
    }
}
