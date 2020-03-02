package comp533.controller;

import comp533.mvc.RemoteTokenCounter;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.rmi.RemoteException;
import java.util.Scanner;

public class TokenCounterController extends AMapReduceTracer implements Controller {
    private Scanner inputHandler;

    public TokenCounterController() {
        this.inputHandler = new Scanner(System.in);
    }

    public void getUserInput(RemoteTokenCounter counter) {
        this.traceThreadPrompt();
        int numThreads = this.inputHandler.nextInt();
        this.inputHandler.nextLine();
        try {
            counter.setNumThreads(numThreads);
            while (true) {
                this.traceNumbersPrompt();
                String line = inputHandler.nextLine();
                Tracer.userMessage("In Controller: " + line);
                if (line.equals(AMapReduceTracer.QUIT)) {
                    counter.interruptThreads();
                    this.traceQuit();
                    break;
                }
                counter.setInputString(line);
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
