package testBase;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import pageObjects.HomePageObjects;
import pageObjects.LoginPageObjects;
import reusableComponents.CommonMethods;
//import pageObjects.EnterInsurantDataPageObjects;
//import pageObjects.EnterProductDataPageObjects;
//import pageObjects.EnterVehicalDataPageObjects;
//import pageObjects.HomePageObjects;
//import pageObjects.SelectPriceOptionsPageObjects;
import reusableComponents.PropertiesOperations;

public class TestBase extends ObjectsRepo{
	
	public static WebDriver driver;
	
	public HomePageObjects homePageObjects ;
	public LoginPageObjects loginPageObjects ;
	public CommonMethods commonMethods = new CommonMethods();
	
	public void LaunchBrowserAndNavigate() throws Exception {
		//read prop file and get browser and url
		String browser = PropertiesOperations.getPropertyValueByKey("browser");
		String url = PropertiesOperations.getPropertyValueByKey("url");
		
		if(browser.equalsIgnoreCase("chrome")) {
//			WebDriverManager.chromedriver().setup(); // selenium -4 does not require setup explicitly
			 driver = new ChromeDriver();
		} else if(browser.equalsIgnoreCase("firefox")) {
//			WebDriverManager.firefoxdriver().setup();
			 driver = new FirefoxDriver();
		} else if(browser.equalsIgnoreCase("ie")) {
//			WebDriverManager.iedriver().setup();
			 driver = new InternetExplorerDriver();
		}

//		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS); selenium-4 uses Duration class for implicit wait
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));
		driver.manage().window().maximize();
		
		driver.get(url);
		
	}

	@BeforeMethod   /// it will get execute before each test method within current class
	public void setupMethod() throws Exception {
		LaunchBrowserAndNavigate();
		homePageObjects = new HomePageObjects();
		loginPageObjects = new LoginPageObjects();
	}
	
	@AfterMethod
	public void cleanUp() {
		driver.close();
	}
}
