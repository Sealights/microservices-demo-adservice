package hipstershop;

import com.google.common.collect.ImmutableListMultimap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class AdServiceTest {
  @Test
  public void getInstanceReturnNonNull() {
    AdService instance = AdService.getInstance();
    Assert.assertNotNull(instance);
  }

  @Test
  public void createAdsMapReturnNonNullAndNotEmpty() {
    ImmutableListMultimap<String, hipstershop.Demo.Ad> adsMap = AdService.createAdsMap();
    Assert.assertNotNull(adsMap);
    Assert.assertTrue("adsMap is empty", adsMap.size() > 0);
  }

  @Test
  public void randomAdsReturnNonNullAndNotEmpty() {
    AdService instance = AdService.getInstance();
    List<hipstershop.Demo.Ad> randomAds = instance.getRandomAds();
    Assert.assertNotNull(randomAds);
    Assert.assertTrue("randomsAds is empty", randomAds.size() > 0);
  }
}
