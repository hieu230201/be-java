package org.example.basewebsub.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.gson.Gson;
import org.example.basewebsub.logging.LogManage;
import org.example.basewebsub.logging.bases.ILogManage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hieunt
 */
public class JsonConvertUtil {
    private static final ILogManage logger = LogManage.getLogManage(JsonConvertUtil.class);

    public static String convertObjectToJson(Object object) {
        String json = null;
        try {
            if (object != null) {
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(object);
            }
        } catch (JsonProcessingException e) {
            logger.error("convertObjectToJson - " + e.getMessage(), e);
            return null;
        }
        return json;
    }

    public static <T> String objectToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            String jsonString = "";
            try {
                jsonString = mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                jsonString = "Can't build json from object";
            }
            return jsonString;
        }
    }

    public static <T> String listToJson(List<T> list) {
        if (list == null || list.size() == 0) {
            //return "[]";
            return null;
        }
        StringBuilder json = new StringBuilder("[");
        for (T item : list) {
            json.append(objectToString(item)).append(",");
        }
        json = new StringBuilder(json.substring(0, json.length() - 1) + "]");
        return json.toString();
    }

    public static <T> List<T> stringJsonToList(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, clazz);
        return mapper.readValue(json, listType);
    }

    public static <T> T convertStringToObjectComplex(String json, Class<T> targetClass) {
        Gson gson = new Gson(); // Or use new GsonBuilder().create();
        return gson.fromJson(json, targetClass); // deserializes json into target2
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertStringToObject(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(str, clazz);
            } catch (IOException e) {
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertStringToObject(String str, TypeReference<T> typeReference) throws JsonProcessingException {
        if (StringUtil.isNullOrEmpty(str) || typeReference == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return typeReference.getType().equals(String.class) ? (T) str : mapper.readValue(str, typeReference);
    }

}

