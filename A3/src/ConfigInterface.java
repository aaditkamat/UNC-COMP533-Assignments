import comp533.keyvalue.TokenCounterKeyValue;
import comp533.barrier.TokenCounterBarrier;
import comp533.client.TokenCounterClient;
import comp533.controller.TokenCounterController;
import comp533.joiner.TokenCounterJoiner;
import comp533.mapper.Mapper;
import comp533.mapper.TokenCounterMapper;
import comp533.mapper.TokenCounterMapperFactory;
import comp533.mvc.DistributedTokenCounter;
import comp533.partitioner.TokenCounterPartitioner;
import comp533.partitioner.TokenCounterPartitionerFactory;
import comp533.reducer.Reducer;
import comp533.reducer.TokenCounterReducer;
import comp533.reducer.TokenCounterReducerFactory;
import comp533.server.IntegerSummerServer;
import comp533.server.TokenCounterServer;
import comp533.slave.TokenCounterSlave;
import comp533.view.TokenCounterView;

interface ConfigInterface {
    // ---------------Mostly A1---------------------------------
    // main classes
    Class<DistributedTokenCounter> getStandAloneTokenCounter();
    MyMapReduceConfiguration getStandAloneIntegerSummer();

    //MVC classes
    Class<DistributedTokenCounter> getModelClass();
    Class<TokenCounterView> getViewClass();
    Class<TokenCounterController> getControllerClass();


    // Factories
    Class<TokenCounterMapperFactory> getMapperFactory();
    Class<TokenCounterReducerFactory> getReducerFactory();
    Class<TokenCounterPartitionerFactory> getPartitionerFactory(); // A2

    //KeyValue defining and processing class
    Class<TokenCounterKeyValue> getKeyValueClass();
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

    Class<TokenCounterServer> getServerTokenCounter();
    Class<IntegerSummerServer> getServerIntegerSummer();
    Class<TokenCounterClient> getClientTokenCounter();// client remains the same in both cases
}
