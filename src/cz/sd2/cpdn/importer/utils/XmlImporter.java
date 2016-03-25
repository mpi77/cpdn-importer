package cz.sd2.cpdn.importer.utils;

import java.io.BufferedReader;
import java.io.FileReader;

public class XmlImporter {

	public static String[] readHead(String fileName) {
		String[] pom = new String[2];
		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				boolean okread = false;
				while ((s = br.readLine()) != null) {
					System.out.println(s);

					if (!okread && s.contains("<info>")) {
						okread = true;
					}
					if (okread) {
						if (s.contains("<name>")) {
							int uka1 = s.indexOf("<name>");
							int uka2 = s.indexOf("</name>");
							pom[0] = s.substring(uka1 + 6, uka2);
						}
						if (s.contains("<description>")) {
							int uka1 = s.indexOf("<description>");
							int uka2 = s.indexOf("</description>");
							pom[1] = s.substring(uka1 + 13, uka2);
						}
					}
					if (okread && s.contains("</info>")) {
						okread = false;
						break;
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
			}
		} else {
		}
		return pom;
	}

	public static String decodeTag(String tag, String txtrec) {
		String res = "";
		if (txtrec.contains(tag)) {
			int uka1 = txtrec.indexOf(tag);
			int uka2 = txtrec.indexOf(tag.substring(0, 1).concat("/").concat(tag.substring(1)));
			res = txtrec.substring(uka1 + tag.length(), uka2);
		}
		return res;
	}

	public static NodeCrate[] readNodes(String fileName, int numNodes) {
		NodeCrate[] nodes = new NodeCrate[numNodes];
		int i = 0;
		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				boolean okread = false;
				boolean oknode = false;
				boolean okmap = false;
				boolean okpower = false;
				boolean okreact = false;
				boolean okvoltage = false;
				String txtdbl = "";
				while ((s = br.readLine()) != null) {
					System.out.println(s);

					if (!okread && s.contains("<nodes>")) {
						okread = true;
					}
					if (okread) {
						if (!oknode && (s.contains("<consumption id=") || s.contains("<power id=")
								|| s.contains("<turbogen id=") || s.contains("<hydrogen id=")
								|| s.contains("<motor id=") || s.contains("<superior-system id="))) {
							oknode = true;
							nodes[i] = new NodeCrate();
							if (s.contains("id=")) {
								String txtint = s.substring(s.indexOf("id=") + 4, s.length() - 2);
								if (!txtint.trim().equals("")) {
									nodes[i].wid = Integer.parseInt(txtint);
								}
							}
							if (s.contains("consumption")) {
								nodes[i].wtype = "O";
							}
							if (s.contains("power")) {
								nodes[i].wtype = "N";
							}
							if (s.contains("turbogen")) {
								nodes[i].wtype = "G";
							}
							if (s.contains("hydrogen")) {
								nodes[i].wtype = "H";
							}
							if (s.contains("motor")) {
								nodes[i].wtype = "M";
							}
							if (s.contains("superior-system")) {
								nodes[i].wtype = "S";
							}
						}
						if (!okpower && (s.contains("</consumption>") || s.contains("</power>")
								|| s.contains("</turbogen>") || s.contains("</hydrogen>") || s.contains("</motor>")
								|| s.contains("</superior-system>"))) {
							oknode = false;
							i++;
						}
						if (oknode) {
							if (s.contains("<label>")) {
								nodes[i].wlabel = decodeTag("<label>", s);
							}
							if (!okmap && s.contains("<map>")) {
								okmap = true;
							}
							if (okmap) {
								if (s.contains("<x>")) {
									txtdbl = decodeTag("<x>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wsx = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<y>")) {
									txtdbl = decodeTag("<y>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wsy = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("</map>")) {
									okmap = false;
								}
							}
							if (!okpower && s.contains("<power>")) {
								okpower = true;
							}
							if (okpower) {
								if (s.contains("<active>")) {
									txtdbl = decodeTag("<active>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wp = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<reactive>")) {
									txtdbl = decodeTag("<reactive>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wq = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<installed>")) {
									txtdbl = decodeTag("<installed>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].ws = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<rated>")) {
									txtdbl = decodeTag("<rated>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].ws = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("</power>")) {
									okpower = false;
								}
							}
							if (!okreact && s.contains("<reactance>")) {
								okreact = true;
							}
							if (okreact) {
								if (s.contains("<lang>")) {
									txtdbl = decodeTag("<lang>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wxd = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<long>")) {
									txtdbl = decodeTag("<long>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wxq = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("</reactance>")) {
									okpower = false;
								}
							}
							if (!okvoltage && s.contains("<voltage>")) {
								okvoltage = true;
							}
							if (okvoltage) {
								if (s.contains("<level>")) {
									txtdbl = decodeTag("<level>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].whladu = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<value>")) {
									txtdbl = decodeTag("<value>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wu = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<phase>")) {
									txtdbl = decodeTag("<phase>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wfi = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<rated>")) {
									txtdbl = decodeTag("<rated>", s);
									if (!txtdbl.trim().equals("")) {
										nodes[i].wug = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("</voltage>")) {
									okvoltage = false;
								}
							}
						}
					}
					if (okread && s.contains("</nodes>")) {
						okread = false;
						break;
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
		int i = 0;

		if (fileName != "") {
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String s;
				boolean okread = false;
				boolean okline = false;
				boolean okspec = false;
				boolean okrea = false;
				boolean okres = false;
				boolean okcurr = false;
				boolean okvoltage = false;
				boolean okvolt1 = false;
				boolean okvolt2 = false;
				boolean okvolt3 = false;
				boolean okloss = false;
				boolean okpower = false;
				String txtdbl = "";
				String txtint = "";
				while ((s = br.readLine()) != null) {
					System.out.println(s);

					if (!okread && s.contains("<sections>")) {
						okread = true;
					}
					if (okread) {
						if (!okline && (s.contains("<line") || s.contains("<transformer")
								|| s.contains("<transformer-w3") || s.contains("<reactor") || s.contains("<switch"))) {
							okline = true;
							sections[i] = new SectionCrate();
							if (s.contains("id=")) {
								txtint = s.substring(s.indexOf("id=") + 4, s.length() - 2);
								if (!txtint.trim().equals("")) {
									sections[i].wid = Integer.parseInt(txtint);
								}
							}
							if (s.contains("line")) {
								sections[i].wtype = "V";
							}
							if (s.contains("transformer")) {
								sections[i].wtype = "T";
							}
							if (s.contains("transformer-w3")) {
								sections[i].wtype = "U";
							}
							if (s.contains("reactor")) {
								sections[i].wtype = "R";
							}
							if (s.contains("switch")) {
								sections[i].wtype = "S";
							}
							sections[i].wstat = true;
						}
						if (okline) {
							if (s.contains("<node-dst>")) {
								txtint = decodeTag("<node-dst>", s);
								if (!txtint.trim().equals("")) {
									sections[i].wNode2 = Integer.parseInt(txtint);
								}
							}
							if (s.contains("<node-src>")) {
								txtint = decodeTag("<node-src>", s);
								if (!txtint.trim().equals("")) {
									sections[i].wNode1 = Integer.parseInt(txtint);
								}
							}
							if (s.contains("<node-trc>")) {
								txtint = decodeTag("<node-trc>", s);
								if (!txtint.trim().equals("")) {
									sections[i].wNode3 = Integer.parseInt(txtint);
								}
							}
							if (!okspec && s.contains("<spec>")) {
								okspec = true;
							}
							if (okspec) {
								if (s.contains("<susceptance>")) {
									txtdbl = decodeTag("<susceptance>", s);
									if (!txtdbl.trim().equals("")) {
										sections[i].wc = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<conductance>")) {
									txtdbl = decodeTag("<conductance>", s);
									if (!txtdbl.trim().equals("")) {
										sections[i].wy = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (s.contains("<status>")) {
									txtint = decodeTag("<status>", s);
									if (!txtint.trim().equals("T") && !txtint.trim().equals("A")
											&& !txtint.trim().equals("1")) {
										sections[i].wstat = false;
									}
								}
								if (!okcurr && s.contains("<current>")) {
									okcurr = true;
								}
								if (okcurr) {
									if (s.contains("<max>")) {
										txtdbl = decodeTag("<max>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wimax = Double
													.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<noload")) {
										txtdbl = decodeTag("<noload>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wio = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
								}
								if (s.contains("</current>")) {
									okcurr = false;
								}
								if (!okloss && s.contains("<losses>")) {
									okloss = true;
								}
								if (okloss) {
									if (s.contains("<short>")) {
										txtdbl = decodeTag("<short>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wpk1 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<short_ab>")) {
										txtdbl = decodeTag("<short_ab>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wpk1 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<short_ac>")) {
										txtdbl = decodeTag("<short_ac>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wpk2 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<short_bc>")) {
										txtdbl = decodeTag("<short_bc>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wpk3 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<noload")) {
										txtdbl = decodeTag("<noload>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wpo = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("</losses>")) {
										okloss = false;
									}
								}
								if (!okpower && s.contains("<power>")) {
									okpower = true;
								}
								if (okpower) {
									if (s.contains("<rated>")) {
										txtdbl = decodeTag("<rated>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].ws1 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<rated_ab>")) {
										txtdbl = decodeTag("<rated_ab>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].ws1 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<rated_ac>")) {
										txtdbl = decodeTag("<rated_ac>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].ws2 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<rated_bc>")) {
										txtdbl = decodeTag("<rated_bc>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].ws3 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<noload")) {
										txtdbl = decodeTag("<noload>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wpo = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("</power>")) {
										okpower = false;
									}
								}
								if (!okres && s.contains("<resistance>")) {
									okres = true;
								}
								if (okres) {
									if (s.contains("<value>")) {
										txtdbl = decodeTag("<value>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wr = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
								}
								if (s.contains("</resistance>")) {
									okres = false;
								}
								if (!okrea && s.contains("<reactance>")) {
									okrea = true;
								}
								if (okrea) {
									if (s.contains("<value>")) {
										txtdbl = decodeTag("<value>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wx = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
								}
								if (s.contains("</reactance>")) {
									okrea = false;
								}
							}
							if (!okvoltage && s.contains("<voltage>")) {
								okvoltage = true;
							}
							if (okvoltage) {
								if (s.contains("<short>")) {
									txtdbl = decodeTag("<short>", s);
									if (!txtdbl.trim().equals("")) {
										sections[i].wuk1 = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
									}
								}
								if (!okvolt1 && s.contains("<primary>")) {
									okvolt1 = true;
								}
								if (okvolt1) {
									if (s.contains("<rated>")) {
										txtdbl = decodeTag("<rated>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wunp = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<actual>")) {
										txtdbl = decodeTag("<actual>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wup = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("</primary>")) {
										okvolt1 = false;
									}
								}
								if (!okvolt2 && s.contains("<secondary>")) {
									okvolt2 = true;
								}
								if (okvolt2) {
									if (s.contains("<rated>")) {
										txtdbl = decodeTag("<rated>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wuns = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<actual>")) {
										txtdbl = decodeTag("<actual>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wus = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("</secondary>")) {
										okvolt2 = false;
									}
								}
								if (!okvolt3 && s.contains("<tercial>")) {
									okvolt3 = true;
								}
								if (okvolt3) {
									if (s.contains("<rated>")) {
										txtdbl = decodeTag("<rated>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wunt = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("<actual>")) {
										txtdbl = decodeTag("<actual>", s);
										if (!txtdbl.trim().equals("")) {
											sections[i].wut = Double.parseDouble(Tools.replaceChar(txtdbl, ",", "."));
										}
									}
									if (s.contains("</tercial>")) {
										okvolt3 = false;
									}
								}
							}
							if (s.contains("</voltage>")) {
								okvoltage = false;
							}
							if (s.contains("</spec>")) {
								okspec = false;
							}
						}
						if (s.contains("</line>") || s.contains("</transformer>") || s.contains("</transformer-w3>")
								|| s.contains("</reactor>") || s.contains("</switch>")) {
							okline = false;
							i++;
						}
					}
					if (okread && s.contains("</sections>")) {
						okread = false;
						break;
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage().toString());
			}
		}
		return sections;
	}
}
