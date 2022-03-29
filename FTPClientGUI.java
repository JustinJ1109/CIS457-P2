import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/* The GUI/View class of the FTPClient

Responsible for drawing to the screen and capturing user input for the
controller to use
*/
public class FTPClientGUI {
    
    final private String[] colNames = {"Speed", "Hostname", "Filename"};

    private String[][] data = {{" ", " ", " "}};

    private JButton connectButton;
    private JButton searchButton;
    private JButton ftpButton;

    private JTextArea ftpTextArea;

    // Data table of files
    private JTable searchTable;

    /* Text Fields */
    // HOST SECTION
    private JTextField serverHostNameField;
    private JTextField portField;
    private JTextField userNameField;
    private JTextField hostNameField;
    private JComboBox<String> speedBox;

    // SEARCH SECTION
    private JTextField keywordField;

    // FTP SECTION
    private JTextField commandField;

    private JScrollPane tableScrollPane;

    public FTPClientGUI(String title) {
        /* GUI frame holds everything*/
        JFrame frame = new JFrame(title);

        // Main panels that fit in frame
        // holds all sub-panels
        JPanel mainPanel = new JPanel();

        // hold components for their section
        JPanel connectionPanel = new JPanel();
        JPanel searchPanel = new JPanel();
        JPanel ftpPanel = new JPanel();

        BoxLayout mainLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(mainLayout);

        /* SET GROUP LAYOUTS AND ASSIGN PANELS */
        GroupLayout connectionLayout = new GroupLayout(connectionPanel);
        connectionLayout.setAutoCreateGaps(true);
        connectionLayout.setAutoCreateContainerGaps(true);
        connectionPanel.setLayout(connectionLayout);

        GroupLayout searchLayout = new GroupLayout(searchPanel);
        searchLayout.setAutoCreateGaps(true);
        searchLayout.setAutoCreateContainerGaps(true);
        searchPanel.setLayout(searchLayout);

        GroupLayout ftpLayout = new GroupLayout(ftpPanel);
        ftpLayout.setAutoCreateGaps(true);
        ftpLayout.setAutoCreateContainerGaps(true);
        ftpPanel.setLayout(ftpLayout);

        // these are the titled blue borders on the layout example
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        ftpPanel.setBorder(BorderFactory.createTitledBorder("FTP"));

    /****************** SWING COMPONENTS BELOW *************************/
    /*******************************************************************/
    /* Labels */
        // HOST SECTION
        JLabel serverHostNameLabel = new JLabel("Server IP");
        JLabel serverPortLabel = new JLabel("Port");
        JLabel serverUserNameLabel = new JLabel("Username");
        JLabel hostNameLabel = new JLabel("Hostname");
        JLabel speedLabel = new JLabel("Speed");

        // SEARCH SECTION
        JLabel keywordLabel = new JLabel("Keyword");

        // FTP SECTION
        JLabel commandLabel = new JLabel("Enter Command");

    /* Text Fields */
        // HOST SECTION
        serverHostNameField = new JTextField(16);
        portField = new JTextField(7);
        userNameField = new JTextField(16);
        hostNameField = new JTextField(64);    
    
        // SEARCH SECTION
        keywordField = new JTextField(16);
    
        // FTP SECTION
        commandField = new JTextField(60);

    /* Text Area */
        //FTP SECTION
        ftpTextArea = new JTextArea();
        ftpTextArea.setSize(new Dimension(500, 500)); 
        ftpTextArea.setLineWrap(true);
        ftpTextArea.setEditable(false); 
        
        // make output panel scrollable
        JScrollPane ftpScrollPane = new JScrollPane(ftpTextArea);
        // ftpScrollPane.setPreferredSize(new Dimension(500, 100));

    /* Drop-Down menu */
        // HOST SECTION
        String[] options = {"Ethernet", "Modem", "T1", "T3"};
        speedBox = new JComboBox<>(options);
        
    /* Search Table */
        
        //TODO: retrieve table from server and insert to this table
        
        searchTable = new JTable(data, colNames);
        searchTable.setPreferredScrollableViewportSize(searchTable.getPreferredSize());
        
        /* scroll pane holds table, without it, doesn't show headers*/
        tableScrollPane = new JScrollPane(searchTable);

    /* Buttons */
        connectButton = new JButton("Connect");
        searchButton = new JButton("Search");
        ftpButton = new JButton("Go");

    /**********************  All components END **************************/

        // set column size for text fields
        serverHostNameField.setColumns(22);
        userNameField.setColumns(10);
        hostNameField.setColumns(16);
        // set button dimensions
        connectButton.setPreferredSize(new Dimension(160,20));

    /* BUILDING CONNECTION PANEL */
        // top line of connection panel
        JPanel connectionPanelTop = new JPanel();
        connectionPanelTop.add(serverHostNameLabel);
        connectionPanelTop.add(serverHostNameField);
        connectionPanelTop.add(serverPortLabel);
        connectionPanelTop.add(portField);
        connectionPanelTop.add(connectButton);

        // bottom line of connection panel
        JPanel connectionPanelBot = new JPanel();
        connectionPanelBot.add(serverUserNameLabel);
        connectionPanelBot.add(userNameField);
        connectionPanelBot.add(hostNameLabel);
        connectionPanelBot.add(hostNameField);
        connectionPanelBot.add(speedLabel);
        connectionPanelBot.add(speedBox);
        
        // let both panels sit in horizontal parallel
        connectionLayout.setHorizontalGroup(
            connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(connectionPanelTop)
                .addComponent(connectionPanelBot)     
        );

        // put top panel above bot panel
        connectionLayout.setVerticalGroup(
            connectionLayout.createSequentialGroup()
                .addComponent(connectionPanelTop)
                .addComponent(connectionPanelBot)
        );

    /* BUILDING SEARCH PANEL */
        keywordField.setColumns(22);
        searchButton.setSize(new Dimension(80, 20));

        JPanel searchPanelTop = new JPanel();
        searchPanelTop.add(keywordLabel);
        searchPanelTop.add(keywordField);
        searchPanelTop.add(searchButton);

        JPanel searchPanelBot = new JPanel();
        searchPanelBot.add(tableScrollPane);

        searchLayout.setHorizontalGroup(
            searchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(searchPanelTop)
                .addComponent(searchPanelBot)
        );

        searchLayout.setVerticalGroup(
            searchLayout.createSequentialGroup()
                .addComponent(searchPanelTop)
                .addComponent(searchPanelBot)
        );

    /* BUILDING FTP PANEL */
        ftpPanel.add(commandLabel);
        ftpPanel.add(commandField);
        ftpPanel.add(ftpButton);
        ftpPanel.add(ftpScrollPane);

        commandField.setColumns(30);
        ftpButton.setSize(new Dimension(50, 20));

        JPanel ftpPanelTop = new JPanel();
        ftpPanelTop.add(commandLabel);
        ftpPanelTop.add(commandField);
        ftpPanelTop.add(ftpButton);

        JPanel ftpPanelBot = new JPanel();
        ftpPanelBot.add(ftpScrollPane);

        ftpLayout.setHorizontalGroup(
            ftpLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(ftpPanelTop)
                .addComponent(ftpPanelBot)
        );

        ftpLayout.setVerticalGroup(
            ftpLayout.createSequentialGroup()
                .addComponent(ftpPanelTop)
                .addComponent(ftpPanelBot)
        );

        Dimension screenSize = new Dimension(1000,600);
        /** finalize frame settings **/
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenSize);
        

        connectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ftpPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        mainPanel.add(connectionPanel);
        mainPanel.add(searchPanel);
        mainPanel.add(ftpPanel);
        // add connection panel to frame
        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.getContentPane().add(mainScrollPane);
        

        // center frame to screen
        frame.setLocationRelativeTo(null);

        // set visibility
        frame.setVisible(true);
    }

    public String[][] getData() {
        return this.data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public void addData(String[] data, int index) {
        this.data[index] = data;
    }

    public JButton getConnectButton() {
        return this.connectButton;
    }

    public JButton getSearchButton() {
        return this.searchButton;
    }

    public JButton getFtpButton() {
        return this.ftpButton;
    }

    public void appendTextBox(String text) {
        ftpTextArea.append(text);
    }

    public JTable getSearchTable() {
        return searchTable;
    }

    public void appendTextBoxln(String text) {
        ftpTextArea.append(text + "\n");
    }

    public void clearTextBox() {
        ftpTextArea.setText("");
    }

    public JTextField getServerHostNameField() {
        return serverHostNameField;
    }

    public JTextField getPortField() {
        return portField;
    }

    public JTextField getUserNameField() {
        return userNameField;
    }

    public JTextField getHostNameField() {
        return hostNameField;
    }

    public JTextField getKeywordField() {
        return keywordField;
    }

    public JTextField getCommandField() {
        return commandField;
    }

    public JComboBox<String> getSpeedBox() {
        return speedBox;
    }

    public JScrollPane getTableScrollPane() {
        return tableScrollPane;
    }

    public void resetSearchTable(String[][] data) {
    }

}
