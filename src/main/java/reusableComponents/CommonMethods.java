package reusableComponents;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import testBase.TestBase;

public class CommonMethods {
	
	//common method to select dd value
	public void selectDropdownOption(WebElement element, String valueToBeSelected) throws Exception {
		Select os = new Select(element);
		try {
		os.selectByVisibleText(valueToBeSelected);
		} catch(Exception e) {
			throw new Exception("Value is not present in dropdown for WebElement: "+element + "Value to be selected is: "+valueToBeSelected);
		}
		}
	
	public void selectRadioButtonValue(List<WebElement> element, String valueToBeSelected) {
		for (WebElement ref : element) {
			if(ref.getText().equalsIgnoreCase(valueToBeSelected)) {
				ref.click();
				break;
			}
			
		}
	}
	
	//alert message handling

		
		public  void handleAlert() {
	       
	         TestBase.driver.switchTo().alert().accept();         
	         
	            
	       
	    }
			public void editAlert() {
			TestBase.driver.switchTo().alert().sendKeys("150");
	     
			}
		
	
	
	//selecting check boxes
	public void selectCheckBoxes(List<WebElement> element, String checks) {
		String[] checksArray = checks.split(",");
		for (String str : checksArray) {   //speeding, Other
			for (WebElement ele : element) {
				if(ele.getText().equalsIgnoreCase(str)) {
					ele.click();
					break;
				}
			}
		}
		
	}
	
	
	
	//get dropdown options as list of string for compare
	public List<String> getDropDownOptionsAsList(WebElement element) {
		Select os = new Select(element);
		List<WebElement> list_webElement_model = os.getOptions();
		List<String> actualContents = new ArrayList<String>();
		
		for (WebElement ref : list_webElement_model) {
			actualContents.add(ref.getText());
		}
		return actualContents;
	}
	// Common Method to Upload a file using Robot Class
	public void selectFileUpLoad(WebElement element, String filePath, WebDriver driver) throws Exception {
		try {
			// file path passed as parameter to StringSelection
			System.out.println(filePath);
			StringSelection s = new StringSelection(filePath);
			// Clipboard copy
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);

			// identify element and click
			JavascriptExecutor j = (JavascriptExecutor) driver;
			j.executeScript("arguments[0].click();", element);

			// Robot object creation
			Robot r = new Robot();

			r.delay(250);
			r.keyPress(KeyEvent.VK_ENTER);
			r.keyRelease(KeyEvent.VK_ENTER);
			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_ENTER);
			r.delay(150);
			r.keyRelease(KeyEvent.VK_ENTER);

		} catch (Exception e) {
			throw new Exception("File Path not found " + filePath);
		}
	}

			
}
