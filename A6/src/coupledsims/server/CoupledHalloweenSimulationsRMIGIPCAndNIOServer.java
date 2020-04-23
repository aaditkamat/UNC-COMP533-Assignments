package coupledsims.server;

import assignments.util.mainArgs.ServerArgsProcessor;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AnAcceptCommandFactory;
import inputport.nio.manager.factories.selectors.AcceptCommandFactorySelector;
import util.annotations.Tags;

import util.tags.DistributedTags;
import util.trace.Tracer;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.nio.SocketChannelBound;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Tags({DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO, DistributedTags.SERVER})
public class CoupledHalloweenSimulationsRMIGIPCAndNIOServer extends CoupledHalloweenSimulationsRMIAndGIPCServer implements NIOServer{
    private static CoupledHalloweenSimulationsRMIGIPCAndNIOServer serverInstance = new CoupledHalloweenSimulationsRMIGIPCAndNIOServer();
    private NIOManager nioManager;
    private ServerRunnable serverRunnable;
    private Thread readThread;

    public CoupledHalloweenSimulationsRMIGIPCAndNIOServer() {
        this.nioManager = NIOManagerFactory.getSingleton();
        this.readThread = new Thread();
        this.readThread.setName(NIOServer.READ_THREAD_NAME);
        this.serverRunnable = new ServerRunnable(this);
    }

    public static CoupledHalloweenSimulationsRMIAndGIPCServer getSingleton() {
        return serverInstance;
    }

    public NIOManager getNioManager() {
        return nioManager;
    }

    @Override
    public void setTracing() {
        NIOTraceUtility.setTracing();
        FactoryTraceUtility.setTracing();
        BeanTraceUtility.setTracing ();
        RMITraceUtility.setTracing ();
        ConsensusTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        GIPCRPCTraceUtility.setTracing();
        this.trace(true);
    }

    protected void setFactories() {
        AcceptCommandFactorySelector.setFactory(new AnAcceptCommandFactory(SelectionKey.OP_READ));
    }

    @Override
    protected void init(String[] args) {
        try {
            super.init(args);
            this.setTracing();
            this.setFactories();
            ServerSocketChannel.open();
            ServerSocketChannel aServerFactoryChannel = ServerSocketChannel.open();
            int nioServerPort = ServerArgsProcessor.getNIOServerPort(args);
            InetSocketAddress anInternetSocketAddress = new InetSocketAddress(nioServerPort);
            aServerFactoryChannel.socket().bind(anInternetSocketAddress);
            SocketChannelBound.newCase(this, aServerFactoryChannel, anInternetSocketAddress);
            this.nioManager.enableListenableAccepts(aServerFactoryChannel, this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(String[] args) {
        super.start(args);
    }

    @Override
    public void socketChannelAccepted(ServerSocketChannel serverSocketChannel, SocketChannel socketChannel) {
        Thread readThread = new Thread(this.serverRunnable);
        readThread.setName(NIOServer.READ_THREAD_NAME);
        readThread.start();
        this.nioManager.addReadListener(socketChannel, this.serverRunnable);
    }

    public static void main(String[] args) {
        CoupledHalloweenSimulationsRMIAndGIPCServer serverInstance = getSingleton();
        serverInstance.start(args);
    }
}
