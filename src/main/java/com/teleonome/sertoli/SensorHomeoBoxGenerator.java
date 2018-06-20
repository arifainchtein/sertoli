package com.teleonome.sertoli;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;

public class SensorHomeoBoxGenerator extends HomeboxGenerator {

	Logger logger;
	
	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement) {
			//
			// Getting the data
			//
		logger = Logger.getLogger(getClass());
		
		JSONObject sensorValueDene;
			String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
			String sensorName = homeboxSourceDataElement.getString("Sensor Name");
			String sensorPort = homeboxSourceDataElement.getString("Port");
			
			String pointerToMicroController = homeboxSourceDataElement.getString("MicroController Pointer");
			String sourceAddress = homeboxSourceDataElement.getString("Provider");
			JSONArray values = homeboxSourceDataElement.getJSONArray("Values");
			
			//
			// processing
			//
			JSONObject homeBoxJSONObject = new JSONObject();
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
			String reportingValueDeneName="";
			
			for(int i=0;i<values.length();i++){
				value = values.getJSONObject(i);
				//
				// first the data
				//
				valueName = value.getString("Name");
				sensorValueUnits = value.getString("Units");
				
				sensorValueDataType = value.getString("Value Type");
				sensorValueRequestPosition = value.getInt("Sensor Value Request Position");
				sensorValueRangeMaximum = value.getDouble("Range Maximum");
				sensorValueRangeMinimum = value.getDouble("Range Minimum");
				sensorValueInitialValue = value.getDouble("Initial Value");
				reportingValueDeneName = value.getString("Reporting Value Dene Name");
				sensorValueReportingAddressPointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_PURPOSE,TeleonomeConstants.DENECHAIN_SENSOR_DATA,reportingValueDeneName, valueName)).toString();	
				sensorValueReportingAddressDenePointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_PURPOSE,TeleonomeConstants.DENECHAIN_SENSOR_DATA, reportingValueDeneName)).toString();	
				denewordValuePointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_SENSORS, valueName + " Value")).toString();	
				deneword = Utils.createDeneWordJSONObject(valueName + " Value", denewordValuePointer, null, TeleonomeConstants.DENEWORD_TYPE_POINTER, true);

				//
				// now create the actual value dene

				logger.debug("sensorValueReportingAddressDenePointer=" + sensorValueReportingAddressDenePointer);
				existingDeneWordCarrierForDene = (JSONObject) existingDeneWordCarrierForDeneIndex.get(sensorValueReportingAddressDenePointer);
				logger.debug("existingDeneWordCarrierForDene=" + existingDeneWordCarrierForDene);
				if(existingDeneWordCarrierForDene==null) {
					existingDeneWordCarrierForDene = new JSONObject();
					existingDeneWordCarrierForDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "DeneWord Carrier For " + reportingValueDeneName);
					existingDeneWordCarrierForDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_DENEWORD_CARRIER);
					existingDeneWordCarrierForDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "@" + teleonomeName + ":" + TeleonomeConstants.NUCLEI_PURPOSE + ":" + TeleonomeConstants.DENECHAIN_SENSOR_DATA);
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
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SENSOR_VALUE_RANGE_MAXIMUM, sensorValueRangeMaximum, null, TeleonomeConstants.DATATYPE_STRING, true);
				sensorValueDeneWordsJSONArray.put(deneword);
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SENSOR_VALUE_RANGE_MINIMUM, sensorValueRangeMinimum, null, TeleonomeConstants.DATATYPE_STRING, true);
				sensorValueDeneWordsJSONArray.put(deneword);
				//
				// do the purpose:sensor data
				reportingValueDeneWord = Utils.createDeneWordJSONObject(valueName, sensorValueInitialValue, sensorValueUnits, sensorValueDataType, true);
				//reportingValueDeneWord.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET,sensorValueReportingAddressPointer);
				existingDeneWordCarrierForDeneWords.put(reportingValueDeneWord);
				//
				// finally check to see if there is Human Interface info
				//
				if(value.has(TeleonomeConstants.HUMAN_INTERFACE_PANEL) && value.has(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION)) {
					logger.debug("found ui info, processing");
					String humanInterfacePanel = value.getString(TeleonomeConstants.HUMAN_INTERFACE_PANEL);
					int inPanelPosition = value.getInt(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PANEL_POSITION);
					String uiDisplayName = value.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME);
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
			
		return homeBoxJSONObject;
	}

}
