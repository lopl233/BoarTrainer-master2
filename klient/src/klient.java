

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Scanner;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


class Klient_reciver {
    public static boolean isLogged=false;
    private  SSLConnector sslConnector;
    private  PrintWriter pw;
    private  BufferedReader br;

    Klient_reciver() throws IOException {

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
        pw = new PrintWriter(sslConnector.sslsocket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(sslConnector.sslsocket.getInputStream()));

    }

    public JSONObject reciver(Map map) throws IOException, JSONException {
        JSONObject message = new JSONObject(map);

        System.out.println("----Sending to server----");
        System.out.println(JsonToString(message));
        pw.write(message.toString());
        pw.write("\n");
        pw.flush();

        String serverAnswer = br.readLine();
        JSONObject answer = new JSONObject(serverAnswer);

        System.out.println("----Answer from server");
        System.out.println(JsonToString(answer));

        return answer;
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
    public JSONObject GetTrainingPropositions() throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "TrainingProposition");
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

}//koniec klasy klient_reciver


class main{
    public static Klient_reciver klient_reciver;

    public static void main(String[] args) throws IOException, JSONException {
        klient_reciver = new Klient_reciver();
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        String login = "";
        String password = "";
        String email = "";
        String verify_way = "";
        String name = "";
        String lastname = "";
        int phone = 0;


        while(true){
            System.out.println("Wybierz akcje");
            System.out.println("1. Login");
            System.out.println("2. Get user data");
            System.out.println("3. Add device");
            System.out.println("4. Training Propositions");


            choice = scanner.next();
            switch (choice) {
                case "1":
                    System.out.println("Login : ");
                    login = scanner.next();
                    System.out.println("Haslo : ");
                    password = scanner.next();
                    System.out.println("ID urzadzenia : ");
                    int device_id = scanner.nextInt();
                    klient_reciver.logIn(login, password, device_id);
                    break;
                case "2":
                    System.out.println("Login : ");
                    login = scanner.next();
                    System.out.println("Haslo : ");
                    password = scanner.next();
                    System.out.println("Imie : ");
                    name = scanner.next();
                    System.out.println("Nazwisko : ");
                    lastname = scanner.next();
                    System.out.println("Email : ");
                    email = scanner.next();
                    System.out.println("Telefon : ");
                    phone = scanner.nextInt();
                    System.out.println("Sposob weryfikacji : ");
                    verify_way = scanner.next();
                    klient_reciver.Register(login, password, name, lastname, email, phone, verify_way);
                    break;

                case "3":
                    klient_reciver.GetData();
                    break;

                case "4":
                    klient_reciver.GetTrainingPropositions();
                    break;
        }


        }

    }




}