package com.teleonome.sertoli.humaninterfacegenerators;

import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;

public class SingleValuePanelCompleteWidthGenerator extends HumanInterfaceGenerator{

		public SingleValuePanelCompleteWidthGenerator() {
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
			JSONArray values = new JSONArray();
			if(data.has("Values") ) {
				values=  data.getJSONArray("Values"); 
			}
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
			/*
			 * process the values
			 */
			if(values.length()>0) {
				JSONObject value, dene;
				String  target, deneName,  displayName,  panelDeneSourcePointer;
				int  panelInPanelPosition;
				for(int i=0;i<values.length();i++) {
					value = values.getJSONObject(i);
					target=new Identity("Egg",TeleonomeConstants.NUCLEI_HUMAN_INTERFACE, panelName).toString();
					deneName=value.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME);
					displayName=value.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME);
					panelDeneSourcePointer=value.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_SOURCE_POINTER);
					panelInPanelPosition=value.getInt(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION);
					visible=value.getBoolean(TeleonomeConstants.DENEWORD_VISIBLE);
					
					dene = getSingleValueDene( target, deneName,  displayName,  panelDeneSourcePointer,  panelInPanelPosition,  visible);
					denes.put(dene);

				}
			}
			
			return homeBoxProcessingResultJSONObject;
		}

		private JSONObject getSingleValueDene(String target,String deneName, String displayName, String panelDeneSourcePointer, int panelInPanelPosition, boolean visible) {
			JSONObject deneJSONObject= new JSONObject();
			JSONArray deneWordsJSONArray = new JSONArray();
			deneJSONObject.put("DeneWords", deneWordsJSONArray);
			deneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, deneName);
			deneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, target);
			
			JSONObject deneword = Utils.createDeneWordJSONObject(deneName + " Pointer", panelDeneSourcePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_SOURCE_POINTER);
			deneWordsJSONArray.put(deneword);

			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION, panelInPanelPosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION);
			deneWordsJSONArray.put(deneword);
			
			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME, displayName, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME);
			deneWordsJSONArray.put(deneword);

			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VISIBLE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
			deneWordsJSONArray.put(deneword);
			return deneJSONObject;
		}
	}
