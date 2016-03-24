package cz.sd2.cpdn.importer.utils;

public class NodeCrate {
	public int wid;
	public String wtype;
	public String wlabel;
	public boolean wstat;
	public double whladu;
	public double wu;
	public double ws;
	public double wfi;
	public double wug;
	public double wsfi;
	public double wxd;
	public double wxq;
	public double wp;
	public double wq;
	public double wsx;
	public double wsy;
	public int wdbid;
	public double wvu;
	public double wvfi;
	public double wvuu;
	public double wvup;

	public NodeCrate() {
		wid = 0;
		wtype = "";
		wu = 0.0;
		ws = 0.0;
		wfi = 0.0;
		wug = 0.0;
		wxd = 0.0;
		wp = 0.0;
		wq = 0.0;
		wdbid = 0;
		wsx = 0.0;
		wsy = 0.0;
		wxq = 0.0;
		whladu = 0.0;
		wstat = true;
		wsfi = 0.0;
		wlabel = "";
		wvu = 0.0;
		wvfi = 0.0;
		wvuu = 0.0;
		wvup = 0.0;
	}

	public NodeCrate(int lid, String ltype, double lhladu, double lu, double ls, double lfi, double lug, double lxd,
			double lxq, double lp, double lq, int ldbid, double lsx, double lsy, double lsfi, boolean lstat,
			String llabel) {
		wid = lid;
		wtype = ltype;
		whladu = lhladu;
		wu = lu;
		ws = ls;
		wfi = lfi;
		wug = lug;
		wxd = lxd;
		wxq = lxq;
		wp = lp;
		wq = lq;
		wdbid = ldbid;
		wsx = lsx;
		wsy = lsy;
		wstat = lstat;
		wsfi = lsfi;
		wlabel = llabel;
		wvu = 0.0;
		wvfi = 0.0;
		wvuu = 0.0;
		wvup = 0.0;
	}
}
