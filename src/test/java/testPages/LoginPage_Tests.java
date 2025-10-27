package testPages;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import reusableComponents.ExcelOperations;
import testBase.TestBase;

public class LoginPage_Tests extends TestBase{

	
	String fileName = "//src//test//resources//testData//APMT_Logins.xlsx";

	ExcelOperations excel = new ExcelOperations(fileName, "Sheet1");
	
	

	@Test(dataProvider = "APMTLogin")
	public void APMTlogins(Object obj1) throws Exception {
		HashMap<String, String> testData = (HashMap<String, String>) obj1;
		homePageObjects.loginButton();
		System.out.println("In login method");
		
		String title  = loginPageObjects.login(testData);
		//String expectedTitle = "qwertt";
		
		//Assert.assertEquals(title, expectedTitle, "Title not matched");
		System.out.println("The Title is:" + title);
		System.out.println("Login done");
		
		
	
	}
	
	
	// Data provider method --> return object array

		@DataProvider(name = "APMTLogin")
		public Object[][] testDataSupplier() throws Exception {
			Object[][] obj = new Object[excel.getRowCount()][1];
			for (int i = 1; i <= excel.getRowCount(); i++) {
				HashMap<String, String> testData = excel.getTestDataInMap(i);
				obj[i - 1][0] = testData;
			}
			return obj;

		}


}
