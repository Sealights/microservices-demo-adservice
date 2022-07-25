package hipstershop.http.netty;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import hipstershop.http.netty.adservice.AdServiceMessagesHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class CustomHttpServerHandler extends SimpleChannelInboundHandler<Object> {

  private HttpRequest request;
  private MessagesHandler messagesHandler = new AdServiceMessagesHandler();
  private ResponseBuilder responseData = messagesHandler.getResponseBuilder();

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

    if (msg instanceof HttpRequest) {
      handleHttpRequest(ctx, (HttpRequest) msg);
    }

    if (msg instanceof HttpContent) {
      handleHttpContent(ctx, msg);
    }
  }

  private void handleHttpContent(ChannelHandlerContext ctx, Object msg) {
    messagesHandler.handleHttpContent(msg, responseData, request);
    writeResponse(ctx, msg, responseData);
  }

  private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest msg) {
    HttpRequest request = this.request = msg;
    messagesHandler.handleHttpRequest(responseData, request);
    if (HttpUtil.is100ContinueExpected(request)) {
      writeResponse(ctx, msg, responseData);
    }
  }


  private void writeResponse(ChannelHandlerContext ctx, Object trailer,
      ResponseBuilder responseData) {
    if (!(trailer instanceof LastHttpContent)) {
      return;
    }
    boolean keepAlive = HttpUtil.isKeepAlive(request);

    FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1,
        ((HttpObject) trailer).decoderResult()
            .isSuccess() ? OK : BAD_REQUEST,
        Unpooled.copiedBuffer(responseData.toString(), CharsetUtil.UTF_8));
    messagesHandler.addResponseHeaders(httpResponse);

    if (keepAlive) {
      httpResponse.headers()
          .setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content()
              .readableBytes());
      httpResponse.headers()
          .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    }

    ctx.write(httpResponse);

    if (!keepAlive) {
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
          .addListener(ChannelFutureListener.CLOSE);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
