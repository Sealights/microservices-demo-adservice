package hipstershop.http.netty.plaintext;

import hipstershop.http.netty.ResponseBuilder;

public class PlainTextResponseBuilder implements ResponseBuilder {

  private StringBuilder stringBuilder;

  @Override
  public ResponseBuilder append(Object object) {
    stringBuilder.append(object);
    return this;
  }

  @Override
  public ResponseBuilder append(String key, Object object) {
    stringBuilder.append(key).append("=").append(object);
    return this;
  }

  @Override
  public String build() {
    return stringBuilder.toString();
  }
  
  @Override
  public String toString() {
    return stringBuilder.toString();
  }
}
