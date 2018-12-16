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
//    private ObjectInputStream inObj;
//    private ObjectOutputStream outObj;

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

//            inObj = new ObjectInputStream(client.getInputStream());
//            outObj = new ObjectOutputStream(client.getOutputStream());

            // Serve the client based on server codes
            while (true) {
                ServerCodes code = Enum.valueOf(ServerCodes.class, in.readLine());
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
            case RECEIVE_PLAYER_NAME:
                try {
                    String name = in.readLine();
                    server.getPlayerNames().add(name);
                    System.out.println("Added player: " + name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_OBJECT:
                try {
                    System.out.println("receiving object for client: " + clientNo);
                    ObjectInputStream inObj = new ObjectInputStream(client.getInputStream());
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
                    CubeFaces cubeFace = Enum.valueOf(CubeFaces.class, in.readLine());
                    int clientNoOfMover = Integer.parseInt(in.readLine());

                    server.setData(new Object[]{playerName, row, col, cubeFace, clientNoOfMover});
                    server.alertAllClientsButSelf(ServerCodes.SEND_MOVE, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_TEXT:
                break;
            case SEND_OBJECT:
                try {
                    ObjectOutputStream outObj = new ObjectOutputStream(client.getOutputStream());
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
            case SEND_PLAYER_NAMES:
                try {
                    ObjectOutputStream outObj = new ObjectOutputStream(client.getOutputStream());
                    outObj.writeObject(server.getPlayerNames());
                    outObj.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RESPOND_GAME_READY:
                int serverSize = -1;
                while (serverSize < server.getServerMaxSize()) {
                    serverSize = server.getNumberOfClients();

                    System.out.print("");

                    if (serverSize >= server.getServerMaxSize()) {
                        out.println(clientNo);
                        break;
                    }
                }
                break;


        }
    }

    public enum ServerCodes {
        RECEIVE_OBJECT,
        RECEIVE_TEXT,
        RECEIVE_MOVE,
        RECEIVE_PLAYER_NAME,
        SEND_OBJECT,
        SEND_TEXT,
        SEND_MOVE,
        SEND_PLAYER_NAMES,
        RESPOND_GAME_READY
    }
}
