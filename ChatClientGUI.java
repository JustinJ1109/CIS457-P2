import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;

import java.awt.event.*;
import java.util.Random;

public class ChatClientGUI {
    
    public ChatClientGUI(String title) {
        /* GUI frame holds everything*/
        JFrame frame = new JFrame(title);

        /* Hold all GUI elements for connect to host section */
        JPanel mainPanel = new JPanel();
        JPanel connectionPanel = new JPanel();
        JPanel searchPanel = new JPanel();
        JPanel FTPPanel = new JPanel();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        GroupLayout connectionLayout = new GroupLayout(connectionPanel);
        GroupLayout searchLayout = new GroupLayout(searchPanel);
        GroupLayout ftpLayout = new GroupLayout(FTPPanel);
        
        connectionPanel.setLayout(connectionLayout);
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
        connectionLayout.setAutoCreateGaps(true);
        connectionLayout.setAutoCreateContainerGaps(true);

        searchPanel.setLayout(searchLayout);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchLayout.setAutoCreateGaps(true);
        searchLayout.setAutoCreateContainerGaps(true);

        FTPPanel.setLayout(ftpLayout);
        FTPPanel.setBorder(BorderFactory.createTitledBorder("FTP"));
        ftpLayout.setAutoCreateGaps(true);
        ftpLayout.setAutoCreateContainerGaps(true);

        /* Labels */
        // HOST SECTION
        JLabel serverHostNameLabel = new JLabel("Server Hostname");
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
        JTextField serverHostNameField = new JTextField(16);
        JTextField portField = new JTextField(7);
        JTextField userNameField = new JTextField(16);
        JTextField hostNameField = new JTextField(64);

        // SEARCH SECTION
        JTextField keywordField = new JTextField(16);

        // FTP SECTION
        JTextField commandField = new JTextField(60);

        JTextArea ftpTextArea = new JTextArea();        
        
        /* Drop-Down menu */
        // HOST SECTION
        String[] options = {"Ethernet", "Modem", "T1", "T3"};
        JComboBox<String> speedBox = new JComboBox<>(options);
        
        /* Search Table */
        String[] colNames = {"speed", "hostname", "filename"};
        
        String[][] data = {{"SPEED_1", "HOST1", "FILENAME1"}, {"SPEED_2", "HOST2", "FILENAME2"}};

        JTable searchTable = new JTable(data, colNames);
        searchTable.setPreferredScrollableViewportSize(searchTable.getPreferredSize());
        JScrollPane tableScrollPane = new JScrollPane(searchTable);
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new FlowLayout());
        tablePanel.add(tableScrollPane);

        /* Button */
        JButton connectButton = new JButton("Connect");
        JButton searchButton = new JButton("Search");
        JButton ftpButton = new JButton("Go");

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent aActionEvent) {
                if (aActionEvent.getSource() == connectButton) {
                    // TODO: add connect button pressed logic here
                    //////////////////////////////////////////////
                    System.out.println("Connect button pressed!");

                    /* Holds user's input*/
                    /****************** USER VARIABLES  ***************************/
                    String serverHostName = serverHostNameField.getText();
                    String port = portField.getText();
                    String userName = userNameField.getText();
                    String hostName = hostNameField.getText();
                    String speed = speedBox.getSelectedItem().toString();
                    System.out.println("Hostname: " + serverHostName + "\nPort: " + port);
                    System.out.println("User Name: " + userName + "\nHost Name: " + hostName);
                    System.out.println("Speed: " + speed);
                    /**************************************************************/
                }
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent aActionEvent) {
                if (aActionEvent.getSource() == searchButton) {
                    // TODO: add connect button pressed logic here
                    //////////////////////////////////////////////
                    System.out.println("Search button pressed!");

                    /* Holds user's input*/
                    String keyword = keywordField.getText();
                    System.out.println("Searching for: " + keyword);
                }
            }
        });
        
        ftpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent aActionEvent) {
                if (aActionEvent.getSource() == ftpButton) {
                    // TODO: add connect button pressed logic here
                    //////////////////////////////////////////////
                    System.out.println("Go button pressed!");

                    /* Holds user's input*/
                    ftpTextArea.append("appending testString " + new Random().nextInt() + "\n");
                }
            }
        });

        /* Set layout for page */
        connectionLayout.setHorizontalGroup(
            connectionLayout.createSequentialGroup()

            // 1st col
            .addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(serverHostNameLabel)
                .addComponent(serverUserNameLabel)
            )

            // 2nd col
            .addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(serverHostNameField, 40, 300, 300)
                .addComponent(userNameField, 40, 300, 300)
            )

            // 3rd col
            .addContainerGap(60, 60)

            // 4th col
            .addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(serverPortLabel)
                .addComponent(hostNameLabel)
            )

            // 5th col
            .addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(portField, 40, 100, 100)
                .addComponent(hostNameField, 40, 160, 160)
            )
            // 6th col
            .addContainerGap(50, 50)

            // 7th col
            .addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(connectButton, 150, 150, 150)
                .addGroup(connectionLayout.createSequentialGroup()
                    .addComponent(speedLabel)
                    .addComponent(speedBox, 40, 100, 100)
                )
            )
        );
        /* set vertical layout for page */
        connectionLayout.setVerticalGroup(
            connectionLayout.createSequentialGroup()
            // 1st row
            .addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(serverHostNameLabel)
                .addComponent(serverHostNameField)
                .addComponent(serverPortLabel)
                .addComponent(portField)
                .addComponent(connectButton)
            )
            // 2nd row
            .addGroup(connectionLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(serverUserNameLabel)
                .addComponent(userNameField)
                .addComponent(hostNameLabel)
                .addComponent(hostNameField)
                .addComponent(speedLabel)
                .addComponent(speedBox)
            )
        );

        /** SEARCH FORMAT */
        searchLayout.setHorizontalGroup(
            searchLayout.createSequentialGroup()
            .addGroup(searchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(searchLayout.createSequentialGroup()
                    .addComponent(keywordLabel)
                    .addComponent(keywordField, 100, 300, 300)
                    .addComponent(searchButton)
                )
            .addComponent(tablePanel)
            )
        );

        searchLayout.setVerticalGroup(
            searchLayout.createSequentialGroup()
            .addGroup(searchLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(keywordLabel)
                .addComponent(keywordField)
                .addComponent(searchButton)
            )
            .addContainerGap(50, 50)
            .addComponent(tablePanel)
        );

        ftpLayout.setHorizontalGroup(
            ftpLayout.createSequentialGroup()
            .addGroup(ftpLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(ftpLayout.createSequentialGroup()
                    .addComponent(commandLabel)
                    .addComponent(commandField, 200, 200, 200)
                    .addComponent(ftpButton)
                )
                .addComponent(ftpTextArea, 500, 500, 500)
            )
            
        );

        ftpLayout.setVerticalGroup(
            ftpLayout.createSequentialGroup()
            .addGroup(ftpLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(commandLabel)
                .addComponent(commandField)
                .addComponent(ftpButton)
            )
            .addComponent(ftpTextArea)
        );

        /** finalize frame settings **/
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        
        mainPanel.add(connectionPanel);
        mainPanel.add(searchPanel);
        mainPanel.add(FTPPanel);

        JScrollPane mainPanelScroll = new JScrollPane(
            mainPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        // faster scroll speed
        mainPanelScroll.getVerticalScrollBar().setUnitIncrement(16);

        // add connection panel to frame
        frame.getContentPane().add(mainPanelScroll);

        // center frame to screen
        frame.setLocationRelativeTo(null);

        // set visibility
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ChatClientGUI cgui = new ChatClientGUI("GV-NAPSTER Host");
    }
}
