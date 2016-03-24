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

public class Object {
	
	private Integer id;
	private String name;
	private Integer scheme;

	public Object(Integer id, String name, Integer scheme) {
		super();
		this.id = id;
		this.name = name;
		this.scheme = scheme;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getScheme() {
		return scheme;
	}

	public void setScheme(Integer scheme) {
		this.scheme = scheme;
	}

	public String createJsonBody() {
		JSONObject r = new JSONObject();
		r.put("name", this.name);
		r.put("scheme", this.scheme);
		return r.toString();
	}

	public String updateJsonBody() {
		return this.createJsonBody();
	}
	
	public static Object buildObject(String json) throws JSONException{
		JSONObject s = new JSONObject(json);
		Integer id = Integer.parseInt(((JSONObject)s.get("_meta")).get("id").toString());
		String name = s.getString("name");
		Integer scheme = new Integer(((JSONObject)((JSONObject) s.get("scheme")).get("_meta")).getInt("id"));
		return new Object(id, name, scheme);
	}
	
	public static List<Object> buildObjects(String json) throws JSONException{
		List<Object> r = new ArrayList<Object>();
		JSONObject root = new JSONObject(json);
		JSONArray a = new JSONArray(root.get("items").toString());
		for (int i = 0; i < a.length(); i++) {
			r.add(Object.buildObject(a.get(i).toString()));
		}
		return r;
	}
	
	public static HttpResponse getAll(int schemeId, int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%sobjects/?q=(schemeId=%d)&e=(object)&pageSize=%d", Connection.API_V1_GW_URL, schemeId, pageSize));
	}
	
	public static HttpResponse get(int id) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%sobjects/%d/", Connection.API_V1_GW_URL, id));
	}
	
	public static HttpResponse create(Object object) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%sobjects/", Connection.API_V1_GW_URL),
			new StringEntity(object.createJsonBody()));
	}
	
	public static HttpResponse update(Object object) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%sobjects/%d/", Connection.API_V1_GW_URL, object.id),
			new StringEntity(object.updateJsonBody()));
	}
	
	public static HttpResponse delete(Object object) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.delete(String.format("%sobjects/%d/", Connection.API_V1_GW_URL, object.id));
	}
	
	public static HttpResponse getMembers(Integer objectId) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%sobjects/%d/nodes", Connection.API_V1_GW_URL, objectId));
	}
	
	public static HttpResponse createMember(Integer objectId, Integer nodeId) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%sobjects/%s/node/%s", Connection.API_V1_GW_URL, objectId, nodeId), null);
	}
	
	public static HttpResponse deleteMember(Integer objectId, Integer nodeId) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.delete(String.format("%sobjects/%s/node/%s", Connection.API_V1_GW_URL, objectId, nodeId));
	}

	public static void main(String[] args) {
		try {
			/*
			 * You need to setup correct clientId, userName, userPassword into Connection.authenticate(...).
			 */
			System.exit(0);
			
			Connection.authenticate("demoClient", "demoUser", "demoUserPasswd");

			// get all objects
			System.out.println(Arrays.toString(Object.buildObjects(EntityUtils.toString(Object.getAll(1, 100).getEntity())).toArray()));

			// get object (id=1)
			System.out.println(Object.buildObject(EntityUtils.toString(Object.get(1).getEntity())).toString());

			Object obj = new Object(1, "JC0123", 1);
			
			// create a new object
			JSONObject create = new JSONObject(EntityUtils.toString(Object.create(obj).getEntity()));
			System.out.println(create.toString());

			// update object (id=1)
			JSONObject update = new JSONObject(EntityUtils.toString(Object.update(obj).getEntity()));
			System.out.println(update.toString());
			
			// set new member (objectId=1, nodeId=1)
			System.out.println(Object.createMember(1, 1));
			
			// get members for object (id=1)
			JSONObject members = new JSONObject(EntityUtils.toString(Object.getMembers(obj.id).getEntity()));
			System.out.println(members.toString());
			
			// delete member (objectId=1, nodeId=1)
			System.out.println(Object.deleteMember(1, 1));
			
			// delete scheme (id=1)
			System.out.println(Object.delete(obj));
			
		} catch (KeyManagementException | JSONException | ParseException | NoSuchAlgorithmException | KeyStoreException
				| IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Object [id=" + id + ", name=" + name + ", scheme=" + scheme + "]";
	}

	
}
