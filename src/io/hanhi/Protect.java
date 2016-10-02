package io.hanhi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.hanhi.accounts.AccountHandler;

public class Protect extends HttpServlet {
	private static final long serialVersionUID = 7878184388336885160L;
	private static String
			FIRST = "<html><head><meta http-equiv=\"refresh\" content=\"0; URL=https://twitter.com/intent/tweet?text=Insert%20your%20title%20here!%20https://www.hanhi.io/v?i=",
			SECOND = "\"/><title>Redirecting...</head></html>",
			ERROR_FIRST = "<html><head><title>Error!</title></head><body>Error: ",
			ERROR_SECOND = "<br>If it says \"Index: 0, Size: 0\", it should work once you refresh the page.</body></html>";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();
		String url = req.getParameter("url");
		if (!url.equals("NONE")) {
			try {
				AccountHandler.protect(url);
				writer.print(FIRST + url + SECOND);
			} catch (Exception e) {
				e.printStackTrace();
				writer.println(ERROR_FIRST + e.getMessage() + ERROR_SECOND);
			}
		}
		writer.flush();
		writer.close();
	}
}