package pageObjects;

import java.time.Duration;
import java.util.LinkedHashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import testBase.TestBase;

public class LoginPageObjects extends TestBase {
	WebDriver driver;
	WebDriverWait wait;
	
	@FindBy(name = "email")
	WebElement emailTb;

	@FindBy(name = "password")
	WebElement passwordTb;

	@FindBy(xpath = "//button[text()='Sign In']")
	WebElement signInBtn;

	@FindBy(xpath = "//div[contains(text(),'Incorrect email or password')]")
	WebElement incorrectEmailOrPwdErrorMsg;

	@FindBy(xpath = "//p[text()='Email is required']")
	WebElement emailReqErrorMsg;
	
	@FindBy(xpath = "//p[text()='Password is required']")
	WebElement pwdReqErrorMsg;
	
	@FindBy(xpath = "//p[text()='Password must be at least 8 characters']")
	WebElement pwdLessCharErrorMsg;
	
	// constructor - to use initElement method
	public LoginPageObjects(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public void resetToLoginPage(String url) {
		try {
			driver.navigate().refresh();
			driver.navigate().to(url);
			wait.until(ExpectedConditions.visibilityOf(emailTb));
		} catch (Exception e) {
			System.out.println("⚠️ Could not load login page: " + e.getMessage());
		}
	}
	
	public void enterEmail(String email) {
		wait.until(ExpectedConditions.visibilityOf(emailTb));
		emailTb.clear();
		if (email != null)
			emailTb.sendKeys(email);
	}

	public void enterPassword(String password) {
		wait.until(ExpectedConditions.visibilityOf(passwordTb));
		passwordTb.clear();
		if (password != null)
			passwordTb.sendKeys(password);
	}

	public void clickLoginButton() {
		wait.until(ExpectedConditions.elementToBeClickable(signInBtn));
		signInBtn.click();
	}

	public boolean isDashboardOpened() {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.urlContains("/user/userdashboard"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isVisible(WebElement element) {
	    try { return element != null && element.isDisplayed(); }
	    catch (Exception e) { return false; }
	}

	public String getErrorMessage(String scenario) {
	    if (scenario == null) scenario = "";
	    String s = scenario.toLowerCase();

	    try {
	        // Map scenario keywords → corresponding error WebElement
	        LinkedHashMap<String, WebElement> scenarioMap = new LinkedHashMap<>();
	        scenarioMap.put("blank username", emailReqErrorMsg);
	        scenarioMap.put("blank password", pwdReqErrorMsg);
	        scenarioMap.put("blank", emailReqErrorMsg); // fallback for any blank-case
	        scenarioMap.put("invalid", incorrectEmailOrPwdErrorMsg);
	        scenarioMap.put("less than", pwdLessCharErrorMsg);

	        // 1️⃣ Check scenario-driven matches
	        for (String key : scenarioMap.keySet()) {
	            if (s.contains(key)) {
	                WebElement element = scenarioMap.get(key);
	                if (isVisible(element)) return element.getText().trim();
	            }
	        }

	        // 2️⃣ Fallback: return first visible known error
	        if (isVisible(incorrectEmailOrPwdErrorMsg)) return incorrectEmailOrPwdErrorMsg.getText().trim();
	        if (isVisible(emailReqErrorMsg))           return emailReqErrorMsg.getText().trim();
	        if (isVisible(pwdReqErrorMsg))             return pwdReqErrorMsg.getText().trim();
	        if (isVisible(pwdLessCharErrorMsg))        return pwdLessCharErrorMsg.getText().trim();

	    } catch (Exception ignored) {}

	    return "No visible error message";
	}


}
