package pageObjects;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import testBase.TestBase;

public class HomePageObjects extends TestBase {

	WebDriver driver;
	WebDriverWait wait;

	@FindBy(xpath = "//button[text()='Login']")
	WebElement loginButton;

	@FindBy(xpath = "//button[text()='SignUp']")
	WebElement signUpButton;

	@FindBy(xpath = "//span[text()='Logout']")
	WebElement logoutSp;

	// constructor - to use initElement method
	public HomePageObjects(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public void clickLoginButton() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click()", loginButton);

	}

	public void signUpButton() {
		signUpButton.click();
	}

	public void clickLogout() {
		logoutSp.click();
	}

	public boolean isLoginBtnDisplayed() {
		return loginButton.isDisplayed();
	}

}
