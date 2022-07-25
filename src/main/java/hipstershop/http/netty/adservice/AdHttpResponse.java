package hipstershop.http.netty.adservice;

public class AdHttpResponse {

  private String productPath;
  private String text;

  public AdHttpResponse() {
  }

  public AdHttpResponse(String productPath, String text) {
    this.productPath = productPath;
    this.text = text;
  }

  public String getProductPath() {
    return productPath;
  }

  public void setProductPath(String productPath) {
    this.productPath = productPath;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
