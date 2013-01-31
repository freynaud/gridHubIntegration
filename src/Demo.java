import org.json.JSONException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.UIAButton;
import org.uiautomation.ios.UIAModels.UIADriver;
import org.uiautomation.ios.UIAModels.UIAElement;
import org.uiautomation.ios.UIAModels.UIALink;
import org.uiautomation.ios.UIAModels.UIASecureTextField;
import org.uiautomation.ios.UIAModels.UIATextField;
import org.uiautomation.ios.UIAModels.predicate.AndCriteria;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.UIAModels.predicate.TypeCriteria;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.device.Device;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;


public class Demo {

  private final String hub = "http://localhost:4444/wd/hub";


  @Test
  public void iwebdriver() throws MalformedURLException, JSONException {
    IOSCapabilities cap = IOSCapabilities.iphone("iWebDriver");
    UIADriver driver = null;
    try {
      driver = new RemoteUIADriver(new URL(hub), cap);

      WebDriver
          iphone =
          new RemoteWebDriver(new URL("http://127.0.0.1:3001/wd/hub"),
                              DesiredCapabilities.iphone());
      iphone.get("http://seleniumhq.wordpress.com/");
      Criteria c = new NameCriteria("Solving Common Problems with Web UI Automation");
      Criteria c1 = new AndCriteria(new TypeCriteria(UIALink.class), c);
      UIAElement el = driver.findElement(c1);
      el.tap();

      File f = new File("ss.png");
      System.out.println(f.getAbsolutePath());
      System.out.println(driver.logElementTree(f, false).toString(2));

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }


  }

  @Test(invocationCount = 13, threadPoolSize = 1)
  public void testIOSDevice() throws MalformedURLException {
    IOSCapabilities cap = IOSCapabilities.iphone("eBay");
    cap.setDevice(Device.iphone);

    UIADriver driver = new RemoteUIADriver(new URL(hub), cap);
    try {
      Criteria criteria = new NameCriteria("Sell", L10NStrategy.serverL10N);
      UIAButton button = (UIAButton) driver.findElement(criteria);
      button.tap();

      Criteria u = new TypeCriteria(UIATextField.class);
      UIATextField userId = (UIATextField) driver.findElement(u);
      userId.setValue("francois_uk1");

      Criteria p = new TypeCriteria(UIASecureTextField.class);
      UIASecureTextField pass = (UIASecureTextField) driver.findElement(p);
      pass.setValue("password");

      // screenshot
      File f = new File(UUID.randomUUID() + "IOSphoneDevice.png");
      //driver.takeScreenshot(f.getAbsolutePath());
      Reporter.log("<img src='" + f.getAbsolutePath() + "'>");

    } finally {
      driver.quit();
    }
  }


  @Test(invocationCount = 15, threadPoolSize = 3)
  public void testIOSApp() throws MalformedURLException {
    UIADriver driver = new RemoteUIADriver(new URL(hub), IOSCapabilities.iphone("eBay"));
    try {
      Criteria c1 = new TypeCriteria(UIAButton.class);
      Criteria c2 = new NameCriteria("Agree");
      Criteria c = new AndCriteria(c1, c2);
      UIAElement button = driver.findElement(c);
      button.tap();

      // screenshot
      File f = new File(UUID.randomUUID() + "IOSphone.png");
      //driver.takeScreenshot(f.getAbsolutePath());
      Reporter.log("<img src='" + f.getAbsolutePath() + "'>");

    } finally {
      driver.quit();
    }
  }


  @DataProvider(name = "ipad")
  public Object[][] getLanguages() {
    return new Object[][]{{"en"}, {"fr"}, {"de"}};
  }

  @Test(dataProvider = "ipad")
  public void testIOSAppIPAD(String l) throws Exception {
    IOSCapabilities cap = IOSCapabilities.ipad("eBay");
    cap.setLanguage(l);

    UIADriver driver = new RemoteUIADriver(new URL(hub), cap);
    try {

      Criteria c1 = new TypeCriteria(UIAButton.class);
      Criteria c2 = new NameCriteria("Agree", L10NStrategy.serverL10N);
      Criteria c = new AndCriteria(c1, c2);
      UIAElement button = driver.findElement(c);

      // screenshot
      File f = new File(UUID.randomUUID() + "IOSpad.png");
      // driver.takeScreenshot(f.getAbsolutePath());
      Reporter.log("<img src='" + f.getAbsolutePath() + "'>");

    } finally {
      driver.quit();
    }
  }

  @Test(invocationCount = 60, threadPoolSize = 2)
  public void testWeb() throws MalformedURLException {
    WebDriver driver = new RemoteWebDriver(new URL(hub), DesiredCapabilities.firefox());

    try {
      driver.get("http://www.ebay.co.uk/");

      // screenshot
      File web = new File("webshot.png");
      Augmenter a = new Augmenter();
      File tmp = ((TakesScreenshot) a.augment(driver)).getScreenshotAs(OutputType.FILE);
      tmp.renameTo(web);
      Reporter.log("<img src='" + web.getAbsolutePath() + "' />");

    } finally {
      driver.quit();
    }
  }

  // @Test(invocationCount = 30)
  /*
   * public void testWindowsApp() throws IOException { Application session = new Application(new
   * URL(hub));
   * 
   * try { session.open("notepad", null);
   * 
   * // Wait for the main window to appear and grab it Window window = session.getWindow();
   * 
   * // Enter some dramatic text window.type("Hello world!\n");
   * 
   * // Take screenshots Screenshot screenshot = window.getScreenshot(); File f = new
   * File("notepad.pnj"); screenshot.save(f); Reporter.log("<img src='" + f.getAbsolutePath() +
   * "' />"); } finally { // kill the app session.close(); } }
   */

}
