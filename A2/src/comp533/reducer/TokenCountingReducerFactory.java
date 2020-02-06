package comp533.reducer;

public class TokenCountingReducerFactory {
    private static Reducer reducer;
    static {
        reducer = new TokenCountingReducer();
    }

    public static Reducer getReducer() {
        return reducer;
    }

    public static void setReducer(Reducer reducer) {
        TokenCountingReducerFactory.reducer = reducer;
    }
}
