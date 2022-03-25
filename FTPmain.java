public class FTPmain {
    public static void main(String[] args) {
        FTPClient model = new FTPClient();
        FTPClientGUI view = new FTPClientGUI("CIS457 Project 2");
        FTPClientController controller = new FTPClientController(model, view);

        controller.initController();
    }
}
