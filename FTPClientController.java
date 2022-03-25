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

        String temp;

        // Check that user input all fields
        // give data to FTPClient
        if ((temp = view.getServerHostNameField().getText()).equals("")) {
            view.appendTextBoxln("Server Host Name required");
        }
        else {
            model.setServerHostName(temp);
        }

        if ((temp = view.getPortField().getText()).equals("")) {
            view.appendTextBoxln("Port required");
        }
        else {
            model.setPort(temp);
        }

        if ((temp = view.getUserNameField().getText()).equals("")) {
            view.appendTextBoxln("Username required");
        }
        else {
            model.setUserName(temp);
        }

        if ((temp = view.getHostNameField().getText()).equals("")) {
            view.appendTextBoxln("Host name required");
        }
        else {
            model.setHostName(temp);
        }
    }

    private void searchFor() {
        System.out.println("Running searchFor");
        if (view.getKeywordField().getText().equals("")) {
            view.appendTextBoxln("Showing all results...");
        }
        else {
            view.appendTextBoxln("Showing all that match '" + view.getKeywordField().getText() + "'");
        }

        model.searchFor(view.getKeywordField().getText());
    }

    private void doCommand() {
        System.out.println("Running doCommand");
        

    }
}
