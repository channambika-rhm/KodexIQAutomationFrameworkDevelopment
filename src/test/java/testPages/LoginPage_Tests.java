package testPages;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import reusableComponents.ExcelOperations;
import testBase.TestBase;

public class LoginPage_Tests extends TestBase{	   

	    @BeforeMethod
	    public void initializeExcel() {
	        excel = new ExcelOperations(fileName, "Login");
	    }

	// Data provider method --> return object array

	@DataProvider(name = "KODEXIQlogin")
	public Object[][] testDataSupplier() throws Exception {
		Object[][] obj = new Object[excel.getRowCount()][1];
		for (int i = 1; i <= excel.getRowCount(); i++) {
			HashMap<String, String> testData = excel.getTestDataInMap(i);
			obj[i - 1][0] = testData;
		}
		return obj;
	}
	
	@Test(dataProvider = "KODEXIQlogin")
	public void KODEXIQloginTests(Object obj1) throws Exception {
		HashMap<String, String> testData = (HashMap<String, String>) obj1;
		homePageObjects.clickLoginButton();
		System.out.println("In login method");
		
//		String title  = loginPageObjects.login(testData);
//		String expectedTitle = "KodeXIQ";
//		
//		Assert.assertEquals(title, expectedTitle, "Title not matched");
//		System.out.println("The Title is:" + title);
//		System.out.println("Login done");			
	}
	

	
	



}
