package io.hanhi;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.hanhi.accounts.AccountHandler;

@SuppressWarnings("serial")
public class GetSaved extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		String id = req.getParameter("id");
		if (!id.equals("NONE")) {
			try {
				AccountHandler.Saved[] saved = AccountHandler.getSaved(id);
				for (AccountHandler.Saved s : saved) {
					writer.print("DEBUG {" + s.getDebug());
					writer.print("} JAVASCRIPT {" + s.getJavascript());
					writer.print("} HTML {" + s.getHtml());
					writer.print("} HTML_HEAD {" + s.getHtmlHead());
					writer.print("} CSS {" + s.getCss() + "}");
					writer.print("} URL: {" + s.getUrl());
					String tags = s.getTags();
					if (tags != null) {
						writer.print("} TAGS {" + s.getTags());
					}
					writer.print("} END URL;");
				}
			} catch (Exception e) {
				e.printStackTrace();
				writer.println("ERROR: " + e.getMessage());
			}
		}
		writer.flush();
		writer.close();
	}
}