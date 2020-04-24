package coupledsims.server;

import assignments.util.MiscAssignmentUtils;
import assignments.util.mainArgs.ServerArgsProcessor;
import coupledsims.nio.ByteBufferInfo;
import coupledsims.nio.ByteBufferSocketChannelInfo;
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
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.nio.SocketChannelBound;
import util.trace.port.nio.SocketChannelRead;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Tags({DistributedTags.RMI, DistributedTags.GIPC, DistributedTags.NIO, DistributedTags.SERVER})
public class CoupledHalloweenSimulationsRMIGIPCAndNIOServer extends CoupledHalloweenSimulationsRMIAndGIPCServer implements NIOServer{
    private static CoupledHalloweenSimulationsRMIGIPCAndNIOServer serverInstance = new CoupledHalloweenSimulationsRMIGIPCAndNIOServer();
    private List<SocketChannel> socketChannels;
    private ArrayBlockingQueue<ByteBufferSocketChannelInfo> messageQueue;
    private NIOManager nioManager;
    private ServerRunnable serverRunnable;

    public CoupledHalloweenSimulationsRMIGIPCAndNIOServer() {
        this.nioManager = NIOManagerFactory.getSingleton();
        this.socketChannels = new ArrayList<>();
        this.serverRunnable = new ServerRunnable(this);
    }

    public static CoupledHalloweenSimulationsRMIAndGIPCServer getSingleton() {
        return serverInstance;
    }

    public NIOManager getNioManager() {
        return nioManager;
    }

    public ArrayBlockingQueue<ByteBufferSocketChannelInfo> getMessageQueue() {
        return messageQueue;
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
        this.init(args);
        Thread readThread = new Thread(this.serverRunnable);
        readThread.setName(NIOServer.READ_THREAD_NAME);
        readThread.start();
    }

    public void sendRequestViaNIO(String command, SocketChannel currentSocketChannel) {
        RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, command);
        ByteBuffer message = ByteBuffer.wrap(command.getBytes());
        for (SocketChannel socketChannel: this.socketChannels) {
            if (!socketChannel.equals(currentSocketChannel)) {
                this.nioManager.write(socketChannel, message);
            }
        }
    }

    public void socketChannelAccepted(ServerSocketChannel serverSocketChannel, SocketChannel socketChannel) {
        Tracer.userMessage(socketChannel + " added to the list of socket channels");
        this.socketChannels.add(socketChannel);
        this.nioManager.addReadListener(socketChannel, this);
    }

    public void socketChannelRead(SocketChannel socketChannel, ByteBuffer newMessage, int messageLength) {
        SocketChannelRead.newCase(this, socketChannel, newMessage, messageLength);
        ByteBuffer readBuffer = MiscAssignmentUtils.deepDuplicate(newMessage);
        ByteBufferInfo messageInfo = new ByteBufferInfo(readBuffer, messageLength);
        ByteBufferSocketChannelInfo messageAndChannelInfo = new ByteBufferSocketChannelInfo(messageInfo, socketChannel);
        if (this.messageQueue.remainingCapacity() > 0) {
            this.messageQueue.add(messageAndChannelInfo);
        } else {
            Tracer.error("The message queue is full");
        }
    }

    public static void main(String[] args) {
        CoupledHalloweenSimulationsRMIAndGIPCServer serverInstance = getSingleton();
        serverInstance.start(args);
    }
}
