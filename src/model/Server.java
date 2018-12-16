package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * @author Mert Duman
 * @version 14.12.2018
 */
public class Server {
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clientHandlers;
    private ArrayList<String> playerNames;
    private int serverMaxSize;
    private int curClients;
    private Object object;
    private Object[] data;

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

    public Server(int port, int serverMaxSize) {
        this.serverMaxSize = serverMaxSize;
        this.curClients = 0;
        this.playerNames = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server running.");

            clientHandlers = new ArrayList<>();

            Thread actualServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
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

    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }

    public int getServerMaxSize() {
        return serverMaxSize;
    }

    public int getNumberOfClients() {
        return clientHandlers.size();
    }

    public int getPlayerNamesSize() { return playerNames.size(); }

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

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
