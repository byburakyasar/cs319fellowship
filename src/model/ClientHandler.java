package model;

import java.io.*;
import java.net.Socket;

/**
 * @author Mert Duman
 * @version 14.12.2018
 */
public class ClientHandler extends Thread {
    private Server server;
    private Socket client;
    private int clientNo;

    private BufferedReader in;
    private PrintWriter out;
    private ObjectInputStream inObj;
    private ObjectOutputStream outObj;

    public ClientHandler(Socket socket, Server server, int clientNo) {
        this.client = socket;
        this.server = server;
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

            // Serve the client based on server codes
            while (true) {
                String codeStr = in.readLine();
                System.out.println("READ CODE: " + codeStr);
                if (codeStr == null) {
                    System.out.println("Closing thread for client: " + clientNo);
                    break;
                }
                ServerCodes code = Enum.valueOf(ServerCodes.class, codeStr);
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

    public synchronized void handleClientRequest(ServerCodes code) {
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
                    server.getPlayers().add(player);
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
                    server.getPlayers().add(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_GAME:
                try {
                    System.out.println("Receiving object for client: " + clientNo);
                    Game obj = (Game)inObj.readObject();

                    server.setObject(obj);
                    server.alertAllClientsButSelf(ServerCodes.SEND_OBJECT, this);
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

                    server.setData(new Object[]{playerName, row, col, cubeFace, clientNoOfMover});
                    server.alertAllClientsButSelf(ServerCodes.SEND_MOVE, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_ENDTIME:
                try {
                    String endTime = in.readLine();

                    server.setObject(endTime);
                    server.alertAllClients(ServerCodes.SEND_ENDTIME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_PLAYER_GIVE_UP:
                try {
                    String playerName = in.readLine();
                    System.out.println("Player gave up: "+ playerName);
                    server.setObject(playerName);
                    server.alertAllClients(ServerCodes.SEND_PLAYER_GIVE_UP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_TEXT:
                break;
            case SEND_OBJECT:
                try {
                    outObj.writeObject(server.getObject());
                    outObj.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_MOVE:
                out.println(String.valueOf(Client.ClientCodes.RECEIVE_MOVE));
                out.println(server.getData()[0]);
                out.println(server.getData()[1]);
                out.println(server.getData()[2]);
                out.println(server.getData()[3]);
                out.println(server.getData()[4]);
                break;
            case SEND_PLAYERS:
                try {
                    outObj.writeObject(server.getPlayers());
                    outObj.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_ENDTIME:
                out.println(String.valueOf(Client.ClientCodes.CLOSE));
                out.println(server.getObject());
                break;
            case SEND_PLAYER_GIVE_UP:
                out.println(String.valueOf(Client.ClientCodes.RECEIVE_PLAYER_GIVE_UP));
                out.println(server.getObject());
                break;
            case RESPOND_GAME_READY:
                int serverSize = -1;
                while (serverSize < server.getServerMaxSize()) {
                    serverSize = server.getPlayersSize();

                    //System.out.print("");

                    if (serverSize >= server.getServerMaxSize()) {
                        out.println(clientNo);
                        break;
                    }
                }
                break;
            case DISTRIBUTE_CLIENT_PLAYERS:
                server.alertAllClients(ServerCodes.SEND_PLAYERS);
                break;
            case CLOSE_SERVER:
                System.out.println("CLOSING SERVER...");
                server.close();
                break;
        }
    }

    public enum ServerCodes {
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
