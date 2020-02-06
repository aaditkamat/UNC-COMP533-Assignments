package comp533.mapper;

public class TokenCountingMapperFactory {
    private static Mapper mapper;
    static {
        mapper = new TokenCountingMapper();
    }

    public static Mapper getMapper() {
        return TokenCountingMapperFactory.mapper;
    }

    public static void setMapper(Mapper mapper) {
        TokenCountingMapperFactory.mapper = mapper;
    }
}
