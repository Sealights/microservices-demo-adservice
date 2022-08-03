package hipstershop.http.netty.adservice;

import static hipstershop.http.netty.HttpUtils.inputQueryStringParamsToMap;

import hipstershop.AdService;
import hipstershop.Demo.Ad;
import hipstershop.http.netty.HttpUtils;
import hipstershop.http.netty.ResponseBuilder;
import hipstershop.http.netty.json.JsonMessagesHandler;
import io.netty.handler.codec.http.HttpRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Level;

public class AdServiceMessagesHandler extends JsonMessagesHandler {

  public static final String CATEGORY = "category";
  public static final String ADS_ENDPOINT = "/ads";
  public static final String ADS = "ads";
  public static final String CONTEXT_KEYS = "context_keys";
  private AdService adService = AdService.getInstance();

  @Override
  public void handleHttpRequest(ResponseBuilder responseData, HttpRequest request) {
    //not required
  }

  @Override
  public void handleHttpContent(Object msg, ResponseBuilder responseData, HttpRequest request) {
    Map<String, Object> resultOfHandleAdsByCategory = handleGetAdsByCategory(msg, request);
    if (resultOfHandleAdsByCategory != null && !resultOfHandleAdsByCategory.isEmpty()) {
      responseData.append(resultOfHandleAdsByCategory);
    }
  }

  public Map<String, Object> handleGetAdsByCategory(Object msg, HttpRequest request) {
    if (request.uri().startsWith(ADS_ENDPOINT)) {
      List<String> categoriesFromRequestBody = getCategoriesFromRequestBody(msg);
      List<String> categoriesFromQueryString = getCategoriesFromQueryString(request);
      List<String> categories = new ArrayList<>();
      categories.addAll(categoriesFromRequestBody);
      categories.addAll(categoriesFromQueryString);
      Map<String, Object> result = new HashMap<>();
      List<Ad> ads = new ArrayList<>();
      if (CollectionUtils.isNotEmpty(categories)) {
        for (String category : categories) {
          Collection<Ad> adsByCategory = adService.getAdsByCategory(category);
          ads.addAll(adsByCategory);
        }
      } else {
        List<Ad> randomAds = adService.getRandomAds();
        ads.addAll(randomAds);
      }
      Set<AdHttpResponse> adsSet = new HashSet<>(convert(ads));
      result.put(ADS, adsSet);
      return result;
    }
    return Collections.emptyMap();
  }

  private List<String> getCategoriesFromQueryString(HttpRequest request) {
    Map<String, List<String>> queryStringParams = inputQueryStringParamsToMap(request);
    List<String> categoriesByCategoriesParameter = queryStringParams.get(CATEGORY);
    List<String> categoriesByContextKeyParameter = queryStringParams.get(CONTEXT_KEYS);
    List<String> result = new ArrayList<>();
    if (categoriesByCategoriesParameter != null) {
      result.addAll(categoriesByCategoriesParameter);
    }
    if (categoriesByContextKeyParameter != null) {
      result.addAll(categoriesByContextKeyParameter);
    }
    return result;
  }

  private List<String> getCategoriesFromRequestBody(Object msg) {
    String requestBody = HttpUtils.getRequestBody(msg);
    AdHttpRequest adHttpRequest = gson.fromJson(requestBody, AdHttpRequest.class);
    if (adHttpRequest != null) {
      List<String> contextKeys = adHttpRequest.getContextKeys();
      if (contextKeys != null) {
        return contextKeys;
      }
    }

    return Collections.emptyList();
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
