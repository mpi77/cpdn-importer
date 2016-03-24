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

public class Section {
	
	public static final String TYPE_LINE = "line";
	public static final String TYPE_TRANSFORMER = "transformer";
	public static final String TYPE_TRANSFORMER_W3 = "transformerW3";
	public static final String TYPE_REACTOR = "reactor";
	public static final String TYPE_SWITCH = "switch";
	
	public static final String STATUS_ON = "on";
	public static final String STATUS_OFF = "off";
	public static final String STATUS_UNDEFINED = null;
	
	private Integer id;
	private Integer scheme;
	private Map<String, Double> calc;
	private Map<String, Integer> nodes;
	private Map<String, Object> spec;

	public Section(Integer id, Integer scheme, Map<String, Double> calc, Map<String, Integer> nodes,
			Map<String, Object> spec) {
		super();
		this.id = id;
		this.scheme = scheme;
		this.calc = calc;
		this.nodes = nodes;
		this.spec = spec;
	}
	
	public Section() {
		super();
		this.id = new Integer(0);
		this.scheme = new Integer(0);
		this.calc = new HashMap<String, Double>();
		this.nodes = new HashMap<String, Integer>();
		this.spec = new HashMap<String, Object>();
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

	public Map<String, Double> getCalc() {
		return calc;
	}

	public void setCalc(Map<String, Double> calc) {
		this.calc = calc;
	}

	public Map<String, Integer> getNodes() {
		return nodes;
	}

	public void setNodes(Map<String, Integer> nodes) {
		this.nodes = nodes;
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
		r.put("nodes", this.getJsonNodesBody());
		r.put("scheme", this.scheme);
		r.put("spec", this.getJsonSpecBody());
		return r.toString();
	}

	public JSONObject getJsonCalcBody() throws NullPointerException {
		JSONObject calc = new JSONObject();
		calc.put("current", (new JSONObject())
				.put("dst", (new JSONObject())
						.put("phase", (this.calc.get("current.dst.phase") != null) ? this.calc.get("current.dst.phase") : JSONObject.NULL)
						.put("value", (this.calc.get("current.dst.value") != null) ? this.calc.get("current.dst.value") : JSONObject.NULL)
						.put("ratio", (this.calc.get("current.dst.ratio") != null) ? this.calc.get("current.dst.ratio") : JSONObject.NULL))
				.put("src", (new JSONObject())
						.put("phase", (this.calc.get("current.src.phase") != null) ? this.calc.get("current.src.phase") : JSONObject.NULL)
						.put("value",(this.calc.get("current.src.value") != null) ? this.calc.get("current.src.value") : JSONObject.NULL)
						.put("ratio", (this.calc.get("current.src.ratio") != null) ? this.calc.get("current.src.ratio") : JSONObject.NULL)));
		calc.put("losses", (new JSONObject())
				.put("active", (this.calc.get("losses.active") != null) ? this.calc.get("losses.active") : JSONObject.NULL)
				.put("reactive", (this.calc.get("losses.reactive") != null) ? this.calc.get("losses.reactive") : JSONObject.NULL));
		calc.put("power", (new JSONObject())
				.put("dst", (new JSONObject())
						.put("active", (this.calc.get("power.dst.active") != null) ? this.calc.get("power.dst.active") : JSONObject.NULL)
						.put("reactive", (this.calc.get("power.dst.reactive") != null) ? this.calc.get("power.dst.reactive") : JSONObject.NULL))
				.put("src", (new JSONObject())
						.put("active", (this.calc.get("power.src.active") != null) ? this.calc.get("power.src.active") : JSONObject.NULL)
						.put("reactive",  (this.calc.get("power.src.reactive") != null) ? this.calc.get("power.src.reactive") : JSONObject.NULL)));
		return calc;
	}
	
	public JSONObject getJsonSpecBody() throws NullPointerException {
		JSONObject spec = new JSONObject();
		spec.put("type", this.spec.get("type"));
		spec.put("label", (this.spec.get("label") != null) ? this.spec.get("label") : JSONObject.NULL);
		spec.put("conductance", (this.spec.get("conductance") != null) ? this.spec.get("conductance") : JSONObject.NULL);
		spec.put("status", (this.spec.get("status") != null) ? this.spec.get("status") : JSONObject.NULL);
		spec.put("susceptance", (this.spec.get("susceptance") != null) ? this.spec.get("susceptance") : JSONObject.NULL);
		spec.put("current", (new JSONObject())
				.put("max", (this.spec.get("current.max") != null) ? this.spec.get("current.max") : JSONObject.NULL)
				.put("noLoad", (this.spec.get("current.noLoad") != null) ? this.spec.get("current.noLoad") : JSONObject.NULL));
		spec.put("reactance", (new JSONObject())
				.put("ratio", (this.spec.get("reactance.ratio") != null) ? this.spec.get("reactance.ratio") : JSONObject.NULL)
				.put("value", (this.spec.get("reactance.value") != null) ? this.spec.get("reactance.value") : JSONObject.NULL));
		spec.put("resistance", (new JSONObject())
				.put("ratio", (this.spec.get("resistance.ratio") != null) ? this.spec.get("resistance.ratio") : JSONObject.NULL)
				.put("value", (this.spec.get("resistance.value") != null) ? this.spec.get("resistance.value") : JSONObject.NULL));
		spec.put("losses", (new JSONObject())
				.put("noLoad", (this.spec.get("losses.noLoad") != null) ? this.spec.get("losses.noLoad") : JSONObject.NULL)
				.put("short", (new JSONObject())
						.put("ab", (this.spec.get("losses.short.ab") != null) ? this.spec.get("losses.short.ab") : JSONObject.NULL)
						.put("ac", (this.spec.get("losses.short.ac") != null) ? this.spec.get("losses.short.ac") : JSONObject.NULL)
						.put("bc", (this.spec.get("losses.short.bc") != null) ? this.spec.get("losses.short.bc") : JSONObject.NULL)));
		spec.put("power", (new JSONObject())
				.put("rated", (new JSONObject())
						.put("ab", (this.spec.get("power.rated.ab") != null) ? this.spec.get("power.rated.ab") : JSONObject.NULL)
						.put("ac", (this.spec.get("power.rated.ac") != null) ? this.spec.get("power.rated.ac") : JSONObject.NULL)
						.put("bc", (this.spec.get("power.rated.bc") != null) ? this.spec.get("power.rated.bc") : JSONObject.NULL)));
		spec.put("voltage", (new JSONObject())
				.put("pri", (new JSONObject())
						.put("actual", (this.spec.get("voltage.pri.actual") != null) ? this.spec.get("voltage.pri.actual") : JSONObject.NULL)
						.put("rated", (this.spec.get("voltage.pri.rated") != null) ? this.spec.get("voltage.pri.rated") : JSONObject.NULL))
				.put("sec", (new JSONObject())
						.put("actual", (this.spec.get("voltage.sec.actual") != null) ? this.spec.get("voltage.sec.actual") : JSONObject.NULL)
						.put("rated", (this.spec.get("voltage.sec.rated") != null) ? this.spec.get("voltage.sec.rated") : JSONObject.NULL))
				.put("trc", (new JSONObject())
						.put("actual", (this.spec.get("voltage.trc.actual") != null) ? this.spec.get("voltage.trc.actual") : JSONObject.NULL)
						.put("rated", (this.spec.get("voltage.trc.rated") != null) ? this.spec.get("voltage.trc.rated") : JSONObject.NULL))
				.put("short", (new JSONObject())
						.put("ab", (this.spec.get("voltage.short.ab") != null) ? this.spec.get("voltage.short.ab") : JSONObject.NULL)
						.put("ac", (this.spec.get("voltage.short.ac") != null) ? this.spec.get("voltage.short.ac") : JSONObject.NULL)
						.put("bc", (this.spec.get("voltage.short.bc") != null) ? this.spec.get("voltage.short.bc") : JSONObject.NULL)));
		return spec;
	}
	
	public JSONObject getJsonSchemeBody() {
		JSONObject r = new JSONObject();
		r.put("scheme", this.scheme);
		return r;
	}
	
	public JSONObject getJsonNodesBody() throws NullPointerException{
		JSONObject nodes = new JSONObject();
		nodes.put("dst", this.nodes.get("dst"));
		nodes.put("src", this.nodes.get("src"));
		nodes.put("trc", (this.nodes.get("trc") != null) ? this.nodes.get("trc") : JSONObject.NULL);
		return nodes;
	}
	
	public static HttpResponse getAll(int schemeId, int pageSize) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%ssections/?q=(schemeId=%d)&e=(section,section.scheme,section.nodes,section.spec,section.calc)&pageSize=%d", Connection.API_V1_GW_URL, schemeId, pageSize));
	}
	
	public static HttpResponse get(int id) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.get(String.format("%ssections/%d/?e=(nodes,spec,calc)", Connection.API_V1_GW_URL, id));
	}
	
	public static HttpResponse create(Section section) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%ssections/", Connection.API_V1_GW_URL),
			new StringEntity(section.createJsonBody()));
	}
	
	public static HttpResponse updateCalc(Section section) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%ssections/%d/calc", Connection.API_V1_GW_URL, section.id),
				new StringEntity(section.getJsonCalcBody().toString()));
	}
	
	public static HttpResponse updateSpec(Section section) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%ssections/%d/spec", Connection.API_V1_GW_URL, section.id),
				new StringEntity(section.getJsonSpecBody().toString()));
	}
	
	public static HttpResponse updateNodes(Section section) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.post(String.format("%ssections/%d/nodes", Connection.API_V1_GW_URL, section.id),
				new StringEntity(section.getJsonNodesBody().toString()));
	}
	
	public static HttpResponse delete(Section section) throws KeyManagementException, JSONException, ParseException,
		ClientProtocolException, NoSuchAlgorithmException, KeyStoreException, IOException, AuthenticationException {
		return Connection.delete(String.format("%ssections/%d/", Connection.API_V1_GW_URL, section.id));
	}
	
	public static Section buildSection(String json) throws JSONException{
		JSONObject s = new JSONObject(json);
		Integer id = new Integer(((JSONObject) s.get("_meta")).getInt("id"));
		Integer scheme = new Integer(((JSONObject)((JSONObject) s.get("scheme")).get("_meta")).getInt("id"));
		JSONObject nodesJs = new JSONObject(s.get("nodes").toString());
		JSONObject calcJs = new JSONObject(s.get("calc").toString());
		JSONObject calcCurrent = (JSONObject) calcJs.get("current");
		JSONObject calcPower = (JSONObject) calcJs.get("power");
		JSONObject calcLosses = (JSONObject) calcJs.get("losses");
		
		JSONObject specJs = new JSONObject(s.get("spec").toString());
		JSONObject specCurrent = (JSONObject) specJs.get("current");
		JSONObject specReactance = (JSONObject) specJs.get("reactance");
		JSONObject specResistance = (JSONObject) specJs.get("resistance");
		JSONObject specLosses = (JSONObject) specJs.get("losses");
		JSONObject specPower = (JSONObject) specJs.get("power");
		JSONObject specVoltage = (JSONObject) specJs.get("voltage");
		
		Map<String, Integer> nodes = Section.buildNodesMap(
				((JSONObject)((JSONObject) nodesJs.get("dst")).get("_meta")).getInt("id"), 
				((JSONObject)((JSONObject) nodesJs.get("src")).get("_meta")).getInt("id"), 
				(nodesJs.isNull("trc") == false) ? ((JSONObject)((JSONObject) nodesJs.get("trc")).get("_meta")).getInt("id") : null);
		
		Map<String, Double> calc = Section.buildCalcMap(
				(((JSONObject) calcCurrent.get("dst")).isNull("phase") == false) ? ((JSONObject) calcCurrent.get("dst")).getDouble("phase") : null, 
				(((JSONObject) calcCurrent.get("dst")).isNull("value") == false) ? ((JSONObject) calcCurrent.get("dst")).getDouble("value") : null, 
				(((JSONObject) calcCurrent.get("dst")).isNull("ratio") == false) ? ((JSONObject) calcCurrent.get("dst")).getDouble("ratio") : null, 
				(((JSONObject) calcCurrent.get("src")).isNull("phase") == false) ? ((JSONObject) calcCurrent.get("src")).getDouble("phase") : null, 
				(((JSONObject) calcCurrent.get("src")).isNull("value") == false) ? ((JSONObject) calcCurrent.get("src")).getDouble("value") : null, 
				(((JSONObject) calcCurrent.get("src")).isNull("ratio") == false) ? ((JSONObject) calcCurrent.get("src")).getDouble("ratio") : null, 
				(calcLosses.isNull("active") == false) ? calcLosses.getDouble("active") : null, 
				(calcLosses.isNull("reactive") == false) ? calcLosses.getDouble("reactive") : null, 
				(((JSONObject) calcPower.get("dst")).isNull("active") == false) ? ((JSONObject) calcPower.get("dst")).getDouble("active") : null, 
				(((JSONObject) calcPower.get("dst")).isNull("reactive") == false) ? ((JSONObject) calcPower.get("dst")).getDouble("reactive") : null, 
				(((JSONObject) calcPower.get("src")).isNull("active") == false) ? ((JSONObject) calcPower.get("src")).getDouble("active") : null, 
				(((JSONObject) calcPower.get("src")).isNull("reactive") == false) ? ((JSONObject) calcPower.get("src")).getDouble("reactive") : null);
		
		Map<String, Object> spec = Section.buildSpecMap(
				specJs.getString("type"),
				(specJs.isNull("label") == false) ? specJs.getString("label") : null, 
				(specJs.isNull("conductance") == false) ? specJs.getDouble("conductance") : null, 
				(specJs.isNull("status") == false) ? specJs.getString("status") : null, 
				(specJs.isNull("susceptance") == false) ? specJs.getDouble("susceptance") : null, 
				(specCurrent.isNull("max") == false) ? specCurrent.getDouble("max") : null, 
				(specCurrent.isNull("noLoad") == false) ? specCurrent.getDouble("noLoad") : null, 
				(specReactance.isNull("ratio") == false) ? specReactance.getDouble("ratio") : null, 
				(specReactance.isNull("value") == false) ? specReactance.getDouble("value") : null, 
				(specResistance.isNull("ratio") == false) ? specResistance.getDouble("ratio") : null, 
				(specResistance.isNull("value") == false) ? specResistance.getDouble("value") : null, 
				(specLosses.isNull("noLoad") == false) ? specLosses.getDouble("noLoad") : null, 
				(((JSONObject) specLosses.get("short")).isNull("ab") == false) ? ((JSONObject) specLosses.get("short")).getDouble("ab") : null, 
				(((JSONObject) specLosses.get("short")).isNull("ac") == false) ? ((JSONObject) specLosses.get("short")).getDouble("ac") : null, 
				(((JSONObject) specLosses.get("short")).isNull("bc") == false) ? ((JSONObject) specLosses.get("short")).getDouble("bc") : null, 
				(((JSONObject) specPower.get("rated")).isNull("ab") == false) ? ((JSONObject) specPower.get("rated")).getDouble("ab") : null, 
				(((JSONObject) specPower.get("rated")).isNull("ac") == false) ? ((JSONObject) specPower.get("rated")).getDouble("ac") : null, 
				(((JSONObject) specPower.get("rated")).isNull("bc") == false) ? ((JSONObject) specPower.get("rated")).getDouble("bc") : null, 
				(((JSONObject) specVoltage.get("pri")).isNull("actual") == false) ? ((JSONObject) specVoltage.get("pri")).getDouble("actual") : null, 
				(((JSONObject) specVoltage.get("pri")).isNull("rated") == false) ? ((JSONObject) specVoltage.get("pri")).getDouble("rated") : null, 
				(((JSONObject) specVoltage.get("sec")).isNull("actual") == false) ? ((JSONObject) specVoltage.get("sec")).getDouble("actual") : null, 
				(((JSONObject) specVoltage.get("sec")).isNull("rated") == false) ? ((JSONObject) specVoltage.get("sec")).getDouble("rated") : null, 
				(((JSONObject) specVoltage.get("trc")).isNull("actual") == false) ? ((JSONObject) specVoltage.get("trc")).getDouble("actual") : null, 
				(((JSONObject) specVoltage.get("trc")).isNull("rated") == false) ? ((JSONObject) specVoltage.get("trc")).getDouble("rated") : null, 
				(((JSONObject) specVoltage.get("short")).isNull("ab") == false) ? ((JSONObject) specVoltage.get("short")).getDouble("ab") : null, 
				(((JSONObject) specVoltage.get("short")).isNull("ac") == false) ? ((JSONObject) specVoltage.get("short")).getDouble("ac") : null, 
				(((JSONObject) specVoltage.get("short")).isNull("bc") == false) ? ((JSONObject) specVoltage.get("short")).getDouble("bc") : null);
		
		return new Section(id, scheme, calc, nodes, spec);
	}
	public static List<Section> buildSections(String json) throws JSONException{
		List<Section> r = new ArrayList<Section>();
		JSONObject root = new JSONObject(json);
		JSONArray a = new JSONArray(root.get("items").toString());
		for (int i = 0; i < a.length(); i++) {
			r.add(Section.buildSection(a.get(i).toString()));
		}
		return r;
	}
	
	public static Map<String, Double> buildCalcMap(Double currentDstPhase, Double currentDstValue, 
			Double currentDstRatio, Double currentSrcPhase, Double currentSrcValue, Double currentSrcRatio, 
			Double lossesActive, Double lossesReactive, Double powerDstActive, 
			Double powerDstReactive, Double powerSrcActive, Double powerSrcReactive) {
		Map<String, Double> calc = new HashMap<String, Double>();
		calc.put("current.dst.phase", currentDstPhase);
		calc.put("current.dst.value", currentDstValue);
		calc.put("current.dst.ratio", currentDstRatio);
		calc.put("current.src.phase", currentSrcPhase);
		calc.put("current.src.value", currentSrcValue);
		calc.put("current.src.ratio", currentSrcRatio);
		calc.put("losses.active", lossesActive);
		calc.put("losses.reactive", lossesReactive);
		calc.put("power.dst.active", powerDstActive);
		calc.put("power.dst.reactive", powerDstReactive);
		calc.put("power.src.active", powerSrcActive);
		calc.put("power.src.reactive", powerSrcReactive);
		return calc;
	}
	
	public static Map<String, Integer> buildNodesMap(Integer dst, Integer src, Integer trc) {
		Map<String, Integer> nodes = new HashMap<String, Integer>();
		nodes.put("dst", dst);
		nodes.put("src", src);
		nodes.put("trc", trc);
		return nodes;
	}
	
	public static Map<String, Object> buildSpecMap(String type, String label, Double conductance, String status, 
			Double susceptance, Double currentMax, Double currentNoLoad, Double reactanceRatio, Double reactanceValue, Double resistanceRatio, 
			Double resistanceValue, Double lossesNoLoad, Double lossesShortAb, Double lossesShortAc, 
			Double lossesShortBc, Double powerRatedAb, Double powerRatedAc, Double powerRatedBc, 
			Double voltagePriActual, Double voltagePriRated, Double voltageSecActual, Double voltageSecRated, 
			Double voltageTrcActual, Double voltageTrcRated, Double voltageShortAb, Double voltageShortAc, 
			Double voltageShortBc) throws IllegalArgumentException{
		if(Section.isValidSpecType(type) != true){
			throw new IllegalArgumentException("Section/spec requires valid type argument.");
		}
		Map<String, Object> spec = new HashMap<String, Object>();
		spec.put("type", type);
		spec.put("label", label);
		spec.put("conductance", conductance);
		spec.put("status", status);
		spec.put("susceptance", susceptance);
		spec.put("current.max", currentMax);
		spec.put("current.noLoad", currentNoLoad);
		spec.put("reactance.ratio", reactanceRatio);
		spec.put("reactance.value", reactanceValue);
		spec.put("resistance.ratio", resistanceRatio);
		spec.put("resistance.value", resistanceValue);
		spec.put("losses.noLoad", lossesNoLoad);
		spec.put("losses.short.ab", lossesShortAb);
		spec.put("losses.short.ac", lossesShortAc);
		spec.put("losses.short.bc", lossesShortBc);
		spec.put("power.rated.ab", powerRatedAb);
		spec.put("power.rated.ac", powerRatedAc);
		spec.put("power.rated.bc", powerRatedBc);
		spec.put("voltage.pri.actual", voltagePriActual);
		spec.put("voltage.pri.rated", voltagePriRated);
		spec.put("voltage.sec.actual", voltageSecActual);
		spec.put("voltage.sec.rated", voltageSecRated);
		spec.put("voltage.trc.actual", voltageTrcActual);
		spec.put("voltage.trc.rated", voltageTrcRated);
		spec.put("voltage.short.ab", voltageShortAb);
		spec.put("voltage.short.ac", voltageShortAc);
		spec.put("voltage.short.bc", voltageShortBc);
		return spec;
	}
	
	public static boolean isValidSpecType(String type){
		return (type.equals(Section.TYPE_LINE) || type.equals(Section.TYPE_REACTOR)
				|| type.equals(Section.TYPE_SWITCH) || type.equals(Section.TYPE_TRANSFORMER)
				|| type.equals(Section.TYPE_TRANSFORMER_W3));
	}
	
	public static void main(String[] args) {
		try {
			/*
			 * You need to setup correct clientId, userName, userPassword into Connection.authenticate(...).
			 */
			System.exit(0);
			
			Connection.authenticate("demoClient", "demoUser", "demoUserPasswd");

			// get all sections
			System.out.println(Arrays.toString(Section.buildSections(EntityUtils.toString(Section.getAll(1, 100).getEntity())).toArray()));

			// get section (id=1)
			System.out.println(Section.buildSection(EntityUtils.toString(Section.get(1).getEntity())).toString());

			Map<String, Double> calc = Section.buildCalcMap(.0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0);
			Map<String, Object> spec = Section.buildSpecMap(Section.TYPE_LINE, "label", null, Section.STATUS_UNDEFINED, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0);
			Map<String, Integer> nodes = Section.buildNodesMap(1, 1, 1);
			
			Section section = new Section(1, 1, calc, nodes, spec);
			
			// create a new section
			JSONObject create = new JSONObject(EntityUtils.toString(Section.create(section).getEntity()));
			System.out.println(create.toString());

			// update section (id=1)
			JSONObject updateCalc = new JSONObject(EntityUtils.toString(Section.updateCalc(section).getEntity()));
			System.out.println(updateCalc.toString());
			
			JSONObject updateSpec = new JSONObject(EntityUtils.toString(Section.updateSpec(section).getEntity()));
			System.out.println(updateSpec.toString());
			
			JSONObject updateNodes = new JSONObject(EntityUtils.toString(Section.updateNodes(section).getEntity()));
			System.out.println(updateNodes.toString());
			
			// delete section (id=1)
			System.out.println(Section.delete(section));
		} catch (KeyManagementException | JSONException | ParseException | NoSuchAlgorithmException | KeyStoreException
				| IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Section [id=" + id + ", scheme=" + scheme + ", calc=" + calc + ", nodes=" + nodes + ", spec=" + spec
				+ "]";
	}

}
