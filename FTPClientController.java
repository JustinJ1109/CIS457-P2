/* The controller class of the FTP client

Responsible for changing the model based on the user
*/

public class FTPClientController {
    private FTPClient model;
    private FTPClientGUI view;

    public FTPClientController(FTPClient m, FTPClientGUI v) {
        model = m;
        view = v;
    }

    public void initController() {
        view.getConnectButton().addActionListener(e -> connectToServer());
        view.getSearchButton().addActionListener(e -> searchFor());
        view.getFtpButton().addActionListener(e -> doCommand());
    }

    private void connectToServer() {
        System.out.println("Running connectToServer");
        System.out.println("Server Host Name: " + view.getServerHostNameField().getText());
        System.out.println("Port: " + view.getPortField().getText());
        System.out.println("User Name: " + view.getUserNameField().getText());
        System.out.println("Host Name: " + view.getHostNameField().getText());
        System.out.println("Speed: " + view.getSpeedBox().getSelectedItem());

        if (view.getServerHostNameField().getText().equals("")) {
            view.appendTextBoxln("Server Host Name required");
        }
        if (view.getPortField().getText().equals("")) {
            view.appendTextBoxln("Port required");
        }
        if (view.getUserNameField().getText().equals("")) {
            view.appendTextBoxln("Username required");
        }
        if (view.getHostNameField().getText().equals("")) {
            view.appendTextBoxln("Host name required");
        }
    }

    private void searchFor() {
        System.out.println("Running searchFor");
        if (view.getKeywordField().getText() == null) {
            view.appendTextBoxln("Showing all results...");
        }
        else {
            view.appendTextBoxln("Showing results for keyword: " + view.getKeywordField().getText());
        }
    }

    private void doCommand() {
        System.out.println("Running doCommand");
        

    }
}
