package hipstershop.http.netty.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hipstershop.http.netty.ResponseBuilder;
import java.util.HashMap;
import java.util.Map;

public class JsonResponseBuilder implements ResponseBuilder {

  private static Gson gson = new GsonBuilder()
      .setPrettyPrinting()
      .create();
  private Map<Object, Object> result = new HashMap<>();

  @Override
  public ResponseBuilder append(Object object) {
    if (object instanceof Map<?, ?> map) {
      result.putAll(map);
    }
    return this;
  }

  @Override
  public ResponseBuilder append(String key, Object object) {
    result.put(key, object);
    return this;
  }

  @Override
  public String build() {
    return gson.toJson(result);
  }

  @Override
  public Object get(String key) {
    return result.get(key);
  }

  @Override
  public String toString() {
    return build();
  }
}
