import comp533.TokenCounterMVC;
import comp533.barrier.Barrier;
import comp533.controller.TokenCounterController;
import comp533.joiner.Joiner;
import comp533.keyvalue.KeyValue;
import comp533.mapper.Mapper;
import comp533.mapper.TokenCountingMapper;
import comp533.mapper.TokenCountingMapperFactory;
import comp533.model.TokenCounterModel;
import comp533.partitioner.Partitioner;
import comp533.partitioner.PartitionerFactory;
import comp533.reducer.Reducer;
import comp533.reducer.TokenCountingReducer;
import comp533.reducer.TokenCountingReducerFactory;
import comp533.slave.SlaveClass;
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
    Class<TokenCountingMapperFactory> getMapperFactory();
    Class<TokenCountingReducerFactory> getReducerFactory();
    Class<PartitionerFactory> getPartitionerFactory(); // A2

    //KeyValue defining and processing class
    Class<KeyValue> getKeyValueClass();
    Class<TokenCountingMapper> getTokenCountingMapperClass();
    MyMapReduceConfiguration getIntSummingMapperClass(); // extra credit
    Class<TokenCountingReducer> getReducerClass();
    Class<Partitioner> getPartitionerClass();

    // Return instances of the required objects, using the relevant factories
    // if they return these objects by default
    Mapper<String, Integer> getTokenCountingMapper(); // default object returned by Mapper factory
    Object getIntSummingMapper();
    Reducer getReducer(); // default object returned by Reducer factory
    Partitioner getPartitioner(); // default object returned by Reducer factory, needed in A2

    // --------------------A2------------------------

    Class<SlaveClass> getSlaveClass();
    Class<Joiner> getJoinerClass();
    Class<Barrier> getBarrierClass();

    // return some instance of the comp533.barrier.Barrier and comp533.joiner.Joiner classes
    Object getBarrier(int aNumThreads);
    Object getJoiner(int aNumThreads);

    // --------------------A3--------------------------

    MyMapReduceConfiguration getServerTokenCounter();
    MyMapReduceConfiguration getServerIntegerSummer();
    MyMapReduceConfiguration getClientTokenCounter();// client remains the same in both cases
}
