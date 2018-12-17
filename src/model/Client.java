package model;

import controller.GameUIController;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

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
    private Vector<Player> clientPlayers;

    private ObjectInputStream inObj;
    private ObjectOutputStream outObj;

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

            inObj = new ObjectInputStream(client.getInputStream());
            outObj = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public void alertServerForAction(String msg) {
        out.println(msg);
        out.flush();
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
            alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RECEIVE_GAME));
            outObj.writeObject(obj);
            outObj.flush();
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

    public void sendPlayer(Player player) {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RECEIVE_PLAYER));
        try {
            outObj.writeObject(player);
            outObj.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerEndTime(long endTime) {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RECEIVE_ENDTIME));
        out.println(endTime);
    }

    public String waitUntilGameReady() {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.RESPOND_GAME_READY));
        String in = readMessageBlocked();
        clientNo = Integer.parseInt(in);
        return in;
    }

    public void requestClientPlayers() {
        alertServerForAction(String.valueOf(ClientHandler.ServerCodes.SEND_PLAYERS));
        Vector<Player> players = readObjectBlocked();
        System.out.println(players);
        clientPlayers = players;
    }

    public void readMessageNonBlockedAlways() {
        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        ClientCodes code = Enum.valueOf(ClientCodes.class, in.readLine());
                        if (code == ClientCodes.CLOSE) {
                            break;
                        }
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
                                    String cubeFaceStr = in.readLine();
                                    CubeFaces cubeFace = cubeFaceStr.equals("null") ? null : Enum.valueOf(CubeFaces.class, cubeFaceStr);
                                    int clientNo = Integer.parseInt(in.readLine());

                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            gameUIController.setBoardFace(playerName, row, col, cubeFace, clientNo);
                                            gameUIController.playerPlayed(playerName, row, col, cubeFace);
                                        }
                                    });
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
        });
        clientThread.start();
    }

    public boolean close() {
        try {
            client.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public enum ClientCodes {
        RECEIVE_TEXT,
        RECEIVE_OBJECT,
        RECEIVE_MOVE,
        CLOSE
    }

    public int getClientNo() {
        return clientNo;
    }

    public void setClientNo(int clientNo) {
        this.clientNo = clientNo;
    }

    public Vector<Player> getClientPlayers() {
        return clientPlayers;
    }
}
