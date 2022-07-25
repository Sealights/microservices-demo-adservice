package hipstershop.http.netty.json;

import static hipstershop.http.netty.HttpUtils.APPLICATION_JSON_CONTENT_TYPE_HEADER;
import static hipstershop.http.netty.HttpUtils.getRequestBody;
import static hipstershop.http.netty.HttpUtils.inputHeadersToMap;
import static hipstershop.http.netty.HttpUtils.inputParamsToMap;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hipstershop.http.netty.MessagesHandler;
import hipstershop.http.netty.ResponseBuilder;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;

public class JsonMessagesHandler implements MessagesHandler {

  public static final String REQUEST_BODY = "requestBody";
  public static final String INPUT_PARAMS = "inputParams";
  public static final String INPUT_HEADERS = "inputHeaders";
  private static Gson gson = new GsonBuilder()
      .setPrettyPrinting()
      .create();

  @Override
  public void handleHttpContent(Object msg, ResponseBuilder responseData, HttpRequest request) {
    String requestBody = getRequestBody(msg);
    if (isNotEmpty(requestBody)) {
      responseData.append(REQUEST_BODY, gson.fromJson(requestBody, Map.class));
    }
  }

  @Override
  public void handleHttpRequest(ResponseBuilder responseData, HttpRequest request) {
    responseData.append(INPUT_PARAMS, inputParamsToMap(request));
    responseData.append(INPUT_HEADERS, inputHeadersToMap(request));
  }

  @Override
  public void addResponseHeaders(FullHttpResponse httpResponse) {
    httpResponse.headers()
        .set(HttpHeaderNames.CONTENT_TYPE, APPLICATION_JSON_CONTENT_TYPE_HEADER);
  }

  @Override
  public ResponseBuilder getResponseBuilder() {
    return new JsonResponseBuilder();
  }
}
