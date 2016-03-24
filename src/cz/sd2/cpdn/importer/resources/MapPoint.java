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

public class MapPoint {
	private Integer id;
	private Double gpsAlt;
	private Double gpsLat;
	private Double gpsLng;
	private Integer x;
	private Integer y;
	private Integer scheme;
	private Integer node;

	public MapPoint(Integer id, Double gpsAlt, Double gpsLat, Double gpsLng, Integer x, Integer y, Integer scheme,
			Integer node) {
		super();
		this.id = id;
		this.gpsAlt = gpsAlt;
		this.gpsLat = gpsLat;
		this.gpsLng = gpsLng;
		this.x = x;
		this.y = y;
		this.scheme = scheme;
		this.node = node;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getGpsAlt() {
		return gpsAlt;
	}

	public void setGpsAlt(Double gpsAlt) {
		this.gpsAlt = gpsAlt;
	}

	public Double getGpsLat() {
		return gpsLat;
	}

	public void setGpsLat(Double gpsLat) {
		this.gpsLat = gpsLat;
	}

	public Double getGpsLng() {
		return gpsLng;
	}

	public void setGpsLng(Double gpsLng) {
		this.gpsLng = gpsLng;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getScheme() {
		return scheme;
	}

	public void setScheme(Integer scheme) {
		this.scheme = scheme;
	}

	public Integer getNode() {
		return node;
	}

	public void setNode(Integer node) {
		this.node = node;
	}

	public String createJsonBody() {
		JSONObject r = new JSONObject();
		r.put("x", this.x);
		r.put("y", this.y);
		r.put("scheme", this.scheme);
		r.put("node", (this.node != null) ? this.node : JSONObject.NULL);
		r.put("gps", (new JSONObject())
				.put("altitude", (this.gpsAlt != null) ? this.gpsAlt : JSONObject.NULL)
				.put("latitude", (this.gpsLat != null) ? this.gpsLat : JSONObject.NULL)
				.put("longitude", (this.gpsLng != null) ? this.gpsLng : JSONObject.NULL));
		return r.toString();
	}

	public String updateJsonBody() {
		return this.createJsonBody();
	}
	
	public static MapPoint buildMapPoint(String json) throws JSONException{
		JSONObject s = new JSONObject(json);
		Integer id = Integer.parseInt(((JSONObject)s.get("_meta")).get("id").toString());
		Integer x = s.getInt("x");
		Integer y = s.getInt("y");
		Integer scheme = new Integer(((JSONObject)((JSONObject) s.get("scheme")).get("_meta")).getInt("id"));
		Integer node = (s.isNull("node") == false) ? new Integer(((JSONObject)((JSONObject) s.get("node")).get("_meta")).getInt("id")) : null;
		Double gpsAlt = ((((JSONObject)s.get("gps")).isNull("altitude")) == false) ? ((JSONObject)s.get("gps")).getDouble("altitude") : null;
		Double gpsLat = ((((JSONObject)s.get("gps")).isNull("latitude")) == false) ? ((JSONObject)s.get("gps")).getDouble("latitude") : null;
		Double gpsLng = ((((JSONObject)s.get("gps")).isNull("longitude")) == false) ? ((JSONObject)s.get("gps")).getDouble("longitude") : null;
		return new MapPoint(id, gpsAlt, gpsLat, gpsLng, x, y, scheme, node);
	}
	
	public static List<MapPoint> buildMapPoints(String json) throws JSONException{
		List<MapPoint> r = new ArrayList<MapPoint>();
		JSONObject root = new JSONObject(json);
		JSONArray a = new JSONArray(root.get("items").toString());
		for (int i = 0; i < a.length(); i++) {
			r.add(MapPoint.buildMapPoint(a.get(i).toString()));
		}
		return r;
	}
	
	public static HttpResponse getAll(int schemeId, int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%smapPoints/?q=(schemeId=%d)&e=(mapPoint)&pageSize=%d", Connection.API_V1_GW_URL, schemeId, pageSize));
	}
	
	public static HttpResponse get(int id) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%smapPoints/%d/", Connection.API_V1_GW_URL, id));
	}
	
	public static HttpResponse create(MapPoint point) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%smapPoints/", Connection.API_V1_GW_URL),
			new StringEntity(point.createJsonBody()));
	}
	
	public static HttpResponse update(MapPoint point) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%smapPoints/%d/", Connection.API_V1_GW_URL, point.id),
			new StringEntity(point.updateJsonBody()));
	}

	public static HttpResponse delete(MapPoint point) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.delete(String.format("%smapPoints/%d/", Connection.API_V1_GW_URL, point.id));
	}

	public static void main(String[] args) {
		try {
			/*
			 * You need to setup correct clientId, userName, userPassword into Connection.authenticate(...).
			 */
			System.exit(0);
			
			Connection.authenticate("demoClient", "demoUser", "demoUserPasswd");

			// get all mapPoints
			System.out.println(Arrays.toString(MapPoint.buildMapPoints(EntityUtils.toString(MapPoint.getAll(1, 100).getEntity())).toArray()));

			// get mapPoint (id=1)
			System.out.println(MapPoint.buildMapPoint(EntityUtils.toString(MapPoint.get(1).getEntity())).toString());

			MapPoint point = new MapPoint(1, null, null, null, 111, 111, 1, null);
			
			// create a new mapPoint
			JSONObject create = new JSONObject(EntityUtils.toString(MapPoint.create(point).getEntity()));
			System.out.println(create.toString());

			// update mapPoint (id=1)
			JSONObject update = new JSONObject(EntityUtils.toString(MapPoint.update(point).getEntity()));
			System.out.println(update.toString());
			
			// delete scheme (id=1)
			System.out.println(MapPoint.delete(point));
		} catch (KeyManagementException | JSONException | ParseException | NoSuchAlgorithmException | KeyStoreException
				| IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "MapPoint [id=" + id + ", gpsAlt=" + gpsAlt + ", gpsLat=" + gpsLat + ", gpsLng=" + gpsLng + ", x=" + x
				+ ", y=" + y + ", scheme=" + scheme + ", node=" + node + "]";
	}
}
