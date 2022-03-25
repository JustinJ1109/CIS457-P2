
public class ConnectionInfo {
    private String serverHostName, port, userName, speed;
    private String keyword, goCommand;

    public ConnectionInfo(String serverHostName, String port, String userName, String speed) {
        this.serverHostName = serverHostName;
        this.port = port;
        this.userName = userName;
        this.speed = speed;
    }

    public String getServerHostName() {
        return serverHostName;
    }

    public void setServerHostName(String name) {
        serverHostName = name;
    }

    public String getPort() {
        return port;
    }
    
    public void setPort(String port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        userName = name;
    }

    public String getSpeed() {
        return speed;
    }
    
    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getKeyWord() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCommand() {
        return goCommand;
    }

    public void setCommand(String command) {
        goCommand = command;
    }
}
