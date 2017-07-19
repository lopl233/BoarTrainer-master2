

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


class Klient_reciver {
    public static boolean isLogged=false;
    private  SSLConnector sslConnector;
    private  PrintWriter pw;
    private  BufferedReader br;

    Klient_reciver(){

        String currentDir = System.getProperty("user.dir")+"/testkeysore.p12";
        System.setProperty("javax.net.ssl.keyStore",currentDir);
        System.setProperty("javax.net.ssl.keyStorePassword","dzikidzik");
        System.setProperty("javax.net.ssl.keyStoreType","PKCS12");
        System.setProperty("javax.net.ssl.trustStore",currentDir);
        System.setProperty("javax.net.ssl.trustStorePassword","dzikidzik");
        System.setProperty("javax.net.ssl.trustStoreType","PKCS12");

        sslConnector = SSLConnector.getInstance();
        try {
            sslConnector.sslsocket.startHandshake();
        } catch (IOException e) {}

    }

    public JSONObject reciver(Map map) throws IOException, JSONException {
        JSONObject message = new JSONObject(map);

        pw.write(message.toString());
        pw.write("\n");
        pw.flush();

        String serverAnswer = br.readLine();

        return new JSONObject(serverAnswer);
    }

    public  JSONObject logIn(String Login, String Password, int device_id) throws JSONException, IOException {
        Map<String, String> data = new LinkedHashMap<>();

        data.put("message_type", "LoginRequest");
        data.put("login", Login);
        data.put("password", Password);
        data.put("device_id",Integer.toString(device_id));

        return reciver(data);
    }//koniec funkcji logowania

    public JSONObject GetData() throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "GetBasicData");
        return reciver(data);
    }
    public JSONObject AddDevice(String login, String password, int device_id,int kod) throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "AddDevice");
        data.put("login", login);
        data.put("password", password);
        data.put("device_id", Integer.toString(device_id));
        data.put("verify_code", Integer.toString(kod));
        return reciver(data);
    }

    public JSONObject Register(String login, String password, String name, String lastname, String email, int phone, String verify_way) throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "RegisterNewClient");
        data.put("login", login);
        data.put("password", password);
        data.put("name", name);
        data.put("lastname", lastname);
        data.put("PHONE",Integer.toString(phone));
        data.put("EMAIL",email);
        data.put("verify_way",verify_way);
        return reciver(data);
    }
    public String JsonToString(JSONObject jsonObject) throws JSONException {
        String result = "";
        Iterator<?> keys = jsonObject.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            result = result + key + ":"+ jsonObject.getString(key) + "\n";
        }
        return result;
    }

}//koniec klasy klient


class main{
    public static Klient_reciver klient_reciver;

    public static void main(String[] args) {
        klient_reciver = new Klient_reciver();
        while(true){






        }

    }




}