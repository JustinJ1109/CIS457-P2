
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ChatClientGUI {
    
    public ChatClientGUI(String title) {
        /* GUI frame holds everything*/
        JFrame frame = new JFrame(title);

        /* Hold all GUI elements for connect to host section */
        JPanel connection_panel = new JPanel();

        GroupLayout layout = new GroupLayout(connection_panel);
        connection_panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        /* Labels for host section*/
        JLabel serverHostNameLabel = new JLabel("Server Hostname");
        JLabel serverPortLabel = new JLabel("Port");
        JLabel serverUserNameLabel = new JLabel("Username");
        JLabel hostNameLabel = new JLabel("Hostname");
        JLabel speedLabel = new JLabel("Speed");

        /* retrieves user input for host section fields */
        JTextField serverHostName = new JTextField(16);
        JTextField portField = new JTextField(7);
        JTextField userName = new JTextField(16);
        JTextField hostName = new JTextField(64);
        
        String[] options = {"Ethernet", "Test"};
        JComboBox<String> speed = new JComboBox<>(options);
        
        /* Button that connects to server with user's given input */
        JButton connect_button = new JButton("Connect");
        connect_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent aActionEvent) {
                if (aActionEvent.getSource() == connect_button) {
                    // TODO: add connect button pressed logic here
                    //////////////////////////////////////////////
                    System.out.println("Connect button pressed!");

                    /* Holds user's input*/
                    String hostName = serverHostName.getText();
                    String port = portField.getText();
                    System.out.println("Hostname: " + hostName + "\nPort: " + port);
                }
            }
        });
        
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(serverHostNameLabel)
                .addComponent(serverUserNameLabel)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(serverHostName, 40, 300, 300)
                .addComponent(userName, 40, 300, 300)
            )
            .addContainerGap(60, 60)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(serverPortLabel)
                .addComponent(hostNameLabel)
            )

            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(portField, 40, 100, 100)
                .addComponent(hostName, 40, 160, 160)
            )
            .addContainerGap(50, 50)

            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(connect_button, 200, 200, 200)

                .addGroup(layout.createSequentialGroup()
                    .addComponent(speedLabel)
                    .addComponent(speed, 40, 100, 100)
                )
                
            )
                
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(serverHostNameLabel)
                .addComponent(serverHostName)
                .addComponent(serverPortLabel)
                .addComponent(portField)
                .addComponent(connect_button)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(serverUserNameLabel)
                .addComponent(userName)
                .addComponent(hostNameLabel)
                .addComponent(hostName)
                .addComponent(speedLabel)
                .addComponent(speed)
            )
        );


        /* add all previous fields to panel */
        // Host name
        // connection_panel.add(serverHostNameLabel);
        // connection_panel.add(serverHostName);

        // // port
        // connection_panel.add(serverPortLabel);
        // connection_panel.add(portField);

        // // button
        // connection_panel.add(connect_button);

        /** finalize frame settings **/
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // add connection panel to frame
        frame.getContentPane().add(connection_panel);

        // center frame to screen
        frame.setLocationRelativeTo(null);

        // set visibility
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ChatClientGUI cgui = new ChatClientGUI("Test");
    }
}
