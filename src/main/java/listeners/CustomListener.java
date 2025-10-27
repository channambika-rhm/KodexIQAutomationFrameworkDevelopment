package listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

import reusableComponents.Utility;

public class CustomListener implements ITestListener {
	
		@Override
		public void onTestFailure(ITestResult result) {
			
			Utility.takeScreenShot();
			
			}

}
