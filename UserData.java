public class UserData {
    private String userName;
    private String hostName;
    private String speed;

    public UserData(String u, String h, String s){
        userName = u;
        hostName = h;
        speed = s;
    }

    public String getUserName() {
        return userName;
    }

    public String getHostName() {
        return hostName;
    }

    public String getSpeed() {
        return speed;
    }
}
