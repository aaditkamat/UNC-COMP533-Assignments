import java.util.Scanner;

public class TokenCounterMVC {
    public static void main(String[] args) {
        Scanner inputHandler = new Scanner(System.in);
        TokenCounterController controller = new TokenCounterController();
        TokenCounterModel model = new TokenCounterModel();
        TokenCounterView view = new TokenCounterView();
        controller.getUserInput(inputHandler, model, view);
    }
}
