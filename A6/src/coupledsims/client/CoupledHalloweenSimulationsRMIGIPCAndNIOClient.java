package coupledsims.client;

import assignments.util.MiscAssignmentUtils;
import assignments.util.mainArgs.ClientArgsProcessor;
import coupledsims.nio.ByteBufferInfo;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AConnectCommandFactory;
import inputport.nio.manager.factories.selectors.ConnectCommandFactorySelector;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import util.annotations.Tags;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.Tracer;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.consensus.communication.CommunicationStateNames;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.nio.ReadListenerAdded;
import util.trace.port.nio.SocketChannelRead;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.rmi.RMITraceUtility;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

public class CoupledHalloweenSimulationsRMIGIPCAndNIOClient extends CoupledHalloweenSimulationsRMIAndGIPCClient implements NIOClient, SocketChannelReadListener {
    private static final CoupledHalloweenSimulationsRMIGIPCAndNIOClient clientInstance = new CoupledHalloweenSimulationsRMIGIPCAndNIOClient();
    private NIOManager nioManager;
    private ClientRunnable clientRunnable;
    private ArrayBlockingQueue<ByteBufferInfo> messageQueue;
    protected SocketChannel socketChannel;

    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    public NIOManager getNIOManager() {
        return this.nioManager;
    }

    public ArrayBlockingQueue<ByteBufferInfo> getMessageQueue() {
        return messageQueue;
    }

    public CoupledHalloweenSimulationsRMIGIPCAndNIOClient() {
        this.nioManager = NIOManagerFactory.getSingleton();
        this.messageQueue = new ArrayBlockingQueue<>(NIOClient.BUFFER_SIZE);
        this.clientRunnable = new ClientRunnable(this);
    }

    public static CoupledHalloweenSimulationsRMIGIPCAndNIOClient getSingleton() {
        return clientInstance;
    }

    @Override
    public void setTracing() {
        NIOTraceUtility.setTracing();
        FactoryTraceUtility.setTracing();
        BeanTraceUtility.setTracing();
        RMITraceUtility.setTracing();
        ConsensusTraceUtility.setTracing();
        ThreadDelayed.enablePrint();
        GIPCRPCTraceUtility.setTracing();
        Tracer.setMaxTraces(5000);
        this.trace(true);
    }

    protected void setFactories() {
        ConnectCommandFactorySelector.setFactory(new AConnectCommandFactory(0));
    }

    @Override
    protected void init(String[] args) {
        try {
            super.init(args);
            this.setTracing();
            this.setFactories();
            int nioServerPort = ClientArgsProcessor.getNIOServerPort(args);
            this.socketChannel = SocketChannel.open();
            String hostName = ClientArgsProcessor.getServerHost(args);
            InetAddress serverAddress = InetAddress.getByName(hostName);
            this.nioManager.connect(this.socketChannel, serverAddress, nioServerPort, this);
            ReadListenerAdded.newCase(this, this.socketChannel, this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void quit(int aCode) {
        this.clientRunnable.synchronizedNotify();
        super.quit(aCode);
    }

    @Override
    public void start(String[] args) {
        this.init(args);
        Thread readThread = new Thread(this.clientRunnable);
        readThread.setName(NIOClient.READ_THREAD_NAME);
        readThread.start();
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    public void receiveProposalLearnedNotificationViaNIO(String command) {
        this.receiveProposalLearnedNotification(CommunicationStateNames.COMMAND, command);
    }

    public void socketChannelRead(SocketChannel socketChannel, ByteBuffer newMessage, int messageLength) {
        SocketChannelRead.newCase(this, socketChannel, newMessage, messageLength);
        ByteBuffer readBuffer = MiscAssignmentUtils.deepDuplicate(newMessage);
        ByteBufferInfo messageInfo = new ByteBufferInfo(readBuffer, messageLength);
        if (this.messageQueue.remainingCapacity() == 0) {
            Tracer.error("The message queue is full");
        } else {
            this.messageQueue.add(messageInfo);
        }
    }

    protected void sendCommandViaNIO(String command) {
        ByteBuffer message = ByteBuffer.wrap(command.getBytes());
        this.nioManager.write(this.socketChannel, message);
    }

    @Override
    public void simulationCommand(String aCommand) {
        if (this.ipcMechanism.equals(IPCMechanism.RMI) || this.ipcMechanism.equals(IPCMechanism.GIPC)) {
            super.simulationCommand(aCommand);
        } else {
            this.sendCommandViaNIO(aCommand);
        }
    }

    @Override
    public void connected(SocketChannel socketChannel) {
        Tracer.userMessage("Successfully connected to " + socketChannel);
    }

    @Override
    public void notConnected(SocketChannel socketChannel, Exception ex) {
        ex.printStackTrace();
    }
}
