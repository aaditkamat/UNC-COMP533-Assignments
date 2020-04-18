package coupledsims.server;


import coupledsims.client.GIPCClient;

public interface GIPCServer {
    void registerGIPCClients();
    void receiveCommandViaGIPC(String aCommand, GIPCClient clientProxy);
}
