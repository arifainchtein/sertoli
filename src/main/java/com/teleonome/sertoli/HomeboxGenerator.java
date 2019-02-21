package com.teleonome.sertoli;

import java.util.ArrayList;

import org.json.JSONObject;

public abstract class HomeboxGenerator {

	public abstract JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndicator, ArrayList externalDataDenesCreated);
}
