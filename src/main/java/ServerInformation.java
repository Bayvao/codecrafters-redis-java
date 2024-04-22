import java.net.Socket;
import java.util.Set;

public class ServerInformation {
    private String role;
    private int port;
    private String masterHost;
    private String masterPort;
    private String masterReplid;

    private Set<Socket> replicaSet;

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

    public Set<Socket> getReplicaSet() {
        return replicaSet;
    }

    public void setReplicaSet(Socket socket) {
        this.replicaSet.add(socket);
    }
}