package model;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class MainHandler extends Thread {
    private MainServer mainServer;
    private Socket client;

    private BufferedReader in;
    private PrintWriter out;

    public MainHandler(Socket socket, MainServer mainServer) {
        this.client = socket;
        this.mainServer = mainServer;
    }

    @Override
    public void run() {
        System.out.println("A client connected: " + client.getInetAddress().toString());

        try {
            // Open streams for input/output
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

            // Serve the client based on mainServer codes
            while (true) {
                String codeStr = in.readLine();
                System.out.println("MAINHANDLER READ CODE: " + codeStr);
                if (codeStr == null) {
                    System.out.println("Closing thread for client.");
                    break;
                }
                MainServerCodes code = Enum.valueOf(MainServerCodes.class, codeStr);
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

    public void handleClientRequest(MainServerCodes code) {
        switch (code) {
            case RECEIVE_SERVER_INFO:
                try {
                    String serverAddress = in.readLine();
                    int serverPort = Integer.valueOf(in.readLine());
                    int serverMaxSize = Integer.valueOf(in.readLine());
                    int serverDifficulty = Integer.valueOf(in.readLine());
                    int serverCubeDimension = Integer.valueOf(in.readLine());
                    String serverGameMode = in.readLine();
                    int serverPatternNo = Integer.valueOf(in.readLine());

                    //System.out.println("received server info: " + serverAddress + " " + serverPort + " "
                    //                    + serverMaxSize + " " + serverDifficulty + " " + serverCubeDimension + " " + serverGameMode);
                    HostServer server = new HostServer(serverAddress, serverPort, serverMaxSize, serverDifficulty, serverCubeDimension, serverGameMode, serverPatternNo);
                    mainServer.getAvailableServers().add(server);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RECEIVE_SERVER_CLOSED:
                try {
                    String serverAddress = in.readLine();
                    int serverPort = Integer.valueOf(in.readLine());

                    HostServer hs = mainServer.findServer(serverAddress, serverPort);
                    mainServer.getAvailableServers().remove(hs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SEND_MATCHING_SERVERS:
                try {
                    int serverMaxSize = Integer.valueOf(in.readLine());
                    int serverDifficulty = Integer.valueOf(in.readLine());
                    int serverCubeDimension = Integer.valueOf(in.readLine());
                    String serverGameMode = in.readLine();
                    int serverPatternNo = Integer.valueOf(in.readLine());

                    Vector<HostServer> filteredServers = mainServer.filterServers(serverMaxSize, serverDifficulty, serverCubeDimension, serverGameMode, serverPatternNo);
                    for (HostServer sd : filteredServers) {
                        out.println(sd.getServerAddress());
                        out.println(sd.getServerPort());
                    }

                    out.println("END");
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }

    public enum MainServerCodes {
        RECEIVE_SERVER_INFO,
        RECEIVE_SERVER_CLOSED,
        SEND_MATCHING_SERVERS
    }
}
