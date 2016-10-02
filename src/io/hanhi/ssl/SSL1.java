package io.hanhi.ssl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SSL1 extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		writer.print("HB8AWqHcaND_xgZ0GN0Pxf77yr4kQD3Ee923HHMxOZg.9I1j2x_blnJRcW_E5RmyAmvEYBaUHHT0Dc3QgqzfYoA");
		writer.flush();
		writer.close();
	}
}
