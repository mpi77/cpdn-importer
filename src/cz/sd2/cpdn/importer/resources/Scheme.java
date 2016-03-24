package cz.sd2.cpdn.importer.resources;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.sd2.cpdn.importer.utils.Connection;

public class Scheme {

	private Integer id;
	private String name;
	private String description;
	private Integer version;
	private boolean lock;

	public Scheme(Integer id, String name, String description, Integer version, boolean lock) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.version = version;
		this.lock = lock;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}
	
	public String createJsonBody() {
		JSONObject r = new JSONObject();
		r.put("name", this.name);
		r.put("description", this.description);
		r.put("version", this.version);
		r.put("lock", (this.lock) ? "1" : "0");
		return r.toString();
	}

	public String updateJsonBody() {
		return this.createJsonBody();
	}
	
	public static Scheme buildScheme(String json) throws JSONException{
		JSONObject s = new JSONObject(json);
		boolean lock = (Integer.parseInt(s.get("lock").toString()) == 1) ? true : false;
		return new Scheme(Integer.parseInt(((JSONObject)s.get("_meta")).get("id").toString()), s.get("name").toString(), s.get("description").toString(), Integer.parseInt(s.get("version").toString()), lock);
	}
	
	public static List<Scheme> buildSchemes(String json) throws JSONException{
		List<Scheme> r = new ArrayList<Scheme>();
		JSONObject root = new JSONObject(json);
		JSONArray a = new JSONArray(root.get("items").toString());
		for (int i = 0; i < a.length(); i++) {
			r.add(Scheme.buildScheme(a.get(i).toString()));
		}
		return r;
	}

	public static HttpResponse getAll(int pageSize) throws KeyManagementException, JSONException, ParseException,
			ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%sschemes/?e=(scheme)&pageSize=%d", Connection.API_V1_GW_URL, pageSize));
	}

	public static HttpResponse get(int id) throws KeyManagementException, JSONException, ParseException,
			ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%sschemes/%d/", Connection.API_V1_GW_URL, id));
	}

	public static HttpResponse create(Scheme scheme) throws KeyManagementException, JSONException, ParseException,
			ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%sschemes/", Connection.API_V1_GW_URL),
				new StringEntity(scheme.createJsonBody()));
	}

	public static HttpResponse update(Scheme scheme) throws KeyManagementException, JSONException, ParseException,
			ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%sschemes/%d/", Connection.API_V1_GW_URL, scheme.id),
				new StringEntity(scheme.updateJsonBody()));
	}

	public static HttpResponse delete(Scheme scheme) throws KeyManagementException, JSONException, ParseException,
			ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.delete(String.format("%sschemes/%d/", Connection.API_V1_GW_URL, scheme.id));
	}

	public static void main(String[] args) {
		try {
			/*
			 * You need to setup correct clientId, userName, userPassword into Connection.authenticate(...).
			 */
			System.exit(0);
			
			Connection.authenticate("demoClient", "demoUser", "demoUserPasswd");

			// get all schemes
			System.out.println(Arrays.toString(Scheme.buildSchemes(EntityUtils.toString(Scheme.getAll(100).getEntity())).toArray()));

			// get scheme (id=1)
			System.out.println(Scheme.buildScheme(EntityUtils.toString(Scheme.get(1).getEntity())).toString());

			Scheme scheme = new Scheme(1, "HK_JIH_2", "", 1, false);
			
			// create a new scheme
			JSONObject create = new JSONObject(EntityUtils.toString(Scheme.create(scheme).getEntity()));
			System.out.println(create.toString());

			// update scheme (id=1)
			JSONObject update = new JSONObject(EntityUtils.toString(Scheme.update(scheme).getEntity()));
			System.out.println(update.toString());
			
			// delete scheme (id=1)
			System.out.println(Scheme.delete(scheme));
		} catch (KeyManagementException | JSONException | ParseException | NoSuchAlgorithmException | KeyStoreException
				| IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Scheme [id=" + id + ", name=" + name + ", description=" + description + ", version=" + version
				+ ", lock=" + lock + "]";
	}
}
