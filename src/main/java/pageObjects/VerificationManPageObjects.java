package pageObjects;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import reusableComponents.PropertiesOperations;
import testBase.TestBase;

public class VerificationManPageObjects extends TestBase {
	
	@FindBy(xpath = "//a[text()='Login']")
	WebElement loginButton;

	@FindBy (name = "emailId")
	WebElement email;
	
	@FindBy(name = "password")
	WebElement password;
	
	@FindBy(xpath = "//span[text()='Login']")
	WebElement login;
	
	@FindBy(xpath = "//span[text()='New Projects']")
	WebElement newprojects;
	
	

	public VerificationManPageObjects() {
		PageFactory.initElements(driver, this);
	}
	
	public String loginAsVerificationManager() throws Exception {
		System.out.println(driver);
		loginButton.click();
		Thread.sleep(2000);
		email.sendKeys(PropertiesOperations.getPropertyValueByKey("VerificationManager"));
		password.sendKeys(PropertiesOperations.getPropertyValueByKey("VerificationPassword"));
		Thread.sleep(2000);
		login.click();
		Thread.sleep(1000);
		commonMethods.handleAlert();
		Thread.sleep(0);
		driver.findElement(By.xpath("//span[text()='Projects']")).click();
		Thread.sleep(2000);
		newprojects.click();
		Thread.sleep(2000);
		//table[contains(@role,'table')]//td[2]
		
		//List<WebElement> projectNames = driver.findElements(By.xpath("//table[contains(@role,'table')]//td[2]"));
		//Boolean projectStatus = false;
		//for(WebElement ele: projectNames) {
			//String value = ele.getText();
			//System.out.println("The second column Values are" + value);
			//if(value.contains("Tools"))
			//if(value.contains("Raji")) {
			//if(value.contains("Java")) {
				//projectStatus = true;
				//break;
			//}
			
		//}
		
		driver.findElement(By.xpath("//table[contains(@role,'table')]//td[3]//a[1]")).click();
		Thread.sleep(7000);
		driver.findElement(By.xpath("//table[contains(@class,'table')]//td[3]/..//td[2]/a")).click();
		Thread.sleep(5000);
		WebElement element = driver.findElement(By.xpath("//span[text() = 'Verify' and @class = 'mdc-button__label']"));		
		
		JavascriptExecutor js = (JavascriptExecutor) driver; 

		// Call scrollTo() method of Javascript using window object. 
		    //js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		//js.executeScript("window.scrollBy(0, 2000)");
		    js.executeScript("arguments[0].scrollIntoView(true);",element);
		    Thread.sleep(2000);
		
		//Point location = driver.findElement(By.xpath("//span[text() = 'Verify' and @class = 'mdc-button__label']")).getLocation();
		//int x= location.getX();
		//int y = location.getY();
		//Actions action = new Actions(driver);
		//action.moveByOffset(x, y).click(); 
	     //action.build().perform(); 
		//Thread.sleep(3000);
		//driver.findElement(By.xpath("//span[text() = 'Approve']")).click();
		    driver.findElement(By.xpath("//span[text() = 'Verify' and @class = 'mdc-button__label']")).click();
		    Thread.sleep(1000);
		    driver.findElement(By.xpath("//span[text() = 'Approve']")).click();
		Thread.sleep(3000);
		    commonMethods.handleAlert();
		    Thread.sleep(3000);
		    commonMethods.handleAlert();
		return driver.getTitle();

		
	}

}
