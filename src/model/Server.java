package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

/**
 * @author Mert Duman
 * @version 14.12.2018
 */
public class Server {
    private ServerSocket serverSocket;
    private Vector<ClientHandler> clientHandlers;
    private Vector<Player> players;
    private int serverMaxSize;
    private int serverDifficulty;
    private int serverCubeDimension;
    private int curClients;
    private Object object;
    private Object[] data;

    public Server(int port, int serverMaxSize, int serverDifficulty, int serverCubeDimension) {
        this.serverMaxSize = serverMaxSize;
        this.serverDifficulty = serverDifficulty;
        this.serverCubeDimension = serverCubeDimension;
        this.curClients = 0;
        this.players = new Vector<>();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server running.");

            clientHandlers = new Vector<>();

            Thread actualServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (clientHandlers.size() < serverMaxSize) {
                        try {
                            ClientHandler clientHandler = new ClientHandler(serverSocket.accept(), Server.this, ++curClients);
                            clientHandlers.add(clientHandler);
                            clientHandler.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            });
            actualServer.setDaemon(true);
            actualServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public int getServerMaxSize() {
        return serverMaxSize;
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

    public void alertAllClientsButSelf(ClientHandler.ServerCodes code, ClientHandler caller) {
        for (ClientHandler ch : clientHandlers) {
            if (ch != caller) {
                ch.handleClientRequest(code);
            }
        }
    }

    public void alertAllClients(ClientHandler.ServerCodes code) {
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

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
