package cz.sd2.cpdn.importer.utils;

public class SectionCrate {
	public int wid;
	public int wNode1;
	public int wNode2;
	public int wNode3;
	public String wtype;
	public String wlabel;
	public boolean wstat;
	public double wr;
	public double wx;
	public double wc;
	public double wy;
	public double ws1;
	public double ws2;
	public double ws3;
	public double wuk1;
	public double wuk2;
	public double wuk3;
	public double wpk1;
	public double wpk2;
	public double wpk3;
	public double wio;
	public double wpo;
	public double wup;
	public double wus;
	public double wut;
	public double wunp;
	public double wuns;
	public double wunt;
	public double wimax;

	public SectionCrate() {
		wid = 0;
		wNode1 = 0;
		wNode2 = 0;
		wNode3 = 0;
		wr = 0.0;
		wx = 0.0;
		wc = 0.0;
		wy = 0.0;
		wtype = "";
		ws1 = 0.0;
		ws2 = 0.0;
		ws3 = 0.0;
		wuk1 = 0.0;
		wuk2 = 0.0;
		wuk3 = 0.0;
		wimax = 0.0;
		wpo = 0.0;
		wlabel = "";
		wup = 0.0;
		wus = 0.0;
		wut = 0.0;
		wunp = 0.0;
		wuns = 0.0;
		wunt = 0.0;
		wstat = true;
		wio = 0.0;
		wpk1 = 0.0;
		wpk2 = 0.0;
		wpk3 = 0.0;
	}

	public SectionCrate(int lid, int lid1, int lid2, int lid3, String ltyp, double lr, double lx, double lc, double ly,
			double ls1, double ls2, double ls3, double luk1, double luk2, double luk3, double lpk1, double lpk2,
			double lpk3, double lup, double lus, double lut, double lunp, double luns, double lunt, double lio,
			double limax, boolean lstav, String llabel) {
		wid = lid;
		wNode1 = lid1;
		wNode2 = lid2;
		wNode3 = lid3;
		wtype = ltyp;
		wr = lr;
		wx = lx;
		wc = lc;
		wy = ly;
		ws1 = ls1;
		ws2 = ls2;
		ws3 = ls3;
		wuk1 = luk1;
		wuk2 = luk2;
		wuk3 = luk3;
		wio = lio;
		wpk1 = lpk1;
		wpk2 = lpk2;
		wpk3 = lpk3;
		wup = lup;
		wus = lus;
		wut = lut;
		wunp = lunp;
		wuns = luns;
		wunt = lunt;
		wstat = lstav;
		wimax = limax;
		wlabel = llabel;
	}
}
