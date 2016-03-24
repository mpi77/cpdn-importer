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

public class Permission {
	public static final String MODE_READ = "r";
	public static final String MODE_WRITE = "w";
	public static final String MODE_EXECUTE = "x";
	public static final String MODE_READ_WRITE = "rw";
	public static final String MODE_READ_EXECUTE = "rx";
	public static final String MODE_WRITE_EXECUTE = "wx";
	public static final String MODE_READ_WRITE_EXECUTE = "rwx";
	
	private Integer id;
	private Integer schemeId;
	private Integer profileId;
	private String mode;
	private String tsFrom;
	private String tsTo;

	public Permission(Integer id, Integer schemeId, Integer profileId, String mode, String tsFrom, String tsTo) {
		super();
		this.id = id;
		this.schemeId = schemeId;
		this.profileId = profileId;
		this.mode = mode;
		this.tsFrom = tsFrom;
		this.tsTo = tsTo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(Integer schemeId) {
		this.schemeId = schemeId;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTsFrom() {
		return tsFrom;
	}

	public void setTsFrom(String tsFrom) {
		this.tsFrom = tsFrom;
	}

	public String getTsTo() {
		return tsTo;
	}

	public void setTsTo(String tsTo) {
		this.tsTo = tsTo;
	}

	public String createJsonBody() {
		JSONObject r = new JSONObject();
		r.put("user", this.profileId);
		r.put("scheme", this.schemeId);
		r.put("mode", this.mode);
		r.put("tsFrom", this.tsFrom);
		r.put("tsTo", this.tsTo);
		return r.toString();
	}

	public String updateJsonBody() {
		return this.createJsonBody();
	}
	
	public static Permission buildPermission(String json) throws JSONException{
		JSONObject s = new JSONObject(json);
		Integer id = ((JSONObject)s.get("_meta")).getInt("id");
		Integer profileId = ((JSONObject)((JSONObject)s.get("user")).get("_meta")).getInt("id");
		Integer schemeId = ((JSONObject)((JSONObject)s.get("scheme")).get("_meta")).getInt("id");
		String mode = s.getString("mode");
		String tsFrom = s.getString("tsFrom");
		String tsTo = s.getString("tsTo");
		
		return new Permission(id, schemeId, profileId, mode, tsFrom, tsTo);
	}
	
	public static List<Permission> buildPermissions(String json) throws JSONException{
		List<Permission> r = new ArrayList<Permission>();
		JSONObject root = new JSONObject(json);
		JSONArray a = new JSONArray(root.get("items").toString());
		for (int i = 0; i < a.length(); i++) {
			r.add(Permission.buildPermission(a.get(i).toString()));
		}
		return r;
	}
	
	public static HttpResponse getAll(int schemeId, int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%spermissions/?q=(schemeId=%d)&e=(permission)&pageSize=%d", Connection.API_V1_GW_URL, schemeId, pageSize));
	}
	
	public static HttpResponse getAll(int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%spermissions/?e=(permission)&pageSize=%d", Connection.API_V1_GW_URL, pageSize));
	}
	
	public static HttpResponse get(int id) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%spermissions/%d/", Connection.API_V1_GW_URL, id));
	}
	
	public static HttpResponse create(Permission perm) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%spermissions/", Connection.API_V1_GW_URL),
			new StringEntity(perm.createJsonBody()));
	}
	
	public static HttpResponse update(Permission perm) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%spermissions/%d/", Connection.API_V1_GW_URL, perm.id),
			new StringEntity(perm.updateJsonBody()));
	}
	
	public static HttpResponse delete(Permission perm) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.delete(String.format("%spermissions/%d/", Connection.API_V1_GW_URL, perm.id));
	}

	public static void main(String[] args) {
		try {
			/*
			 * You need to setup correct clientId, userName, userPassword into Connection.authenticate(...).
			 */
			System.exit(0);
			
			Connection.authenticate("demoClient", "demoUser", "demoUserPasswd");

			// get all permissions
			System.out.println(Arrays.toString(Permission.buildPermissions(EntityUtils.toString(Permission.getAll(100).getEntity())).toArray()));

			// get permission (id=1)
			System.out.println(Permission.buildPermission(EntityUtils.toString(Permission.get(1).getEntity())).toString());

			Permission perm = new Permission(null, 1, 1, Permission.MODE_READ_WRITE_EXECUTE, "2015-12-01 00:00:00", "2017-12-01 00:00:00");
			
			// create a new permission
			JSONObject create = new JSONObject(EntityUtils.toString(Permission.create(perm).getEntity()));
			System.out.println(create.toString());

			// update permission (id=1)
			//JSONObject update = new JSONObject(EntityUtils.toString(Permission.update(perm).getEntity()));
			//System.out.println(update.toString());
			
			// delete permission (id=1)
			// System.out.println(Permission.delete(perm));
		} catch (KeyManagementException | JSONException | ParseException | NoSuchAlgorithmException | KeyStoreException
				| IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Permission [id=" + id + ", schemeId=" + schemeId + ", profileId=" + profileId + ", mode=" + mode
				+ ", tsFrom=" + tsFrom + ", tsTo=" + tsTo + "]";
	}

}
