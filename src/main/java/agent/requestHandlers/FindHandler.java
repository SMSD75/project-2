package agent.requestHandlers;

import common.FileHandler;
import common.JsonHandler;
import common.JsonIOHandler;
import common.StringHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Shayan on 2/28/2017.
 */
public class FindHandler {

    protected Socket socket;

    public String doAction(String request, String rootIp, int rootListenPort) {
        request += " " + getTime();
        ArrayList<String> foundIp = new ArrayList<String>();
        List<String> parsedRequest = StringHandler.parseStringByDiv(request, " ");
        String response = null;

        try {
            FileHandler fileHandler = new FileHandler("CachedData.txt");
//            System.out.println("00000000000000000000000");
            ArrayList<String> cacheLines = fileHandler.getLines();
            response = isInCache(parsedRequest.get(0), parsedRequest.get(1), cacheLines);
            foundIp.add(response);
            if(response == null) {
                foundIp = findIp(parsedRequest.get(0), parsedRequest.get(1), rootIp, rootListenPort, null);
                cacheData(parsedRequest.get(0), parsedRequest.get(1), foundIp.get(0), foundIp.get(1), timeOfNowInSec(), cacheLines);
            }
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }

        return foundIp.get(0);
    }

    private Long timeOfNowInSec(){
        long timeMillis = System.currentTimeMillis();
        long timeSecond = timeMillis/1000;

        return timeSecond;
    }

    private void cacheData(String domain, String time, String ip, String validationTime, Long date, ArrayList<String> cacheLines) throws IOException {
        boolean find = false;
        FileHandler file = new FileHandler("CachedData.txt");
        for (int counter = 0; counter < cacheLines.size(); counter++) {
            List<String> parsedLine = StringHandler.parseStringByDiv(cacheLines.get(counter), " ");
            if (parsedLine.get(0).equals(domain) && parsedLine.get(1).equals(time)) {
                parsedLine.set(2, ip);
//                System.out.println("I find in cache: " + date.toString());
                parsedLine.set(3, date.toString());
                parsedLine.set(4, validationTime);
                cacheLines.set(counter, parsedLine.get(0) + " " + parsedLine.get(1) + " " + parsedLine.get(2) + " " + parsedLine.get(3) + " " + parsedLine.get(4));
                find = true;
                break;
            }
        }
        if(!find)
            file.addLineToFile(domain + " " + time + " " + ip + " " + date + " " + validationTime);
        else
            file.overWriteFile(cacheLines, "CachedData.txt");
    }


    private boolean isValid(String cachedDate, String validatinTime){
        if(timeOfNowInSec() - Long.valueOf(cachedDate).longValue() < Long.valueOf(validatinTime))
            return true;
        return false;
    }

    protected String isInCache(String domain, String time, ArrayList<String> cacheLines){
        if(cacheLines.size() == 0)
            return null;
//        System.out.println("domain : " + domain + " - -");
//        System.out.println("time : " + time);
        for(int counter = 0; counter < cacheLines.size(); counter ++){
            List<String> parsedLine = StringHandler.parseStringByDiv(cacheLines.get(counter), " ");
//            System.out.println(parsedLine.get(0) + "  " + parsedLine.get(1));
//            System.out.println("999999999999999999999999999999");
            if(parsedLine.get(0).equals(domain) && parsedLine.get(1).equals(time) && isValid(parsedLine.get(3), parsedLine.get(4))) {
                System.out.println("requested domain is in cache :)");
                return parsedLine.get(2);
            }
        }

        return null;
    }

    private String getTime(){
        String[] Times = new String[]{"Morning", "Noon", "AfterNoon", "Night"};
        Random rand = new Random();
        return Times[rand.nextInt(4)];
    }

    private ArrayList<String> findIp(String domain, String time,  String rootIp, int rootListenPort, String method) throws IOException {

        String finalIp = null;
        String validationTime = null;
        ArrayList<String> foundData = new ArrayList<String>();

        initConnection(rootIp, rootListenPort);

        JsonIOHandler jsonIOHandler = new JsonIOHandler(socket);

        JSONObject jsonObject = new JSONObject();
        JsonHandler.addPair(jsonObject, "request", "find");
        JsonHandler.addPair(jsonObject, "domain", domain);
        JsonHandler.addPair(jsonObject, "time", time);

        if (method != null) { //dar soorati ke null bashad yani in payam be root ferestade khahad shod be khatare hamin hanooz method (iterative ya recursive) moshkhas nashode
            JsonHandler.addPair(jsonObject,"method", method);
        }

        jsonIOHandler.sendJsonMassage(jsonObject);

        jsonObject = jsonIOHandler.getJsonMassage();

        System.out.println("json: " + jsonObject);

        String responseMethod = findResponseMethod(jsonObject);

        if (responseMethod.equals("recursive")) {
            finalIp = findResultValue(jsonObject);
            validationTime = findTimeValue(jsonObject);
        }
        else {
            // so it's iterative
            String nextDnsServerPort = FindNextDnsServerPort(jsonObject);
            foundData = findIp(domain, time, "localhost", Integer.parseInt(nextDnsServerPort), "iterative");
        }

        jsonIOHandler.close();
        foundData.add(finalIp);
        foundData.add(validationTime);
        return foundData;
    }

    protected  String findTimeValue(JSONObject jsonObject){
        return JsonHandler.getValue(jsonObject, "ttl");
    }

    protected String FindNextDnsServerPort(JSONObject jsonObject) {
        String result = JsonHandler.getValue(jsonObject, "nextDns");
        return result;
    }

    protected String findResultValue(JSONObject jsonObject) {
        String result = JsonHandler.getValue(jsonObject, "result");
        return result;
    }

    protected String findResponseMethod(JSONObject jsonObject) {
        String result = JsonHandler.getValue(jsonObject, "method");
        return result;
    }

    protected void initConnection(String rootIp, int rootListenPort) throws IOException {
        socket = new Socket(rootIp, rootListenPort);
    }
}
