package comp533.reducer;

public class TokenCounterReducerFactory {
    private static Reducer<String, Integer> reducer;
    static {
        reducer = new TokenCounterReducer();
    }

    public static Reducer<String, Integer> getReducer() {
        return reducer;
    }

    public static void setReducer(Reducer<String, Integer> reducer) {
        TokenCounterReducerFactory.reducer = reducer;
    }
}
