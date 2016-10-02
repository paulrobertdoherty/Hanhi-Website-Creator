package io.hanhi.templates;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Story extends HttpServlet {
	public static final int ORDER = 4, SEED = 123456789, LENGTH = 2500, BREAK_EVERY = 100;
	
	private String getResult(String content) {
		Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
		
		//Get the mappings
		for (int i = 0; i < content.length() - (ORDER * 2); i++) {
			String key = content.substring(i, i + ORDER);
			String value = content.substring(i + ORDER, i + (ORDER * 2));
			
			Map<String, Integer> counts = map.get(key);
			if (counts != null) {
				Integer valueCount = counts.get(value);
				if (valueCount != null) {
					counts.put(value, valueCount + 1);
				} else {
					counts.put(value, 1);
				}
			} else {
				counts = new HashMap<String, Integer>();
				counts.put(value, 1);
				map.put(key, counts);
			}
		}
		
		//Put the mappings in to a string
		Random r = new Random(SEED);
		String lastKey = getRandomKey(r, map);
		StringBuilder result = new StringBuilder(lastKey);
		for (int i = 0; i < LENGTH; i++) {
			Map<String, Integer> counts = map.get(lastKey);
			if (counts != null) {
				Set<String> values = counts.keySet();
				String[] valueArray = values.toArray(new String[values.size()]);
				lastKey = valueArray[r.nextInt(valueArray.length)];
			} else {
				lastKey = getRandomKey(r, map);
			}
			result.append(lastKey);
			if (i % BREAK_EVERY == 0) {
				result.append("<br>");
			}
		}
		return result.toString();
	}
	
	private String getRandomKey(Random r, Map<String, Map<String, Integer>> map) {
		Set<String> keySet = map.keySet();
		String[] keys = keySet.toArray(new String[keySet.size()]);
		return keys[r.nextInt(keys.length)];
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String content = req.getParameter("content");
		if (content != null) {
			if (content.length() >= ORDER * 2)  {
				PrintWriter writer = resp.getWriter();
				writer.flush();
				resp.setContentType("text/plain");
				writer.print(getResult(content));
				writer.flush();
				writer.close();
			} else {
				resp.sendError(400, "The inspiration was too short. It must be at least a character long!");
			}
		} else {
			resp.sendError(400, "No inspiration was sent!");
		}
	}
}