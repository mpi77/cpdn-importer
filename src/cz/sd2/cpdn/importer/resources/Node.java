package cz.sd2.cpdn.importer.resources;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Object;

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

public class Node {
	
	public static final String TYPE_POWER = "power";
	public static final String TYPE_CONSUMPTION = "consumption";
	public static final String TYPE_TURBO_GEN = "turbogen";
	public static final String TYPE_HYDRO_GEN = "hydrogen";
	public static final String TYPE_SUPERIOR_SYSTEM = "superiorSystem";
	
	private Integer id;
	private Integer scheme;
	private Integer mapPoint;
	private Map<String, Double> calc;
	private Map<String, Object> spec;
	
	
	public Node(Integer id, Map<String, Double> calc, Map<String, Object> spec, Integer scheme, Integer mapPoint) {
		super();
		this.id = id;
		this.calc = calc;
		this.spec = spec;
		this.scheme = scheme;
		this.mapPoint = mapPoint;
		
	}
	
	public Node() {
		super();
		this.calc = new HashMap<String, Double>();
		this.spec = new HashMap<String, Object>();
		this.scheme = new Integer(0);
		this.mapPoint = new Integer(0);
		this.id = new Integer(0);
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getScheme() {
		return scheme;
	}

	public void setScheme(Integer scheme) {
		this.scheme = scheme;
	}

	public Integer getMapPoint() {
		return mapPoint;
	}

	public void setMapPoint(Integer mapPoint) {
		this.mapPoint = mapPoint;
	}

	public Map<String, Double> getCalc() {
		return calc;
	}

	public void setCalc(Map<String, Double> calc) {
		this.calc = calc;
	}

	public Map<String, Object> getSpec() {
		return spec;
	}

	public void setSpec(Map<String, Object> spec) {
		this.spec = spec;
	}

	public String createJsonBody() throws NullPointerException {
		JSONObject r = new JSONObject();
		r.put("calc", this.getJsonCalcBody());
		r.put("mapPoint", this.mapPoint);
		r.put("scheme", this.scheme);
		r.put("spec", this.getJsonSpecBody());
		return r.toString();
	}
	
	public JSONObject getJsonCalcBody() throws NullPointerException {
		JSONObject calc = new JSONObject();
		calc.put("load", (new JSONObject())
				.put("active", (this.calc.get("load.active") != null) ? this.calc.get("load.active") : JSONObject.NULL)
				.put("reactive", (this.calc.get("load.reactive") != null) ? this.calc.get("load.reactive") : JSONObject.NULL));
		calc.put("voltage", (new JSONObject())
				.put("dropKv", (this.calc.get("voltage.dropKv") != null) ? this.calc.get("voltage.dropKv") : JSONObject.NULL)
				.put("dropProc", (this.calc.get("voltage.dropProc") != null) ? this.calc.get("voltage.dropProc") : JSONObject.NULL)
				.put("phase", (this.calc.get("voltage.phase") != null) ? this.calc.get("voltage.phase") : JSONObject.NULL)
				.put("value", (this.calc.get("voltage.value") != null) ? this.calc.get("voltage.value") : JSONObject.NULL));
		return calc;
	}
	
	public JSONObject getJsonSpecBody() throws NullPointerException {
		JSONObject spec = new JSONObject();
		spec.put("type", this.spec.get("type"));
		spec.put("label", (this.spec.get("label") != null) ? this.spec.get("label") : JSONObject.NULL);
		spec.put("cosFi", (this.spec.get("cosFi") != null) ? this.spec.get("cosFi") : JSONObject.NULL);
		spec.put("mi", (this.spec.get("mi") != null) ? this.spec.get("mi") : JSONObject.NULL);
		spec.put("lambda", (new JSONObject())
				.put("max", (this.spec.get("lambda.max") != null) ? this.spec.get("lambda.max") : JSONObject.NULL)
				.put("min", (this.spec.get("lambda.min") != null) ? this.spec.get("lambda.min") : JSONObject.NULL));
		spec.put("power", (new JSONObject())
				.put("active", (this.spec.get("power.active") != null) ? this.spec.get("power.active") : JSONObject.NULL)
				.put("installed", (this.spec.get("power.installed") != null) ? this.spec.get("power.installed") : JSONObject.NULL)
				.put("rated", (this.spec.get("power.rated") != null) ? this.spec.get("power.rated") : JSONObject.NULL)
				.put("reactive", ( this.spec.get("power.reactive") != null) ? this.spec.get("power.reactive") : JSONObject.NULL));
		spec.put("reactance", (new JSONObject())
				.put("longitudinal", (this.spec.get("reactance.longitudinal") != null) ? this.spec.get("reactance.longitudinal") : JSONObject.NULL)
				.put("transverse", (this.spec.get("reactance.transverse") != null) ? this.spec.get("reactance.transverse") : JSONObject.NULL));
		spec.put("voltage", (new JSONObject())
				.put("level", (this.spec.get("voltage.level") != null) ? this.spec.get("voltage.level") : JSONObject.NULL)
				.put("phase", (this.spec.get("voltage.phase") != null) ? this.spec.get("voltage.phase") : JSONObject.NULL)
				.put("rated", (this.spec.get("voltage.rated") != null) ? this.spec.get("voltage.rated") : JSONObject.NULL)
				.put("value", (this.spec.get("voltage.value") != null) ? this.spec.get("voltage.value") : JSONObject.NULL));
		return spec;
	}
	
	public JSONObject getJsonMapPointBody() {
		JSONObject r = new JSONObject();
		r.put("mapPoint", this.mapPoint);
		return r;
	}
	
	public JSONObject getJsonSchemeBody() {
		JSONObject r = new JSONObject();
		r.put("scheme", this.scheme);
		return r;
	}
	
	public static HttpResponse getAll(int schemeId, int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%snodes/?q=(schemeId=%d)&e=(node,node.scheme,node.mapPoint,node.spec,node.calc)&pageSize=%d", Connection.API_V1_GW_URL, schemeId, pageSize));
	}
	
	public static HttpResponse get(int id) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%snodes/%d/?e=(spec,calc)", Connection.API_V1_GW_URL, id));
	}
	
	public static HttpResponse create(Node node) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%snodes/", Connection.API_V1_GW_URL),
			new StringEntity(node.createJsonBody()));
	}
	
	public static HttpResponse updateCalc(Node node) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%snodes/%d/calc", Connection.API_V1_GW_URL, node.id),
				new StringEntity(node.getJsonCalcBody().toString()));
	}
	
	public static HttpResponse updateSpec(Node node) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%snodes/%d/spec", Connection.API_V1_GW_URL, node.id),
				new StringEntity(node.getJsonSpecBody().toString()));
	}
	
	public static HttpResponse updateMapPoint(Node node) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%snodes/%d/mapPoint", Connection.API_V1_GW_URL, node.id),
				new StringEntity(node.getJsonMapPointBody().toString()));
	}
	
	public static HttpResponse delete(Node node) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.delete(String.format("%snodes/%d/", Connection.API_V1_GW_URL, node.id));
	}
	
	public static Node buildNode(String json) throws JSONException{
		JSONObject s = new JSONObject(json);
		Integer id = new Integer(((JSONObject) s.get("_meta")).getInt("id"));
		Integer scheme = new Integer(((JSONObject)((JSONObject) s.get("scheme")).get("_meta")).getInt("id"));
		Integer mapPoint = new Integer(((JSONObject)((JSONObject) s.get("mapPoint")).get("_meta")).getInt("id"));
		JSONObject calcJs = new JSONObject(s.get("calc").toString());
		JSONObject calcLoad = (JSONObject) calcJs.get("load");
		JSONObject calcVoltage = (JSONObject) calcJs.get("voltage");
		JSONObject specJs = new JSONObject(s.get("spec").toString());
		JSONObject specLambda = (JSONObject) specJs.get("lambda");
		JSONObject specPower = (JSONObject) specJs.get("power");
		JSONObject specReactance = (JSONObject) specJs.get("reactance");
		JSONObject specVoltage = (JSONObject) specJs.get("voltage");
		
		Map<String, Double> calc = Node.buildCalcMap(
				(calcLoad.isNull("active") == false) ? calcLoad.getDouble("active") : null, 
				(calcLoad.isNull("reactive") == false) ? calcLoad.getDouble("reactive") : null, 
				(calcVoltage.isNull("dropKv") == false) ? calcVoltage.getDouble("dropKv") : null, 
				(calcVoltage.isNull("dropProc") == false) ? calcVoltage.getDouble("dropProc") : null, 
				(calcVoltage.isNull("phase") == false) ? calcVoltage.getDouble("phase") : null, 
				(calcVoltage.isNull("value") == false) ? calcVoltage.getDouble("value") : null);
		Map<String, Object> spec = Node.buildSpecMap(
				specJs.getString("type"),
				(specJs.isNull("label") == false) ? specJs.getString("label") : null, 
				(specJs.isNull("cosFi") == false) ? specJs.getDouble("cosFi") : null, 
				(specJs.isNull("mi") == false) ? specJs.getDouble("mi") : null, 
				(specLambda.isNull("max") == false) ? specLambda.getDouble("max") : null, 
				(specLambda.isNull("min") == false) ? specLambda.getDouble("min") : null, 
				(specPower.isNull("active") == false) ? specPower.getDouble("active") : null, 
				(specPower.isNull("installed") == false) ? specPower.getDouble("installed") : null, 
				(specPower.isNull("rated") == false) ? specPower.getDouble("rated") : null, 
				(specPower.isNull("reactive") == false) ? specPower.getDouble("reactive") : null, 
				(specReactance.isNull("longitudinal") == false) ? specReactance.getDouble("longitudinal") : null, 
				(specReactance.isNull("transverse") == false) ? specReactance.getDouble("transverse") : null, 
				(specVoltage.isNull("level") == false) ? specVoltage.getDouble("level") : null, 
				(specVoltage.isNull("phase") == false) ? specVoltage.getDouble("phase") : null, 
				(specVoltage.isNull("rated") == false) ? specVoltage.getDouble("rated") : null, 
				(specVoltage.isNull("value") == false) ? specVoltage.getDouble("value") : null);
		
		return new Node(id, calc, spec, scheme, mapPoint);
	}
	public static List<Node> buildNodes(String json) throws JSONException{
		List<Node> r = new ArrayList<Node>();
		JSONObject root = new JSONObject(json);
		JSONArray a = new JSONArray(root.get("items").toString());
		for (int i = 0; i < a.length(); i++) {
			r.add(Node.buildNode(a.get(i).toString()));
		}
		return r;
	}
	public static Map<String, Double> buildCalcMap(Double loadActive, Double loadReactive, 
			Double voltageDropKV, Double voltageDropProc, Double voltagePhase, Double voltageValue) {
		Map<String, Double> calc = new HashMap<String, Double>();
		calc.put("load.active", loadActive);
		calc.put("load.reactive", loadReactive);
		calc.put("voltage.dropKv", voltageDropKV);
		calc.put("voltage.dropProc", voltageDropProc);
		calc.put("voltage.phase", voltagePhase);
		calc.put("voltage.value", voltageValue);
		return calc;
	}
	
	public static Map<String, Object> buildSpecMap(String type, String label, Double cosFi, Double mi, Double lambdaMax, Double lambdaMin,
			Double powerActive, Double powerInstalled, Double powerRated, Double powerReactive, 
			Double reactanceLongitudinal, Double reactanceTransverse, Double voltageLevel, Double voltagePhase,
			Double voltageRated, Double voltageValue) throws IllegalArgumentException{
		if(Node.isValidSpecType(type) != true){
			throw new IllegalArgumentException("Node/spec requires valid type argument.");
		}
		Map<String, Object> spec = new HashMap<String, Object>();
		spec.put("type", type);
		spec.put("label", label);
		spec.put("cosFi", cosFi);
		spec.put("mi", mi);
		spec.put("lambda.max", lambdaMax);
		spec.put("lambda.min", lambdaMin);
		spec.put("power.active", powerActive);
		spec.put("power.installed", powerInstalled);
		spec.put("power.rated", powerRated);
		spec.put("power.reactive", powerReactive);
		spec.put("reactance.longitudinal", reactanceLongitudinal);
		spec.put("reactance.transverse", reactanceTransverse);
		spec.put("voltage.level", voltageLevel);
		spec.put("voltage.phase", voltagePhase);
		spec.put("voltage.rated", voltageRated);
		spec.put("voltage.value", voltageValue);
		return spec;
	}
	
	public static boolean isValidSpecType(String type){
		return (type.equals(Node.TYPE_POWER) || type.equals(Node.TYPE_CONSUMPTION)
				|| type.equals(Node.TYPE_TURBO_GEN) || type.equals(Node.TYPE_HYDRO_GEN)
				|| type.equals(Node.TYPE_SUPERIOR_SYSTEM));
	}

	@Override
	public String toString() {
		return "Node [calc=" + calc + ", spec=" + spec + ", scheme=" + scheme + ", mapPoint=" + mapPoint + ", id=" + id
				+ "]";
	}

	public static void main(String[] args) {
		try {
			/*
			 * You need to setup correct clientId, userName, userPassword into Connection.authenticate(...).
			 */
			System.exit(0);
			
			Connection.authenticate("demoClient", "demoUser", "demoUserPasswd");

			// get all nodes
			System.out.println(Arrays.toString(Node.buildNodes(EntityUtils.toString(Node.getAll(1, 100).getEntity())).toArray()));

			// get node (id=1)
			System.out.println(Node.buildNode(EntityUtils.toString(Node.get(1).getEntity())).toString());

			Map<String, Double> calc = Node.buildCalcMap(.0, null, .0, .0, .0, .0);
			Map<String, Object> spec = Node.buildSpecMap(Node.TYPE_POWER, "label", .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0);
			Node node = new Node(1, calc, spec, 1, 1);
			
			// create a new node
			JSONObject create = new JSONObject(EntityUtils.toString(Node.create(node).getEntity()));
			System.out.println(create.toString());

			// update node (id=1)
			JSONObject updateCalc = new JSONObject(EntityUtils.toString(Node.updateCalc(node).getEntity()));
			System.out.println(updateCalc.toString());
			
			JSONObject updateSpec = new JSONObject(EntityUtils.toString(Node.updateSpec(node).getEntity()));
			System.out.println(updateSpec.toString());
			
			JSONObject updatePoint = new JSONObject(EntityUtils.toString(Node.updateMapPoint(node).getEntity()));
			System.out.println(updatePoint.toString());
			
			// delete scheme (id=1)
			System.out.println(Node.delete(node));
		} catch (KeyManagementException | JSONException | ParseException | NoSuchAlgorithmException | KeyStoreException
				| IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}

}
