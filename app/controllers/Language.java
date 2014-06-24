package controllers;

import play.mvc.Controller;

public class Language extends Controller {

	public static final String LANGUAGE_HU = "hu";
	public static final String LANGUAGE_EN = "en";

	public static void setLang(String lang) {
		session("lang", lang);
	}

	public static String getLang() {
		return session().get("lang");
	}

}
