package org.uiautomation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class IOSIDEServlet extends HttpServlet {



  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      process(request, response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected void process(HttpServletRequest request, HttpServletResponse response)
      throws IOException, Exception {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    JSONObject o = new JSONObject();
    o.put("test", "test");
    response.getWriter().print(o.toString(2));
    response.getWriter().close();

  }
}
