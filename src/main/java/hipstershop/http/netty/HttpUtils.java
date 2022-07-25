package hipstershop.http.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtils {

  public static final String CHARSET_UTF_8 = "charset=UTF-8";
  public static final String TEXT_PLAIN_CONTENT_TYPE = "text/plain";
  public static final String TEXT_PLAIN_CONTENT_TYPE_HEADER =
      TEXT_PLAIN_CONTENT_TYPE + "; " + CHARSET_UTF_8;
  public static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
  public static final String APPLICATION_JSON_CONTENT_TYPE_HEADER =
      APPLICATION_JSON_CONTENT_TYPE + "; " + CHARSET_UTF_8;

  private HttpUtils() {
  }

  public static Map<String, String> inputHeadersToMap(HttpRequest request) {
    Map<String, String> inputHeadersMap = new HashMap<>();
    HttpHeaders headers = request.headers();
    Iterator<Entry<String, String>> entryIterator = headers.iteratorAsString();
    while (entryIterator.hasNext()) {
      Entry<String, String> headerEntry = entryIterator.next();
      inputHeadersMap.put(headerEntry.getKey(), headerEntry.getValue());
    }
    return inputHeadersMap;
  }

  public static Map<String, List<String>> inputParamsToMap(HttpRequest request) {
    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
    return queryStringDecoder.parameters();
  }

  public static String getRequestBody(Object msg) {
    if (msg instanceof HttpContent httpContent) {
      ByteBuf content = httpContent.content();
      if (content.isReadable()) {
        return content.toString(CharsetUtil.UTF_8);
      }
    }
    return "";
  }
}
