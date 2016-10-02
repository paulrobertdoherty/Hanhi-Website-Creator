package io.hanhi;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.*;
import io.hanhi.accounts.AccountHandler;

@SuppressWarnings("serial")
public class Save extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		String id = req.getParameter("id");
		save(req, writer, id);
		writer.flush();
		writer.close();
	}
	
	private void save(HttpServletRequest req, PrintWriter writer, String id) {
		String debug = req.getParameter("debug");
		String javascript = req.getParameter("javascript");
		String html = req.getParameter("html");
		String htmlHead = req.getParameter("htmlHead");
		String css = req.getParameter("css");
		String urlIn = req.getParameter("url");
		String tags = req.getParameter("tags");
		
		try {
			String url = AccountHandler.save(id, debug, javascript, html, htmlHead, css, urlIn, tags);
			writer.print(url);
		} catch (Exception e) {
			e.printStackTrace();
			writer.println("ERROR: " + e.getMessage());
		}
	}
}
