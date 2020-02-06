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
    public Class<TokenCountingMapperFactory> getMapperFactory() {
        return TokenCountingMapperFactory.class;
    }

    @Override
    public Class<TokenCountingReducerFactory> getReducerFactory() {
        return TokenCountingReducerFactory.class;
    }

    @Override
    public Class<PartitionerFactory> getPartitionerFactory() {
        return PartitionerFactory.class;
    }

    @Override
    public Class<KeyValue> getKeyValueClass() {
        return KeyValue.class;
    }

    @Override
    public Mapper<String, Integer> getTokenCountingMapper() {
        return TokenCountingMapperFactory.getMapper();
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
    public Class<Partitioner> getPartitionerClass() {
        return Partitioner.class;
    }

    @Override
    public Class<SlaveClass> getSlaveClass() {
        return SlaveClass.class;
    }

    @Override
    public Barrier getBarrier(int aNumThreads) {
        return new Barrier(aNumThreads);
    }

    @Override
    public Class<Barrier> getBarrierClass() {
        return Barrier.class;
    }

    @Override
    public Object getJoiner(int aNumThreads) {
        return new Joiner(aNumThreads);
    }

    @Override
    public Class<Joiner> getJoinerClass() {
        return Joiner.class;
    }

    @Override
    public Reducer getReducer() {
        return TokenCountingReducerFactory.getReducer();
    }

    @Override
    public Partitioner getPartitioner() {
        return PartitionerFactory.getPartitioner();
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

