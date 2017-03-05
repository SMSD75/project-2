package common;

import org.json.*;

import java.io.*;
import java.net.*;

/**
 * Created by Shayan on 2/27/2017.
 */
public class JsonIOHandler extends SocketIOHandler{

    public JsonIOHandler(Socket clientSocket) throws IOException {
        super(clientSocket);
    }

    public JSONObject getJsonMassage() throws IOException {

        StringBuilder message = new StringBuilder();
        String line;

        while (!reader.ready());

        while (reader.ready()) {
            line = reader.readLine();
            message.append(line);
        }

        JSONObject result = new JSONObject(message.toString());

        return result;
    }

    public void sendJsonMassage(JSONObject response) throws IOException {
        writer.print(response.toString() + "\n");
        writer.flush();
    }

    public static void sendBroadcastJsonMassage(DatagramSocket broadcastSocket, int broadcastPort, JSONObject broadcastObject) throws IOException {
        byte[] broadcastMassage = (broadcastObject.toString()).getBytes();
        InetAddress ip = InetAddress.getByName("255.255.255.255");
        DatagramPacket dp = new DatagramPacket(broadcastMassage , broadcastMassage.length , ip , broadcastPort);
        broadcastSocket.send(dp);
    }

    public static JSONObject getBroadcastJsonMassage(MulticastSocket broadcastSocket) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        broadcastSocket.receive(packet);

        byte[] data = packet.getData();
        String dataToString = new String(data, 0, packet.getLength());
        JSONObject jsonObject = new JSONObject(dataToString);

        return jsonObject;
    }

}
