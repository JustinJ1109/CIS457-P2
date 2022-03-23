
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ChatClientGUI {
    
    public ChatClientGUI(String title) {
        JFrame frame = new JFrame(title);

        /* Hold all GUI elements for connect to host section */
        JPanel connection_panel = new JPanel();

        /* Labels for host section*/
        JLabel serverHostNameLabel = new JLabel("Server Hostname");
        JLabel serverPortLabel = new JLabel("Port");
        
        /* Button that connects to server with user's given input */
        JButton connect_button = new JButton("Connect");
        connect_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent aActionEvent) {
                if (aActionEvent.getSource() == connect_button) {
                    // TODO: add connect button pressed logic here
                    //////////////////////////////////////////////
                    System.out.println("Connect button pressed!");
                }
            }
        });
        
        /* retrieves user input for host section fields */
        JTextField serverHostName = new JTextField(10);
        JTextField portField = new JTextField(6);

        /* Holds user's input*/
        String hostName = serverHostName.getText();
        String port = portField.getText();

        /* add all previous fields to panel */
        // Host name
        connection_panel.add(serverHostNameLabel);
        connection_panel.add(serverHostName);

        // port
        connection_panel.add(serverPortLabel);
        connection_panel.add(portField);

        // button
        connection_panel.add(connect_button);

        /** finalize frame settings **/
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        // add connection panel to frame
        frame.getContentPane().add(BorderLayout.NORTH, connection_panel);

        // center frame to screen
        frame.setLocationRelativeTo(null);

        // set visibility
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ChatClientGUI cgui = new ChatClientGUI("Test");
    }
}
