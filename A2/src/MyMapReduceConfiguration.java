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
    public Class<TokenCounterMapperFactory> getMapperFactory() {
        return TokenCounterMapperFactory.class;
    }

    @Override
    public Class<TokenCounterReducerFactory> getReducerFactory() {
        return TokenCounterReducerFactory.class;
    }

    @Override
    public Class<TokenCounterPartitionerFactory> getPartitionerFactory() {
        return TokenCounterPartitionerFactory.class;
    }

    @Override
    public Class<KeyValue> getKeyValueClass() {
        return KeyValue.class;
    }

    @Override
    public Mapper<String, Integer> getTokenCountingMapper() {
        return TokenCounterMapperFactory.getMapper();
    }

    @Override
    public Object getIntSummingMapper() {
        return null;
    }

    @Override
    public Class<TokenCounterReducer> getReducerClass() {
        return TokenCounterReducer.class;
    }

    @Override
    public Class<TokenCounterMapper> getTokenCountingMapperClass() {
        return TokenCounterMapper.class;
    }

    @Override
    public MyMapReduceConfiguration getIntSummingMapperClass() {
        return null;
    }

    @Override
    public Class<TokenCounterPartitioner> getPartitionerClass() {
        return TokenCounterPartitioner.class;
    }

    @Override
    public Class<TokenCounterSlave> getSlaveClass() {
        return TokenCounterSlave.class;
    }

    @Override
    public TokenCounterBarrier getBarrier(int aNumThreads) {
        return new TokenCounterBarrier(aNumThreads);
    }

    @Override
    public Class<TokenCounterBarrier> getBarrierClass() {
        return TokenCounterBarrier.class;
    }

    @Override
    public Object getJoiner(int aNumThreads) {
        return new TokenCounterJoiner(aNumThreads);
    }

    @Override
    public Class<TokenCounterJoiner> getJoinerClass() {
        return TokenCounterJoiner.class;
    }

    @Override
    public Reducer getReducer() {
        return TokenCounterReducerFactory.getReducer();
    }

    @Override
    public TokenCounterPartitioner getPartitioner() {
        return TokenCounterPartitionerFactory.getPartitioner();
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

