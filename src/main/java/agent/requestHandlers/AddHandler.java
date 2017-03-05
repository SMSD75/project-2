package agent.requestHandlers;

import common.JsonHandler;
import common.JsonIOHandler;
import common.StringHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by Shayan on 2/28/2017.
 */
public class AddHandler extends FindHandler{

    public String doAction(String request, String rootIp, int rootListenPort) {

        String response = null;

        try {
            List<String> parsedRequest = StringHandler.parseStringByDiv(request, " ");
            response = addDomain(parsedRequest.get(0), parsedRequest.get(1), parsedRequest.get(2), rootIp, rootListenPort, null);
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }

        return response;
    }

    private String addDomain(String domain, String ip, String time,  String rootIp, int rootListenPort, String method) throws IOException {

        String result;

        initConnection(rootIp, rootListenPort);

        JsonIOHandler jsonIOHandler = new JsonIOHandler(socket);

        JSONObject jsonObject = new JSONObject();
        JsonHandler.addPair(jsonObject, "request", "add");
        JsonHandler.addPair(jsonObject, "domain", domain);
        JsonHandler.addPair(jsonObject, "ip", ip);
        JsonHandler.addPair(jsonObject, "time", time);

        if (method != null) {
            JsonHandler.addPair(jsonObject,"method", method);
        }

        jsonIOHandler.sendJsonMassage(jsonObject);

        jsonObject = jsonIOHandler.getJsonMassage();

        System.out.println("json: " + jsonObject);

        String responseMethod = findResponseMethod(jsonObject);

        if (responseMethod.equals("recursive")) {
            result = findResultValue(jsonObject);
        }
        else {
            // so it's iterative
            String nextDnsServerPort = FindNextDnsServerPort(jsonObject);
            result = addDomain(domain, ip,time, "localhost", Integer.parseInt(nextDnsServerPort), "iterative");
        }

        jsonIOHandler.close();
        return result;
    }
}
