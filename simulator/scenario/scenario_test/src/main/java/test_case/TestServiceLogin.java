package test_case;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class TestServiceLogin {
    private WebDriver driver;
    private String baseUrl;
    private static void ServiceLogin(WebDriver driver, String username, String password){
        driver.findElement(By.id("login_email")).clear();
        driver.findElement(By.id("login_email")).sendKeys(username);
        driver.findElement(By.id("login_password")).clear();
        driver.findElement(By.id("login_password")).sendKeys(password);
        driver.findElement(By.id("login_button")).click();
    }

    @BeforeClass
    public void setUp() throws Exception {
//        System.setProperty("webdriver.chrome.driver", "/Users/hechuan/Downloads/chromedriver");
//        driver = new ChromeDriver();
        driver = new RemoteWebDriver(new URL("http://hub:4444/wd/hub"),
                DesiredCapabilities.chrome());
        baseUrl = "http://10.141.211.179:30099";
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    public void testSignIn()throws Exception{
        driver.get(baseUrl + "/");

        //call function login
        String username = (int)(Math.random() * 2000) + "@163.com";
        String password = "123456";
        ServiceLogin(driver, username, password);
        Thread.sleep(1000);

        //get login status
        String statusSignIn = driver.findElement(By.id("login_result_msg")).getText();
        if (!"".equals(statusSignIn))
            System.out.println("Sign Up btn status: "+statusSignIn);
        else
            System.out.println("False，Status of Sign In btn is NULL!");
        Assert.assertTrue(statusSignIn.startsWith("Success"));
    }

    @AfterClass
    public void tearDown() throws Exception {
        driver.quit();
    }
}
