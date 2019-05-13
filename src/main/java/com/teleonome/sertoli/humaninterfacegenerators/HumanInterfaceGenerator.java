package com.teleonome.sertoli.humaninterfacegenerators;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;
 
public abstract class HumanInterfaceGenerator {

	public HumanInterfaceGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract JSONObject process( JSONObject data, int currentActionIndex);

	//
	// this is the dene that goes int the denechain that represents the page
	public JSONObject getPageDene(String target, String panelName, String panelDeneChainPointer, String visualizationStyle, int panelInPagePosition, boolean visible) {
		JSONObject deneJSONObject= new JSONObject();
		JSONArray deneWordsJSONArray = new JSONArray();
		deneJSONObject.put("DeneWords", deneWordsJSONArray);
		deneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, panelName);
		deneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, target);
		
		JSONObject deneword = Utils.createDeneWordJSONObject(panelName + " Pointer", panelDeneChainPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER);
		deneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject("Visualization Style", visualizationStyle, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_VISUALIZATION_STYLE);
		deneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION, panelInPagePosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION);
		deneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VISIBLE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		deneWordsJSONArray.put(deneword);
		return deneJSONObject;
	}
}
