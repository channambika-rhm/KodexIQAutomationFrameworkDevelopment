package pageObjects;

import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import reusableComponents.CommonConstants;
import reusableComponents.PropertiesOperations;
import testBase.TestBase;

public class SalesManagerObjects  extends TestBase{
	@FindBy(xpath = "//a[text()='Login']")
	WebElement loginButton;
	


	@FindBy (name = "emailId")
	WebElement email;
	
	@FindBy(name = "password")
	WebElement password;
	
	@FindBy(xpath = "//span[text()='Login']")
	WebElement login;
	
	
	@FindBy (xpath= "//span[text()='Projects']")
	WebElement projects;
	
	@FindBy (xpath = "//a[text()=' Computer Vision']")
	WebElement projectId;
	
	@FindBy (xpath = "//a[text()='Details']")
	WebElement details;
	
	
	@FindBy ( xpath = "//span[text()='Edit Price']")
	WebElement editpriceButton;
	
	
	@FindBy (xpath = "//span[text()='Edit Price']")
	WebElement editPrice;
	
	
	
	//constructor - to use initElement method
	public SalesManagerObjects() {
		PageFactory.initElements(driver, this);
	}
	
	public String loginAsSalesManager() throws Exception {
		System.out.println(driver);
		loginButton.click();
		Thread.sleep(2000);
		email.sendKeys(PropertiesOperations.getPropertyValueByKey("salesManagerUsername"));
		password.sendKeys(PropertiesOperations.getPropertyValueByKey("salesManagerPassword"));
		Thread.sleep(3000);
		login.click();
		Thread.sleep(1000);
		commonMethods.handleAlert();
		
		
		projects.click();
		Thread.sleep(2000);
		projectId.click();
		Thread.sleep(2000);
		details.click();
		Thread.sleep(2000);
		
		JavascriptExecutor j = (JavascriptExecutor) driver;
		j.executeScript("arguments[0].click()", editPrice);
		
		
		//Thread.sleep(1000);
		//commonMethods.editAlert();
		
		return driver.getTitle();
	}

	
	
}
