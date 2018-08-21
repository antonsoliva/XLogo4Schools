package xlogo.storage;


import org.json.JSONException;
import org.json.JSONObject;
import xlogo.storage.workspace.Serializer;

public abstract class JSONSerializer<T> implements Serializer<T> {
    public abstract JSONObject serialize2JSON(T object);

    public abstract T deserialize(JSONObject json);

    @Override
    public T deserialize(String json) {
        try {
            return deserialize(new JSONObject(json.trim()));
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public String serialize2String(T target) {
        JSONObject json = serialize2JSON(target);
        String serialized = json.toString();
        return serialized;
    }
}
