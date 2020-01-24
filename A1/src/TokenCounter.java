import util.trace.Tracer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TokenCounter {
    public static void main(String[] args) {
        Scanner inputHandler = new Scanner(System.in);
        String[] tokens = inputHandler.nextLine().split(" ");
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
        Tracer.userMessage(resultOutput.substring(1, resultOutput.length() - 1));
    }
}
