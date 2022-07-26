package hipstershop.http.netty.adservice;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdHttpRequest {

  @SerializedName("context_keys")
  private List<String> contextKeys;
}
