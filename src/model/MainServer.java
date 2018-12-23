package model;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Vector;

public class MainServer {
    private ServerSocket serverSocket;
    private Vector<MainHandler> mainHandlers;
    private Vector<HostServer> availableServers;

    public MainServer(int port) {
        this.availableServers = new Vector<>();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("MainServer running.");

            mainHandlers = new Vector<>();

            Thread actualServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            MainHandler mainHandler = new MainHandler(serverSocket.accept(), MainServer.this);
                            mainHandlers.add(mainHandler);
                            mainHandler.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            });
            actualServer.setDaemon(true);
            actualServer.start();
        } catch (BindException e) {
            System.out.println("Main Server already running...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HostServer findServer(String address, int port) {
        for (HostServer hs : availableServers) {
            if (hs.getServerAddress().equals(address) && hs.getServerPort() == port) {
                return hs;
            }
        }

        return null;
    }

    public Vector<HostServer> filterServers(int playerCount, int difficulty, int cubeDimension, String gameMode, int patternNo) {
        Vector<HostServer> filteredVectors = new Vector<>();
        for (HostServer hs : availableServers) {
            if (hs.getServerMaxSize() == playerCount &&
                    hs.getServerDifficulty() == difficulty &&
                        hs.getServerCubeDimension() == cubeDimension &&
                            hs.getServerGameMode().equals(gameMode) &&
                                hs.getServerPatternNo() == patternNo) {
                filteredVectors.add(hs);
            }
        }

        return filteredVectors;
    }

    public void alertAllClientsButSelf(MainHandler.MainServerCodes code, MainHandler caller) {
        for (MainHandler mh : mainHandlers) {
            if (mh != caller) {
                mh.handleClientRequest(code);
            }
        }
    }

    public void alertAllClients(MainHandler.MainServerCodes code) {
        for (MainHandler mh : mainHandlers) {
            mh.handleClientRequest(code);
        }
    }

    public Vector<HostServer> getAvailableServers() {
        return availableServers;
    }
}
