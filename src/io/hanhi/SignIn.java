package io.hanhi;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import javax.servlet.http.*;
import com.google.gson.*;

import io.hanhi.accounts.AccountHandler;

@SuppressWarnings("serial")
public class SignIn extends HttpServlet {
	public static String URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=";
	public static String CLIENT_ID = "lol no";
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		String idToken = req.getParameter("idToken");
		if (idToken != null) {
			//Validate the token
			JsonObject j = readJsonFromUrl(URL + idToken);
			if (j != null && j.get("aud").getAsString().equals(CLIENT_ID)) {
				//Create an account if not already created
				String id = j.get("sub").getAsString();
				AccountHandler.addAccount(id);
				writer.print("Signed in successfully!");
			} else {
				writer.print("Could not authorize." + j);
			}
		} else {
			writer.print("Failed to validate.");
		}
		writer.flush();
		writer.close();
	}
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	}
	
	public static JsonObject readJsonFromUrl(String url) throws IOException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JsonObject json = new JsonParser().parse(jsonText).getAsJsonObject();
	      return json;
	    } finally {
	      is.close();
	    }
	}
}
