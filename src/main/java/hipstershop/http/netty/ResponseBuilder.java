package hipstershop.http.netty;

public interface ResponseBuilder {

  ResponseBuilder append(Object object);

  ResponseBuilder append(String key, Object object);

  String build();
}
