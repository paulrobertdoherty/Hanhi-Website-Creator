package io.hanhi;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.hanhi.accounts.AccountHandler;

@SuppressWarnings("serial")
public class View extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();
		String i = req.getParameter("i");
		writer.print(AccountHandler.getHtml(i));
		writer.flush();
		writer.close();
	}
}
