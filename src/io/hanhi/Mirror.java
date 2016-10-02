package io.hanhi;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Mirror extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String url = req.getParameter("url");
		if (url != null) {
			URLConnection urlC = new URL(url).openConnection();
			OutputStream os = resp.getOutputStream();
			os.flush();
			resp.setContentType(urlC.getHeaderField("content-type"));
			os.write(getBytes(urlC));
			os.flush();
			os.close();
		} else {
			resp.sendError(400);
		}
	}
	//
	private byte[] getBytes(URLConnection urlC) throws IOException {
		//URL stuff, like getting the size of the image
		int length = urlC.getContentLength();
		if (length == -1) {
			length = 4096;
		}
		//Get the bytes from the image
		byte[] bytes = new byte[length];
		InputStream is = urlC.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int n;
		while ((n = is.read(bytes)) > 0) {
			baos.write(bytes, 0, n);
		}
		is.close();
		return baos.toByteArray();
	}
}
