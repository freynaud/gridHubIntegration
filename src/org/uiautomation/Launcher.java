package org.uiautomation;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.utils.GridHubConfiguration;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.Device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Launcher {


  public static void main(String[] args) throws Exception {

    GridHubConfiguration c = new GridHubConfiguration();
    c.setTimeout(0);
    c.setCapabilityMatcher(new IOSAugmentedCapabilityMatcher());
    /*List<String> servlets = new ArrayList<String>();
    servlets.add(IDEServlet.class.getCanonicalName());
    c.setServlets(servlets);*/
    c.setPort(4444);
    Hub h = new Hub(c);
    h.start();

    // twin
    //registerTwin("192.168.0.40", 4444);

    // ios-driver
    //registerIOS("192.168.0.42", 5555);

    registerIOS("192.168.0.43", 5555);
    registerIOS("192.168.0.44", 5555);
    registerIOS("192.168.0.45", 5555); // (5)

    registerIOS("192.168.0.2", 5555);

    //registerIOS("192.168.0.46", 5555);
    //registerIOS("192.168.0.47", 5555);

    // selenium 
    registerSelenium("192.168.0.38", 4444);
    registerSelenium("192.168.0.39", 4444);


  }

  private static void registerSelenium(String nodehost, int port) throws Exception {
    RegistrationRequest node = new RegistrationRequest();
    DesiredCapabilities ff = DesiredCapabilities.firefox();
    ff.setVersion("14");
    ff.setPlatform(Platform.MAC);
    node.addDesiredCapability(ff);
    node.addDesiredCapability(ff);
    node.addDesiredCapability(ff);
    node.addDesiredCapability(ff);
    node.addDesiredCapability(ff);

    DesiredCapabilities ch = DesiredCapabilities.chrome();
    ch.setVersion("24");

    node.addDesiredCapability(ch);
    node.addDesiredCapability(ch);
    node.addDesiredCapability(ch);
    node.addDesiredCapability(ch);
    node.addDesiredCapability(ch);

    //node.getConfiguration().put(RegistrationRequest.ID, "Selenium node");
    node.getConfiguration().put(RegistrationRequest.AUTO_REGISTER, true);

    node.getConfiguration().put(RegistrationRequest.HUB_HOST, "localhost");
    node.getConfiguration().put(RegistrationRequest.HUB_PORT, 4444);
    node.getConfiguration().put(RegistrationRequest.REMOTE_HOST, "http://" + nodehost + ":" + port);

    SelfRegisteringRemote remote = new SelfRegisteringRemote(node);
    remote.startRegistrationProcess();

  }

  private static void registerTwin(String nodehost, int port) {
    RegistrationRequest node = new RegistrationRequest();

    DesiredCapabilities cap = new DesiredCapabilities();
    // cap.setCapability("name", "notepad");
    cap.setCapability("applicationName", "notepad");
    node.addDesiredCapability(cap);

    node.getConfiguration().put(RegistrationRequest.ID, "Twin node");
    node.getConfiguration().put(RegistrationRequest.AUTO_REGISTER, true);

    node.getConfiguration().put(RegistrationRequest.HUB_HOST, "localhost");
    node.getConfiguration().put(RegistrationRequest.HUB_PORT, "4444");
    node.getConfiguration().put(RegistrationRequest.REMOTE_HOST, "http://" + nodehost + ":" + port);
    node.getConfiguration().put(RegistrationRequest.MAX_SESSION, 1);

    SelfRegisteringRemote remote = new SelfRegisteringRemote(node);
    remote.startRegistrationProcess();

  }

  private static void registerIOS(String nodehost, int port) throws JSONException, Exception {
    RegistrationRequest node = new RegistrationRequest();

    HttpClient client = new DefaultHttpClient();

    String url = "http://" + nodehost + ":" + port + "/wd/hub/status";
    BasicHttpRequest r = new BasicHttpRequest("GET", url);

    HttpResponse response = client.execute(new HttpHost(nodehost, port), r);
    JSONObject status = extractObject(response);
    String ios = status.getJSONObject("value").getJSONObject("ios").optString("simulatorVersion");
    JSONArray supportedApps = status.getJSONObject("value").getJSONArray("supportedApps");

    for (int i = 0; i < supportedApps.length(); i++) {
      Map<String, Object> capability = new HashMap<String, Object>();
      if (ios.isEmpty()) {
        capability.put("ios", "5.1");
        capability.put("browserName", "IOS Device");
        capability.put(IOSCapabilities.DEVICE, Device.iphone);
      } else {
        capability.put("ios", ios);
        capability.put("browserName", "IOS Simulator");
      }
      JSONObject app = supportedApps.getJSONObject(i);
      for (String key : JSONObject.getNames(app)) {
        if ("locales".equals(key)) {
          JSONArray loc = app.getJSONArray(key);
          List<String> locales = new ArrayList<String>();
          for (int j = 0; j < loc.length(); j++) {
            locales.add(loc.getString(j));
          }
          capability.put("locales", locales);
        } else {
          Object o = app.get(key);
          capability.put(key, o);
        }
      }
      node.addDesiredCapability(capability);
    }

    if (ios.isEmpty()) {
      node.getConfiguration().put(RegistrationRequest.ID, "IOS native device");
    } else {
      node.getConfiguration().put(RegistrationRequest.ID, "IOS native sim" + nodehost);
    }

    node.getConfiguration().put(RegistrationRequest.AUTO_REGISTER, true);
    node.getConfiguration().put(RegistrationRequest.PROXY_CLASS,
                                IOSRemoteProxy.class.getCanonicalName());

    node.getConfiguration().put(RegistrationRequest.HUB_HOST, "localhost");
    node.getConfiguration().put(RegistrationRequest.HUB_PORT, "4444");
    node.getConfiguration().put(RegistrationRequest.REMOTE_HOST, "http://" + nodehost + ":5555");
    node.getConfiguration().put(RegistrationRequest.MAX_SESSION, 1);

    // System.out.println(node.getAssociatedJSON().toString(2));

    SelfRegisteringRemote remote = new SelfRegisteringRemote(node);
    remote.startRegistrationProcess();
  }

  private static JSONObject extractObject(HttpResponse resp) throws IOException, JSONException {
    BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
    StringBuilder s = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      s.append(line);
    }
    rd.close();
    return new JSONObject(s.toString());
  }


}
