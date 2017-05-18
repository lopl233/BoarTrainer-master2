
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Messages{
    static JSONObject jsonLogin(String login, String password){
        Map<String,String> data = new LinkedHashMap<>();
        data.put("login", login);
        data.put("hasło", password);
         return new JSONObject(data);
    }
}
