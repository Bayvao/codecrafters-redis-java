import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerInformation {
    private String role;
    private int port;
    private String masterHost;
    private String masterPort;
    private String masterReplid;

    private volatile Set<Socket> replicas = new HashSet<>();

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMasterHost() {
        return masterHost;
    }

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
    }

    public String getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(String masterPort) {
        this.masterPort = masterPort;
    }

    public String getMasterReplid() {
        return masterReplid;
    }

    public void setMasterReplid(String masterReplid) {
        this.masterReplid = masterReplid;
    }

    public synchronized Set<Socket> getReplicaSet() {
        return replicas;
    }

    public synchronized void setReplicaSet(Socket socket) throws IOException {
        this.replicas.add(socket);
    }

    @Override
    public String toString() {
        return "ServerInformation{" +
                "role='" + role + '\'' +
                ", port=" + port +
                ", masterHost='" + masterHost + '\'' +
                ", masterPort='" + masterPort + '\'' +
                ", masterReplid='" + masterReplid + '\'' +
                ", replicas=" + replicas +
                '}';
    }
}