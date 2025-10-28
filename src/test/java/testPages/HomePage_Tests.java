package testPages;



import org.testng.annotations.Test;

import testBase.TestBase;

public class HomePage_Tests  extends TestBase{
	
	
	@Test 
	public void loginButtonOnHomePage() throws Exception {
		
		
	homePageObjects.loginButton();
	
	}

}
