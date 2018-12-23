package model;

import controller.GameOptionsController;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * @author Mert Duman
 * @version 14.12.2018
 */
public class HostServer {
    private ServerSocket serverSocket;
    private MainClient mainClient;
    private Vector<ClientHandler> clientHandlers;
    private Vector<Player> players;
    private int serverMaxSize;
    private int serverDifficulty;
    private int serverCubeDimension;
    private GameOptionsController.GameModes serverGameMode;
    private int serverPatternNo;
    private String serverAddress;
    private int serverPort;
    private int curClients;
    private Object object;
    private Object[] data;

    public HostServer(String serverAddress, int serverPort, int serverMaxSize, int serverDifficulty, int serverCubeDimension, String serverGameMode, int serverPatternNo) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.serverMaxSize = serverMaxSize;
        this.serverDifficulty = serverDifficulty;
        this.serverCubeDimension = serverCubeDimension;
        this.serverGameMode = Enum.valueOf(GameOptionsController.GameModes.class, serverGameMode);
        this.serverPatternNo = serverPatternNo;
    }

    public HostServer(int serverMaxSize, int serverDifficulty, int serverCubeDimension, GameOptionsController.GameModes serverGameMode, int serverPatternNo, MainClient mainClient) {
        this.serverMaxSize = serverMaxSize;
        this.serverDifficulty = serverDifficulty;
        this.serverCubeDimension = serverCubeDimension;
        this.serverGameMode = serverGameMode;
        this.serverPatternNo = serverPatternNo;
        this.curClients = 0;
        this.players = new Vector<>();
        this.mainClient = mainClient;

        try {
            this.serverAddress = InetAddress.getLocalHost().getHostAddress();
            try(final DatagramSocket socket = new DatagramSocket()){
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                this.serverAddress = socket.getLocalAddress().getHostAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            serverSocket = new ServerSocket(0);
            this.serverPort = serverSocket.getLocalPort();
            System.out.println("HostServer running.");

            clientHandlers = new Vector<>();

            Thread actualServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (clientHandlers.size() < serverMaxSize) {
                        try {
                            ClientHandler clientHandler = new ClientHandler(serverSocket.accept(), HostServer.this, ++curClients);
                            clientHandlers.add(clientHandler);
                            clientHandler.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }

                    // server reached its limit, close it.
                    close();
                    mainClient.sendServerClosed();
                }
            });
            actualServer.setDaemon(true);
            actualServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void alertAllClientsButSelf(ClientHandler.HostServerCodes code, ClientHandler caller) {
        for (ClientHandler ch : clientHandlers) {
            if (ch != caller) {
                ch.handleClientRequest(code);
            }
        }
    }

    public void alertAllClients(ClientHandler.HostServerCodes code) {
        for (ClientHandler ch : clientHandlers) {
            ch.handleClientRequest(code);
        }
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }

    public Vector<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public int getNumberOfClients() {
        return clientHandlers.size();
    }

    public Vector<Player> getPlayers() {
        return players;
    }

    public int getPlayersSize() {
        return players.size();
    }

    public int getServerMaxSize() {
        return serverMaxSize;
    }

    public int getServerDifficulty() {
        return serverDifficulty;
    }

    public int getServerCubeDimension() {
        return serverCubeDimension;
    }

    public String getServerGameMode() {
        return String.valueOf(serverGameMode);
    }

    public int getServerPatternNo() {
        return serverPatternNo;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
