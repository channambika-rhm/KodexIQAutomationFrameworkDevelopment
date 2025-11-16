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

/**
 * Page Object representing the Login page of the application.
 * <p>
 * Contains element locators and high-level interaction helpers used by tests,
 * along with safe wait-and-check utilities for error messages and navigation.
 * </p>
 */
public class LoginPageObjects extends TestBase {
    WebDriver driver;
    WebDriverWait wait;

    // -------------------------
    // Page element locators
    // -------------------------

    /** Email input textbox (name="email") */
    @FindBy(name = "email")
    WebElement emailTb;

    /** Password input textbox (name="password") */
    @FindBy(name = "password")
    WebElement passwordTb;

    /** Sign In button (text 'Sign In') */
    @FindBy(xpath = "//button[text()='Sign In']")
    WebElement signInBtn;

    /** Generic incorrect credentials error message shown on failed login */
    @FindBy(xpath = "//div[contains(text(),'Incorrect email or password')]")
    WebElement incorrectEmailOrPwdErrorMsg;

    /** "Email is required" inline validation message */
    @FindBy(xpath = "//p[text()='Email is required']")
    WebElement emailReqErrorMsg;

    /** "Password is required" inline validation message */
    @FindBy(xpath = "//p[text()='Password is required']")
    WebElement pwdReqErrorMsg;

    /** Password-min-length validation message (e.g. 'Password must be at least 8 characters') */
    @FindBy(xpath = "//p[text()='Password must be at least 8 characters']")
    WebElement pwdLessCharErrorMsg;

    // -------------------------
    // Constructor
    // -------------------------

    /**
     * Page Object constructor — initializes WebElements and the default wait.
     *
     * @param driver WebDriver instance from the test (injected by TestBase)
     */
    public LoginPageObjects(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // -------------------------
    // Navigation / Actions
    // -------------------------

    /**
     * Reset browser to the login page to ensure tests start from a clean state.
     * <p>
     * Performs a refresh then navigates to the provided URL and waits for the
     * email textbox to be visible. Any exception is caught and logged (non-fatal).
     * </p>
     *
     * @param url absolute URL of the login page (e.g. https://example.com/login)
     */
    public void resetToLoginPage(String url) {
        try {
            // Refresh first to clear any existing state, then load the login URL.
            driver.navigate().refresh();
            driver.navigate().to(url);

            // Wait until email textbox is visible before continuing.
            wait.until(ExpectedConditions.visibilityOf(emailTb));
        } catch (Exception e) {
            // Non-fatal: tests will handle cases where reset wasn't successful.
            System.out.println("⚠️ Could not load login page: " + e.getMessage());
        }
    }

    /**
     * Enter text into the email textbox.
     * <p>
     * Waits for the element to be visible, clears any existing value, then types
     * the provided email. If {@code email} is null the field is only cleared.
     * </p>
     *
     * @param email email address to type (may be null to only clear the field)
     */
    public void enterEmail(String email) {
        // Ensure the element is visible to avoid ElementNotInteractableException.
        wait.until(ExpectedConditions.visibilityOf(emailTb));
        emailTb.clear();
        if (email != null) {
            emailTb.sendKeys(email);
        }
    }

    /**
     * Enter text into the password textbox.
     * <p>
     * Waits for password field visibility, clears previous value, then types the
     * provided password. If {@code password} is null the field is only cleared.
     * </p>
     *
     * @param password password to type (may be null to only clear the field)
     */
    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordTb));
        passwordTb.clear();
        if (password != null) {
            passwordTb.sendKeys(password);
        }
    }

    /**
     * Click the Sign In button.
     * <p>
     * Waits until the button is clickable to reduce flakiness caused by timing.
     * </p>
     */
    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(signInBtn));
        signInBtn.click();
    }

    // -------------------------
    // State checks / Utilities
    // -------------------------

    /**
     * Check whether the dashboard page is opened after login.
     * <p>
     * Uses a short explicit wait to verify the URL contains the dashboard path.
     * Returns {@code true} if URL contains "/user/userdashboard", otherwise {@code false}.
     * </p>
     *
     * @return {@code true} when dashboard URL is detected, {@code false} otherwise
     */
    public boolean isDashboardOpened() {
        try {
            // Use a short wait here because we only need to detect quick navigation.
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.urlContains("/user/userdashboard"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Safe visibility checker for a WebElement.
     * <p>
     * Wraps {@code element.isDisplayed()} with exception handling to avoid test
     * failures due to stale or detached elements. Returns {@code false} if the
     * element is null or any Selenium exception occurs.
     * </p>
     *
     * @param element WebElement to check
     * @return {@code true} if element exists and is displayed, otherwise {@code false}
     */
    private boolean isVisible(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------
    // Error handling / reporting
    // -------------------------

    /**
     * Determine and return the most relevant visible error message for the current
     * login attempt, guided by a human-readable {@code scenario} string.
     *
     * <p>Behavior summary:
     * <ul>
     *   <li>Matches scenario keywords (case-insensitive) against a prioritized map
     *       of scenario → WebElement and returns the text of the first visible match.</li>
     *   <li>If the scenario does not match any keywords or the matched element is not
     *       visible, the method falls back to returning the first visible known error
     *       element (priority: incorrect credentials, email required, password required,
     *       password-too-short).</li>
     *   <li>If no error element is visible, returns the string {@code "No visible error message"}.</li>
     * </ul>
     * </p>
     *
     * @param scenario descriptive scenario string from test data (e.g. "blank password", "Invalid credentials")
     * @return trimmed error message text from the UI, or {@code "No visible error message"} if none are visible
     */
    public String getErrorMessage(String scenario) {
        if (scenario == null) scenario = "";
        String s = scenario.toLowerCase();

        try {
            // Map scenario keywords → corresponding error WebElement (ordering matters)
            LinkedHashMap<String, WebElement> scenarioMap = new LinkedHashMap<>();
            scenarioMap.put("blank username", emailReqErrorMsg);
            scenarioMap.put("blank password", pwdReqErrorMsg);
            scenarioMap.put("blank", emailReqErrorMsg); // generic blank fallback
            scenarioMap.put("invalid", incorrectEmailOrPwdErrorMsg);
            scenarioMap.put("less than", pwdLessCharErrorMsg);

            // 1) Scenario-driven lookup: return the first visible element matched by scenario keyword.
            for (String key : scenarioMap.keySet()) {
                if (s.contains(key)) {
                    WebElement element = scenarioMap.get(key);
                    if (isVisible(element)) {
                        return element.getText().trim();
                    }
                }
            }

            // 2) Fallback: return the first visible known error in a fixed priority order.
            if (isVisible(incorrectEmailOrPwdErrorMsg)) return incorrectEmailOrPwdErrorMsg.getText().trim();
            if (isVisible(emailReqErrorMsg))           return emailReqErrorMsg.getText().trim();
            if (isVisible(pwdReqErrorMsg))             return pwdReqErrorMsg.getText().trim();
            if (isVisible(pwdLessCharErrorMsg))        return pwdLessCharErrorMsg.getText().trim();

        } catch (Exception ignored) {
            // Swallowing intentionally: callers expect a string result even if detection fails.
        }

        // Final fallback if nothing matched or an exception occurred.
        return "No visible error message";
    }

}
