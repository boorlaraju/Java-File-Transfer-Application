import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferServerGUI extends JFrame {
    private JTextArea statusArea;
    private JButton startButton;
    private ServerSocket serverSocket;

    public FileTransferServerGUI() {
        setTitle("File Transfer Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // GUI Components
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        startButton = new JButton("Start Server");

        // Add components to frame
        add(new JScrollPane(statusArea), BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startServer());

        setVisible(true);
    }

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                statusArea.append("Server started. Waiting for clients...\n");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    statusArea.append("Client connected. Receiving file...\n");

                    // Handle file receiving
                    try (DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {
                        // Read filename
                        String fileName = in.readUTF();
                        File receivedFile = new File("received_" + fileName);

                        // Save file
                        try (FileOutputStream fileOut = new FileOutputStream(receivedFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                fileOut.write(buffer, 0, bytesRead);
                            }
                            statusArea.append("File received successfully: " + receivedFile.getName() + "\n");
                        }
                    } catch (IOException ex) {
                        statusArea.append("Error receiving file: " + ex.getMessage() + "\n");
                    }
                    clientSocket.close();
                }
            } catch (IOException ex) {
                statusArea.append("Server error: " + ex.getMessage() + "\n");
            }
        }).start();
        startButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileTransferServerGUI::new);
    }
}

