package model;

/**
 * @author Mert Duman
 * @version 23.12.2018
 */
public class ServerInfo {
    private String serverAddress;
    private int serverPort;

    public ServerInfo(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public String toString() {
        return "Address: " + serverAddress + ". Port: " + serverPort;
    }
}
