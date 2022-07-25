package hipstershop.http.netty.plaintext;

import hipstershop.http.netty.HttpUtils;
import hipstershop.http.netty.MessagesHandler;
import hipstershop.http.netty.ResponseBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class PlainTextMessagesHandler implements MessagesHandler {


  StringBuilder formatParams(HttpRequest request) {
    StringBuilder responseData = new StringBuilder();
    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
    Map<String, List<String>> params = queryStringDecoder.parameters();
    if (!params.isEmpty()) {
      for (Entry<String, List<String>> p : params.entrySet()) {
        String key = p.getKey();
        List<String> values = p.getValue();
        for (String value : values) {
          responseData.append("Parameter: ")
              .append(key.toUpperCase())
              .append(" = ")
              .append(value.toUpperCase())
              .append("\r\n");
        }
      }
      responseData.append("\r\n");
    }
    return responseData;
  }

  StringBuilder formatBody(HttpContent httpContent) {
    StringBuilder responseData = new StringBuilder();
    ByteBuf content = httpContent.content();
    if (content.isReadable()) {
      responseData.append(content.toString(CharsetUtil.UTF_8)
          .toUpperCase());
      responseData.append("\r\n");
    }
    return responseData;
  }

  StringBuilder evaluateDecoderResult(HttpObject o) {
    StringBuilder responseData = new StringBuilder();
    DecoderResult result = o.decoderResult();

    if (!result.isSuccess()) {
      responseData.append("..Decoder Failure: ");
      responseData.append(result.cause());
      responseData.append("\r\n");
    }

    return responseData;
  }

  StringBuilder prepareLastResponse(LastHttpContent trailer) {
    StringBuilder responseData = new StringBuilder();
    responseData.append("Good Bye!\r\n");

    if (!trailer.trailingHeaders()
        .isEmpty()) {
      responseData.append("\r\n");
      for (CharSequence name : trailer.trailingHeaders()
          .names()) {
        for (CharSequence value : trailer.trailingHeaders()
            .getAll(name)) {
          responseData.append("P.S. Trailing Header: ");
          responseData.append(name)
              .append(" = ")
              .append(value)
              .append("\r\n");
        }
      }
      responseData.append("\r\n");
    }
    return responseData;
  }

  @Override
  public void handleHttpContent(Object msg, ResponseBuilder responseData,
      HttpRequest request) {
    responseData.append(evaluateDecoderResult(request));
    HttpContent httpContent = (HttpContent) msg;

    responseData.append(formatBody(httpContent));
    responseData.append(evaluateDecoderResult(request));

    if (msg instanceof LastHttpContent lastHttpContent) {
      responseData.append(prepareLastResponse(lastHttpContent));
    }
  }

  @Override
  public void handleHttpRequest(ResponseBuilder responseData, HttpRequest request) {
    responseData.append(evaluateDecoderResult(request));
    responseData.append(formatParams(request));
  }

  @Override
  public void addResponseHeaders(FullHttpResponse httpResponse) {
    httpResponse.headers()
        .set(HttpHeaderNames.CONTENT_TYPE, HttpUtils.TEXT_PLAIN_CONTENT_TYPE_HEADER);
  }

  @Override
  public ResponseBuilder getResponseBuilder() {
    return new PlainTextResponseBuilder();
  }
}
