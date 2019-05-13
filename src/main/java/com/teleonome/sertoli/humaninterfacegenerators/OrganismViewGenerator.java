package com.teleonome.sertoli.humaninterfacegenerators;

import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;
 
public class OrganismViewGenerator extends HumanInterfaceGenerator{

	public OrganismViewGenerator() {
		// TODO Auto-generated constructor stub
	}
	public JSONObject process( JSONObject data, int currentActionIndex) {
		int nextActionValue=currentActionIndex;
		JSONObject homeBoxProcessingResultJSONObject = new JSONObject();
		
		JSONArray denes = new JSONArray();
		JSONArray actions= new JSONArray();
		
		homeBoxProcessingResultJSONObject.put("Denes", denes);
		homeBoxProcessingResultJSONObject.put("Actions", actions);
		
		String containerPageIdentity = data.getString("Container Page Identity");
		String panelName = data.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME);
		
		String panelDeneChainPointer = data.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER);
		String visualizationStyle = data.getString(TeleonomeConstants.DENE_TYPE_VISUALIZATION_STYLE);
		int panelInPagePosition = data.getInt(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION);
		boolean visible = data.getBoolean(TeleonomeConstants.DENEWORD_VISIBLE);
		JSONArray values = data.getJSONArray("Values"); 
		//
		// Create the Dene that goes in the home Page
		JSONObject pageDene = getPageDene( containerPageIdentity,  panelName,  panelDeneChainPointer,  visualizationStyle,  panelInPagePosition,  visible);
		
		denes.put(pageDene);
		
		//
		// Create DeneChain by adding an action
		//
		JSONObject actionDene = new JSONObject();
		actions.put(actionDene);
		JSONArray actionsDeneWordsJSONArray = new JSONArray();
		actionDene.put("DeneWords", actionsDeneWordsJSONArray);
		
		actionDene.put(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE, "Create "  + panelName +" DeneChain");
		actionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "@Egg:Human Interface");
		actionDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.SPERM_DENE_TYPE_CREATE_DENE_CHAIN);
		
		JSONObject deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		actionsDeneWordsJSONArray.put(deneword);
		nextActionValue++;
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, nextActionValue, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actionsDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT,TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actionsDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_DENECHAIN_NAME,panelName , null, TeleonomeConstants.DATATYPE_STRING, true);
		actionsDeneWordsJSONArray.put(deneword);
		
		return homeBoxProcessingResultJSONObject;
	}

}
