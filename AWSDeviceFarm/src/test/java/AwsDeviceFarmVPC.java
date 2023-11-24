import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.devicefarm.DeviceFarmClient;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlRequest;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlResponse;

import java.net.URL;

public class AwsDeviceFarmVPC {
    private static RemoteWebDriver driver;

    @BeforeTest
    void setUp() {
        try {
            String myProjectARN = "arn:aws:devicefarm:us-west-2:552190243836:testgrid-project:47b48218-70ef-4586-bbb1-26ddc7a1841f";
            DeviceFarmClient client = DeviceFarmClient.builder()
                    .region(Region.US_WEST_2)
                    .build();
            CreateTestGridUrlRequest request = CreateTestGridUrlRequest.builder()
                    .expiresInSeconds(300)
                    .projectArn(myProjectARN)
                    .build();
            CreateTestGridUrlResponse response = client.createTestGridUrl(request);
            URL testGridUrl = new URL(response.url());
            DesiredCapabilities desired_capabilities = new DesiredCapabilities();
            desired_capabilities.setCapability("browserName","firefox");
            desired_capabilities.setCapability("browserVersion", "latest");
            desired_capabilities.setCapability("platform", "windows");
            // Or
            //DesiredCapabilities desired_capabilities = DesiredCapabilities.firefox();
            driver = new RemoteWebDriver(testGridUrl, desired_capabilities);//DesiredCapabilities.chrome());
        } catch (Exception ex ) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testStaticWeb() {
        driver.manage().window().maximize();
        driver.navigate().to("http://staticwebdfvpc.s3-website-us-west-2.amazonaws.com/");
        WebElement element = driver.findElement(By.tagName("h1"));
        Assert.assertEquals(element.getText(), "Welcome to my website");
    }

    @AfterTest
    void tearDown() {
        driver.quit();
    }
}
