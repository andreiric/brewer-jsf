package com.algaworks.brewer.util.string;

import java.text.ParseException;

import javax.swing.text.MaskFormatter;

public class StringUtil {

	public static boolean isNotEmpty(String str) {
		return str != null && !"".equals(str);
	}
	
	public static String formatar(String mascara, String str) {
		try {
			MaskFormatter mask = new MaskFormatter(mascara);
			mask.setValueContainsLiteralCharacters(false);
			return mask.valueToString(str);
		} catch (ParseException e) {
			return str;
		}
	}
	
}
