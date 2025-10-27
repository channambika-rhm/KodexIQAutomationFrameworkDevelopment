package pageObjects;

import java.util.HashMap;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import reusableComponents.CommonConstants;
import testBase.TestBase;

public class LoginPageObjects extends TestBase {
	
	@FindBy (name = "emailId")
	WebElement email;
	
	@FindBy(name = "password")
	WebElement password;
	
	@FindBy(xpath = "//span[text()='Login']")
	WebElement login;
	

	//constructor - to use initElement method
	public LoginPageObjects() {
		PageFactory.initElements(driver, this);
	}
	
	public String login(HashMap<String, String> testData) throws Exception {
		System.out.println(driver);
		
		Thread.sleep(2000);
		email.sendKeys(testData.get(CommonConstants.USERNAME).toString());
		password.sendKeys(testData.get(CommonConstants.PASSWORD).toString());
		Thread.sleep(3000);
		login.click();
		Thread.sleep(3000);
		commonMethods.handleAlert();
		return driver.getTitle();
	}

}
