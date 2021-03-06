package hipstershop.http.netty.adservice;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdHttpResponse {

  @SerializedName("redirect_url")
  private String redirectUrl;
  private String text;
}
