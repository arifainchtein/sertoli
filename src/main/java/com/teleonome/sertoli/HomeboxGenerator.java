package com.teleonome.sertoli;

import org.json.JSONObject;

public abstract class HomeboxGenerator {

	public abstract JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement);
}
