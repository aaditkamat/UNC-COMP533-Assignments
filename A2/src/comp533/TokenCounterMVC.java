package comp533;

import comp533.controller.TokenCounterController;
import comp533.model.TokenCounterModel;
import comp533.view.TokenCounterView;

public class TokenCounterMVC {
    public static void main(String[] args) {
        TokenCounterController controller = new TokenCounterController();
        TokenCounterModel model = new TokenCounterModel();
        TokenCounterView view = new TokenCounterView();
        controller.getUserInput(model, view);
    }
}
