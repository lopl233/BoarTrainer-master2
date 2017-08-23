

import org.json.JSONArray;
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

    public JSONObject Register(String login, String password, String name, String lastname, String email, int phone, String verify_way, int age,int weight, int height, String goal, String frequency,String advancment_level) throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "RegisterNewClient");
        data.put("login", login);
        data.put("password", password);
        data.put("name", name);
        data.put("lastname", lastname);
        data.put("PHONE",Integer.toString(phone));
        data.put("EMAIL",email);
        data.put("verify_way",verify_way);
        data.put("age", Integer.toString(age));
        data.put("weight", Integer.toString(weight));
        data.put("height", Integer.toString(height));
        data.put("frequency", frequency);
        data.put("advancment_level", advancment_level);
        data.put("goal", goal);
        return reciver(data);
    }

    public JSONObject GetTraining(int training_id, boolean start) throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "GetTraining");
        data.put("training_id", Integer.toString(training_id));
        data.put("start_training", Boolean.toString(start));
        return reciver(data);
    }

    public JSONObject ExerciseReplacement(int exercise_id, int id_replacment_group) throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "ExerciseReplacement");
        data.put("exercise_id", Integer.toString(exercise_id));
        data.put("id_replacment_group", Integer.toString(id_replacment_group));
        return reciver(data);
    }

    public JSONObject GetParameters() throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "GetParameters");
        return reciver(data);
    }

    public JSONObject InsertParameters(int age, int weight, int height, String frequency, String advancement_level, String goal) throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "InsertParameters");
        data.put("age", Integer.toString(age));
        data.put("weight", Integer.toString(weight));
        data.put("height", Integer.toString(height));
        data.put("frequency", frequency);
        data.put("advancment_level", advancement_level);
        data.put("goal", goal);
        return reciver(data);
    }
    public JSONObject ChangeData(String name, String last_name, String email, int phone) throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "ChangeData");
        data.put("phone", Integer.toString(phone));
        data.put("last_name", last_name);
        data.put("name", name);
        data.put("email", email);
        return reciver(data);
    }

    public JSONObject ChangePassword(String new_password, String old_password) throws IOException, JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "ChangePassword");
        data.put("new_password", new_password);
        data.put("old_password", old_password);
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
        int training_id = 0;
        boolean start = false;
        int exercise_id = 0;
        int id_replacement_group = 0;
        int age = 0;
        int weight = 0;
        int height = 0;
        String advancment_level = "";
        String goal = "";
        String frequency = "";
        String old_password = "";
        String new_password = "";

        while(true){
            System.out.println("Wybierz akcje");
            System.out.println("1.  Login");
            System.out.println("2.  Register");
            System.out.println("3.  Get user data");
            System.out.println("4.  Training Propositions");
            System.out.println("5.  Get and Start Training");
            System.out.println("6.  Add device");
            System.out.println("7.  Exercise Replacement");
            System.out.println("8.  Get user parameters");
            System.out.println("9.  Add user parameters");
            System.out.println("10. Updata user data ");
            System.out.println("11. Change password");



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
                    System.out.println("Age : ");
                    age = scanner.nextInt();
                    System.out.println("Weight : ");
                    weight = scanner.nextInt();
                    System.out.println("Height : ");
                    height = scanner.nextInt();
                    System.out.println("Goal : ");
                    goal = scanner.next();
                    System.out.println("Frequency : ");
                    frequency = scanner.next();
                    System.out.println("Advancment_level : ");
                    advancment_level = scanner.next();
                    klient_reciver.Register(login, password, name, lastname, email, phone, verify_way, age, weight, height, goal, frequency, advancment_level);
                    break;

                case "3":
                    klient_reciver.GetData();
                    break;

                case "4":
                    klient_reciver.GetTrainingPropositions();
                    break;
                case "5":
                    System.out.println("Id treningu : ");
                    training_id = scanner.nextInt();
                    System.out.println("Czy rozpoczac od razu? : ");
                    start = scanner.nextBoolean();
                    klient_reciver.GetTraining(training_id, start);
                    break;
                case "6":
                    System.out.println("Login : ");
                    login = scanner.next();
                    System.out.println("Haslo : ");
                    password = scanner.next();
                    System.out.println("Device id : ");
                    device_id = scanner.nextInt();
                    klient_reciver.AddDevice(login, password, device_id, 1111);
                    break;
                case "7":
                    System.out.println("Id cwiczenia : ");
                    exercise_id = scanner.nextInt();
                    System.out.println("Id grupy : ");
                    id_replacement_group = scanner.nextInt();
                    klient_reciver.ExerciseReplacement(exercise_id, id_replacement_group);
                    break;
                case "8":
                    klient_reciver.GetParameters();
                    break;
                case "9":
                    System.out.println("Age : ");
                    age = scanner.nextInt();
                    System.out.println("Weight : ");
                    weight = scanner.nextInt();
                    System.out.println("Height : ");
                    height = scanner.nextInt();
                    System.out.println("Goal : ");
                    goal = scanner.next();
                    System.out.println("Frequency : ");
                    frequency = scanner.next();
                    System.out.println("Advancment_level : ");
                    advancment_level = scanner.next();
                    klient_reciver.InsertParameters(age, weight, height, frequency, advancment_level, goal);
                    break;
                case "10":
                    System.out.println("Name : ");
                    name = scanner.next();
                    System.out.println("Last name : ");
                    lastname = scanner.next();
                    System.out.println("Email : ");
                    email = scanner.next();
                    System.out.println("Phone : ");
                    phone = scanner.nextInt();
                    klient_reciver.ChangeData(name, lastname , email, phone);
                    break;
                case "11":
                    System.out.println("New_password : ");
                    new_password = scanner.next();
                    System.out.println("Old_password : ");
                    old_password = scanner.next();
                    klient_reciver.ChangePassword(new_password, old_password);
                    break;
            }

        }

    }




}