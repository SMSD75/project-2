package agent;

import common.FileHandler;
import common.SocketIOHandler;
import common.StringHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shayan on 2/27/2017.
 */
public class Agent {

    public static int AGENT_LISTEN_PORT = 1234;
    public static String AGENT_CONF_FILE = "agentConf.txt";
    public static ServerSocket agentListenSocket;
    public static void main(String args[]) throws IOException {
        try {
            initAgent();
            runAgent();
        }
        catch (Exception e) {

            System.out.println("SOMETHING WENT WRONG!");
            e.printStackTrace();
            terminateAgent();
        }
    }

    private static void initAgent() throws IOException {
        agentListenSocket = new ServerSocket(AGENT_LISTEN_PORT);
    }

    private static void terminateAgent() throws IOException {
        agentListenSocket.close();
    }

    private static void runAgent() throws IOException {
        Socket newClientSocket;

        while (true) {
            newClientSocket = agentListenSocket.accept();
            SocketIOHandler socketIOHandler = new SocketIOHandler(newClientSocket);
            NewClientHandler newClientHandler = new NewClientHandler(socketIOHandler);
            newClientHandler.start();
        }
    }

    public static class NewClientHandler extends Thread{

        private SocketIOHandler socketIOHandler;

        public NewClientHandler(SocketIOHandler socketIOHandler) {
            this.socketIOHandler = socketIOHandler;
        }

        public void run() {
            try {
                handleNewClient(socketIOHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void handleNewClient(SocketIOHandler socketIOHandler) throws IOException {
        while (true) {
            String clientRequest = socketIOHandler.getMassage();
            System.out.println("CLIENT REQUEST IS: " + clientRequest);
            List<String> agentConf = findAgentConf();
            System.out.println("ROOT SERVER IP: " + agentConf.get(0) + " ROOT SERVER PORT: " + agentConf.get(1));
            AgentRequestHandler agentRequestHandler = new AgentRequestHandler(agentConf.get(0), Integer.parseInt(agentConf.get(1)));
            List<String> request = StringHandler.parseStringByDiv(clientRequest, " ");
            String requestValue = getRequestValue(request);
            String requestKey = getRequestKey(request);
            String agentResponse = agentRequestHandler.handleRequest(requestKey, requestValue); // key Domain, Value ipAddress
            socketIOHandler.sendMassage(agentResponse + "\n");
        }
    }

    private static String getRequestKey(List<String> request) {
        return request.get(0);
    }

    private static String getRequestValue(List<String> request) {
        String requestKey = request.get(1);
        for (int i = 2; i < request.size(); i++) {
            requestKey += " " + request.get(i);
        }
        return requestKey;
    }


    private static List<String> findAgentConf() {
        FileHandler fileHandler = new FileHandler(AGENT_CONF_FILE);
        ArrayList<String> fileLines = fileHandler.getLines();
        StringHandler stringHandler = new StringHandler();
        List<String> agentConf = stringHandler.parseStringByDiv(fileLines.get(0), " ");
        return agentConf;
    }

}
