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
        } catch (JSONException e) {return GetErrorJSON("NoMessageType");}

        switch (message_type){
            case "LoginRequest" : return LoginRequest(message);
            case "GetBasicData" : return GetBasicData();
            case "UpdateClientData" : return UpdateClientData(message);
            case "RegisterNewClient" : return RegisterNewClient(message);
            case "AddDevice" : return  AddDevice(message);
            default : return GetErrorJSON("WrongMessageType");
        }
    }

    public String hashString(String s) {
        byte[] hash = null;

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        hash = md.digest(s.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hash.length; ++i) {
            String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                sb.append(0);
                sb.append(hex.charAt(hex.length() - 1));
            } else {
                sb.append(hex.substring(hex.length() - 2));
            }
        }
        return sb.toString();
    }

    private JSONObject GetErrorJSON(String type){
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "ERROR");
        data.put("error_type", type);
        return new JSONObject(data);
    }

    private JSONObject LoginRequest(JSONObject klientRequest) {
        try {
            String login = klientRequest.getString("login");
            String password = klientRequest.getString("password");
            int device_id = klientRequest.getInt("device_id");

            login = hashString(login);
            password = hashString(password);
            Connection connection = MakeConnection();

            //budowanie i realizowanie zapytania
            Statement stmt = null;
            stmt = connection.createStatement();
            String sql = "SELECT USER_ID,PASSWORD FROM logins where LOGIN='"+login+"'";
            ResultSet rs = stmt.executeQuery(sql);

            //przetwarzanie odpowiedzi z bazy
            if(!rs.next()){
                Map<String, String> data = new LinkedHashMap<>();
                data.put("message_type", "LoginRequest");
                data.put("islogged", "false");
                return new JSONObject(data);}

                String pass = rs.getString("PASSWORD");
                if(!pass.equals(password)){
                    Map<String, String> data = new LinkedHashMap<>();
                    data.put("message_type", "LoginRequest");
                    data.put("islogged", "false");
                    data.put("text","wrong login/password");
                    return new JSONObject(data);}

            //dane logowania się zgadzaja sprawadzam czy zaufane urzadzenie.

            int temp_user_id=  rs.getInt("USER_ID");
            stmt = connection.createStatement();
            sql = "SELECT * FROM devices where USER_ID="+temp_user_id+" and     DEVICE_ID= "+ device_id;
            rs = stmt.executeQuery(sql);

            if(rs.next()){
                //jezeli tak to urzadzenie zaufane i mozemy zalogowac
                Map<String, String> data = new LinkedHashMap<>();
                USER_ID = rs.getInt("USER_ID");
                data.put("message_type", "LoginRequest");
                data.put("islogged", "true");
                return new JSONObject(data);}


            //zaczynamy dodawanie urzadzenia

            stmt = connection.createStatement();
            sql = "SELECT VERIFY FROM logins where USER_ID="+temp_user_id;
            rs = stmt.executeQuery(sql);

            rs.next();
            String verify_method = rs.getString("VERIFY");

            stmt = connection.createStatement();
            sql = "SELECT * from user_data where USER_ID="+temp_user_id;
            rs = stmt.executeQuery(sql);

            rs.next();
            Random generator = new Random();
            AUTH_CODE = generator.nextInt(100000)+1;
            AUTH_CODE = 1111;
            AUTH_CODE_OWNER_ID = temp_user_id;


            if(verify_method.equals("EMAIL")){
                String email= rs.getString("EMAIL");
                if(!SendEmail(email,AUTH_CODE)){
                    System.out.println("sass");return GetErrorJSON("ServerError");}
            }
            else if(verify_method.equals("PHONE")){
                int phone = rs.getInt("PHONE");
                if(!SendSMS(phone,AUTH_CODE)){return GetErrorJSON("ServerError");}
            }

            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "LoginRequest");
            data.put("islogged", "false");
            data.put("text","enter verify code");
            return new JSONObject(data);


        } catch (JSONException|ClassNotFoundException|SQLException e) {
            System.out.println(e);return GetErrorJSON("ServerError");}
    }

    private JSONObject AddDevice(JSONObject klientRequest){
        try {
            String login = klientRequest.getString("login");
            String password = klientRequest.getString("password");
            int device_id = klientRequest.getInt("device_id");
            int verify_code = klientRequest.getInt("verify_code");
            login = hashString(login);
            password = hashString(password);

            if(verify_code == AUTH_CODE){
                Connection connection = MakeConnection();
                Statement stmt = null;
                stmt = connection.createStatement();
                String sql = "SELECT USER_ID,PASSWORD FROM logins where LOGIN='"+login+"' and PASSWORD ='"+password+"'";
                ResultSet rs = stmt.executeQuery(sql);

                if(!rs.next()){Map<String, String> data = new LinkedHashMap<>();
                    data.put("message_type", "AddDevice");
                    data.put("added", "false");
                    data.put("text","wrong login/password");
                    return new JSONObject(data);}

                System.out.println(3);
                int temp_user_id = rs.getInt("USER_ID");
                if(!(temp_user_id == AUTH_CODE_OWNER_ID)){
                    System.out.println(4);
                    Map<String, String> data = new LinkedHashMap<>();
                    data.put("message_type", "AddDevice");
                    data.put("added", "false");
                    data.put("text","wrong user");
                    return new JSONObject(data);
                }


                USER_ID=temp_user_id;
                stmt = connection.createStatement();
                sql = "INSERT INTO devices (`USER_ID`,`DEVICE_ID`) VALUES ("+temp_user_id+","+device_id+")";
                stmt.executeUpdate(sql);
                Map<String, String> data = new LinkedHashMap<>();
                data.put("message_type", "AddDevice");
                data.put("added", "true");
                return new JSONObject(data);

            }


        } catch (Exception e) {
            System.out.println(e);return GetErrorJSON("ServerError");}
        return GetErrorJSON("ServerError");
    }


    private JSONObject GetBasicData(){

        if (USER_ID == -1)return GetErrorJSON("NotLogged");

        try {

            Connection connection = MakeConnection();

            //budowanie i realizowanie zapytania
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM USER_DATA WHERE USER_ID ='"+USER_ID+"'";
            ResultSet rs = stmt.executeQuery(sql);

            //przetwarzanie odpowiedzi z bazy
            if(!rs.next()){
                Map<String, String> data = new LinkedHashMap<>();
                data.put("message_type", "GetBasicData");
                data.put("Name", rs.getString("*"));
                data.put("Lastname",  rs.getString("*"));
                return new JSONObject(data);}

            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "GetBasicData");
            data.put("Name", rs.getString("NAME"));
            data.put("Lastname",  rs.getString("LASTNAME"));
            return new JSONObject(data);

        } catch (ClassNotFoundException|SQLException e) {return GetErrorJSON("ServerError");}
    }

    private JSONObject RegisterNewClient(JSONObject message){
        try {
            Connection connection = MakeConnection();
            String login = message.getString("login");
            String password = message.getString("password");
            String imie = message.getString("name");
            String nazwisko = message.getString("lastname");
            String email = message.getString("EMAIL");
            int phone = message.getInt("PHONE");
            String verify_way = message.getString("verify_way");

            login = hashString(login);
            password = hashString(password);

            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM logins WHERE LOGIN ='"+login+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){return GetErrorJSON("LoginTaken");}

            sql = "INSERT INTO `logins` (`USER_ID`, `LOGIN`, `PASSWORD`, `VERIFY`) VALUES (NULL,'"+login+" ','"+password+"','"+verify_way+"')";
            stmt.executeUpdate(sql);


            sql = "SELECT * FROM logins WHERE LOGIN ='"+login+"'";
            rs = stmt.executeQuery(sql);
            rs.next();
            String User_ID = rs.getString("USER_ID");

            sql = "INSERT INTO `USER_DATA` (`USER_ID`, `NAME`, `LASTNAME`, `EMAIL`, `PHONE`) VALUES ('"
                    +User_ID+"', '"+imie+"', '"+nazwisko+"','"+email+"',"+phone+")";
            stmt.executeUpdate(sql);

            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "RegisterNewClient");
            return new JSONObject(data);

        } catch (SQLException|ClassNotFoundException|JSONException e) {;
            System.out.println(e);return GetErrorJSON("ServerError");}
    }

    private JSONObject UpdateClientData(JSONObject message){
        try {
            if(USER_ID==-1){return GetErrorJSON("NotLogged");}

            Connection connection = MakeConnection();
            String imie = message.getString("name");
            String nazwisko = message.getString("lastname");

            Statement stmt = connection.createStatement();
            String sql = "UPDATE user_data set NAME = '"+imie+"', LASTNAME = '"+nazwisko+"' where USER_ID = '"+USER_ID+"'";
            stmt.executeQuery(sql);

            Map<String, String> data = new LinkedHashMap<>();
            data.put("message_type", "UpdateClientData");
            return new JSONObject(data);

        } catch (SQLException|ClassNotFoundException|JSONException e) {return GetErrorJSON("ServerError");}
    }
    private boolean SendEmail(String email,int kod){
        try {
            Properties mailServerProperties;
            Session getMailSession;
            MimeMessage generateMailMessage;

            mailServerProperties = System.getProperties();
            mailServerProperties.put("mail.smtp.port", "587");
            mailServerProperties.put("mail.smtp.auth", "true");
            mailServerProperties.put("mail.smtp.starttls.enable", "true");
            mailServerProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            getMailSession = Session.getDefaultInstance(mailServerProperties, null);
            generateMailMessage = new MimeMessage(getMailSession);
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
            generateMailMessage.setSubject("Twoj kod do weryfikacji");
            String emailBody = "Twoj kod to :  " + kod;
            generateMailMessage.setContent(emailBody, "text/html");

            Transport transport = getMailSession.getTransport("smtp");

            transport.connect("smtp.gmail.com", "boartrainer", "dzikidzik");
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();
        }catch (Exception e){System.out.println(e);return false;}
        return true;
    }//koniec funkcji wysylania maila

    private boolean SendSMS(int number,int kod){return false;}//koniec funkcji wysylania maila

}
