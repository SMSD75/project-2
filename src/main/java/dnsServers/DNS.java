package dnsServers;

import common.FileHandler;
import common.JsonHandler;
import common.JsonIOHandler;
import common.StringHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Shayan on 3/1/2017.
 */
public class DNS {

    protected ServerSocket serverSocket;
    protected int SERVER_LISTEN_PORT;
    protected String SERVER_NAME;

    protected boolean ITERATIVE = true;
    protected boolean RECURSIVE = false;


    public DNS(int dns_listen_port, String server_name) throws IOException {
        SERVER_LISTEN_PORT = dns_listen_port;
        SERVER_NAME = server_name;



//        File file = new File(server_name + "Data.txt");
//            if (file.createNewFile()){
//                System.out.println("File is created!");
//            }
//            else{
//                System.out.println("File already exists.");
//            }
        }


    protected String findInFile(String search, int x, int y) {       //x: what index to search  y: what index to return

        FileHandler fileHandler = new FileHandler(SERVER_NAME + "Data.txt");
        ArrayList<String> lines = fileHandler.getLines();

        int end = lines.size();
        for (int i = 0; i < end; i++) {
            List<String> parsedLine = StringHandler.parseStringByDiv(lines.get(i), " ");
//            System.out.println(parsedLine.get(x));
            if (parsedLine.get(x).equals(search)) {
                return parsedLine.get(y);
            }
//            System.out.println("---------------------------------");
        }
        return "NOT FOUND!";
    }

    protected String findInFile(String search, String time,  int x, int y) {       //x: what index to search  y: what index to return

        FileHandler fileHandler = new FileHandler(SERVER_NAME + "Data.txt");
        ArrayList<String> lines = fileHandler.getLines();

//        System.out.println("time is : " + time);

        int end = lines.size();
        for (int i = 0; i < end; i++) {
            List<String> parsedLine = StringHandler.parseStringByDiv(lines.get(i), " ");
            if (parsedLine.get(x).equals(search) && parsedLine.get(x + 2).equals(time)) {
                return parsedLine.get(y);
            }
        }
        return "NOT FOUND!";
    }



    public static class RootDNS extends DNS {

        private int BROADCAST_PORT;

        public RootDNS(int dns_listen_port, String server_name) throws IOException {
            super(dns_listen_port, server_name);
        }

        public RootDNS(int dns_listen_port, String server_name, int broadcast_port) throws IOException {
            super(dns_listen_port, server_name);
            BROADCAST_PORT = broadcast_port;
        }

        void run() {
                try {

                    boolean method = RECURSIVE;
                    serverSocket = new ServerSocket(SERVER_LISTEN_PORT);

                    DatagramSocket broadcastSocket = new DatagramSocket();

                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            handleReceiveBroadcastMassage();
                        }
                    });

                    t.start();


                    while (true) {


                        Socket agentSocket = serverSocket.accept();

                        JsonIOHandler agentJsonHandler = new JsonIOHandler(agentSocket);
                        JSONObject agentRequest = agentJsonHandler.getJsonMassage();

                        String correspondTld = findCorrespondTldInRequest(agentRequest);
                        String tldPort = findTldPort(correspondTld);


                        JSONObject agentResponse = new JSONObject();


                        if (tldPort.equals("NOT FOUND!")) {// so broadcast to other root servers
                            System.out.println("ROOT broadcasts to other roots.");
                            agentResponse = handleNotFoundInDataBase(broadcastSocket, agentRequest);
                        }
                        else if (method == RECURSIVE) {
                            System.out.println("ROOT chooses RECURSIVE method.");
                            System.out.println("ROOT request is: " + agentRequest.toString());
                            agentResponse = handleRecursive(agentRequest, tldPort);
                        }
                        else if (method == ITERATIVE) {
                            System.out.println("ROOT chooses ITERATIVE method.");
                            agentResponse = handlerIterative(agentRequest, tldPort);
                        }

                        System.out.println("ROOT answer is: " + agentResponse.toString());

                        agentJsonHandler.sendJsonMassage(agentResponse);

                        method = !method; //change method !
                    }
                } catch(IOException e){
                    System.out.println("IOException");
                    e.printStackTrace();
//                    System.out.println("hi");
                }
        }

        private JSONObject handleNotFoundInDataBase(DatagramSocket broadcastSocket, JSONObject agentRequest) throws IOException {
            JSONObject agentResponse;

            JSONObject broadcastObject = agentRequest;
            JsonHandler.addPair(broadcastObject, "from", SERVER_NAME);
            JsonIOHandler.sendBroadcastJsonMassage(broadcastSocket, BROADCAST_PORT, broadcastObject);

            try {
                MulticastSocket forBroadcast = new MulticastSocket(BROADCAST_PORT);
                forBroadcast.setSoTimeout(2000);
                JSONObject receive = JsonIOHandler.getBroadcastJsonMassage(forBroadcast);
                String to = findToDNSInRequest(receive);
                if (to.equals(SERVER_NAME)) {
                    receive.remove("from");
                    receive.remove("to");
                    agentResponse = receive;
                }
                else { //halati ke javabe dade shode baraye ma naboode
                    JsonHandler.addPair(broadcastObject, "method", "recursive");
                    JsonHandler.updateValue(broadcastObject, "result", "NOT FOUND!");
                    agentResponse = broadcastObject;
                }
            } catch (IOException e) { //halati ke javabi be dade nshode be dalile ghozashte time out
                JsonHandler.addPair(broadcastObject, "method", "recursive");
                JsonHandler.updateValue(broadcastObject, "result", "NOT FOUND!");
                agentResponse = broadcastObject;
            }
            return agentResponse;
        }

        private void handleReceiveBroadcastMassage() {



            try {
                while (true) {

                    MulticastSocket broadcastSocket = new MulticastSocket(BROADCAST_PORT);
                    JSONObject receive = JsonIOHandler.getBroadcastJsonMassage(broadcastSocket);
//                    System.out.println("receive: " + receive);

                    String from = findFromDNSInRequest(receive);

                    if (!from.equals(SERVER_NAME)) {
                        String tldPort = findTldPort(findCorrespondTldInRequest(receive));
                        if (!tldPort.equals("NOT FOUND!")) {
                            JsonHandler.addPair(receive, "method", "iterative");
                            JSONObject response = handleRecursive(receive, tldPort);
//                            System.out.println(response);
                            JsonHandler.addPair(response, "to", from);
                            JsonIOHandler.sendBroadcastJsonMassage(broadcastSocket, BROADCAST_PORT, response);
                        }
                    }
                    broadcastSocket.close();
                }



            } catch (IOException e) {
                e.printStackTrace();
            }


//
        }


        protected JSONObject handlerIterative(JSONObject agentRequest, String tldPort) {
            JSONObject agentResponse;
            agentResponse = agentRequest;
            JsonHandler.addPair(agentResponse,"method", "iterative");
            JsonHandler.addPair(agentResponse,"nextDns", tldPort);
            return agentResponse;
        }

        protected JSONObject handleRecursive(JSONObject agentRequest, String tldPort) throws IOException {
            JSONObject tldResponse;
            JSONObject tldRequest = agentRequest;
            JsonHandler.addPair(tldRequest, "method", "recursive");

            Socket tldSocket = new Socket("localhost", Integer.parseInt(tldPort));
            JsonIOHandler tldJsonHandler = new JsonIOHandler(tldSocket);
            tldJsonHandler.sendJsonMassage(tldRequest);

            tldResponse = tldJsonHandler.getJsonMassage();
            return tldResponse;
        }

        private String findCorrespondTldInRequest(JSONObject agentRequest) {
            String domain = JsonHandler.getValue(agentRequest, "domain");
            List<String> parsedDomain = StringHandler.parseStringByDiv(domain, "\\.");
            return parsedDomain.get(parsedDomain.size() - 1);
        }

        protected String findTldPort(String correspondTld) {
            return findInFile(correspondTld, 0, 1);
        }

        private String findFromDNSInRequest(JSONObject jsonObject) {
            String from = JsonHandler.getValue(jsonObject, "from");
            return from;
        }

        private String findToDNSInRequest(JSONObject receive) {
            String to = JsonHandler.getValue(receive, "to");
            return to;
        }
    }













    public static class TldDNS extends RootDNS {

        public TldDNS(int dns_listen_port, String server_name) throws IOException {
            super(dns_listen_port, server_name);
        }

        public void run() {

            try {
                serverSocket = new ServerSocket(SERVER_LISTEN_PORT);

                while (true) {
                        Socket agentSocket = serverSocket.accept();

                        JsonIOHandler jsonIOHandler = new JsonIOHandler(agentSocket);
                        JSONObject request = jsonIOHandler.getJsonMassage();

                        String method = findMethodInRequest(request);

                        JSONObject response = request;


                        String correspondTld = findCorrespondAuthInRequest(request);
                        String tldPort = findTldPort(correspondTld);

                        if (method.equals("recursive")) {
                            System.out.println("TLD (" + SERVER_NAME + ") request is: " + request.toString());
                            response = handleRecursive(request, tldPort);
                        }
                        else if (method.equals("iterative")) {
                            response = handlerIterative(response, tldPort);
                        }

                    System.out.println("TLD (" + SERVER_NAME + ") answer is: " + response.toString());

                    jsonIOHandler.sendJsonMassage(response);

                    }
            } catch (IOException e) {
                System.out.println("IOException");

            }
        }

        private String findCorrespondAuthInRequest(JSONObject request) {
            String domain = JsonHandler.getValue(request, "domain");
            List<String> parsedDomain = StringHandler.parseStringByDiv(domain, "\\.");
            String result = parsedDomain.get(parsedDomain.size()- 2) + "." + parsedDomain.get(parsedDomain.size() - 1);
//            for (int i = parsedDomain.size() - 2; i < parsedDomain.size(); i++) {
//                result += "." + parsedDomain.get(i);
//            }
            return result;
        }

        private String findMethodInRequest(JSONObject request) {
            String result = JsonHandler.getValue(request, "method");
            return result;
        }
    }























    public static class AuthDNS extends DNS {

        public AuthDNS(int dns_listen_port, String server_name) throws IOException {
            super(dns_listen_port, server_name);
        }

        public void run() {

            try {
                serverSocket = new ServerSocket(SERVER_LISTEN_PORT);

                while (true) {
                    Socket agentSocket = serverSocket.accept();

                    JsonIOHandler jsonIOHandler = new JsonIOHandler(agentSocket);
                    JSONObject request = jsonIOHandler.getJsonMassage();

                    String requestType = findRequestTypeInRequest(request);

                    String answer = findAnswer(requestType, request);

                    JSONObject response = request;
                    JsonHandler.addPair(response, "result", answer);
                    JsonHandler.updateValue(response, "method", "recursive");
                    JsonHandler.addPair(response, "ttl", String.valueOf(getValidationTime()));

                    System.out.println("AUTHORITATIVE (" + SERVER_NAME + ") answer is: " + response.toString());

                    jsonIOHandler.sendJsonMassage(response);
                }
            } catch (IOException e) {
                System.out.println("IOException");

            }
        }

        private String findAnswer(String requestType, JSONObject request) {

            String answer = "SORRY I CAN'T!";
            if (requestType.equals("find")) {
                answer = handleFind(request);
            }
            else if(requestType.equals("add")) {
                answer = handleAdd(request);
            }
            else if(requestType.equals("update")) {
                answer = handleUpdate(request);
            }
            return answer;
        }

        private String handleUpdate(JSONObject request) {
            String answer;
            String domain = findDomainInRequest(request);
            String time = findTimeInRequest(request);
            String oldIp = findIpInDataBase(domain, time);
            String newIp = findIpInRequest(request);
            FileHandler fileHandler = new FileHandler(SERVER_NAME + "Data.txt");
            fileHandler.findAndReplaceWord(oldIp, newIp, time);
            answer = "done";
            return answer;
        }

        private String handleAdd(JSONObject request) {
            String answer;
            String domain = findDomainInRequest(request);
            String ip = findIpInRequest(request);
            String time = findTimeInRequest(request);
            FileHandler fileHandler = new FileHandler(SERVER_NAME + "Data.txt");
            fileHandler.addLineToFile(domain + " " + ip + " " + time);
            answer = "done";
            return answer;
        }

        private String handleFind(JSONObject request) {
            String answer;
            String domain = findDomainInRequest(request);
            String time = findTimeInRequest(request);
            answer = findIpInDataBase(domain, time);
            return answer;
        }

        private Long getValidationTime()
        {
            Random rand = new Random();
            return Long.valueOf(rand.nextInt(100));
        }

        private String findRequestTypeInRequest(JSONObject request) {
            String requestType = JsonHandler.getValue(request, "request");
            return requestType;
        }

        private String findIpInDataBase(String domain, String time) {
            return findInFile(domain, time,  0, 1);
        }

        private String findDomainInRequest(JSONObject request) {
            String domain = JsonHandler.getValue(request, "domain");
            return domain;
        }

        private String findTimeInRequest(JSONObject request) {
            String time = JsonHandler.getValue(request, "time");
            return time;
        }

        private String findIpInRequest(JSONObject request) {
            String ip = JsonHandler.getValue(request, "ip");
            return ip;
        }

    }

}
