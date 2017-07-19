import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) throws JSONException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("message_type", "RegisterNewClient");
        data.put("login", "login");
        data.put("password", "password");
        data.put("name", "name");
        data.put("lastname", "lastname");
        data.put("PHONE","phone");
        data.put("EMAIL","email");
        data.put("verify_way","verify_way");
        JSONObject jsonObject = new JSONObject(data);

        String result = "";
        Iterator<?> keys = jsonObject.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            result = result + key + ":"+ jsonObject.getString(key) + "\n";
        }
        System.out.println(result);
    }

}

