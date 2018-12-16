package model;

import controller.GameUIController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Mert Duman
 * @version 14.12.2018
 */
public class Client {
    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private String host;
    private int port;
    private int clientNo;
    private ArrayList<String> clientPlayerNames;

    private GameUIController gameUIController;

    public void setGameUIController(GameUIController gameUIController) {
        this.gameUIController = gameUIController;
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean joinServer() {
        try {
            client = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public void alertServerForAction(String msg) {
        out.println(msg);
    }

    public boolean sendReceiveMessageBlocked(String msg) {
        out.println(msg);
        try {
            String response = in.readLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String readMessageBlocked() {
        try {
            String msg = in.readLine();
            return msg;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T readObjectBlocked() {
        try {
            ObjectInputStream inObj = new ObjectInputStream(client.getInputStream());
            T obj = (T)inObj.readObject();
            return obj;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String sendMessageBlocked(String msg) {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RECEIVE_TEXT));
        out.println(msg);
        return msg;
    }

    public <T> T sendObjectBlocked(T obj) {
        try {
            alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RECEIVE_OBJECT));
            ObjectOutputStream objOut = new ObjectOutputStream(client.getOutputStream());
            objOut.writeObject(obj);
            objOut.flush();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendPlayerMove(String playerName, int row, int col, CubeFaces cubeFace) {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RECEIVE_MOVE));
        out.println(playerName);
        out.println(row);
        out.println(col);
        out.println(String.valueOf(cubeFace));
        out.println(clientNo);
    }

    public void sendPlayerName(String playerName) {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RECEIVE_PLAYER_NAME));
        out.println(playerName);
    }

    public String waitUntilGameReady() {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RESPOND_GAME_READY));
        String in = readMessageBlocked();
        clientNo = Integer.parseInt(in);
        return in;
    }

    public void requestClientPlayerNames() {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.SEND_PLAYER_NAMES));
        ArrayList<String> names = readObjectBlocked();
        System.out.println(names);
        clientPlayerNames = names;
    }

    public void readMessageNonBlockedAlways() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        ClientCodes code = Enum.valueOf(ClientCodes.class, in.readLine());
                        switch (code) {
                            case RECEIVE_TEXT:
                                break;
                            case RECEIVE_OBJECT:
                                break;
                            case RECEIVE_MOVE:
                                try {
                                    String playerName = in.readLine();
                                    int row = Integer.parseInt(in.readLine());
                                    int col = Integer.parseInt(in.readLine());
                                    CubeFaces cubeFace = Enum.valueOf(CubeFaces.class, in.readLine());
                                    int clientNo = Integer.parseInt(in.readLine());
                                    gameUIController.setBoardFace(playerName, row, col, cubeFace, clientNo);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public boolean close() {
        try {
            in.close();
            out.close();
            client.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public enum ClientCodes {
        RECEIVE_TEXT,
        RECEIVE_OBJECT,
        RECEIVE_MOVE
    }

    public int getClientNo() {
        return clientNo;
    }

    public void setClientNo(int clientNo) {
        this.clientNo = clientNo;
    }

    public ArrayList<String> getClientPlayerNames() {
        return clientPlayerNames;
    }
}
