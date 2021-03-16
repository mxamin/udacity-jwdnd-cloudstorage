package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import javax.validation.constraints.AssertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private EncryptionService encryptionService;

	@Autowired
	private CredentialService credentialService;

	private String homeUrl;
	private String loginUrl;
	private String signupUrl;

	private WebDriver driver;
	private SignupPage signupPage;
	private LoginPage loginPage;
	private WebDriverWait waitDriver;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
		this.signupPage = new SignupPage(this.driver);
		this.loginPage = new LoginPage(this.driver);
		this.waitDriver = new WebDriverWait(driver, 100);

		this.homeUrl = "http://localhost:" + this.port + "/home";
		this.loginUrl = "http://localhost:" + this.port + "/login";
		this.signupUrl = "http://localhost:" + this.port + "/signup";
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void testLoginPageAccess() {
		driver.navigate().to(homeUrl);
		Assertions.assertEquals("Login", driver.getTitle());
	}

	@Test
	public void testSignupPageAccess() {
		driver.navigate().to(signupUrl);
		Assertions.assertEquals("Sign Up", driver.getTitle());
	}

	@Test
	public void testUnauthorizedHomePageAccess() {
		driver.navigate().to(loginUrl);
		Assertions.assertEquals("Login", driver.getTitle()); // Only logged in users can access home page
	}

	@Test
	public void testSignupAndLogin() {
		String username = "test username1";
		String password = "test password";

		// Signup
		driver.navigate().to(signupUrl);
		signupPage.signup("test firstname", "test lastname", username, password);

		// Login
		driver.navigate().to(loginUrl);
		loginPage.login(username, password);

		// Home
		driver.navigate().to(homeUrl);
		Assertions.assertEquals("Home", driver.getTitle()); // Home page

		// Logout
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("buttonLogout"))).click();
		driver.navigate().to(homeUrl);
		Assertions.assertEquals("Login", driver.getTitle()); // Login page

	}

	private void signupAndLogin(String username, String password) {
		// Signup
		driver.navigate().to(signupUrl);
		signupPage.signup("test firstname", "test lastname", username, password);

		// Login
		driver.navigate().to(loginUrl);
		loginPage.login(username, password);
	}

	@Test
	public void testNote() {
		signupAndLogin("test username2", "test password");

		// Add note
		String title = "test title";
		String description = "test description";
		driver.navigate().to(homeUrl);
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click(); // Click nav
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("buttonAddNote"))).click();  // Click add
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("note-title"))).sendKeys(title);  // Fill title
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("note-description"))).sendKeys(description); // Fill description
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("buttonSaveNote"))).click(); // Save note

		// Check note was added
		driver.navigate().to(homeUrl);
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click(); // Click nav
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[2]"))));
		Assertions.assertEquals(title, driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[2]")).getText());
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[3]"))));
		Assertions.assertEquals(description, driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[3]")).getText());

		// Update note
		String newTitle = "test new title";
		String newDescription = "test new description";
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[1]/button")))).click();
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("note-title"))).clear();  // Clear title
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("note-title"))).sendKeys(newTitle);  // Fill new title
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("note-description"))).clear(); // Clear description
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("note-description"))).sendKeys(newDescription); // Fill new description
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("buttonSaveNote"))).click(); // Save note

		// Check note was updated
		driver.navigate().to(homeUrl);
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click(); // Click nav
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[2]"))));
		Assertions.assertEquals(newTitle, driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[2]")).getText());
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[3]"))));
		Assertions.assertEquals(newDescription, driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[3]")).getText());

		// Delete note
		waitDriver.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//*[@id=\"noteTable\"]/tbody/tr/td[1]/a")))).click();  // Delete note

		// Check note was deleted
		driver.navigate().to(homeUrl);
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click(); // Click nav
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("noteTable"))));
		Integer noteCount = driver.findElement(By.id("noteTable")).findElements(By.tagName("td")).size();
		Assertions.assertEquals(noteCount, 0);
	}

	@Test
	public void testCredential() {
		signupAndLogin("test username3", "test password");

		// Add Credential
		String url = "test url";
		String username = "test username";
		String password = "test password";
		driver.navigate().to(homeUrl);
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click(); // Click nav
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("buttonAddCredential"))).click();  // Click add
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-url"))).sendKeys(url);  // Fill url
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-username"))).sendKeys(username); // Fill username
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-password"))).sendKeys(password); // Fill password
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("buttonSaveCredential"))).click(); // Save credential

		// Check credential was added
		driver.navigate().to(homeUrl);
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click(); // Click nav
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[2]"))));
		Assertions.assertEquals(url, driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[2]")).getText());
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[3]"))));
		Assertions.assertEquals(username, driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[3]")).getText());

		// Check encrypted password
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[4]"))));
		String encryptedPassword = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[4]")).getText();
		waitDriver.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[1]/a"))));
		String href = driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[1]/a")).getAttribute("href");
		int pos = href.lastIndexOf("/");
		Integer credentialId = Integer.parseInt(href.substring(pos + 1));
		Credential credential = credentialService.getCredential(credentialId);
		Assertions.assertEquals(password, encryptionService.decryptValue(encryptedPassword, credential.getKey()));

		// Update credential
		String newUrl = "test new url";
		String newUsername = "test new username";
		String newPassword = "test new password";
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[1]/button")))).click();
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-url"))).clear();  // Clear url
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-url"))).sendKeys(newUrl);  // Fill new url
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-username"))).clear(); // Clear username
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-username"))).sendKeys(newUsername); // Fill new username
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-password"))).clear(); // Clear password
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("credential-password"))).sendKeys(newPassword); // Fill new password
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("buttonSaveCredential"))).click(); // Save credential

		// Check note was updated
		driver.navigate().to(homeUrl);
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click(); // Click nav
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[2]"))));
		Assertions.assertEquals(newUrl, driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[2]")).getText());
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[3]"))));
		Assertions.assertEquals(newUsername, driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[3]")).getText());
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[4]"))));
		Assertions.assertNotEquals(newPassword, driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[4]")).getText()); // Password should not match

		// Delete note
		waitDriver.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//*[@id=\"credentialTable\"]/tbody/tr/td[1]/a")))).click();  // Delete credential

		// Check credential was deleted
		driver.navigate().to(homeUrl);
		waitDriver.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click(); // Click nav
		waitDriver.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("credentialTable"))));
		Integer credentialCount = driver.findElement(By.id("noteTable")).findElements(By.tagName("td")).size();
		Assertions.assertEquals(credentialCount, 0);
	}
}
