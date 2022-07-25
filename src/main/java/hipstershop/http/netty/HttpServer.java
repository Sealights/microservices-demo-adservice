package hipstershop.http.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServer {

  private int port;
  static Logger logger = LoggerFactory.getLogger(HttpServer.class);

  public HttpServer() {
    this.port = Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT", "9556"));
  }

  public HttpServer(int port) {
    this.port = port;
  }

  public static void main(String[] args) throws Exception {
    new HttpServer().start();
  }

  public void start() throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline p = ch.pipeline();
              p.addLast(new HttpRequestDecoder());
              p.addLast(new HttpResponseEncoder());
              p.addLast(new CustomHttpServerHandler());
            }
          });

      ChannelFuture f = b.bind(port)
          .sync();
      f.channel()
          .closeFuture()
          .sync();

    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
