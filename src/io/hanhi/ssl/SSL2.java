package io.hanhi.ssl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SSL2 extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		writer.print("q2GtU0bOQMUvDhdL_R0sLlq01e50wR7r9DQlHZLNwi8.9I1j2x_blnJRcW_E5RmyAmvEYBaUHHT0Dc3QgqzfYoA");
		writer.flush();
		writer.close();
	}
}
