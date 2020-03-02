package comp533.server;

import comp533.client.Client;
import comp533.client.TokenCounterClient;
import comp533.controller.Controller;
import comp533.controller.TokenCounterController;
import comp533.mvc.DistributedTokenCounter;
import comp533.mvc.RemoteTokenCounter;
import comp533.mvc.TokenCounter;
import comp533.view.TokenCounterView;
import comp533.view.View;
import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class TokenCounterServer {
    private static Registry rmiRegistry;

    public static void main(String[] args) {
        try {
            rmiRegistry = LocateRegistry.createRegistry(1099);
            RemoteTokenCounter counter1 = new DistributedTokenCounter();
            UnicastRemoteObject.exportObject(counter1, 0);
            rmiRegistry.rebind(RemoteTokenCounter.class.getName(), counter1);
            View view = new TokenCounterView();
            Controller controller = new TokenCounterController(counter1, view);
            controller.getUserInput(counter1);
            System.exit(0);
        } catch (RemoteException ex) {
            Tracer.error(ex.getMessage());
        }
    }
}
