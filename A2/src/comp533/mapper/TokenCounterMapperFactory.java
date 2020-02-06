package comp533.mapper;

public class TokenCounterMapperFactory {
    private static Mapper mapper;
    static {
        mapper = new TokenCounterMapper();
    }

    public static Mapper getMapper() {
        return TokenCounterMapperFactory.mapper;
    }

    public static void setMapper(Mapper mapper) {
        TokenCounterMapperFactory.mapper = mapper;
    }
}
