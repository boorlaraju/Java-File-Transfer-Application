import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class FileTransferClientGUI extends JFrame {
    private JTextArea statusArea;
    private JButton sendButton;
    private JTextField serverAddressField;
    private JFileChooser fileChooser;

    public FileTransferClientGUI() {
        setTitle("File Transfer Client");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // GUI Components
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        sendButton = new JButton("Send File");
        serverAddressField = new JTextField("Enter Server IP (e.g., 127.0.0.1)", 20);

        // File chooser
        fileChooser = new JFileChooser();

        // Add components to frame
        JPanel topPanel = new JPanel();
        topPanel.add(serverAddressField);
        topPanel.add(sendButton);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);

        sendButton.addActionListener(e -> chooseAndSendFile());

        setVisible(true);
    }

    private void chooseAndSendFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String serverAddress = serverAddressField.getText().trim();
            sendFile(file, serverAddress);
        }
    }

    private void sendFile(File file, String serverAddress) {
        new Thread(() -> {
            try (Socket socket = new Socket(serverAddress, 5000);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 FileInputStream fileIn = new FileInputStream(file)) {

                statusArea.append("Connected to server. Sending file: " + file.getName() + "\n");

                // Send the filename
                out.writeUTF(file.getName());

                // Send the file content
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                statusArea.append("File sent successfully.\n");

            } catch (IOException ex) {
                statusArea.append("Error sending file: " + ex.getMessage() + "\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileTransferClientGUI::new);
    }
}

