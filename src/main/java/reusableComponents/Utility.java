package reusableComponents;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import testBase.TestBase;

public class Utility  extends TestBase {
	
	public static void takeScreenShot() {
		
		System.out.println("inside screenshot method");
		File screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		System.out.println("after taking screenshot");
		try {
			//Using the time date time stamp to create unique file name for the each screenshot
			System.out.println("path before");
			FileUtils.copyFile(screenshotFile, new File ("E:\\Pavani Nadupalli\\Pavani_WorkSpace\\APMT_Automation\\screenshots"+timestamp()+".png"));
			System.out.println("path after");
		}catch (IOException e) {
			e.printStackTrace();
		  
	}
	}

	public static String timestamp() {
		
		return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
	}
}
