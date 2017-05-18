import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Messages{
    static JSONObject jsonMessage(String messageType, String userId){
        Map<String,String> data = new LinkedHashMap<>();
        data.put("typ", messageType);
        data.put("id", userId);
        return new JSONObject(data);
    }
}