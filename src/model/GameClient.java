package model;

import controller.EndController;
import controller.GameUIController;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

/**
 * @author Mert Duman
 * @version 14.12.2018
 */
public class GameClient {
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
    private Player player;

    public void setGameUIController(GameUIController gameUIController) {
        this.gameUIController = gameUIController;
    }

    public GameClient(String host, int port, Player player) {
        this.host = host;
        this.port = port;
        this.player = player;
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
        alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.RECEIVE_TEXT));
        out.println(msg);
        return msg;
    }

    public <T> T sendObjectBlocked(T obj) {
        try {
            alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.RECEIVE_GAME));
            outObj.writeObject(obj);
            outObj.flush();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendPlayerMove(String playerName, int row, int col, CubeFaces cubeFace) {
        alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.RECEIVE_MOVE));
        out.println(playerName);
        out.println(row);
        out.println(col);
        out.println(String.valueOf(cubeFace));
        out.println(clientNo);
    }

    public void sendPlayer(Player player) {
        alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.RECEIVE_PLAYER));
        try {
            outObj.writeObject(player);
            outObj.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerProperties(String playerName, String playerVisibleName, int dimensions) {
        alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.RECEIVE_PLAYER_PROPERTIES));
        out.println(playerName);
        out.println(playerVisibleName);
        out.println(dimensions);
        System.out.println("sent player properties");
    }

    public void sendPlayerEndTime(long endTime) {
        alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.RECEIVE_ENDTIME));
        out.println(endTime);
    }

    public void sendPlayerGiveUp(String playerName) {
        alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.RECEIVE_PLAYER_GIVE_UP));
        out.println(playerName);
    }

    public String waitUntilGameReady() {
        alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.RESPOND_GAME_READY));
        String in = readMessageBlocked();
        clientNo = Integer.parseInt(in);
        return in;
    }

    public void distributeClientPlayers() {
        alertServerForAction(String.valueOf(ClientHandler.HostServerCodes.DISTRIBUTE_CLIENT_PLAYERS));
        Vector<Player> players = readObjectBlocked();
        System.out.println(players);
        clientPlayers = players;
    }

    public void waitClientPlayers() {
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
                        String codeStr = in.readLine();
                        if (codeStr == null) {
                            break;
                        }
                        ClientCodes code = Enum.valueOf(ClientCodes.class, codeStr);

                        switch (code) {
                            case CLOSE:
                                return;
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
                            case RECEIVE_PLAYER_GIVE_UP:
                                try {
                                    String playerName = in.readLine();
                                    boolean everyoneGaveUp = true;

                                    for (Player p : clientPlayers) {
                                        if (p.getName().equals(playerName)) {
                                            p.setDidGiveUp(true);
                                        }
                                        if (!p.didGiveUp()) {
                                            everyoneGaveUp = false;
                                        }
                                    }

                                    // If you are the last person to give up, close the server and exit.
                                    if (everyoneGaveUp) {
                                        close();

                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                gameUIController.handleGameEndMultiplayer(0, null, EndController.EndType.GIVE_UP);
                                            }
                                        });

                                        return;
                                    }

                                    // If you are the person that gave up but you are not the last, close your client and exit.
                                    if (player.getName().equals(playerName)) {
                                        close();

                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    gameUIController.loadEndScene(0, null, EndController.EndType.GIVE_UP);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                        return;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                    catch (SocketException e) {
                        e.printStackTrace();
                        break;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        break;
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
        RECEIVE_PLAYER_GIVE_UP,
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
