public class FTPmain {
    public static void main(String[] args) {
        System.out.println("Main making client");
        FTPClient model = new FTPClient();
        FTPClientGUI view = new FTPClientGUI("CIS457 Project 2");
        System.out.println("Main making controller");
        FTPClientController controller = new FTPClientController(model, view);

        System.out.println("initializing controller");
        controller.initController();
    }
}
