package model;

import java.io.*;
import java.net.Socket;

/**
 * @author Mert Duman
 * @version 14.12.2018
 */
public class ClientHandler extends Thread {
    private HostServer hostServer;
    private Socket client;
    private int clientNo;

    private BufferedReader in;
    private PrintWriter out;
    private ObjectInputStream inObj;
    private ObjectOutputStream outObj;

    public ClientHandler(Socket socket, HostServer hostServer, int clientNo) {
        this.client = socket;
        this.hostServer = hostServer;
        this.clientNo = clientNo;
    }

    @Override
    public void run() {
        System.out.println("A client connected: " + client.getInetAddress().toString() + " with no: " + clientNo);
        try {
            // Open streams for input/output
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

            outObj = new ObjectOutputStream(client.getOutputStream());
            inObj = new ObjectInputStream(client.getInputStream());

            // Serve the client based on hostServer codes
            while (true) {
                String codeStr = in.readLine();
                System.out.println("READ CODE: " + codeStr);
                if (codeStr == null) {
                    System.out.println("Closing thread for client: " + clientNo);
                    break;
                }
                HostServerCodes code = Enum.valueOf(HostServerCodes.class, codeStr);
                handleClientRequest(code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void handleClientRequest(HostServerCodes code) {
        switch (code) {
            case RECEIVE_TEXT:
                try {
                    String message = in.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_PLAYER:
                try {
                    Player player = (Player)inObj.readObject();
                    System.out.println("Added player: " + player.getVisibleName());
                    hostServer.getPlayers().add(player);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_PLAYER_PROPERTIES:
                try {
                    String playerName = in.readLine();
                    String playerVisibleName = in.readLine();
                    int dimensions = Integer.valueOf(in.readLine());
                    Player player = new Player(playerName, dimensions);
                    player.setVisibleName(playerVisibleName);

                    System.out.println("Added player: " + player.getVisibleName());
                    hostServer.getPlayers().add(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_GAME:
                try {
                    System.out.println("Receiving object for client: " + clientNo);
                    Game obj = (Game)inObj.readObject();

                    hostServer.setObject(obj);
                    hostServer.alertAllClientsButSelf(HostServerCodes.SEND_OBJECT, this);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_MOVE:
                try {
                    String playerName = in.readLine();
                    int row = Integer.parseInt(in.readLine());
                    int col = Integer.parseInt(in.readLine());
                    String cubeFaceStr = in.readLine();
                    CubeFaces cubeFace = cubeFaceStr.equals("null") ? null : Enum.valueOf(CubeFaces.class, cubeFaceStr);
                    int clientNoOfMover = Integer.parseInt(in.readLine());

                    hostServer.setData(new Object[]{playerName, row, col, cubeFace, clientNoOfMover});
                    hostServer.alertAllClientsButSelf(HostServerCodes.SEND_MOVE, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_ENDTIME:
                try {
                    String endTime = in.readLine();

                    hostServer.setObject(endTime);
                    hostServer.alertAllClients(HostServerCodes.SEND_ENDTIME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_PLAYER_GIVE_UP:
                try {
                    String playerName = in.readLine();
                    System.out.println("Player gave up: "+ playerName);
                    hostServer.setObject(playerName);
                    hostServer.alertAllClients(HostServerCodes.SEND_PLAYER_GIVE_UP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_TEXT:
                break;
            case SEND_OBJECT:
                try {
                    outObj.writeObject(hostServer.getObject());
                    outObj.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_MOVE:
                out.println(String.valueOf(GameClient.ClientCodes.RECEIVE_MOVE));
                out.println(hostServer.getData()[0]);
                out.println(hostServer.getData()[1]);
                out.println(hostServer.getData()[2]);
                out.println(hostServer.getData()[3]);
                out.println(hostServer.getData()[4]);
                break;
            case SEND_PLAYERS:
                try {
                    outObj.writeObject(hostServer.getPlayers());
                    outObj.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_ENDTIME:
                out.println(String.valueOf(GameClient.ClientCodes.CLOSE));
                out.println(hostServer.getObject());
                break;
            case SEND_PLAYER_GIVE_UP:
                out.println(String.valueOf(GameClient.ClientCodes.RECEIVE_PLAYER_GIVE_UP));
                out.println(hostServer.getObject());
                break;
            case RESPOND_GAME_READY:
                int serverSize = -1;
                while (serverSize < hostServer.getServerMaxSize()) {
                    serverSize = hostServer.getPlayersSize();

                    //System.out.print("");

                    if (serverSize >= hostServer.getServerMaxSize()) {
                        out.println(clientNo);
                        break;
                    }
                }
                break;
            case DISTRIBUTE_CLIENT_PLAYERS:
                hostServer.alertAllClients(HostServerCodes.SEND_PLAYERS);
                break;
            case CLOSE_SERVER:
                System.out.println("CLOSING SERVER...");
                hostServer.close();
                break;
        }
    }

    public enum HostServerCodes {
        RECEIVE_GAME,
        RECEIVE_TEXT,
        RECEIVE_MOVE,
        RECEIVE_PLAYER,
        RECEIVE_PLAYER_PROPERTIES,
        RECEIVE_ENDTIME,
        RECEIVE_PLAYER_GIVE_UP,
        SEND_OBJECT,
        SEND_TEXT,
        SEND_MOVE,
        SEND_PLAYERS,
        SEND_ENDTIME,
        SEND_PLAYER_GIVE_UP,
        RESPOND_GAME_READY,
        DISTRIBUTE_CLIENT_PLAYERS,
        CLOSE_SERVER
    }
}
