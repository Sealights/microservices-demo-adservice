package hipstershop.http.netty;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public interface MessagesHandler {

  void handleHttpContent(Object msg, ResponseBuilder responseData,
      HttpRequest request);

  void handleHttpRequest(ResponseBuilder responseData, HttpRequest request);

  void addResponseHeaders(FullHttpResponse httpResponse);

  ResponseBuilder getResponseBuilder();
}
