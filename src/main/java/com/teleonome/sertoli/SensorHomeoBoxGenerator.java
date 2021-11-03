package com.teleonome.sertoli;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.*;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;

public class SensorHomeoBoxGenerator extends HomeboxGenerator {

	Logger logger;
	
	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndex, ArrayList externalDataDenesCreated) {
			//
			// Getting the data
			//
		logger = Logger.getLogger(getClass());
		int nextActionValue=currentActionIndex;
		JSONObject sensorValueDene;
			String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
			String sensorName = homeboxSourceDataElement.getString("Sensor Name");
			String sensorPort = homeboxSourceDataElement.getString("Port");
			
			String pointerToMicroController = homeboxSourceDataElement.getString("Microcontroller Pointer");
			String sourceAddress = homeboxSourceDataElement.getString("Provider");
			String containerPageIdentityPointer = homeboxSourceDataElement.getString("Container Page Identity Pointer");
			String humanInterfacePanel = homeboxSourceDataElement.getString(TeleonomeConstants.HUMAN_INTERFACE_PANEL);
			String reportingValueDeneName = homeboxSourceDataElement.getString("Reporting Value Dene Name");
			JSONArray values = homeboxSourceDataElement.getJSONArray("Values");
			Identity containerPageIdentity = new Identity(containerPageIdentityPointer);
			
			//
			// processing
			//
			
			
			JSONObject homeBoxProcessingResultJSONObject = new JSONObject();
			JSONObject homeBoxJSONObject = new JSONObject();
			homeBoxProcessingResultJSONObject.put("Homeobox", homeBoxJSONObject);
			
			
			JSONArray actionJSONArray = new JSONArray();
			homeBoxProcessingResultJSONObject.put("Actions", actionJSONArray);
			
			
			
			homeBoxJSONObject.put("Name", homeBoxName);
			JSONArray denesJSONArray = new JSONArray();
			homeBoxJSONObject.put("Denes", denesJSONArray);
			//
			// create the metadata dene 
			// 
			JSONObject metaDataDene = new JSONObject();
			denesJSONArray.put(metaDataDene);
			metaDataDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "");
			metaDataDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "Meta Data");
			metaDataDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_HOMEOBOX_METADATA);
			SimpleDateFormat dateFormat = new SimpleDateFormat(TeleonomeConstants.SPERM_DATE_FORMAT);	
			JSONArray deneWordsJSONArray = new JSONArray();
			metaDataDene.put("DeneWords", deneWordsJSONArray);
			JSONObject deneword = Utils.createDeneWordJSONObject("Created On", dateFormat.format(new Date()), null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneWordsJSONArray.put(deneword);
			deneword = Utils.createDeneWordJSONObject("Description", "", null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			
			
			
			JSONObject sensorDene = new JSONObject();
			denesJSONArray.put(sensorDene);
			String sensorDeneTergetPointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_SENSORS)).toString();	
			sensorDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, sensorDeneTergetPointer);
			
			
			sensorDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, sensorName);
			sensorDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_SENSOR);

			 deneWordsJSONArray = new JSONArray();
			sensorDene.put("DeneWords", deneWordsJSONArray);
			 deneword = Utils.createDeneWordJSONObject("Pointer to Microcontroller", pointerToMicroController, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			 deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_SENSOR_MICROCONTROLLER_POINTER);
			 deneWordsJSONArray.put(deneword);
			deneword = Utils.createDeneWordJSONObject("Codon", sensorName, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			deneword = Utils.createDeneWordJSONObject("Source", sourceAddress, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			deneword = Utils.createDeneWordJSONObject("Port", sensorPort, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			//
			// do the loop for values twice, once to add denewords to the sensor dene and another to add the actual value denes
			JSONObject value;
			String valueName;
			for(int i=0;i<values.length();i++){
				value = values.getJSONObject(i);
				//
				// first the data
				//
				valueName = value.getString("Name");
				deneword = Utils.createDeneWordJSONObject(valueName + " Value", "@" + teleonomeName + ":" + TeleonomeConstants.NUCLEI_INTERNAL + ":" + TeleonomeConstants.DENECHAIN_SENSORS + ":" + valueName + " Value", null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
				deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_SENSOR_VALUE);
				deneWordsJSONArray.put(deneword);
				
			}
			
			//
			// if we are creating a ui then add the panel to the page
			//
			
			if(humanInterfacePanel!=null && 
			!humanInterfacePanel.equals("") &&
			containerPageIdentityPointer!=null && 
			!containerPageIdentityPointer.equals("") ){
				JSONObject pageDene = new JSONObject();
				denesJSONArray.put(pageDene);
				JSONArray pageDeneWordsJSONArray = new JSONArray();
				pageDene.put("DeneWords", pageDeneWordsJSONArray);
				
				pageDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, humanInterfacePanel);
				pageDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, containerPageIdentityPointer);
				
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VISIBLE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
				pageDeneWordsJSONArray.put(deneword);
				nextActionValue++;
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION, nextActionValue, null, TeleonomeConstants.DATATYPE_INTEGER, true);
				deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION);
				pageDeneWordsJSONArray.put(deneword);

				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_PANEL_VISUALIZATION_STYLE,TeleonomeConstants.PANEL_VISUALIZATION_STYLE_SINGLE_VALUE_PANEL_COMPLETE_WIDTH, null, TeleonomeConstants.DATATYPE_STRING, true);
				deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_VISUALIZATION_STYLE);
				pageDeneWordsJSONArray.put(deneword);
				String panelDeneChainPointer=(new Identity(teleonomeName, TeleonomeConstants.NUCLEI_HUMAN_INTERFACE,humanInterfacePanel)).toString();	
				
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER,panelDeneChainPointer, null, TeleonomeConstants.DATATYPE_STRING, true);
				deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER);
				pageDeneWordsJSONArray.put(deneword);
			}
			
			
			
			
			String denewordValuePointer,sensorValueReportingAddressPointer, sensorValueReportingAddressDenePointer;
			Hashtable existingDeneWordCarrierForDeneIndex = new Hashtable();
			JSONArray sensorValueDeneWordsJSONArray;
			JSONObject existingDeneWordCarrierForDene,reportingValueDeneWord;
			JSONArray existingDeneWordCarrierForDeneWords;
			String sensorValueUnits;
			String sensorValuePort;
			String sensorValueDataType;
			int sensorValueRequestPosition;
			double sensorValueRangeMaximum=0;
			double sensorValueRangeMinimum=0;
			double sensorValueInitialValue=0;
			String containerTarget, visualizationStyle;
			
			boolean visible=false;
			int panelInPagePosition=0;
			
			for(int i=0;i<values.length();i++){
				value = values.getJSONObject(i);
				logger.debug(value);
				//
				// first the data
				//
				valueName = value.getString("Name");
				sensorValueUnits = value.getString("Units");
				
				sensorValueDataType = value.getString("Value Type");
				sensorValueRequestPosition =  value.getInt(TeleonomeConstants.DENEWORD_SENSOR_REQUEST_QUEUE_POSITION);
				sensorValueRangeMaximum = -9999;
				sensorValueRangeMinimum = -9999;
				sensorValueInitialValue=0;
				
				
				if( sensorValueDataType.equals(TeleonomeConstants.DATATYPE_INTEGER) ||
						sensorValueDataType.equals(TeleonomeConstants.DATATYPE_DOUBLE)
						) {
					if(value.has("Range Maximum")) {
						sensorValueRangeMaximum = value.getDouble("Range Maximum");
					}
					if(value.has("Range Minimum")) {
						sensorValueRangeMinimum = value.getDouble("Range Minimum");
					}
					if(value.has("Initial Value")) {
						sensorValueInitialValue = value.getDouble("Initial Value");
					}
				}
				
				
				logger.debug("line 146 reportingValueDeneName=" + reportingValueDeneName);
				sensorValueReportingAddressPointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_PURPOSE,TeleonomeConstants.DENECHAIN_SENSOR_DATA,reportingValueDeneName, valueName)).toString();	
				sensorValueReportingAddressDenePointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_PURPOSE,TeleonomeConstants.DENECHAIN_SENSOR_DATA, reportingValueDeneName)).toString();	
				denewordValuePointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_SENSORS, valueName + " Value")).toString();	
				deneword = Utils.createDeneWordJSONObject(valueName + " Value", denewordValuePointer, null, TeleonomeConstants.DENEWORD_TYPE_POINTER, true);

				

				logger.debug("sensorValueReportingAddressDenePointer=" + sensorValueReportingAddressDenePointer);
				existingDeneWordCarrierForDene = (JSONObject) existingDeneWordCarrierForDeneIndex.get(sensorValueReportingAddressDenePointer);
				logger.debug("existingDeneWordCarrierForDene=" + existingDeneWordCarrierForDene);
				if(existingDeneWordCarrierForDene==null) {
					existingDeneWordCarrierForDene = new JSONObject();
					existingDeneWordCarrierForDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "DeneWord Carrier For " + reportingValueDeneName);
					existingDeneWordCarrierForDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_DENEWORD_CARRIER);
					String hoxDeneTarget="@" + teleonomeName + ":" + TeleonomeConstants.NUCLEI_PURPOSE + ":" + TeleonomeConstants.DENECHAIN_SENSOR_DATA + ":" + reportingValueDeneName;
					logger.debug("line 162, hoxDeneTarget=" + hoxDeneTarget);
					existingDeneWordCarrierForDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, hoxDeneTarget);
					existingDeneWordCarrierForDeneWords = new JSONArray();
					existingDeneWordCarrierForDene.put("DeneWords",existingDeneWordCarrierForDeneWords);
					existingDeneWordCarrierForDeneIndex.put(sensorValueReportingAddressDenePointer,existingDeneWordCarrierForDene);
					denesJSONArray.put(existingDeneWordCarrierForDene);

				}else{
					existingDeneWordCarrierForDeneWords = existingDeneWordCarrierForDene.getJSONArray("DeneWords");
				}

				//
				// now create the actual value dene
				//
				sensorValueDene = new JSONObject();
				denesJSONArray.put(sensorValueDene);
				sensorValueDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName + " Value");
				sensorValueDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_SENSOR_VALUE_DEFINITION);
				sensorValueDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, sensorDeneTergetPointer);
				
				sensorValueDeneWordsJSONArray = new JSONArray();
				sensorValueDene.put("DeneWords", sensorValueDeneWordsJSONArray);
				deneword = Utils.createDeneWordJSONObject("Codon", sensorName, null, TeleonomeConstants.DATATYPE_STRING, true);
				sensorValueDeneWordsJSONArray.put(deneword);
				deneword = Utils.createDeneWordJSONObject("Unit", sensorValueUnits, null, TeleonomeConstants.DATATYPE_STRING, true);
				sensorValueDeneWordsJSONArray.put(deneword);
				
				deneword = Utils.createDeneWordJSONObject("Value Type", sensorValueDataType, null, TeleonomeConstants.DATATYPE_STRING, true);
				sensorValueDeneWordsJSONArray.put(deneword);
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_SENSOR_REQUEST_QUEUE_POSITION, sensorValueRequestPosition, null, TeleonomeConstants.DATATYPE_STRING, true);
				sensorValueDeneWordsJSONArray.put(deneword);
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_REPORTING_ADDRESS, sensorValueReportingAddressPointer, null, TeleonomeConstants.DATATYPE_STRING, true);
				sensorValueDeneWordsJSONArray.put(deneword);
				if( sensorValueRangeMaximum>-9999) {
					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SENSOR_VALUE_RANGE_MAXIMUM, sensorValueRangeMaximum, null, TeleonomeConstants.DATATYPE_STRING, true);
					sensorValueDeneWordsJSONArray.put(deneword);
				}
				if( sensorValueRangeMinimum>-9999) {
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SENSOR_VALUE_RANGE_MINIMUM, sensorValueRangeMinimum, null, TeleonomeConstants.DATATYPE_STRING, true);
				sensorValueDeneWordsJSONArray.put(deneword);
				}
				//
				// do the purpose:sensor data
				
				
				reportingValueDeneWord = Utils.createDeneWordJSONObject(valueName, sensorValueInitialValue, sensorValueUnits, sensorValueDataType, true);
				existingDeneWordCarrierForDeneWords.put(reportingValueDeneWord);
				
				
				
				//
				// finally check to see if there is Human Interface info
				//
				logger.debug("has hip=" + value.has(TeleonomeConstants.HUMAN_INTERFACE_PANEL) + " has pip " + value.has(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION));
				if(humanInterfacePanel!=null && 
						!humanInterfacePanel.equals("") &&
						value.has(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME) && 
						value.has(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION)) {
					
					logger.debug("found ui info, processing");
					//
					// get  "Human Interface Container":"@Egg:Human Interface:Home Page" ,
					// the container is the denechain, ie Home Page or Search Panel or System Info
					// where the panel stored in "Human Interface Panel" will be viewed
					// so if you want the "Internal Power Daily Stats" panel to show in the home page
					// then in the hsd 
//					 "Human Interface Container":"@Egg:Human Interface:Home Page" ,
//			         "Human Interface Panel":"Internal Power Daily Stats" ,
			         
					// so get the denechain, and create a dene with the 4 denewords
					
					
		            	
					
					
					
		            //
					//
					int inPanelPosition = value.getInt(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION);
					String uiDisplayName = value.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME);
					logger.debug("humanInterfacePanel=" + humanInterfacePanel + " inPanelPosition=" + inPanelPosition + " uiDisplayName=" + uiDisplayName);
					
					//
					// create an action that creates a denechain
					//
					JSONObject actionDene = new JSONObject();
					actionJSONArray.put(actionDene);
					JSONArray actionsDeneWordsJSONArray = new JSONArray();
					actionDene.put("DeneWords", actionsDeneWordsJSONArray);
					
					actionDene.put(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE, "Create "+humanInterfacePanel +" DeneChain");
					actionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "@Egg:Human Interface");
					actionDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.SPERM_DENE_TYPE_CREATE_DENE_CHAIN);
					
					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
					actionsDeneWordsJSONArray.put(deneword);
					nextActionValue++;
					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, nextActionValue, null, TeleonomeConstants.DATATYPE_INTEGER, true);
					actionsDeneWordsJSONArray.put(deneword);

					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT,TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_INTEGER, true);
					actionsDeneWordsJSONArray.put(deneword);

					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_DENECHAIN_NAME,humanInterfacePanel, null, TeleonomeConstants.DATATYPE_STRING, true);
					actionsDeneWordsJSONArray.put(deneword);
		                       
					
					//
					// now do the ui
					//
					//
					// create  the link to the ui
					//
					//containerPageIdentity
					
					JSONObject uiDene = new JSONObject();
					denesJSONArray.put(uiDene);
					
					uiDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
					uiDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "@" + teleonomeName + ":Human Interface:" + humanInterfacePanel);
					
					JSONArray uiDeneWordsJSONArray = new JSONArray();
					uiDene.put("DeneWords", uiDeneWordsJSONArray);
					
					deneword = Utils.createDeneWordJSONObject("Display Name", uiDisplayName, null, TeleonomeConstants.DATATYPE_STRING, true);
					deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME);
					uiDeneWordsJSONArray.put(deneword);
					
					
					deneword = Utils.createDeneWordJSONObject("Panel In Panel Position", inPanelPosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
					deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION);
					uiDeneWordsJSONArray.put(deneword);
					
					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_SOURCE_POINTER, sensorValueReportingAddressPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
					deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_SOURCE_POINTER);
					
					uiDeneWordsJSONArray.put(deneword);
					
					
				}
			} // looping ver values
			
			
			
			//
			// create the homeobox index dene
			// 
			JSONObject homeoboxDene = new JSONObject();
			homeoboxDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "");
			homeoboxDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, TeleonomeConstants.SPERM_HOMEOBOX_INDEX);
			homeoboxDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_HOMEOBOX_INDEX);
			deneWordsJSONArray = new JSONArray();
			homeoboxDene.put("DeneWords", deneWordsJSONArray);
			String denePointer = "@Sperm:Hypothalamus:" ;
			String deneName, deneType;
			for(int i=0;i<denesJSONArray.length();i++) {
				deneName = denesJSONArray.getJSONObject(i).getString(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE);
				
				deneType="";
				if(denesJSONArray.getJSONObject(i).has(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE)) {
					deneType = denesJSONArray.getJSONObject(i).getString(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE);
				}
				logger.debug("deneName="+ deneName + " deneType=" + deneType);
				
				if(!deneName.equals("Meta Data") &&
					!deneType.equals(TeleonomeConstants.SPERM_DENE_TYPE_DENEWORD_CARRIER) &&
					!deneType.equals(TeleonomeConstants.SPERM_DENE_TYPE_DENEWORD_REMOVER) 
				) {
					denePointer = "@Sperm:Hypothalamus:" + homeBoxName + ":" + deneName;
					deneword = Utils.createDeneWordJSONObject(deneName, denePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
					deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_HOX_DENE_POINTER);
					deneWordsJSONArray.put(deneword);
				}				
			}
			
			denesJSONArray.put(homeoboxDene);
			
		return homeBoxProcessingResultJSONObject;
	}

	@Override
	public ArrayList getExternalTeleonomeNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
