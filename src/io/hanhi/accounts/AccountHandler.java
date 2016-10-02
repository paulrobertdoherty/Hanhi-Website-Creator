package io.hanhi.accounts;

import java.util.*;

import org.json.JSONObject;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;

public class AccountHandler {
	private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public static String NO_HTML = "<html><head><title>No site found</title></head><body onclick=\"location.reload();\">" +
	"No site found. Did you type the URL correctly? If the URL is correct, click to reload the page.</body></html>";
	
	/**
	 * Reurns all accounts with the id
	 * @param id
	 * @return All entities with that account id
	 */
	private static List<Entity> getAccounts(String id) {
		Filter filter = new FilterPredicate("id", FilterOperator.EQUAL, id);
		Query q = new Query("Entry").setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
		return results;
	}
	
	private static List<Entity> getUrl(String url) {
		Filter filter = new FilterPredicate("urlId", FilterOperator.EQUAL, url);
		Query q = new Query("Entry").setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());
		return results;
	}
	
	public static String getHtml(String url) {
		List<Entity> results = getUrl(url);
		if (results.size() > 0) {
			Saved s = getSaved(results.toArray(new Entity[results.size()]))[0];
			if (s.valid()) {
				if (s.getHtmlHead().equals("NONE") &&
					s.getCss().equals("NONE") &&
					s.getJavascript().equals("NONE")) {
					return s.getHtml();
				} else {
					return "<html><head>" + s.getHtmlHead() + "<style>" + s.getCss() + "</style></head><body>" + s.getHtml()
					+ "<script>" + s.getJavascript() + "</script></body></html>";
				}
			} else {
				return NO_HTML;
			}
		}
		return NO_HTML;
	}
	
	public static Saved[] getSaved(Entity[] results) {
		List<Saved> saved = new ArrayList<Saved>();
		for (int i = 0; i < results.length; i++) {
			Text debug = (Text)results[i].getProperty("debug");
			if (debug == null) {
				debug = new Text("");
			}
			Text javascript = (Text)results[i].getProperty("javascript");
			Text html = (Text)results[i].getProperty("html");
			Text htmlHead = (Text)results[i].getProperty("htmlHead");
			Text css = (Text)results[i].getProperty("css");
			String url = (String)results[i].getProperty("urlId");
			Text tags = (Text)results[i].getProperty("tags");
			String finalTags = null;
			if (tags != null) {
				finalTags = tags.getValue();
			}
			if (javascript != null && html != null && htmlHead != null && css != null) {
				saved.add(new AccountHandler.Saved(
						debug.getValue(),
						javascript.getValue(),
						html.getValue(),
						htmlHead.getValue(),
						css.getValue(),
						url,
						finalTags));
			}
		}
		return saved.toArray(new Saved[saved.size()]);
	}
	
	public static void addAccount(String id) {
		List<Entity> results = getAccounts(id);
		
		//If the account doesn't exist, add it
		if (results.size() == 0) {
			Entity account = new Entity("Entry");
			account.setProperty("id", id);
			datastore.put(account);
		}
	}
	
	private static String fix(String text, List<Map<String, Object>> tags) {
		if (tags != null && !text.equals("NONE")) {
			String newText = text;
			for (Map<String, Object> tag : tags) {
				newText = newText.replace((String)tag.get("name"), (String)tag.get("value"));
			}
			return newText;
		} else {
			return text;
		}
	}
	
	/**
	 * Saves the code of the user.  
	 * @param id
	 * @param code
	 * @param css 
	 * @param html 
	 * @param css 
	 * @param tags 
	 * @return Link ID for the site
	 * @throws Exception 
	 */
	public static String save(String id, String debug, String javascript, String html, String htmlHead, String css, String url, String tags) throws Exception {
		Entity account = null;
		if (!id.equals("NONE")) {
			List<Entity> results = getAccounts(id);
			if (results.size() > 0) {
				account = getWith(results, url, id);
			} else {
				throw new Exception("Could not save.  Are you logged in?");
			}
		} else {
			account = getWith(null, url, id);
		}
		
		List<Map<String, Object>> tagMaps = null;
		if (!tags.equals("NO_TAGS") && tags.length() > 0) {
			account.setProperty("tags", new Text(tags));
			tagMaps = new ArrayList<Map<String, Object>>();
			String[] jsonList = tags.split("JSON_SPLITTER_STRING");
			for (String s : jsonList) {
				tagMaps.add(new JSONObject(s).toMap());
			}
		}
		
		account.setProperty("debug", new Text(debug));
		account.setProperty("javascript", new Text(fix(javascript, tagMaps)));
		account.setProperty("html", new Text(fix(html, tagMaps)));
		account.setProperty("htmlHead", new Text(fix(htmlHead, tagMaps)));
		account.setProperty("css", new Text(fix(css, tagMaps)));
		
		datastore.put(account);
		String urlId = (String)account.getProperty("urlId");
		return urlId;
		
	}

	private static Entity getWith(List<Entity> results, String url, String id) {
		boolean urlIsNull = url != null && !url.equals("n");
		if (!id.equals("NONE") && urlIsNull) {
			for (Entity e : results) {
				String nUrl = (String)e.getProperty("urlId");
				if (nUrl == null) {
					e.setProperty("urlId", url);
					return e;
				} else if (nUrl.equals(url)) {
					return e;
				}
			}
		} else if (urlIsNull) {
			//Delete all with the key
			Filter filter = new FilterPredicate("urlId", FilterOperator.EQUAL, url);
			Query query = new Query("Entry").setFilter(filter);
			PreparedQuery pq = datastore.prepare(query);
			Entity e = pq.asSingleEntity();
			if (e != null) {
				Boolean isProtected = false;
				try {
					isProtected = (Boolean)e.getProperty("protected");
					if (isProtected) {
						datastore.delete(pq.asSingleEntity().getKey());
					}
				} catch (Exception cce) {}
			}
		}
		Entity e = new Entity("Entry");
		e.setProperty("id", id);
		
		//Generate an id
		String newId = AlphabetEncoder.getRandom();
		List<Entity> urls = getUrl(newId);
		while (urls.size() > 0) {
			newId = AlphabetEncoder.getRandom();
			urls = getUrl(newId);
		}
		e.setProperty("urlId", newId);
		return e;
	}

	public static Saved[] getSaved(String id) throws Exception {
		List<Entity> results = getAccounts(id);
		if (results.size() > 0) {
			Saved[] saved = getSaved(results.toArray(new Entity[results.size()]));
			Saved[] toReturn = new Saved[saved.length];
			for (int i = 0; i < saved.length; i++) {
				if (saved[i].valid()) {
					toReturn[i] = saved[i];
				} else {
					throw new Exception("Nothing yet saved.");
				}
			}
			return toReturn;
		} else {
			throw new Exception("ID not valid.");
		}
	}
	
	public static class Saved {
		@Override
		public String toString() {
			return "Saved [debug=" + debug + ", javascript=" + javascript + ", html=" + html + ", htmlHead=" + htmlHead
					+ ", css=" + css + ", url=" + url + ", tags=" + tags + "]";
		}

		public String getJavascript() {
			return javascript;
		}

		public String getHtml() {
			return html;
		}
		
		public String getHtmlHead() {
			return htmlHead;
		}

		public String getCss() {
			return css;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getDebug() {
			return debug;
		}
		
		public String getTags() {
			return tags;
		}

		public Saved(String debug, String javascript, String html, String htmlHead, String css, String url, String tags) {
			this.debug = debug;
			this.javascript = javascript;
			this.html = html;
			this.htmlHead = htmlHead;
			this.css = css;
			this.url = url;
			this.tags = tags;
		}
		
		public boolean valid() {
			return javascript != null && html != null && htmlHead != null && css != null && url != null;
		}

		private String debug, javascript, html, htmlHead, css, url, tags;
	}
	
	private static class AlphabetEncoder {
	    private static final char[] ALPHABET = {
	    		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
	    		'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
	    		'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	    private static final int LENGTH = 6;

	    public static String getRandom() {
	    	Random r = new Random();
	    	StringBuilder b = new StringBuilder();
	    	for (int i = 0; i < LENGTH; i++) {
	    		b.append(ALPHABET[r.nextInt(ALPHABET.length)]);
	    	}
			return b.toString();
		}
	}

	public static void protect(String url) {
		Entity account = getUrl(url).get(0);
		account.setProperty("protected", true);
		datastore.put(account);
	}
}
