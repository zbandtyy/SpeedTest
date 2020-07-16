package spark.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/15 2:29
 * @description：
 * @modified By：
 * @version: $
 */
public class InheritanceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private static final String CLASSNAME = "CLASSNAME";
    private static final String INSTANCE  = "INSTANCE";
    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();

        Class<?> klass = null;
        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new JsonParseException(e.getMessage());
        }
        return context.deserialize(jsonObject.get(INSTANCE), klass);

    }

    @Override
    public JsonElement serialize(T src, Type type, JsonSerializationContext context) {
        JsonObject retValue = new JsonObject();//需要序列化的总对象
        String className = src.getClass().getName();
        retValue.addProperty(CLASSNAME, className);//增加属性className
        JsonElement elem = context.serialize(src);
        retValue.add(INSTANCE, elem);//增加实际存在的对象
        return retValue;
    }
}
