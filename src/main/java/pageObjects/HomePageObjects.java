package pageObjects;



import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import testBase.TestBase;

public class HomePageObjects  extends TestBase{
	
	@FindBy(xpath = "//a[text()='Login']")
	WebElement loginButton;
	
	
	@FindBy( xpath = "//a[text()='SignUp']")
	WebElement signUpButton;
	
	
	//constructor - to use initElement method
			public HomePageObjects() {
				PageFactory.initElements(driver, this);
			}
			
			public void loginButton() {
				System.out.println(driver);
				loginButton.click();
				
			}
			
			public void signUpButton() {
				System.out.println(driver);
				signUpButton.click();
			}

}
