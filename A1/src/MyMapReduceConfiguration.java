interface ConfigInterface {
    // ---------------Mostly A1---------------------------------
    // main classes
    Class<TokenCounterMVC> getStandAloneTokenCounter();
    MyMapReduceConfiguration getStandAloneIntegerSummer();

    //MVC classes
    Class<TokenCounterModel> getModelClass();
    Class<TokenCounterView> getViewClass();
    Class<TokenCounterController> getControllerClass();


    // Factories
    MyMapReduceConfiguration getMapperFactory();
    MyMapReduceConfiguration getReducerFactory();
    MyMapReduceConfiguration getPartitionerFactory(); // A2

    //KeyValue defining and processing class
    Class<KeyValue> getKeyValueClass();
    Class<TokenCountingMapper> getTokenCountingMapperClass();
    MyMapReduceConfiguration getIntSummingMapperClass(); // extra credit
    Class<TokenCountingReducer> getReducerClass();

    // Return instances of the required objects, using the relevant factories
    // if they return these objects by default
    Object getTokenCountingMapper(); // default object returned by Mapper factory
    Object getIntSummingMapper();
    Object getReducer(); // default object returned by Reducer factory
    Object getPartitioner(); // default object returned by Reducer factory, needed in A2

    // --------------------A2------------------------

    // return some instance of the Barrier and Joiner classes
    Object getBarrier(int aNumThreads);
    Object getJoiner(int aNumThreads);

    // --------------------A3--------------------------

    MyMapReduceConfiguration getServerTokenCounter();
    MyMapReduceConfiguration getServerIntegerSummer();
    MyMapReduceConfiguration getClientTokenCounter();// client remains the same in both cases
}

public class MyMapReduceConfiguration implements ConfigInterface {
    @Override
    public Class<TokenCounterMVC> getStandAloneTokenCounter() {
        return TokenCounterMVC.class;
    }

    @Override
    public MyMapReduceConfiguration getStandAloneIntegerSummer() {
        return null;
    }

    @Override
    public Class<TokenCounterModel> getModelClass() {
        return TokenCounterModel.class;
    }

    @Override
    public Class<TokenCounterView> getViewClass() {
        return TokenCounterView.class;
    }

    @Override
    public Class<TokenCounterController> getControllerClass() {
        return TokenCounterController.class;
    }

    @Override
    public MyMapReduceConfiguration getMapperFactory() {
        return null;
    }

    @Override
    public MyMapReduceConfiguration getReducerFactory() {
        return null;
    }

    @Override
    public MyMapReduceConfiguration getPartitionerFactory() {
        return null;
    }

    @Override
    public Class<KeyValue> getKeyValueClass() {
        return KeyValue.class;
    }

    @Override
    public Object getTokenCountingMapper() {
        return null;
    }

    @Override
    public Object getIntSummingMapper() {
        return null;
    }

    @Override
    public Class<TokenCountingReducer> getReducerClass() {
        return TokenCountingReducer.class;
    }

    @Override
    public Class<TokenCountingMapper> getTokenCountingMapperClass() {
        return TokenCountingMapper.class;
    }

    @Override
    public MyMapReduceConfiguration getIntSummingMapperClass() {
        return null;
    }

    @Override
    public Object getBarrier(int aNumThreads) {
        return null;
    }

    @Override
    public Object getJoiner(int aNumThreads) {
        return null;
    }

    @Override
    public Object getReducer() {
        return null;
    }

    @Override
    public Object getPartitioner() {
        return null;
    }

    @Override
    public MyMapReduceConfiguration getServerTokenCounter() {
        return null;
    }

    @Override
    public MyMapReduceConfiguration getServerIntegerSummer() {
        return null;
    }

    @Override
    public MyMapReduceConfiguration getClientTokenCounter() {
        return null;
    }
}

