package cz.sd2.cpdn.importer.utils;

import java.io.BufferedReader;
import java.io.FileReader;

public class CsvImporter {

	public static String[] readHead(String fileName) {
		String[] scheme = new String[2];
		String txtsch = "Scheme - ";
		String[] pom = new String[60];
		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				while ((s = br.readLine()) != null) {
					System.out.println(s);

					pom = decodeRecord(s);
					if (pom[0].equals("A")) {
						try {
							scheme[0] = pom[1];
							scheme[1] = txtsch.concat(pom[1]);
						} catch (Exception e) {
							System.err.println(e.getMessage().toString());
						}
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
			}
		}
		return scheme;
	}

	public static String[] decodeRecord(String record) {
		String[] pom = new String[100];
		int poc = 0;
		int iw;
		boolean ok = true;
		do {
			iw = record.indexOf(";", 0);
			if (iw > -1) {
				pom[poc] = record.substring(0, iw);
				record = record.substring(iw + 1);
			} else {
				pom[poc] = record;
				ok = false;
			}

			poc += 1;
		} while (ok);
		return pom;
	}

	public static NodeCrate[] readNodes(String fileName, int numNodes) {
		NodeCrate[] nodes = new NodeCrate[numNodes];
		String[] pom = new String[60];
		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				int i = 0;
				while ((s = br.readLine()) != null) {
					System.out.println(s);

					pom = decodeRecord(s);
					if (pom[0].equals("B")) {
						try {
							nodes[i] = new NodeCrate();
							if (!pom[2].trim().equals("")) {
								nodes[i].wid = Integer.parseInt(pom[2]);
							}
							nodes[i].wtype = pom[1];
							nodes[i].wstat = true;
							if (!pom[3].trim().equals("") && !pom[3].trim().equals("T") && !pom[3].trim().equals("A")) {
								nodes[i].wstat = false;
							}
							if (!pom[4].trim().equals("")) {
								nodes[i].wsx = Double.parseDouble(Tools.replaceChar(pom[4], ",", "."));
							}
							if (!pom[5].trim().equals("")) {
								nodes[i].wsy = Double.parseDouble(Tools.replaceChar(pom[5], ",", "."));
							}
							if (!pom[8].trim().equals("")) {
								nodes[i].wu = Double.parseDouble(Tools.replaceChar(pom[8], ",", "."));
							}
							if (!pom[9].trim().equals("")) {
								nodes[i].wfi = Double.parseDouble(Tools.replaceChar(pom[9], ",", "."));
							}
							if (!pom[10].trim().equals("")) {
								nodes[i].wp = Double.parseDouble(Tools.replaceChar(pom[10], ",", "."));
							}
							if (!pom[11].trim().equals("")) {
								nodes[i].wq = Double.parseDouble(Tools.replaceChar(pom[11], ",", "."));
							}
							if (!pom[12].trim().equals("")) {
								nodes[i].whladu = Double.parseDouble(Tools.replaceChar(pom[12], ",", "."));
							}
							if (!pom[13].trim().equals("")) {
								nodes[i].wug = Double.parseDouble(Tools.replaceChar(pom[13], ",", "."));
							}
							if (!pom[14].trim().equals("")) {
								nodes[i].wsfi = Double.parseDouble(Tools.replaceChar(pom[14], ",", "."));
							}
							if (!pom[16].trim().equals("")) {
								nodes[i].ws = Double.parseDouble(Tools.replaceChar(pom[16].trim(), ",", "."));
							}
							if (!pom[17].trim().equals("")) {
								nodes[i].wxd = Double.parseDouble(Tools.replaceChar(pom[17], ",", "."));
							}
							if (!pom[18].trim().equals("")) {
								nodes[i].wxq = Double.parseDouble(Tools.replaceChar(pom[18], ",", "."));
							}
							i += 1;
						} catch (Exception e) {
							System.err.println(e.getMessage().toString());
						}
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
			}
		}
		return nodes;
	}

	public static SectionCrate[] readSections(String fileName, int numSections) {
		SectionCrate[] sections = new SectionCrate[numSections];
		String[] pom = new String[60];
		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				int i = 0;
				while ((s = br.readLine()) != null) {
					System.out.println(s);

					pom = decodeRecord(s);
					if (pom[0].equals("C")) {
						try {
							sections[i] = new SectionCrate();
							if (!pom[2].trim().equals("")) {
								sections[i].wid = Integer.parseInt(pom[2]);
							}
							sections[i].wtype = pom[1];
							sections[i].wstat = true;
							if (!pom[3].trim().equals("")) {
								sections[i].wNode1 = Integer.parseInt(pom[3].trim());
							}
							if (!pom[4].trim().equals("")) {
								sections[i].wNode2 = Integer.parseInt(pom[4].trim());
							}
							if (!pom[5].trim().equals("")) {
								sections[i].wNode3 = Integer.parseInt(pom[5].trim());
							}
							if (!pom[6].trim().equals("") && !pom[6].trim().equals("T") && !pom[6].trim().equals("A")) {
								sections[i].wstat = false;
							}
							if (!pom[7].trim().equals("")) {
								sections[i].wr = Double.parseDouble(Tools.replaceChar(pom[7].trim(), ",", "."));
							}
							if (!pom[8].trim().equals("")) {
								sections[i].wx = Double.parseDouble(Tools.replaceChar(pom[8].trim(), ",", "."));
							}
							if (!pom[9].trim().equals("")) {
								sections[i].wy = Double.parseDouble(Tools.replaceChar(pom[9].trim(), ",", "."));
							}
							if (!pom[10].trim().equals("")) {
								sections[i].wc = Double.parseDouble(Tools.replaceChar(pom[10].trim(), ",", "."));
							}
							if (!pom[11].trim().equals("")) {
								sections[i].wunp = Double.parseDouble(Tools.replaceChar(pom[11].trim(), ",", "."));
							}
							if (!pom[12].trim().equals("")) {
								sections[i].wuns = Double.parseDouble(Tools.replaceChar(pom[12].trim(), ",", "."));
							}
							if (!pom[13].trim().equals("")) {
								sections[i].wunt = Double.parseDouble(Tools.replaceChar(pom[13].trim(), ",", "."));
							}
							if (!pom[14].trim().equals("")) {
								sections[i].wup = Double.parseDouble(Tools.replaceChar(pom[14].trim(), ",", "."));
							}
							if (!pom[15].trim().equals("")) {
								sections[i].wus = Double.parseDouble(Tools.replaceChar(pom[15].trim(), ",", "."));
							}
							if (!pom[16].trim().equals("")) {
								sections[i].wut = Double.parseDouble(Tools.replaceChar(pom[16].trim(), ",", "."));
							}
							if (!pom[17].trim().equals("")) {
								sections[i].wuk1 = Double.parseDouble(Tools.replaceChar(pom[17].trim(), ",", "."));
							}
							if (!pom[18].trim().equals("")) {
								sections[i].wuk2 = Double.parseDouble(Tools.replaceChar(pom[18].trim(), ",", "."));
							}
							if (!pom[19].trim().equals("")) {
								sections[i].wuk3 = Double.parseDouble(Tools.replaceChar(pom[19].trim(), ",", "."));
							}
							if (!pom[20].trim().equals("")) {
								sections[i].ws1 = Double.parseDouble(Tools.replaceChar(pom[20].trim(), ",", "."));
							}
							if (!pom[21].trim().equals("")) {
								sections[i].ws2 = Double.parseDouble(Tools.replaceChar(pom[21].trim(), ",", "."));
							}
							if (!pom[22].trim().equals("")) {
								sections[i].ws3 = Double.parseDouble(Tools.replaceChar(pom[22].trim(), ",", "."));
							}
							if (!pom[23].trim().equals("")) {
								sections[i].wpk1 = Double.parseDouble(Tools.replaceChar(pom[23].trim(), ",", "."));
							}
							if (!pom[24].trim().equals("")) {
								sections[i].wpk2 = Double.parseDouble(Tools.replaceChar(pom[24].trim(), ",", "."));
							}
							if (!pom[25].trim().equals("")) {
								sections[i].wpk3 = Double.parseDouble(Tools.replaceChar(pom[25].trim(), ",", "."));
							}
							if (!pom[26].trim().equals("")) {
								sections[i].wpo = Double.parseDouble(Tools.replaceChar(pom[26].trim(), ",", "."));
							}
							if (!pom[27].trim().equals("")) {
								sections[i].wio = Double.parseDouble(Tools.replaceChar(pom[27].trim(), ",", "."));
							}
							if (!pom[28].trim().equals("")) {
								sections[i].wimax = Double.parseDouble(Tools.replaceChar(pom[28].trim(), ",", "."));
							}
							i += 1;
						} catch (Exception e) {
							System.err.println(e.getMessage().toString());
						}
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
			}
		} 
		return sections;
	}
}
