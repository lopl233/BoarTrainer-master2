import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Plan_percent{
    String name = "";
    int id;
    HashMap<String,HashMap<String,Integer>> percents = new HashMap<String, HashMap<String,Integer>>();

    Plan_percent(String name, int id) throws SQLException {
        this.id = id;
        this.name = name;
        fill();
    }

    void fill() throws SQLException {
        Connection connection = ConnectionPool.getInstance().getConnection();
        Statement stmt = connection.createStatement();
        String sql = String.format("SELECT * FROM `plan_parameter_values` WHERE plan_id = %s", Integer.toString(id));
        ResultSet rs = stmt.executeQuery(sql);
        ConnectionPool.getInstance().releaseConnection(connection);

        HashMap<String,Integer> hashmapa = new HashMap<String,Integer>();
        percents.put("age", hashmapa);
        hashmapa = new HashMap<String,Integer>();
        percents.put("height", hashmapa);
        hashmapa = new HashMap<String,Integer>();
        percents.put("weight",  hashmapa);
        hashmapa = new HashMap<String,Integer>();
        percents.put("fraquency",  hashmapa);
        hashmapa = new HashMap<String,Integer>();
        percents.put("advancement_level",  hashmapa);
        hashmapa = new HashMap<String,Integer>();
        percents.put("goal",  hashmapa);

        while (rs.next()){
            String parameter = rs.getString("parameter");
            String value = rs.getString("value");
            int percent = rs.getInt("percent");
            try {
                percents.get(parameter).put(value, percent);
            }catch(Exception e){System.out.println(parameter + " " + value + " " + percent);}
        }


    }

    int Calculate(String Age, String Height, String Weight, String Fraquency, String Advancement_level, String Goal  ){
        double result = 100;
        result*= percents.get("age").get(Age);
        result /=100;
        result*= percents.get("height").get(Height);
        result /=100;
        result*= percents.get("weight").get(Weight);
        result /=100;
        result*= percents.get("fraquency").get(Fraquency);
        result /=100;
        result*= percents.get("advancement_level").get(Advancement_level);
        result /=100;
        result*= percents.get("goal").get(Goal);
        result /=100;
        return  (int)result;
    }
}
