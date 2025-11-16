package testPages;

import java.util.HashMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import reusableComponents.ExcelOperations;
import testBase.TestBase;

public class LoginPage_Tests extends TestBase {

	@BeforeClass
	public void initializeExcel() {
		// fileName is read in TestBase.readConfig() which runs before subclass
		excel = new ExcelOperations(fileName, "Login");
	}

	// Data provider: returns (testDataMap, rowNumber)
	@DataProvider(name = "KODEXIQlogin")
	public Object[][] testDataSupplier() throws Exception {
		int totalRows = excel.getRowCount();
		Object[][] obj = new Object[totalRows][2];
		for (int i = 1; i <= totalRows; i++) {
			HashMap<String, String> testData = excel.getTestDataInMap(i);
			obj[i - 1][0] = testData;
			obj[i - 1][1] = i; // row number so test can write back to correct row
		}
		return obj;
	}

	@Test(dataProvider = "KODEXIQlogin")
	public void testLoginCombinations(HashMap<String, String> data, Integer rowNum) throws Exception {
		System.out.println("\n=== 🧾 Executing Test Data Row: " + rowNum + " ===");

		String scenario = data.get("Scenario");
		String email_address = data.get("Email Address");
		String password = data.get("Password");
		String expectedResult = data.get("Expected Result");

		System.out.println("Scenario: " + scenario);
		System.out.println("Email: " + email_address);
		System.out.println("Password: " + password);
		System.out.println("ExpectedResult: " + expectedResult);

		// Always start from login page to avoid stale state
		loginPageObjects.resetToLoginPage(url);

		loginPageObjects.enterEmail(email_address);
		loginPageObjects.enterPassword(password);

		try {
			loginPageObjects.clickLoginButton();
		} catch (Exception e) {
			System.out.println("⚠️ Login button not clickable for Row " + rowNum);
		}

		String actualResult;

		// Determine actual outcome
		if (loginPageObjects.isDashboardOpened()) {
			actualResult = "KodeXIQ";
		} else {
			actualResult = loginPageObjects.getErrorMessage(scenario);
		}

		// Log output
		System.out.println("Expected: " + expectedResult);
		System.out.println("Actual:   " + actualResult);

		// ✅ Compare actual vs expected
		String status;
		if (actualResult != null && actualResult.trim().equalsIgnoreCase(expectedResult.trim())) {
			status = "PASS";
			System.out.println("✅ PASS: Expected and actual match for scenario '" + scenario + "'");
		} else {
			status = "FAIL";
			System.out.println("❌ FAIL: Mismatch for scenario '" + scenario + "'");
			System.out.println("   Expected → " + expectedResult);
			System.out.println("   Actual   → " + actualResult);
		}

		// ✅ Write all outputs to Excel
		excel.writeResult(rowNum, status);
		excel.writeActualResult(rowNum, actualResult == null ? "No visible error message" : actualResult);
		excel.writeExecutionTime(rowNum); // <-- Writes timestamp

		System.out.println("\n=== ✅ All Excel rows executed successfully ===");
	}
}
