package lk.ijse.chat_application.controller.server;

import lk.ijse.chat_application.controller.ClientWindowFormController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable{

    public static final List<ClientHandler> clientHandlerList = new ArrayList<>();
    public static ClientWindowFormController chatFormController;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final String clientName;
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        clientName = inputStream.readUTF();
        clientHandlerList.add(this);
    }

    @Override
    public void run() {
        l1: while (socket.isConnected()) {
            try {
                String message = inputStream.readUTF();
                if (message.equals("*image*")) {
                   receiveImage();
                } else {
                    for (ClientHandler handler : clientHandlerList) {

                        System.out.println(handler.clientName);
                        System.out.println(clientName);
                        if (!handler.clientName.equals(clientName)) {
                            handler.sendMessage(clientName, message);
                        }
                    }
                }
            } catch (IOException e) {

                clientHandlerList.remove(this);
//                System.out.println(clientName+" removed");
//                System.out.println(clientHandlerList.size());
                break;
            }
        }
    }

    private void sendMessage(String sender, String msg) throws IOException {
        outputStream.writeUTF(sender + ": " + msg);
        outputStream.flush();
    }

    private void receiveImage() throws IOException {
        int size = inputStream.readInt();
        byte[] bytes = new byte[size];
        inputStream.readFully(bytes);
        for (ClientHandler handler : clientHandlerList) {
            if (!handler.clientName.equals(clientName)) {
                handler.sendImage(clientName, bytes);
//                System.out.println(clientName+" - image sent ");
            }
        }
    }

    private void sendImage(String sender, byte[] bytes) throws IOException {
        outputStream.writeUTF("*image*");
        outputStream.writeUTF(sender);
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);
        outputStream.flush();
    }
}
