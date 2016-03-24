package cz.sd2.cpdn.importer.utils;

public class Tools {

	public static String replaceChar(String input, String what, String replacement) {
		String s = input;
		int i = s.indexOf(what, 0);
		if (i > -1) {
			s = s.substring(0, i) + replacement + s.substring(i + 1);
		}
		return s;
	}

}
