package comp533;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TokenCounter extends AMapReduceTracer {
    public static void main(String[] args) {
        TokenCounter counter = new TokenCounter();
        Scanner inputHandler = new Scanner(System.in);
        while (true)  {
            counter.traceNumbersPrompt();
            String line = inputHandler.nextLine();
            if (line.equals(AMapReduceTracer.QUIT)) {
                return;
            }
            String[] tokens = line.split(" ");
            Map<String, Integer> result = new HashMap<String, Integer>();
            for (String token: tokens) {
                if (result.get(token) == null) {
                    result.put(token, 1);
                } else {
                    int value = result.get(token);
                    result.put(token, value + 1);
                }
            }
            String resultOutput = result.toString();
            System.out.println(resultOutput.substring(1, resultOutput.length() - 1));
        }
    }
}
