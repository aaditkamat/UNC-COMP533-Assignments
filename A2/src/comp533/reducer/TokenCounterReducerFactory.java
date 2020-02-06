package comp533.reducer;

public class TokenCounterReducerFactory {
    private static Reducer reducer;
    static {
        reducer = new TokenCounterReducer();
    }

    public static Reducer getReducer() {
        return reducer;
    }

    public static void setReducer(Reducer reducer) {
        TokenCounterReducerFactory.reducer = reducer;
    }
}
