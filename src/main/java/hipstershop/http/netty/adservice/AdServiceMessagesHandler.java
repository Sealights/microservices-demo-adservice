package hipstershop.http.netty.adservice;

import static hipstershop.http.netty.HttpUtils.inputParamsToMap;

import hipstershop.AdService;
import hipstershop.Demo.Ad;
import hipstershop.http.netty.ResponseBuilder;
import hipstershop.http.netty.json.JsonMessagesHandler;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public class AdServiceMessagesHandler extends JsonMessagesHandler {

  public static final String CATEGORY = "category";
  public static final String RANDOM_CATEGORY = "random";
  public static final String ADS_ENDPOINT = "/ads";
  private AdService adService = AdService.getInstance();

  @Override
  public void handleHttpRequest(ResponseBuilder responseData, HttpRequest request) {
    Map<String, Object> resultOfHandleAdsByCategory = handleGetAdsByCategory(request);
    if (resultOfHandleAdsByCategory != null && !resultOfHandleAdsByCategory.isEmpty()) {
      responseData.append(resultOfHandleAdsByCategory);
    }
  }

  public Map<String, Object> handleGetAdsByCategory(HttpRequest request) {
    if (request.uri().startsWith(ADS_ENDPOINT)) {
      Map<String, List<String>> inputParams = inputParamsToMap(request);
      List<String> categories = inputParams.get(CATEGORY);
      Map<String, Object> result = new HashMap<>();
      if (CollectionUtils.isNotEmpty(categories)) {
        for (String category : categories) {
          Collection<Ad> adsByCategory = adService.getAdsByCategory(category);
          result.put(category, convert(adsByCategory));
        }
      } else {
        List<Ad> randomAds = adService.getRandomAds();
        result.put(RANDOM_CATEGORY, convert(randomAds));
      }
      return result;
    }
    return Collections.emptyMap();
  }

  public AdHttpResponse convert(Ad ad) {
    return new AdHttpResponse(ad.getRedirectUrl(), ad.getText());
  }

  public List<AdHttpResponse> convert(Collection<Ad> ads) {
    return ads.stream()
        .map(this::convert)
        .toList();
  }
}
