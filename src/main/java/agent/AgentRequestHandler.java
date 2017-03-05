package agent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Shayan on 2/27/2017.
 */
public class AgentRequestHandler {

    private String rootIp;
    private int rootListenPort;

    public AgentRequestHandler(String rootIp, int rootListenPort) throws IOException {
        this.rootIp = rootIp;
        this.rootListenPort = rootListenPort;
    }

    public String handleRequest(String requestKey, String requestValue) throws IOException {

        String response = null;

        String equivalentClass = "agent.requestHandlers." + Character.toUpperCase(requestKey.charAt(0)) + requestKey.substring(1) + "Handler";

//        System.out.println("requestKey " + requestKey + " requestValue " + requestValue);

        try {
            Class requestHandlerClass = Class.forName(equivalentClass);
            Object requestHandlerObject = requestHandlerClass.newInstance();
            Method handleRequest = requestHandlerClass.getMethod("doAction", String.class, String.class, int.class);
            response = (String) handleRequest.invoke(requestHandlerObject, requestValue, rootIp, rootListenPort);

        } catch (ClassNotFoundException e1) {
            System.out.println("ClassNotFoundException!");
            e1.printStackTrace();
        } catch (NoSuchMethodException e2) {
            System.out.println("NoSuchMethodException!");
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            System.out.println("IllegalAccessException!");
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            System.out.println("InvocationTargetException!");
            e4.printStackTrace();
        } catch (IllegalArgumentException e5) {
            System.out.println("IllegalArgumentException!");
            e5.printStackTrace();
        } catch (InstantiationException e6) {
            System.out.println("InstantiationException!");
            e6.printStackTrace();
        }

        return response;
    }
}
