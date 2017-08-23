import org.json.JSONObject;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Fasade {
    ConnectionPool connectionPool = ConnectionPool.getInstance();

    public int CanBeLogged(String login, String password) throws SQLException {
        login = hashString(login);
        password = hashString(password);
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = String.format("SELECT USER_ID,PASSWORD FROM logins where LOGIN='%s'",login);
        ResultSet rs = stmt.executeQuery(sql);
        connectionPool.releaseConnection(connection);
        if(!rs.next())return -1;
        String pass = rs.getString("PASSWORD");
        if(!pass.equals(password)){return -1;}
        return rs.getInt("USER_ID");
    }

    public boolean IsDeviceTrusted(String user_id, String devide_id) throws SQLException {
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = String.format("SELECT * FROM devices where USER_ID = %s and DEVICE_ID = %s", user_id, devide_id);
        ResultSet rs = stmt.executeQuery(sql);
        connectionPool.releaseConnection(connection);
        if (rs.next())return true;
        return false;
    }
    //"SELECT MAX(USER_ID) FROM `user_data`"

    public int GetHighestUserId() throws SQLException {
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = "SELECT MAX(USER_ID) FROM user_data";
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        return rs.getInt("MAX(USER_ID)");
    }

    public int SendVerifyCode(String user_id) throws SQLException, MessagingException {
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = "SELECT VERIFY FROM logins where USER_ID="+user_id;
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        String verify_method = rs.getString("VERIFY");
        stmt = connection.createStatement();
        sql = "SELECT * from user_data where USER_ID="+user_id;
        rs = stmt.executeQuery(sql);
        rs.next();
        Random generator = new Random();
        int AUTH_CODE = generator.nextInt(100000)+1;
        AUTH_CODE = 1111;

        System.out.println("Sending message with code = "+ AUTH_CODE);

        if(verify_method.equals("EMAIL")){
               String email= rs.getString("EMAIL");
               SendEmail(email,AUTH_CODE);}
        else{
            int phone = rs.getInt("PHONE");
            SendSMS(phone,AUTH_CODE);}

        connectionPool.releaseConnection(connection);
        return AUTH_CODE;
    }

    public int addDevice(int device_id, String login, String password) throws SQLException {
        login = hashString(login);
        password = hashString(password);
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = "SELECT USER_ID,PASSWORD FROM logins where LOGIN='"+login+"' and PASSWORD ='"+password+"'";
        ResultSet rs = stmt.executeQuery(sql);
        if(!rs.next()){connectionPool.releaseConnection(connection);return -1;}
        int user_id = rs.getInt("USER_ID");
        stmt = connection.createStatement();
        sql = "INSERT INTO devices (`USER_ID`,`DEVICE_ID`) VALUES ("+user_id+","+device_id+")";
        stmt.executeUpdate(sql);
        connectionPool.releaseConnection(connection);
        return user_id;
    }

    public ResultSet GetBasicData(int user_id) throws SQLException {
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = "SELECT * FROM user_data WHERE USER_ID ='"+user_id+"'";
        ResultSet rs = stmt.executeQuery(sql);
        connectionPool.releaseConnection(connection);
        return rs;
    }

    public boolean Register(String login, String password, String imie, String nazwisko, String email, int phone, String verify_way) throws SQLException {
        login = hashString(login);
        password = hashString(password);
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = "SELECT * FROM logins WHERE LOGIN ='"+login+"'";
        ResultSet rs = stmt.executeQuery(sql);
        if(rs.next()){connectionPool.releaseConnection(connection);return false;}
        sql = "INSERT INTO `logins` (`USER_ID`, `LOGIN`, `PASSWORD`, `VERIFY`) VALUES (NULL,'"+login+" ','"+password+"','"+verify_way+"')";
        stmt.executeUpdate(sql);
        sql = "SELECT * FROM logins WHERE LOGIN ='"+login+"'";
        rs = stmt.executeQuery(sql);
        rs.next();
        String User_ID = rs.getString("USER_ID");
        sql = "INSERT INTO `user_data` (`USER_ID`, `NAME`, `LASTNAME`, `EMAIL`, `PHONE`) VALUES ('"
                +User_ID+"', '"+imie+"', '"+nazwisko+"','"+email+"',"+phone+")";
        stmt.executeUpdate(sql);
        connectionPool.releaseConnection(connection);
        return true;
    }
    public void UpdateData(String imie, String nazwisko, int phone, String email, int user_id) throws SQLException {
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = String.format("UPDATE user_data set NAME = '%s', LASTNAME = '%s', PHONE = '%s', EMAIL = '%s' where USER_ID = '%s'",
                        imie,nazwisko,Integer.toString(phone), email, Integer.toString(user_id));
        stmt.executeUpdate(sql);
        connectionPool.releaseConnection(connection);
    }

    public void InsertParameters(int user_id, int age, int height, int weight, String fraquency, String advancement_level , String goal) throws SQLException {
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = String.format("INSERT INTO `user_parameters` (`USER_ID`, `AGE`, `HEIGHT`, `WEIGHT`, `FRAQUENCY`, `ADVANCMENT_LEVEL`, `GOAL`, `DATA_DODANIA`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', CURRENT_TIMESTAMP)",
                user_id, age, height, weight, fraquency, advancement_level, goal);
        stmt.executeUpdate(sql);
        connectionPool.releaseConnection(connection);
    }

    public ResultSet GetParamenters(int user_id)throws SQLException{
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = String.format("SELECT * FROM user_parameters where USER_ID = '%s' order by DATA_DODANIA DESC",user_id);
        ResultSet rs = stmt.executeQuery(sql);
        connectionPool.releaseConnection(connection);
        return rs;
    }

    public void ChangePassword(int user_id, String old_password, String new_password)throws SQLException{
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String OLDPASS = hashString(old_password);
        String NEWPASS = hashString(new_password);
        String sql = String.format("UPDATE logins set PASSWORD = '%s' WHERE USER_ID = '%s' and PASSWORD = '%s'", NEWPASS, user_id, OLDPASS);
        stmt.executeUpdate(sql);
        connectionPool.releaseConnection(connection);
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
    private void SendEmail(String email,int kod) throws MessagingException {
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
    }//koniec funkcji wysylania maila

    private void SendSMS(int number,int kod){return;}//koniec funkcji wysylania maila


    public List<String> GetTrainingExercises(int training_id) throws SQLException {
        List<String> lista = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = String.format("SELECT * FROM `training_exercise` as A join exercise as B on A.exercise_id = B.exercise_id WHERE A.training_id = %s order by A.order_in_training", Integer.toString(training_id));
        ResultSet rs = stmt.executeQuery(sql);
        connectionPool.releaseConnection(connection);
        Map<String,String> data = new LinkedHashMap<>();

        while (rs.next()) {
            data = new LinkedHashMap<>();
            data.put("exercise_id", rs.getString("exercise_id"));
            data.put("id_replacment_group", rs.getString("id_replacment_group"));
            data.put("description", rs.getString("description"));
            data.put("video_link", rs.getString("video_link"));
            data.put("exercise_name", rs.getString("exercise_name"));
            data.put("reps", rs.getString("reps"));
            lista.add(new JSONObject(data).toString());
        }

        return lista;
    }

    public List<String> GetRTrainingProposition(int user_id){
        List<String> lista = new ArrayList<>();
        Map<String,String> data = new LinkedHashMap<>();
        data.put("trainind_id", "1");
        data.put("description", "opis fbw 1");
        data.put("percentage", "100");
        lista.add(new JSONObject(data).toString());
        data = new LinkedHashMap<>();
        data.put("trainind_id", "2");
        data.put("description", "opis fbw 2");
        data.put("percentage", "80");
        lista.add(new JSONObject(data).toString());

        return lista;
    }

    public List<String> ExerciseReplacement(int exercise_id, int id_replacement_group) throws SQLException {
        List<String> lista = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        Statement stmt = connection.createStatement();
        String sql = String.format("SELECT exercise_id, exercise_name FROM `replacment_group` join exercise on replacment_group.id_exercise = exercise.exercise_id WHERE id = %s and id_exercise != %s", Integer.toString(id_replacement_group),Integer.toString(exercise_id));
        ResultSet rs = stmt.executeQuery(sql);
        connectionPool.releaseConnection(connection);
        Map<String,String> data = new LinkedHashMap<>();
        while (rs.next()){
            data = new LinkedHashMap<>();
            data.put("exercise_id",rs.getString("exercise_id"));
            data.put("exercise_name",rs.getString("exercise_name"));
            lista.add(new JSONObject(data).toString());
        }
        return lista;
    }

    public static void main(String[] args) throws SQLException {
        Fasade fasade = new Fasade();
        List <String> lista = fasade.ExerciseReplacement(1,1);
        for (String x: lista) {
            System.out.println(x);
        }
        System.exit(0);
    }


}
