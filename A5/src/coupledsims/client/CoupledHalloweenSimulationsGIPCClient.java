package coupledsims.client;

import assignments.util.mainArgs.ClientArgsProcessor;
import coupledsims.server.RemoteServer;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;
import util.trace.bean.BeanTraceUtility;
import util.trace.port.rpc.gipc.GIPCObjectLookedUp;
import util.trace.port.rpc.gipc.GIPCObjectRegistered;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.gipc.GIPCRegistryLocated;

import java.rmi.RemoteException;

@Tags({DistributedTags.CLIENT, DistributedTags.RMI, DistributedTags.GIPC})
public class CoupledHalloweenSimulationsGIPCClient extends CoupledHalloweenSimulationsRMIClient {
    private static CoupledHalloweenSimulationsGIPCClient clientInstance = new CoupledHalloweenSimulationsGIPCClient();
    private GIPCRegistry gipcRegistry;
    protected RemoteServer serverGIPCProxy;
    public static CoupledHalloweenSimulationsGIPCClient getSingleton() {
        return clientInstance;
    }

    @Override
    public void setTracing() {
        super.setTracing();
        BeanTraceUtility.setTracing();
        GIPCRPCTraceUtility.setTracing();
    }

    @Override
    protected void init(String[] args) {
        try {
            this.setTracing();
            super.init(args);
            int gipcPort = ClientArgsProcessor.getGIPCPort(args);
            String gipcHostName = ClientArgsProcessor.getServerHost(args);
            this.locateGIPCRegistry(gipcPort, gipcHostName);
            this.lookupGIPCServerProxy();
            this.exportGIPCClientProxy();
            this.serverGIPCProxy.registerClients();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void locateGIPCRegistry(int gipcPort, String gipcHostName) {
        this.gipcRegistry = GIPCLocateRegistry.getRegistry(gipcHostName, gipcPort, RemoteClient.class.getName());
        GIPCRegistryLocated.newCase(this, gipcHostName,gipcPort, RemoteClient.class.getName());
    }

    public void lookupGIPCServerProxy() {
        this.serverGIPCProxy = (RemoteServer) gipcRegistry.lookup(RemoteServer.class, RemoteServer.class.getName());
        GIPCObjectLookedUp.newCase(this, this.serverGIPCProxy, RemoteServer.class, RemoteServer.class.getName(), this.gipcRegistry);
    }

    public void exportGIPCClientProxy() {
        this.gipcRegistry.rebind(RemoteClient.class.getName(), this);
        GIPCObjectRegistered.newCase(this, RemoteClient.class.getName(), this, this.gipcRegistry);
    }

    @Override
    public void start(String[] args) {
        this.init(args);
        SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(this);
        SimulationParametersControllerFactory.getSingleton().processCommands();
    }

    public static void main(String[] args) {
        CoupledHalloweenSimulationsGIPCClient clientInstance = new CoupledHalloweenSimulationsGIPCClient();
        clientInstance.start(args);
    }
}
