package testBase;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pageObjects.HomePageObjects;
import pageObjects.LoginPageObjects;
import reusableComponents.PropertiesOperations;

public class TestBase extends ObjectsRepo{
	
	public static WebDriver driver;
	String browser ;
	protected String url;
	protected String fileName;
	
    /**
     * Read configuration properties once per class. This ensures subclasses'
     * @BeforeClass methods can rely on fileName/url being available.
     */
    @BeforeClass
    public void readConfig() throws Exception {
        url = PropertiesOperations.getPropertyValueByKey("url");
        fileName = PropertiesOperations.getPropertyValueByKey("testDataLocation");
        browser = PropertiesOperations.getPropertyValueByKey("browser");
    }
    
    /**
     * Setup browser before each test method to keep tests isolated.
     */
	@BeforeMethod   /// it will get execute before each test method within current class
	public void setupMethod() throws Exception {	
        if (browser == null) {
            // fallback read (shouldn't be needed if readConfig runs)
            browser = PropertiesOperations.getPropertyValueByKey("browser");
        }
        
		if(browser.equalsIgnoreCase("chrome")) {
			 driver = new ChromeDriver();
		} else if(browser.equalsIgnoreCase("firefox")) {
			 driver = new FirefoxDriver();
		} else if(browser.equalsIgnoreCase("ie")) {
			 driver = new InternetExplorerDriver();
		} else {
            // default to chrome if unknown
            driver = new ChromeDriver();
        }

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));
		driver.manage().window().maximize();
		
		driver.get(url);
		
		// initialize page objects with driver
		homePageObjects = new HomePageObjects(driver);
		loginPageObjects = new LoginPageObjects(driver);
	}
	
	@AfterMethod
	public void cleanUp() {
		if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Warning: error during driver.quit(): " + e.getMessage());
            }
        }
	}
}
