public class ServerInformation {
    private String role;
    private int port;
    private String replicaOfHost;
    private String replicaOfPort;

    private String masterReplid;

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

    public String getReplicaOfHost() {
        return replicaOfHost;
    }

    public void setReplicaOfHost(String replicaOfHost) {
        this.replicaOfHost = replicaOfHost;
    }

    public String getReplicaOfPort() {
        return replicaOfPort;
    }

    public void setReplicaOfPort(String replicaOfPort) {
        this.replicaOfPort = replicaOfPort;
    }

    public String getMasterReplid() {
        return masterReplid;
    }

    public void setMasterReplid(String masterReplid) {
        this.masterReplid = masterReplid;
    }

    @Override
    public String toString() {
        return "ServerInformation{" +
                "role='" + role + '\'' +
                ", port=" + port +
                ", replicaOfHost='" + replicaOfHost + '\'' +
                ", replicaOfPort='" + replicaOfPort + '\'' +
                ", masterReplid='" + masterReplid + '\'' +
                '}';
    }
}
