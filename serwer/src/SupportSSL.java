import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;


import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.sql.DriverManager;
import java.util.*;

public class SupportSSL extends Thread {
    private SSLSocket sslsocket;
    private Fasade fasade = new Fasade();
    private int USER_ID = -1;
    private int AUTH_CODE=-1;
    private int AUTH_CODE_OWNER_ID=-1;

    public SupportSSL(SSLSocket sslsocket) {
        super("SupportSSL");
        this.sslsocket = sslsocket;
    }

    public SupportSSL() {
        super("SupportSSL");
    }

    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
                PrintWriter pw = new PrintWriter(sslsocket.getOutputStream());
                String data = br.readLine();
                if (!(data == null))
                    try {
                        System.out.println("Wiadomosc od usera'a: "+USER_ID+"=  "+data);
                        JSONObject clientRequest = new JSONObject(data);
                        pw.println(CreateAnswer(clientRequest));
                        pw.flush();
                    } catch (JSONException e) {
                        pw.println(GetErrorJSON("JSONPARSE"));
                        pw.flush();
                }
            } catch (SocketException ioe) {return;
            } catch (IOException ioe) {return;}
        }//koniec while'a
    }//koniec run'a

    private Connection MakeConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        String serverName = "localhost";
        String mydatabase = "mydatabase";
        String url = "jdbc:mysql://" + "localhost" + "/" + "dzik";
        return DriverManager.getConnection(url, "root", "");


    }

    private JSONObject CreateAnswer(JSONObject message){
        String message_type="";
        try {
            message_type = message.getString("message_type");

            switch (message_type){
                case "LoginRequest" : return LoginRequest(message);
                case "GetBasicData" : return GetBasicData();
                case "ChangeData" : return ChangeData(message);
                case "RegisterNewClient" : return RegisterNewClient(message);
                case "AddDevice" : return  AddDevice(message);
                case "TrainingProposition" : return  TrainingProposition(message);
                case "GetTraining" : return GetTraining(message);
                case "ExerciseReplacement" : return ExerciseReplacement(message);
                case "InsertParameters" : return InsertParameters(message);
                case "GetParameters" : return GetParameters(message);
                case "ChangePassword" : return ChangePassword(message);
                default : return GetErrorJSON("WrongMessageType");
            }
        } catch (JSONException|SQLException| MessagingException| ClassNotFoundException e) {
            System.out.println(e.toString());
            return GetErrorJSON("ServerError");}
    }


    private JSONObject GetErrorJSON(String type){
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "ERROR");
        data.put("error_type", type);
        return new JSONObject(data);
    }

    private JSONObject LoginRequest(JSONObject klientRequest) throws SQLException, JSONException, MessagingException {
            String login = klientRequest.getString("login");
            String password = klientRequest.getString("password");
            int device_id = klientRequest.getInt("device_id");
            int temp_user_id = fasade.CanBeLogged(login,password);

            if (temp_user_id == -1){
                Map<String, String> data = new LinkedHashMap<>();
                data.put("message_type", "LoginRequest");
                data.put("islogged", "false");
                data.put("text","wrong login/password");
                return new JSONObject(data);}

            if(fasade.IsDeviceTrusted( Integer.toString(temp_user_id), Integer.toString(device_id))){
                Map<String, String> data = new LinkedHashMap<>();
                USER_ID = temp_user_id;
                data.put("message_type", "LoginRequest");
                data.put("islogged", "true");
                data.put("user_id", Integer.toString(temp_user_id));
                return new JSONObject(data);}

            AUTH_CODE = fasade.SendVerifyCode(Integer.toString(temp_user_id));
            AUTH_CODE_OWNER_ID = temp_user_id;

            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "LoginRequest");
            data.put("islogged", "false");
            data.put("text","enter verify code");
            return new JSONObject(data);
    }

    private JSONObject AddDevice(JSONObject klientRequest) throws SQLException, ClassNotFoundException, JSONException {
            String login = klientRequest.getString("login");
            String password = klientRequest.getString("password");
            int device_id = klientRequest.getInt("device_id");
            int verify_code = klientRequest.getInt("verify_code");
            if(verify_code == AUTH_CODE){
                int temp_user_id = fasade.addDevice(device_id, login,password);
                if(temp_user_id == -1){
                    Map<String, String> data = new LinkedHashMap<>();
                    data.put("message_type", "AddDevice");
                    data.put("added", "false");
                    data.put("text","wrong login/password");
                    return new JSONObject(data);}
                USER_ID = temp_user_id;
                Map<String, String> data = new LinkedHashMap<>();
                data.put("message_type", "AddDevice");
                data.put("added", "true");
                return new JSONObject(data);
            }
            return GetErrorJSON("ServerError");
    }

    private JSONObject GetBasicData() throws SQLException, ClassNotFoundException {
        if (USER_ID == -1)return GetErrorJSON("NotLogged");
            ResultSet rs = fasade.GetBasicData(USER_ID);
            if(!rs.next()){return GetErrorJSON("ServerError"); }
            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "GetBasicData");
            data.put("name", rs.getString("NAME"));
            data.put("last_name",  rs.getString("LASTNAME"));
            data.put("phone",  rs.getString("PHONE"));
            data.put("email",  rs.getString("EMAIL"));
            return new JSONObject(data);
    }

    private JSONObject RegisterNewClient(JSONObject message) throws SQLException, ClassNotFoundException, JSONException {

        Connection connection = MakeConnection();
        String login = message.getString("login");
        String password = message.getString("password");
        String imie = message.getString("name");
        String nazwisko = message.getString("lastname");
        String email = message.getString("EMAIL");
        int phone = message.getInt("PHONE");
        String verify_way = message.getString("verify_way");
        int age =  message.getInt("age");
        int weight  =  message.getInt("weight");
        int height  =  message.getInt("height");
        String frequency =  message.getString("frequency");
        String advancement_level =  message.getString("advancment_level");
        String goal =  message.getString("goal");
        if (!fasade.Register(login, password, imie, nazwisko, email, phone, verify_way)) {return GetErrorJSON("LoginTaken");}
        USER_ID = fasade.GetHighestUserId();
        fasade.InsertParameters(USER_ID, age, height, weight, frequency, advancement_level, goal);
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "RegisterNewClient");
        return new JSONObject(data);
    }

    private JSONObject ChangeData(JSONObject message) throws JSONException, SQLException, ClassNotFoundException {
            if(USER_ID==-1){return GetErrorJSON("NotLogged");}
            String imie = message.getString("name");
            String nazwisko = message.getString("last_name");
            int phone = message.getInt("phone");
            String email = message.getString("email");

            fasade.UpdateData(imie, nazwisko, phone, email, USER_ID);
            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "ChangeData");
            return new JSONObject(data);
    }


    private JSONObject InsertParameters(JSONObject message)throws JSONException, SQLException, ClassNotFoundException {
        if(USER_ID==-1){return GetErrorJSON("NotLogged");}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "InsertParameters");
        int age =  message.getInt("age");
        int weight  =  message.getInt("weight");
        int height  =  message.getInt("height");
        String frequency =  message.getString("frequency");
        String advancement_level =  message.getString("advancment_level");
        String goal =  message.getString("goal");

        fasade.InsertParameters(USER_ID, age, height, weight, frequency, advancement_level, goal);
        return new JSONObject(data);
    }

    private JSONObject GetParameters(JSONObject message)throws JSONException, SQLException{
        if(USER_ID==-1){return GetErrorJSON("NotLogged");}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "GetParameters");
        ResultSet rs = fasade.GetParamenters(USER_ID);
        rs.next();
        data.put("age",rs.getString("AGE"));
        data.put("weight",rs.getString("WEIGHT"));
        data.put("height",rs.getString("HEIGHT"));
        data.put("frequency",rs.getString("FRAQUENCY"));
        data.put("advancement_level",rs.getString("ADVANCMENT_LEVEL"));
        data.put("goal",rs.getString("GOAL"));

        return new JSONObject(data);
    }

    private JSONObject ChangePassword(JSONObject message)throws JSONException, SQLException{
        if(USER_ID==-1){return GetErrorJSON("NotLogged");}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "ChangePassword");

        String old_password = message.getString("new_password");
        String new_password = message.getString("old_password");

        fasade.ChangePassword(USER_ID, new_password, old_password);

        return new JSONObject(data);

    }


    private JSONObject TrainingProposition(JSONObject message) throws JSONException, SQLException, ClassNotFoundException {
        if(USER_ID==-1){return GetErrorJSON("NotLogged");}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "TrainingProposition");
        data.put("trainings",fasade.GetRTrainingProposition(USER_ID).toString());
        return new JSONObject(data);
    }

    private JSONObject GetTraining(JSONObject message) throws JSONException, SQLException, ClassNotFoundException {
        if(USER_ID==-1){return GetErrorJSON("NotLogged");}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "GetTraining");
        data.put("exercises",fasade.GetTrainingExercises(message.getInt("training_id")).toString());
        return new JSONObject(data);
    }

    private JSONObject ExerciseReplacement(JSONObject message) throws JSONException, SQLException, ClassNotFoundException {
        if(USER_ID==-1){return GetErrorJSON("NotLogged");}
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "ExerciseReplacement");
        data.put("exercises ",fasade.ExerciseReplacement(message.getInt("exercise_id"),message.getInt("id_replacment_group") ).toString());
        return new JSONObject(data);
    }


}
