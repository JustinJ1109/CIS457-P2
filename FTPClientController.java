public class FTPClientController {
    private FTPClient model;
    private FTPClientGUI view;

    public FTPClientController(FTPClient m, FTPClientGUI v) {
        model = m;
        view = v;
    }

    /* Add button listeners to GUI */
    public void initController() {
        view.getConnectButton().addActionListener(e -> connectToServer());
        view.getSearchButton().addActionListener(e -> searchFor());
        view.getFtpButton().addActionListener(e -> doCommand());
    }

    /* Connect to the server when user presses button */
    private void connectToServer() {
        System.out.println("Running connectToServer");
        System.out.println("Server Host Name: " + view.getServerHostNameField().getText());
        System.out.println("Port: " + view.getPortField().getText());
        System.out.println("User Name: " + view.getUserNameField().getText());
        System.out.println("Host Name: " + view.getHostNameField().getText());
        System.out.println("Speed: " + view.getSpeedBox().getSelectedItem());

        String temp;

        boolean invalidInput = false;

        /* fetch all data from fields provided from user and assign to model's variables */
        // serverHostName
        if ((temp = view.getServerHostNameField().getText()).equals("")) {
            view.appendTextBoxln("Server Host Name required");
            invalidInput = true;
        }
        else {
            model.setServerHostName(temp);
        }
        // port
        if ((temp = view.getPortField().getText()).equals("")) {
            view.appendTextBoxln("Port required");
            invalidInput = true;
        }
        else {
            try {
                int port = Integer.parseInt(temp);
                model.setPort(port);
            }
            catch (Exception e) {
                System.out.println("Invalid port receieved");
                view.appendTextBoxln("Invalid port received, try a valid number");
                invalidInput = true;
            }
        }
        // userName
        if ((temp = view.getUserNameField().getText()).equals("")) {
            view.appendTextBoxln("Username required");
            invalidInput = true;
        }
        else {
            model.setUserName(temp);
        }
        // hostName
        if ((temp = view.getHostNameField().getText()).equals("")) {
            view.appendTextBoxln("Host name required");
            invalidInput = true;
        }
        else {
            model.setHostName(temp);
        }

        // all info looks good, try to connect
        if (!invalidInput) {
            if (model.connectToServer()) {
                view.appendTextBoxln("Connected to " + model.getServerHostName() + " on port " + model.getPort());
            }
        }
    }

    /* Give view the new table received from model */
    private void searchFor() {
        if (!model.getIsConnected()) {
            view.appendTextBoxln("[!] Must be connected to a server [!]");
            return;
        }
        System.out.println("Running searchFor");
        if (view.getKeywordField().getText().equals("")) {
            view.appendTextBoxln("Showing all results...");
        }

        else {
            view.appendTextBoxln("Showing all that match '" + view.getKeywordField().getText() + "'");
        }

        model.searchFor(view.getKeywordField().getText());
    }

    /* Called when player presses Go button */
    private void doCommand() {
        if (!model.getIsConnected()) {
            view.appendTextBoxln("[!] Must be connected to a server [!]");
            return;
        }
        // get what command they want
        String command = view.getCommandField().getText();
        String filename = null;
        if (view.getSearchTable().getSelectedRow() != -1) {
            filename = view.getSearchTable().getValueAt(view.getSearchTable().getSelectedRow(), 2).toString();
        }
        
        if (command.equals("store")) {
            if (filename == null) {
                view.appendTextBoxln("Must select a file");
                return;
            }
            // successfully stored
            if(model.doStore(filename)) {
                view.appendTextBoxln("Storing " + filename);
            }
            // failed store
            else {
                view.appendTextBoxln("Unable to store " + filename);
            }

            view.getCommandField().setText("");
            view.getSearchTable().clearSelection();
        }

        else if (command.equals("get")) {
            if (filename == null) {
                view.appendTextBoxln("Must select a file");
                return;
            }

            if (model.doGet(filename)) {
                view.appendTextBoxln("Getting " + filename);
            }
            else {
                view.appendTextBoxln("Unable to get " + filename);
            }

            view.getCommandField().setText("");
            view.getSearchTable().clearSelection();
        }
        else if (command.equals("list")) {
            model.doList();
        }
        // unknown command or no command
        else {
            view.getCommandField().setText("");
            if (command.equals("")) {
                view.appendTextBoxln("Must enter a command");
                return;
            }
            view.appendTextBoxln("Unknown command, try another");
        }
    }
}
