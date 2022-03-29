public class FTPClientController {
    private FTPClient model;
    private FTPClientGUI view;

    private boolean connectedToServer = false;

    public FTPClientController(FTPClient m, FTPClientGUI v) {
        model = m;
        view = v;
        System.out.println("controller created");
    }

    /* Add button listeners to GUI */
    public void initController() {
        view.getConnectButton().addActionListener(e -> connectToServer());
        view.getSearchButton().addActionListener(e -> searchFor());
        view.getFtpButton().addActionListener(e -> doCommand());
        System.out.println("controller initilized");
    }

    /* Connect to the server when user presses button */
    private void connectToServer() {

        if (connectedToServer) {
            disconnectFromServer();
            return;
        }
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
        //FIXME: uncomment
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

        model.setSpeed(view.getSpeedBox().getSelectedItem().toString());

        // FIXME: remove, this is only for justin
        // model.setServerHostName("192.168.1.64");
        // view.getServerHostNameField().setText("192.168.1.64");
        // model.setUserName("j");
        // view.getUserNameField().setText("j");
        // model.setPort(1370);
        // view.getPortField().setText("1370");
        // model.setHostName("pc");
        // view.getHostNameField().setText("pc");


        // all info looks good, try to connect
        if (!invalidInput) {
            if (model.doConnection()) {
                view.appendTextBoxln("Connected to " + model.getServerHostName() + " on port " + model.getPort());
                connectedToServer = true;
                view.getConnectButton().setText("Disconnect");
            
            }
        }
    }

    private void disconnectFromServer() {
        view.getServerHostNameField().setText("");
        view.getPortField().setText("");
        view.getUserNameField().setText("");
        view.getHostNameField().setText("");
        model.disconnectFromServer();
        connectedToServer = false;
        view.getConnectButton().setText("Connect");

        view.appendTextBoxln("Disconnected from server");
    }

    /* Give view the new table received from model */
    private void searchFor() {
        if (!connectedToServer) {
            view.appendTextBoxln("[!] Must be connected to a server [!]");
            return;
        }
        System.out.println("Running searchFor");
        if (view.getKeywordField().getText().equals("")) {
            view.appendTextBoxln("Must input a keyword");
            return;
        }

        else {
            model.searchFor(view.getKeywordField().getText());
            view.appendTextBoxln("Showing all that match '" + view.getKeywordField().getText() + "'");
        }

        String[] eles = model.getTableData().toArray(new String[model.getTableData().size()]);
        int i = 0;
        String[][] data = new String[eles.length][3];
        for (String s: eles) {
            data[i] = s.split(" ");
            i++;
        }
        // view.setData(data);

        System.out.println("data length " + data.length);
        for (i = 0; i < data.length; i++) {
            view.getSearchTable().setValueAt(data[i][0], 0, 0);
            view.getSearchTable().setValueAt(data[i][1], 0, 1);
            view.getSearchTable().setValueAt(data[i][2], 0, 2);
        };
        
        // view.resetSearchTable(data);
        //FIXME: does not update GUI for some reason
        // look up how to update JTable data
        // Or maybe how to update JTable within a JScrollPane
        // CHECK OUT LINES 127-131 IN GUI

        System.out.println("row count " + view.getSearchTable().getRowCount());
    }

    /* Called when player presses Go button */
    private void doCommand() {
        System.out.println("Doing command");
        if (!connectedToServer) {
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
            String[] fileToGet = null;

            try{
                fileToGet = view.getData()[view.getSearchTable().getSelectedRow()];
            }
            catch (Exception e) {
                view.appendTextBoxln("Must select a row");
                System.out.println("User did not select a row");
                return;
            }
            if (fileToGet == null || fileToGet[0].equals(" ") || fileToGet[0].equals("")) {
                view.appendTextBoxln("Must select a file");
                return;
            }

            if (model.doGet(fileToGet)) {
                view.appendTextBoxln("Getting " + fileToGet[2]);
            }
            else {
                view.appendTextBoxln("Unable to get " + fileToGet[2]);
            }

            view.getCommandField().setText("");
            view.getSearchTable().clearSelection();
        }
        else if (command.equals("list")) {
            if (model.doList()) {
                view.appendTextBox("Received: ");
                view.appendTextBoxln(model.getTableData().toString());
            }
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
