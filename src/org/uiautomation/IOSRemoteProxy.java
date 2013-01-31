package org.uiautomation;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.SeleniumProtocol;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.grid.web.servlet.beta.MiniCapability;
import org.openqa.grid.web.servlet.beta.SlotsLines;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.seleniumhq.jetty7.util.ajax.JSON;
import org.seleniumhq.jetty7.util.ajax.JSONObjectConvertor;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.server.application.IOSApplication;

public class IOSRemoteProxy extends DefaultRemoteProxy {


  public IOSRemoteProxy(RegistrationRequest request, Registry registry) {
    super(request, registry);
  }


  @Override
  public HtmlRenderer getHtmlRender() {
    return new IOSDRiverHTMLRenderer(this);
  }


  class IOSDRiverHTMLRenderer implements HtmlRenderer {
    private IOSRemoteProxy proxy;

    public IOSDRiverHTMLRenderer(IOSRemoteProxy p) {
      this.proxy = p;
    }

    @Override
    public String renderSummary() {
      try {
        StringBuilder builder = new StringBuilder();
        builder.append("<div class='proxy'>");
        builder.append("<p class='proxyname'>");
        builder.append(proxy.getClass().getSimpleName());

        // TODO freynaud

        builder.append(getHtmlNodeVersion());

        String platform = "MAC OSX";

        builder.append("<p class='proxyid'>id : ");
        builder.append(proxy.getId());
        builder.append(", OS : " + platform + "</p>");

        builder.append(nodeTabs());

        builder.append("<div class='content'>");

        builder.append(tabBrowsers());
        builder.append(tabConfig());

        builder.append("</div>");
        builder.append("</div>");

        return builder.toString();
      } catch (JSONException e) {
        e.printStackTrace();
        return e.getMessage();
      }


    }



    private String getHtmlNodeVersion() {
      try {
        JSONObject object = proxy.getStatus();
        String version = object.getJSONObject("value").getJSONObject("build").getString("version");
        return " (version : " + version + ")";
      } catch (Exception e) {
        return " unknown version," + e.getMessage();
      }
    }

    // content of the config tab.
    private String tabConfig() {
      StringBuilder builder = new StringBuilder();
      builder.append("<div type='config' class='content_detail'>");
      Map<String, Object> config = proxy.getConfig();

      for (String key : config.keySet()) {
        builder.append("<p>");
        builder.append(key);
        builder.append(":");
        builder.append(config.get(key));
        builder.append("</p>");
      }

      builder.append("</div>");
      return builder.toString();
    }


    // content of the browsers tab
    private String tabBrowsers() throws JSONException {
      StringBuilder builder = new StringBuilder();
      builder.append("<div type='browsers' class='content_detail'>");

      builder.append("<p class='protocol' >WebDriver</p>");
      for (TestSlot slot : proxy.getTestSlots()) {
        Map<String, Object> cap = slot.getCapabilities();
        JSONObject resources = (JSONObject) cap.get("resources");

        builder.append("<img height='24' width='24' src='" + proxy.getRemoteHost()
            + resources.getString(IOSCapabilities.ICON) + "' title='" + cap + "' />");
        builder.append(" " + cap.get(IOSCapabilities.DEVICE) );
        String buildId = (String) cap.get("BuildIdentifier");
        if (buildId != null) {
          builder.append(",build id:" + cap.get("BuildIdentifier"));
        }

        if (slot.getSession() != null) {
          /*
           * builder.append("<a href='/grid/admin/IDEServlet/session/" +
           * slot.getSession().getExternalKey() + "/home' >IDE</a>");
           */
          String u =
              "http://" + slot.getRemoteURL().getHost() + ":" + slot.getRemoteURL().getPort();
          builder.append("<a href='" + u + "/ide/session/" + slot.getSession().getExternalKey()
              + "/home' >IDE.</a>");
        }
        builder.append("<br>");
      }
      builder.append("</div>");
      return builder.toString();
    }



    // the tabs header.
    private String nodeTabs() {
      StringBuilder builder = new StringBuilder();
      builder.append("<div class='tabs'>");
      builder.append("<ul>");
      builder
          .append("<li class='tab' type='browsers'><a title='test slots' href='#'>Apps</a></li>");
      builder
          .append("<li class='tab' type='config'><a title='node configuration' href='#'>Configuration</a></li>");
      builder.append("</ul>");
      builder.append("</div>");
      return builder.toString();
    }



  }
}
