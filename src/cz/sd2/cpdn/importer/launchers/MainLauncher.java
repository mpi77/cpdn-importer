package cz.sd2.cpdn.importer.launchers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import cz.sd2.cpdn.importer.resources.MapPoint;
import cz.sd2.cpdn.importer.resources.Node;
import cz.sd2.cpdn.importer.resources.Permission;
import cz.sd2.cpdn.importer.resources.Scheme;
import cz.sd2.cpdn.importer.resources.Section;
import cz.sd2.cpdn.importer.utils.Connection;
import cz.sd2.cpdn.importer.utils.CsvImporter;
import cz.sd2.cpdn.importer.utils.SectionCrate;
import cz.sd2.cpdn.importer.utils.XmlImporter;
import cz.sd2.cpdn.importer.utils.NodeCrate;

public class MainLauncher {

	public static final String CLIENT_ID = "client_id";
	public static final String USER_NAME = "username";
	public static final String USER_PASSWORD = "password";
	public static final int JAVA_IMPORTER_PROFILE = 2;
	
	public static final int MODE_CSV = 1;
	public static final int MODE_XML = 2;

	public static void main(String[] args) {
		String fileName = "";
		int mode = 0;

		for (int i = 0; i < args.length; i++) {
			if (args[i].contains("/f:")) {
				fileName = args[i].substring(3);
			}
			if (args[i].contains("/m:")) {
				mode = Integer.parseInt(args[i].substring(3));
			}
		}
		System.out.println("File: " + fileName);
		System.out.printf("Mode: %s\n", (mode == MainLauncher.MODE_CSV ? "csv" : ((mode == MainLauncher.MODE_XML ? "xml" : ""))));
		System.out.println("-------------------------------------------");
		if(fileName.length() > 0 && (mode == MainLauncher.MODE_CSV || mode == MainLauncher.MODE_XML)){
			importFile(mode, fileName);
		} else {
			System.out.println("Invalid console input args. Required args are: /f:filename and /m:mode");
		}
		System.out.println("-------------------------------------------");
		System.out.println("End of import. Bye bye :)");
	}

	public static void importFile(int mode, String fileName) {
		String[] scheme = readHead(mode, fileName);
		NodeCrate[] nodes = readNodes(mode, fileName);
		int num_nodes = nodes.length;
		SectionCrate[] lines = readSections(mode, fileName);
		int num_lines = lines.length;
		if (num_nodes > 0 && num_lines > 0 && !scheme[0].trim().equals("")) {
			sendToAPI(scheme, nodes, lines);
		}
	}

	public static void sendToAPI(String[] lscheme, NodeCrate[] lnodes, SectionCrate[] lsections) {
		try {
			Connection.authenticate(MainLauncher.CLIENT_ID, MainLauncher.USER_NAME,	MainLauncher.USER_PASSWORD);
			System.out.println("-------------------------------------------");
			// create a new scheme
			Scheme scheme = new Scheme(null, lscheme[0], lscheme[1], 1, false);
			JSONObject schemeCreate = new JSONObject(EntityUtils.toString(Scheme.create(scheme).getEntity()));
			scheme.setId((schemeCreate.isNull("id") == false) ? schemeCreate.getInt("id") : null);
			System.out.println(scheme);

			// create a new permission
			Permission perm = new Permission(null, scheme.getId(), MainLauncher.JAVA_IMPORTER_PROFILE,
					Permission.MODE_READ_WRITE_EXECUTE, "2015-12-01 00:00:00", "2019-12-01 00:00:00");
			JSONObject create = new JSONObject(EntityUtils.toString(Permission.create(perm).getEntity()));
			System.out.println(create.toString());

			for (int loop = 0; loop < lnodes.length; loop++) {
				// create new mapPoints
				int lsx = 0;
				int lsy = 0;
				if (lnodes[loop].wsx != 0.0) {
					lsx = (int) lnodes[loop].wsx;
				}
				if (lnodes[loop].wsy != 0.0) {
					lsy = (int) lnodes[loop].wsy;
				}

				MapPoint point1 = new MapPoint(null, null, null, null, lsx, lsy, scheme.getId(), null);
				JSONObject point1Create = new JSONObject(EntityUtils.toString(MapPoint.create(point1).getEntity()));
				point1.setId((point1Create.isNull("id") == false) ? point1Create.getInt("id") : null);
				System.out.println(point1);

				// create new nodes
				String typ = "";
				switch (lnodes[loop].wtype) {
				case "O":
					typ = Node.TYPE_CONSUMPTION;
					break;
				case "N":
					typ = Node.TYPE_POWER;
					break;
				case "S":
					typ = Node.TYPE_SUPERIOR_SYSTEM;
					break;
				case "G":
					typ = Node.TYPE_TURBO_GEN;
					break;
				case "M":
					typ = Node.TYPE_HYDRO_GEN;
					break;
				}
				Map<String, Double> calc1 = Node.buildCalcMap(.0, .0, .0, .0, .0, .0);
				Map<String, Object> spec1 = Node.buildSpecMap(typ, lnodes[loop].wlabel, lnodes[loop].wfi, 0.0, 0.0, 0.0,
						lnodes[loop].wp, 0.0, lnodes[loop].ws, lnodes[loop].wq, lnodes[loop].wxq, lnodes[loop].wxd,
						lnodes[loop].whladu, lnodes[loop].wsfi, lnodes[loop].wug, lnodes[loop].wu);
				Node node1 = new Node(null, calc1, spec1, scheme.getId(), point1.getId());
				JSONObject createNode1 = new JSONObject(EntityUtils.toString(Node.create(node1).getEntity()));
				node1.setId((createNode1.isNull("id") == false) ? createNode1.getInt("id") : null);
				lnodes[loop].wdbid = node1.getId();

				System.out.println(node1);
				int interr = createNode1.getInt("code");
				if (interr != 201) {
					System.out.println(createNode1.toString());
					System.out.println(node1);
					break;
				}
			}

			for (int loop = 0; loop < lsections.length; loop++) {
				String typ = "";
				switch (lsections[loop].wtype) {
				case "V":
					typ = Section.TYPE_LINE;
					break;
				case "T":
					typ = Section.TYPE_TRANSFORMER;
					break;
				case "S":
					typ = Section.TYPE_SWITCH;
					break;
				case "U":
					typ = Section.TYPE_TRANSFORMER_W3;
					break;
				case "R":
					typ = Section.TYPE_REACTOR;
					break;
				}
				int node1 = 0;
				int node2 = 0;
				int node3 = 0;
				for (int iw = 0; iw < lnodes.length; iw++) {
					if (node1 == 0) {
						if (lnodes[iw].wid == lsections[loop].wNode1) {
							node1 = lnodes[iw].wdbid;
						}
					}
					if (node2 == 0) {
						if (lnodes[iw].wid == lsections[loop].wNode2) {
							node2 = lnodes[iw].wdbid;
						}
					}
					if (node3 == 0) {
						if (lnodes[iw].wid == lsections[loop].wNode3) {
							node3 = lnodes[iw].wdbid;
						}
					}
				}
				String status = Section.STATUS_ON;
				if (!lsections[loop].wstat) {
					status = Section.STATUS_OFF;
				}
				// create new sections
				Map<String, Integer> nodes;
				if (typ != Section.TYPE_TRANSFORMER_W3) {
					nodes = Section.buildNodesMap(node1, node2, null);
				} else {
					nodes = Section.buildNodesMap(node1, node2, node3);
				}
				Map<String, Double> calc = Section.buildCalcMap(.0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0, .0);
				Map<String, Object> spec = Section.buildSpecMap(typ, lsections[loop].wlabel, lsections[loop].wy, status,
						lsections[loop].wc, lsections[loop].wimax, lsections[loop].wio, 0.0, lsections[loop].wx, 0.0,
						lsections[loop].wr, lsections[loop].wpo, lsections[loop].wpk1, lsections[loop].wpk2, lsections[loop].wpk3,
						lsections[loop].ws1, lsections[loop].ws2, lsections[loop].ws3, lsections[loop].wup, lsections[loop].wunp,
						lsections[loop].wus, lsections[loop].wuns, lsections[loop].wut, lsections[loop].wunt, lsections[loop].wuk1,
						lsections[loop].wuk2, lsections[loop].wuk3);
				Section section1 = new Section(null, scheme.getId(), calc, nodes, spec);
				JSONObject createSection1 = new JSONObject(EntityUtils.toString(Section.create(section1).getEntity()));
				section1.setId((createSection1.isNull("id") == false) ? createSection1.getInt("id") : null);

				System.out.println(section1);
				int interr = createSection1.getInt("code");
				if (interr != 201) {
					System.out.println(createSection1.toString());
					System.out.println(section1);
					break;
				}
			}

		} catch (KeyManagementException | JSONException | ParseException | NoSuchAlgorithmException | KeyStoreException
				| IOException | AuthenticationException e) {
			e.printStackTrace();
		}
	}

	public static int getNumHead(int mode, String fileName) {
		int numHead = 0;
		String pom = "";
		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				while ((s = br.readLine()) != null) {
					System.out.println(s);
					switch (mode) {
					case MainLauncher.MODE_CSV:
						pom = s.substring(0, 1);
						if (pom.equals("A")) {
							numHead += 1;
						}
						break;
					case MainLauncher.MODE_XML:
						pom = s;
						if (pom.contains("<info")) {
							numHead += 1;
						}
						break;
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
			} finally {
			}
		}
		return numHead;
	}

	public static int getNumNodes(int mode, String fileName) {
		int numNodes = 0;
		String pom = "";
		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				while ((s = br.readLine()) != null) {
					switch (mode) {
					case MainLauncher.MODE_CSV:
						pom = s.substring(0, 1);
						if (pom.equals("B")) {
							numNodes += 1;
						}
						break;
					case MainLauncher.MODE_XML:
						pom = s;
						if (pom.contains("<consumption id=") || pom.contains("<power id=")
								|| pom.contains("<turbogen id=") || pom.contains("<hydrogen id=")
								|| pom.contains("<motor id=") || pom.contains("<superior-system id=")) {
							numNodes += 1;
						}
						break;
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
			} finally {
			}
		}
		return numNodes;
	}

	public static int getNumSections(int mode, String fileName) {
		int numSections = 0;
		String pom = "";
		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				while ((s = br.readLine()) != null) {
					switch (mode) {
					case 1:
						pom = s.substring(0, 1);
						if (pom.equals("C")) {
							numSections += 1;
						}
						break;
					case 2:
						pom = s;
						if (pom.contains("<line id=") || pom.contains("<transformer id=")
								|| pom.contains("<transformer-w3 id=") || pom.contains("<reactor id=")
								|| pom.contains("<switch id=")) {
							numSections += 1;
						}
						break;
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
			} finally {
			}
		}
		return numSections;
	}

	public static String[] readHead(int mode, String fileName) {
		int numHead = getNumHead(mode, fileName);
		String lscheme[] = new String[2];

		if (numHead > 0) {
			switch (mode) {
			case MainLauncher.MODE_CSV:
				lscheme = CsvImporter.readHead(fileName);
				break;
			case MainLauncher.MODE_XML:
				lscheme = XmlImporter.readHead(fileName);
				break;
			}
		}
		return lscheme;
	}

	public static NodeCrate[] readNodes(int mode, String fileName) {
		int numNodes = getNumNodes(mode, fileName);
		NodeCrate[] lnodes = null;
		if (numNodes > 0) {
			lnodes = new NodeCrate[numNodes];
			switch (mode) {
			case MainLauncher.MODE_CSV:
				lnodes = CsvImporter.readNodes(fileName, numNodes);
				break;
			case MainLauncher.MODE_XML:
				lnodes = XmlImporter.readNodes(fileName, numNodes);
				break;
			}
		}
		return lnodes;
	}

	public static SectionCrate[] readSections(int mode, String fileName) {
		int numSections = getNumSections(mode, fileName);
		SectionCrate[] lsections = null;
		if (numSections > 0) {
			lsections = new SectionCrate[numSections];
			switch (mode) {
			case MainLauncher.MODE_CSV:
				lsections = CsvImporter.readSections(fileName, numSections);
				break;
			case MainLauncher.MODE_XML:
				lsections = XmlImporter.readSections(fileName, numSections);
				break;
			}
		}
		return lsections;
	}

}
