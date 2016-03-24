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

public class Task {
	public static final String STATUS_PREPARING = "preparing";
	public static final String STATUS_NEW = "new";
	public static final String STATUS_WORKING = "working";
	public static final String STATUS_COMPLETE = "complete";

	private Integer id;
	private String executorId;
	private Integer schemeId;
	private Integer profileId;
	private String status;
	private Integer priority;
	private String command;
	private String result;
	
	public Task(Integer id, String executorId, Integer schemeId, Integer profileId, String status, Integer priority,
			String command, String result) {
		super();
		this.id = id;
		this.executorId = executorId;
		this.schemeId = schemeId;
		this.profileId = profileId;
		this.status = status;
		this.priority = priority;
		this.command = command;
		this.result = result;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExecutorId() {
		return executorId;
	}

	public void setExecutorId(String executorId) {
		this.executorId = executorId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String createJsonBody() {
		JSONObject r = new JSONObject();
		r.put("user", this.profileId);
		r.put("executor", this.executorId);
		r.put("scheme", this.schemeId);
		r.put("status", this.status);
		r.put("priority", this.priority);
		r.put("command", (this.command != null) ? this.command : JSONObject.NULL);
		r.put("result", (this.result != null) ? this.result : JSONObject.NULL);
		return r.toString();
	}
	
	public String updateJsonBody() {
		return this.createJsonBody();
	}
	
	public static Task buildTask(String json) throws JSONException{
		JSONObject s = new JSONObject(json);
		Integer id = ((JSONObject)s.get("_meta")).getInt("id");
		Integer profileId = ((JSONObject)((JSONObject)s.get("user")).get("_meta")).getInt("id");
		String executorId = ((JSONObject)((JSONObject)s.get("executor")).get("_meta")).getString("id");
		Integer schemeId = ((JSONObject)((JSONObject)s.get("scheme")).get("_meta")).getInt("id");
		String status = s.getString("status");
		Integer priority = s.getInt("priority");
		String command = (s.isNull("command") == false) ? s.getString("command") : null;
		String result = (s.isNull("result") == false) ? s.getString("result") : null;
		
		return new Task(id, executorId, schemeId, profileId, status, priority, command, result);
	}
	
	public static List<Task> buildTasks(String json) throws JSONException{
		List<Task> r = new ArrayList<Task>();
		JSONObject root = new JSONObject(json);
		JSONArray a = new JSONArray(root.get("items").toString());
		for (int i = 0; i < a.length(); i++) {
			r.add(Task.buildTask(a.get(i).toString()));
		}
		return r;
	}
	
	public static HttpResponse getAll( int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%stasks/?e=(task)&pageSize=%d", Connection.API_V1_GW_URL, pageSize));
	}
	
	public static HttpResponse getAll(int schemeId, int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%stasks/?q=(schemeId=%d)&e=(task)&pageSize=%d", Connection.API_V1_GW_URL, schemeId, pageSize));
	}
	
	public static HttpResponse getAll(String executorId, int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%stasks/?q=(executorId=%d)&e=(task)&pageSize=%d", Connection.API_V1_GW_URL, executorId, pageSize));
	}
	
	public static HttpResponse getAll(String executorId, String status, int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%stasks/?q=(executorId=%s;status=%s)&e=(task)&pageSize=%d", Connection.API_V1_GW_URL, executorId, status, pageSize));
	}
	
	public static HttpResponse get(int id) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%stasks/%d/", Connection.API_V1_GW_URL, id));
	}
	
	public static HttpResponse create(Task task) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%stasks/", Connection.API_V1_GW_URL),
			new StringEntity(task.createJsonBody()));
	}
	
	public static HttpResponse update(Task task) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
	return Connection.post(String.format("%stasks/%d/", Connection.API_V1_GW_URL, task.id),
			new StringEntity(task.updateJsonBody()));
	}

	public static void main(String[] args) {
		try {
			/*
			 * You need to setup correct clientId, userName, userPassword into Connection.authenticate(...).
			 */
			System.exit(0);
			
			Connection.authenticate("demoClient", "demoUser", "demoUserPasswd");

			Task task = new Task(null, "javaExecDC1", 1, 2, Task.STATUS_PREPARING, 1, null, null);
			
			// create a new task
			JSONObject create = new JSONObject(EntityUtils.toString(Task.create(task).getEntity()));
			task.setId((create.isNull("id") == false) ? create.getInt("id") : null);
			System.out.println(create.toString());
			
			// get all tasks
			System.out.println(Arrays.toString(Task.buildTasks(EntityUtils.toString(Task.getAll(100).getEntity())).toArray()));

			// get task (id=task.id)
			System.out.println(Task.buildTask(EntityUtils.toString(Task.get(task.getId()).getEntity())).toString());

		} catch (KeyManagementException | JSONException | ParseException | NoSuchAlgorithmException | KeyStoreException
				| IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", executorId=" + executorId + ", schemeId=" + schemeId + ", profileId=" + profileId
				+ ", status=" + status + ", priority=" + priority + ", command=" + command + ", result=" + result + "]";
	}

}
