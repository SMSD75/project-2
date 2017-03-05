package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Shayan on 2/27/2017.
 */
public class SocketIOHandler {

    private Socket socket;
    protected BufferedReader reader;
    protected PrintWriter writer;


    public SocketIOHandler(Socket clientSocket) throws IOException {
        socket = clientSocket;
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new PrintWriter(clientSocket.getOutputStream());
    }


    public String getMassage() throws IOException {
        String massage = "";

        while (!reader.ready());

        while (reader.ready()) {
            if (!massage.equals("")) {
                massage += "\n";
            }
            massage += reader.readLine();
        }
        return massage;
    }

    public void sendMassage(String agentResponse) throws IOException {
        writer.print(agentResponse);
        writer.flush();
    }

    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
