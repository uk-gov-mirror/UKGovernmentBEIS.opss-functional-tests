package uk.gov.beis.par.stepdefs;

import java.net.URL;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import uk.gov.beis.par.Utils.AppProperties;
import uk.gov.beis.par.Utils.EnvironmentProperties;

public class SharedWebDriver extends EventFiringWebDriver {
	// private static AndroidDriver driver1;
	private static WebDriver driver;
    /*
     * Test run in below platforms
     * In-progress
     */
	static {
		final String _OS_LINUX = "linux";
		final String _OS_WINDOWS = "Windows";
		final String _OS_MAC = "MAC";

		try {
			String browser = AppProperties.get("browser");
			String platform = AppProperties.get("platform");
			String envUrl = EnvironmentProperties.getServiceUrl();

			String OS = AppProperties.get("OS").toLowerCase();
			if (OS.length() == 0 || "detect".equals(OS.toLowerCase())) {
				if (SystemUtils.IS_OS_WINDOWS)
					OS = _OS_WINDOWS;
				else if (SystemUtils.IS_OS_MAC)
					OS = _OS_MAC;
				else if (SystemUtils.IS_OS_LINUX)
					OS = _OS_LINUX;
			}

			if (platform.equalsIgnoreCase("Remote")) {
				if (browser.equalsIgnoreCase("firefox")) {
					DesiredCapabilities cap = DesiredCapabilities.firefox();
					cap.setBrowserName("firefox");
					cap.setPlatform(org.openqa.selenium.Platform.ANY);

					driver = new RemoteWebDriver(new URL("remote host ip"), cap);
				}

			} 
			/**
			 * browser stack integration- Please note user name/pwd for browserstack
			 * will be provided in env file(not in GIT)
			 */
			else if (platform.equalsIgnoreCase("browserstack")) {
				String USERNAME = AppProperties.get("bsUsername");
				String AUTOMATE_KEY = AppProperties.get("bsUserkey");

				String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub.browserstack.com/wd/hub";

				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setCapability("os", "Windows");
				caps.setCapability("os_version", "10");
				caps.setCapability("browser", "IE");
				caps.setCapability("browser_version", "11.0");
				caps.setCapability("browserstack.local", "false");
				caps.setCapability("browserstack.selenium_version", "3.5.2");
				caps.setCapability("browserstack.debug", "true");

				driver = new RemoteWebDriver(new URL(URL), caps);

			} 
			/*
			 * To run tests in FF
			 */
			else if (browser.equalsIgnoreCase("firefox")) {
				System.out.println("shared webdriver");
				FirefoxProfile prof = new FirefoxProfile();
				driver = new FirefoxDriver();

				System.out.println("#####Started test run on  " + envUrl + "  on " + browser + " browser #####");
				driver.manage().window().maximize();
			} 
			/*
			 * To run tests in chrome
			 */
			else if (browser.equalsIgnoreCase("chrome")) {
				//System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "/src/test/resources/chromedriver");

				ChromeOptions options = new ChromeOptions();
				//options.setHeadless(true);
				options.addArguments("--no-sandbox");
				options.addArguments("--disable-dev-shm-usage");
				options.addArguments("window-size=1024x768");
				driver = new ChromeDriver(options);

				driver.manage().window().maximize();
				System.out.println("#####Started test run on  " + envUrl + "  on " + browser + " browser #####");
			} 
			/*
			 * to run tests in IE
			 */
			else if (browser.equalsIgnoreCase("ie")) {
				System.setProperty("webdriver.ie.driver",
						System.getProperty("user.dir") + "/src/test/resources/IEDriverServer.exe");
				driver = new InternetExplorerDriver();
				System.out.println("#####Started test run on  " + envUrl + "  on " + browser + " browser #####");

				driver.manage().window().maximize();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					driver.quit();
				} catch (Exception e) {
					System.out.println(e);
				}
			}

		});

	}

	public SharedWebDriver() {
		super(driver);
	}
}