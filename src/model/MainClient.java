package model;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

/**
 * @author Mert Duman
 * @version 23.12.2018
 */
public class MainClient {
    private BufferedReader in;
    private PrintWriter out;
    private Socket client;
    private String host;
    private int port;
    private HostServer hostServer;

    public MainClient(String host, int port) {
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
        out.flush();
    }

    public void sendServerInfo() {
        if (hostServer != null) {
            alertServerForAction(String.valueOf(MainHandler.MainServerCodes.RECEIVE_SERVER_INFO));
            out.println(hostServer.getServerAddress());
            out.println(hostServer.getServerPort());
            out.println(hostServer.getServerMaxSize());
            out.println(hostServer.getServerDifficulty());
            out.println(hostServer.getServerCubeDimension());
            out.println(hostServer.getServerGameMode());
            out.println(hostServer.getServerPatternNo());
        }
    }

    public void sendServerClosed() {
        if (hostServer != null) {
            alertServerForAction(String.valueOf(MainHandler.MainServerCodes.RECEIVE_SERVER_CLOSED));
            out.println(hostServer.getServerAddress());
            out.println(hostServer.getServerPort());
        }
    }

    public Vector<ServerInfo> getMatchingServers(int serverMaxSize, int serverDifficulty, int serverCubeDimension, String serverGameMode, int serverPatternNo) {
        alertServerForAction(String.valueOf(MainHandler.MainServerCodes.SEND_MATCHING_SERVERS));
        out.println(serverMaxSize);
        out.println(serverDifficulty);
        out.println(serverCubeDimension);
        out.println(serverGameMode);
        out.println(serverPatternNo);

        Vector<ServerInfo> matchingServers = new Vector<>();

        try {
            String response = in.readLine();
            String address;
            int port;
            while (!response.equals("END")) {
                address = response;
                response = in.readLine();
                port = Integer.valueOf(response);
                response = in.readLine();

                matchingServers.add(new ServerInfo(address, port));
            }

            return matchingServers;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HostServer getHostServer() {
        return hostServer;
    }

    public void setHostServer(HostServer hostServer) {
        this.hostServer = hostServer;
    }
}
