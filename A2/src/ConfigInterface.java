import comp533.TokenCounterMVC;
import comp533.barrier.TokenCounterBarrier;
import comp533.controller.TokenCounterController;
import comp533.joiner.TokenCounterJoiner;
import comp533.keyvalue.KeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.TokenCounterMapper;
import comp533.mapper.TokenCounterMapperFactory;
import comp533.model.TokenCounterModel;
import comp533.partitioner.TokenCounterPartitioner;
import comp533.partitioner.TokenCounterPartitionerFactory;
import comp533.reducer.Reducer;
import comp533.reducer.TokenCounterReducer;
import comp533.reducer.TokenCounterReducerFactory;
import comp533.slave.TokenCounterSlave;
import comp533.view.TokenCounterView;

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
    Class<TokenCounterMapperFactory> getMapperFactory();
    Class<TokenCounterReducerFactory> getReducerFactory();
    Class<TokenCounterPartitionerFactory> getPartitionerFactory(); // A2

    //KeyValue defining and processing class
    Class<KeyValue> getKeyValueClass();
    Class<TokenCounterMapper> getTokenCountingMapperClass();
    MyMapReduceConfiguration getIntSummingMapperClass(); // extra credit
    Class<TokenCounterReducer> getReducerClass();
    Class<TokenCounterPartitioner> getPartitionerClass();

    // Return instances of the required objects, using the relevant factories
    // if they return these objects by default
    Mapper<String, Integer> getTokenCountingMapper(); // default object returned by Mapper factory
    Object getIntSummingMapper();
    Reducer getReducer(); // default object returned by Reducer factory
    TokenCounterPartitioner getPartitioner(); // default object returned by Reducer factory, needed in A2

    // --------------------A2------------------------

    Class<TokenCounterSlave> getSlaveClass();
    Class<TokenCounterJoiner> getJoinerClass();
    Class<TokenCounterBarrier> getBarrierClass();

    // return some instance of the comp533.barrier.Barrier and comp533.joiner.Joiner classes
    Object getBarrier(int aNumThreads);
    Object getJoiner(int aNumThreads);

    // --------------------A3--------------------------

    MyMapReduceConfiguration getServerTokenCounter();
    MyMapReduceConfiguration getServerIntegerSummer();
    MyMapReduceConfiguration getClientTokenCounter();// client remains the same in both cases
}
