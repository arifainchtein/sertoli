package com.teleonome.sertoli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;

public class MicroControllerHomeoBoxGenerator extends HomeboxGenerator {
	Logger logger;
	@Override
	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndex,ArrayList externalDataDenesCreated) {
		//
		// Getting the data
		//
		logger = Logger.getLogger(getClass());
		int nextActionValue=currentActionIndex;
		JSONObject sensorValueDene;
		String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
		String componentName = homeboxSourceDataElement.getString("Component Name");
		String className = homeboxSourceDataElement.getString("Class");
		boolean isMother=homeboxSourceDataElement.getBoolean("Is Mother");

		JSONObject softwareJSONObject = homeboxSourceDataElement.getJSONObject("Software");
		int processingQueuePostion = softwareJSONObject.getInt("Processing Queue Position");
		JSONArray parameters = softwareJSONObject.getJSONArray("Parameters");

		JSONObject hardwareJSONObject=null;
		String storageManager = null;
		String timeManager = null;
		if(homeboxSourceDataElement.has("Hardware")) {
			hardwareJSONObject = homeboxSourceDataElement.getJSONObject("Hardware");
			
			if(hardwareJSONObject.has("Storage Manager")){
				storageManager = hardwareJSONObject.getString("Storage Manager");
			}
			if(hardwareJSONObject.has("Time Manager")){
				timeManager = hardwareJSONObject.getString("Time Manager");
			}
		}
		 
		



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



		JSONObject microControllerDene = new JSONObject();
		denesJSONArray.put(microControllerDene);
		String componentsDeneTergetPointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS)).toString();	
		microControllerDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneTergetPointer);


		microControllerDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, componentName);
		microControllerDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER);

		deneWordsJSONArray = new JSONArray();
		microControllerDene.put("DeneWords", deneWordsJSONArray);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.PROCESSING_QUEUE_POSITION, processingQueuePostion, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		deneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject("Codon", componentName, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(componentName + " Class Name", className, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_MICROCONTROLLER_PROCESSING_CLASSNAME);
		deneWordsJSONArray.put(deneword);
		
		String configParamListDeneTergetPointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS,componentName + " Config Parameter List" )).toString();	
		
		deneword = Utils.createDeneWordJSONObject(componentName + " Config Parameter List", configParamListDeneTergetPointer, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_MICROCONTROLLER_CONFIG_PARAM_LIST);
		deneWordsJSONArray.put(deneword);
		
		//
		// the ConfigParamList Dene
		//
		
		JSONObject configParamListDene = new JSONObject();
		denesJSONArray.put(configParamListDene);
		configParamListDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneTergetPointer);


		configParamListDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, componentName + " Config Parameter List");
		configParamListDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER_LIST);

		deneWordsJSONArray = new JSONArray();
		configParamListDene.put("DeneWords", deneWordsJSONArray);
		String parameterName, parameterDataType, configListParamAddressPointer;
		JSONObject parameterJSONObject;
		double parameterRangeMaximum, parameterRangeMinimum;
		JSONObject existingDeneWordCarrierForDene;
		//
		// first run this loop to get
		// the denewords and the pointers
		//
		for(int i=0;i<parameters.length();i++) {
			parameterJSONObject = parameters.getJSONObject(i);
			logger.debug(parameterJSONObject);
			//
			// first the data
			//
			parameterName = parameterJSONObject.getString("Name");
			configListParamAddressPointer = (new Identity(teleonomeName, TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS,parameterName)).toString();	
			deneword = Utils.createDeneWordJSONObject(parameterName, configListParamAddressPointer, null, TeleonomeConstants.DENEWORD_TYPE_POINTER, true);

		}
		
		//
		// now run it again to create the actual denes
		//
		JSONObject configParamDene;
		double paramValue;
		for(int i=0;i<parameters.length();i++) {
			parameterJSONObject = parameters.getJSONObject(i);
			
			
			logger.debug(parameterJSONObject);
			//
			// first the data
			//
			parameterName = parameterJSONObject.getString("Name");
			paramValue = parameterJSONObject.getDouble("Value");
			parameterDataType = parameterJSONObject.getString("Value Type");
			parameterRangeMaximum = -9999;
			parameterRangeMinimum = -9999;
			
			if( parameterDataType.equals(TeleonomeConstants.DATATYPE_INTEGER) ) {
				parameterRangeMaximum = parameterJSONObject.getDouble("Range Maximum");
				parameterRangeMinimum = parameterJSONObject.getDouble("Range Minimum");
			}
			
			if( parameterDataType.equals(TeleonomeConstants.DATATYPE_DOUBLE)) {
				if(parameterJSONObject.has("Range Maximum")) {
					parameterRangeMaximum = parameterJSONObject.getDouble("Range Maximum");
				}
				if(parameterJSONObject.has("Range Minimum")) {
					parameterRangeMinimum = parameterJSONObject.getDouble("Range Minimum");
				}
			}
			
			configParamDene = new JSONObject();
			denesJSONArray.put(configParamDene);
			configParamDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneTergetPointer);


			configParamDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, parameterName );
			configParamDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);

			deneWordsJSONArray = new JSONArray();
			configParamDene.put("DeneWords", deneWordsJSONArray);
			
			deneword = Utils.createDeneWordJSONObject("Codon", componentName, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			
			deneword = Utils.createDeneWordJSONObject(parameterName, paramValue, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			
			
		}
		
		return homeBoxProcessingResultJSONObject;
	}

	@Override
	public ArrayList<String> getExternalTeleonomeNames() {
		return null;
	}
}
