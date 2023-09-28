package ios;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

import com.github.tomaslanger.chalk.Ansi;
import com.github.tomaslanger.cli.progress.ProgressBar;
import com.github.tomaslanger.cli.progress.StatusLoc;

public class PerfectoNativeSample {
	StopWatch pageLoad = new StopWatch();

	public static void main(String[] args) throws Exception {

		int exitCode = 0;
		ProgressBar progressBar = createProgressBar();
		DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);

		// 1. Replace <<cloud name>> with your Perfecto cloud name (for example, 'demo' is the cloud name of demo.perfectomobile.com).
		String cloudName = "iiht-1";

		// 2. Replace <<security token>> with your Perfecto security token.
		String securityToken = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhZGY3NmZjMi00NTllLTQ5NmYtYTBlNS03ODFkYjMwN2ExZGEifQ.eyJpYXQiOjE2OTE1ODM0NDMsImp0aSI6ImRjZDM3ZDM3LTY0MzktNGQ3Ny04YmQxLWJhYWM5YWRjZmVlNSIsImlzcyI6Imh0dHBzOi8vYXV0aDgucGVyZmVjdG9tb2JpbGUuY29tL2F1dGgvcmVhbG1zL2lpaHQtMS1wZXJmZWN0b21vYmlsZS1jb20iLCJhdWQiOiJodHRwczovL2F1dGg4LnBlcmZlY3RvbW9iaWxlLmNvbS9hdXRoL3JlYWxtcy9paWh0LTEtcGVyZmVjdG9tb2JpbGUtY29tIiwic3ViIjoiOGVkOTdmMzYtYWFhZS00MTc4LWI1MDUtZWI0M2M3YjhmMTExIiwidHlwIjoiT2ZmbGluZSIsImF6cCI6Im9mZmxpbmUtdG9rZW4tZ2VuZXJhdG9yIiwibm9uY2UiOiJlZDhjYjkwYy00M2Q3LTRjZDEtODA1OS00ZjNmZjUyNGI2NDUiLCJzZXNzaW9uX3N0YXRlIjoiMDgyODZlODYtODAxYy00ZjQ0LWJmMjUtOTg1MDA0MWI0MzIxIiwic2NvcGUiOiJvcGVuaWQgb2ZmbGluZV9hY2Nlc3MgcHJvZmlsZSBlbWFpbCJ9.u5znwTuwVK3NKSDdtV42dSmVDi7jSiY_dK4urOIsXXo";

		capabilities.setCapability("securityToken", securityToken);

		// 3. Set the device capabilities.
		capabilities.setCapability("platformName", "iOS");
		capabilities.setCapability("platformVersion", "14.6");
		capabilities.setCapability("platformBuild", "18F72");
		capabilities.setCapability("location", "NA-US-BOS");
		capabilities.setCapability("resolution", "1170x2532");
		capabilities.setCapability("accountName", "ios2");
		capabilities.setCapability("deviceStatus", "CONNECTED");
		capabilities.setCapability("manufacturer", "Apple");
		capabilities.setCapability("model", "iPhone-12");
		capabilities.setCapability("platformName", "iOS");
		// capabilities.setCapability("platformVersion", "16.0");
		// capabilities.setCapability("platformBuild", "20A357");
		// capabilities.setCapability("location", "NA-US-BOS");
		// capabilities.setCapability("resolution", "1170x2532");
		// capabilities.setCapability("accountName", "dev1");
		// capabilities.setCapability("deviceStatus", "CONNECTED");
		// capabilities.setCapability("manufacturer", "Apple");
		// capabilities.setCapability("model", "iPhone-14");

		// 4. Set the Perfecto media repository path of the app under test.
		capabilities.setCapability("app", "PRIVATE:test-app.ipa");

		// 5. Set the unique identifier of your app.
		capabilities.setCapability("bundleId", "io.perfecto.expense.tracker");

		// 6. Set other capabilities.
		capabilities.setCapability("enableAppiumBehavior", true); // Enable the new Appium Architecture.
		capabilities.setCapability("autoLaunch", true); // Whether to install and launch the app automatically.
		capabilities.setCapability("autoInstrument", true); // To work with hybrid applications, install the iOS/Android application as instrumented.
		capabilities.setCapability("takesScreenshot", true);
		capabilities.setCapability("screenshotOnError", true);
		capabilities.setCapability("waitForAvailableLicense", true);
		// capabilities.setCapability("fullReset", false); // Reset the app state by uninstalling the app.

		// 7. Initialize the IOSDriver driver.
		progressBar.setProgress(1);
		Logger.getLogger("org.openqa.selenium.remote").setLevel(Level.OFF);
		IOSDriver<IOSElement> driver = new IOSDriver<IOSElement>(
				new URL("https://" + cloudName + ".perfectomobile.com/nexperience/perfectomobile/wd/hub"),
				capabilities);

		// 8. Set an implicit wait.
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		PerfectoExecutionContext perfectoExecutionContext;
		if (System.getenv("jobName") != null) {
			perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
					.withProject(new Project("Sample Project", "1.0"))
					.withJob(new Job(System.getenv("jobName"),
							Integer.parseInt(System.getenv("jobNumber"))))
					.withWebDriver(driver).build();
		} else {
			perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
					.withProject(new Project("Sample Project", "1.0"))
					.withWebDriver(driver)
					.build();
		}

		ReportiumClient reportiumClient = new ReportiumClientFactory()
				.createPerfectoReportiumClient(perfectoExecutionContext);

		reportiumClient.testStart("Native Java iOS Sample", new TestContext("native", "ios"));

		try {

		    /**
		    *****************************
		    *** Your test starts here. If you test a different app, you need to modify the test steps accordingly. ***
		    *****************************
		    */

			progressBar.setProgress(2);
			// Device Vitals
			reportiumClient.stepStart("start device vitals");
			Map<String, Object> params2 = new HashMap<>();
			params2.put("sources", "Device");
			driver.executeScript("mobile:monitor:start", params2);

			// Method 1: User experience timer with Visual Text
			// Launch Web application
			// reportiumClient.stepStart("User experience timer with Visual text");
			// switchToContext(driver, "WEBVIEW");
			// driver.get("https://www.perfecto.io");
			Thread.sleep(2000);
			// driver.get("https://www.etihad.com/en-us/book");

			// TextValidation(driver, "Book a flight");

			// Measure UX timer 1 - Able to retrieve UX Timer value
			long AppLaunchTime = timerGet(driver, "ux");
			System.out.println("Captured UX time for App launch is: " + AppLaunchTime + "ms");

			// Wind Tunnel: Add timer to Wind Tunnel Report
			reportTimer(driver, AppLaunchTime, 10000, "Checkpoint load time of App launch.", "AppLaunchTime");

			// Method 2: Custom timer for xpaths
			// reportiumClient.stepStart("Custom timer with xpath");
			// driver.get("https://www.perfecto.io");
			// Thread.sleep(2000);
			// startTimer(pageLoad);
			// driver.get("https://www.etihad.com/en-us/book");
			// try {
			// 	driver.findElement(By.xpath("//*[@class=\"header-text-logo\"]//a"));
			// 	stopTimer(pageLoad);
			// 	measureTimer(driver, pageLoad, 10000, "Checkpoint load time of App launch.", "AppLaunchTime-xpath");
			// } catch (Exception e) {
			// }
			// stop Network Virtualization
			reportiumClient.stepStart("stop virtual network and device vitals");
			Map<String, Object> pars1 = new HashMap<>();
			driver.executeScript("mobile:vnetwork:stop", pars1);


			progressBar.setProgress(3);
			reportiumClient.stepStart("Enter email");
			WebDriverWait wait = new WebDriverWait(driver, 30);
			IOSElement email = (IOSElement) wait
					.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_email"))));
			email.sendKeys("test@perfecto.com");
			reportiumClient.stepEnd();

			progressBar.setProgress(4);
			reportiumClient.stepStart("Enter password");
			IOSElement password = (IOSElement) wait
					.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_password"))));
			password.sendKeys("test123");
			driver.hideKeyboard();
			reportiumClient.stepEnd();

			progressBar.setProgress(5);
			reportiumClient.stepStart("Click login");
			IOSElement login = (IOSElement) wait
					.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("login_login_btn"))));
			login.click();
			reportiumClient.stepEnd();

			progressBar.setProgress(6);
			reportiumClient.stepStart("Add expense");
			IOSElement add_expense = (IOSElement) wait
					.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("list_add_btn"))));
			add_expense.click();
			reportiumClient.stepEnd();

			progressBar.setProgress(7);
			reportiumClient.stepStart("Select head");
			IOSElement head = (IOSElement) wait
					.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("edit_head"))));
			head.click();
			List<WebElement> picker = wait
					.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@value=\"- Select -\"]")));
			picker.get(0).sendKeys("Flight");
			reportiumClient.stepEnd();

			progressBar.setProgress(8);
			reportiumClient.stepStart("Enter amount");
			IOSElement amount = (IOSElement) new WebDriverWait(driver, 30)
					.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("edit_amount"))));
			amount.sendKeys("100");
			reportiumClient.stepEnd();

			progressBar.setProgress(9);
			reportiumClient.stepStart("Save expense");
			IOSElement save_expense = (IOSElement) new WebDriverWait(driver, 30)
					.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.name("add_save_btn"))));
			save_expense.click();
			reportiumClient.stepEnd();

		    /**
		    *****************************
		    *** Your test ends here. ***
		    *****************************
		    */

			progressBar.setProgress(10);
			// stop vitals
			Map<String, Object> params3 = new HashMap<>();
			driver.executeScript("mobile:monitor:stop", params3);
			reportiumClient.stepEnd();
			reportiumClient.testStop(TestResultFactory.createSuccess());
		} catch (Exception e) {
			progressBar.setProgress(10);
			reportiumClient.testStop(TestResultFactory.createFailure(e));
			exitCode = 1;
		}



	// @AfterMethod
	// public void afterMethod(ITestResult result) {
	// 	try {
	// 		TestResult testResult = null;
	// 		if (result.getStatus() == result.SUCCESS) {
	// 			testResult = TestResultFactory.createSuccess();
	// 		} else if (result.getStatus() == result.FAILURE) {
	// 			testResult = TestResultFactory.createFailure(result.getThrowable());
	// 		}
	// 		reportiumClient.testStop(testResult);
	// 		// Retrieve the URL to the DigitalZoom Report
	// 		String reportURL = reportiumClient.getReportUrl();
	// 		System.out.println(reportURL);
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 	}
	// 	driver.quit();

	// }

		// Obtains the Report URL
		String reportURL = reportiumClient.getReportUrl() + "&onboardingJourney=automated&onboardingDevice=nativeApp";

		// Quits the driver
		progressBar.setProgress(11);
		driver.quit();

		// Prints the Report URL
		progressBar.setProgress(12);
		System.out.println("\n\nOpen this link to continue with the guide: " + reportURL + "\n");

		// Launch browser with the Report URL
		try {
			Desktop.getDesktop().browse(new URI(reportURL));
		} catch (Exception e) {
			System.out.println("Unable to open Reporting URL in browser: " + e.getMessage());
		}

		System.exit(exitCode);
	}

	private static ProgressBar createProgressBar() {
		int TOTAL_STEPS = 12;
		int PROGRESS_BAR_CHAR_COUNT = 50;
		ProgressBar.Builder builder = new ProgressBar.Builder();
		builder.setMax(TOTAL_STEPS)
				.setCharCount(PROGRESS_BAR_CHAR_COUNT)
				.setBaseChar(' ')
				.setProgressChar('=')
				.setStatusLocation(StatusLoc.FIRST_LINE)
				.setKeepSingleColor(true)
				.setBeginString("[")
				.setEndString("]")
				.setFgColor(Ansi.Color.WHITE)
				.setBgColor(Ansi.BgColor.BLACK)
				.claimNoOuts();
		return builder.build();
	}

	private StopWatch getPageLoad() {
		return pageLoad;
	}

	private static void switchToContext(RemoteWebDriver driver, String context) {
		RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", context);
		executeMethod.execute(DriverCommand.SWITCH_TO_CONTEXT, params);
	}

	private static long timerGet(RemoteWebDriver driver, String timerType) {
		String command = "mobile:timer:info";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", timerType);
		params.put("timerId", "myTime");
		long result = (long) driver.executeScript(command, params);
		return result;
	}

	private static void TextValidation(RemoteWebDriver driver, String content) {
		// verify that the correct page is displayed as result of signing in.
		Map<String, Object> params1 = new HashMap<>();
		// Check for the text that indicates that the sign in was successful
		params1.put("content", content);
		// allow up-to 30 seconds for the page to display
		params1.put("timeout", "40");
		// Wind Tunnel: Adding accurate timers to text checkpoint command
		params1.put("measurement", "accurate");
		params1.put("source", "camera");
		params1.put("analysis", "automatic");
		params1.put("threshold", "90");
		params1.put("index", "1");
		String resultString = (String) driver.executeScript("mobile:checkpoint:text", params1);
	}

	public static String reportTimer(RemoteWebDriver driver, long result, long threshold, String description,
			String name) {
		Map<String, Object> params = new HashMap<String, Object>(7);
		params.put("result", result);
		params.put("threshold", threshold);
		params.put("description", description);
		params.put("name", name);
		String status = (String) driver.executeScript("mobile:status:timer", params);
		return status;
	}

	public static void startTimer(StopWatch pageLoad) {
		pageLoad.start();
	}

	public static void stopTimer(StopWatch pageLoad) {
		pageLoad.stop();
	}

	public static void measureTimer(RemoteWebDriver driver, StopWatch pageLoad, long threshold, String description,
			String name) throws Exception {
		long result = pageLoad.getTime() > 820 ? pageLoad.getTime() - 820 : 0;
		System.out.println("Captured custom time for App launch is: " + result + "ms");
		reportTimer(driver, result, threshold, description, name);
		if (result > threshold) {
			throw new Exception("Timer for " + description + " failed!");
		}
	}

}
