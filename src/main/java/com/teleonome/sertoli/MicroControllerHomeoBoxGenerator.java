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
		
		// 2026-10-31: the asynchronous cycle's settings, read by the controller (AnnabelleController, and the RS485
		// one Chinampa will have); optional in the part
		if(softwareJSONObject.has(TeleonomeConstants.DENEWORD_MICROCONTROLLER_ASYNC_REQUEST_DELAY_MILLIS)) {
			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_MICROCONTROLLER_ASYNC_REQUEST_DELAY_MILLIS, softwareJSONObject.getInt(TeleonomeConstants.DENEWORD_MICROCONTROLLER_ASYNC_REQUEST_DELAY_MILLIS), null, TeleonomeConstants.DATATYPE_INTEGER, true);
			deneWordsJSONArray.put(deneword);
		}
		if(softwareJSONObject.has(TeleonomeConstants.DENEWORD_MICROCONTROLLER_ASYNC_EXECUTION_MODE)) {
			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_MICROCONTROLLER_ASYNC_EXECUTION_MODE, softwareJSONObject.getString(TeleonomeConstants.DENEWORD_MICROCONTROLLER_ASYNC_EXECUTION_MODE), null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
		}
		// written either way, as the Denomes have it (it was written only when true)
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_MOTHER_MICROCONTROLER, isMother, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		deneWordsJSONArray.put(deneword);
		if(isMother) {
			
			//
			// now remove the deneword from the simple microcontroller
			// by Creating the DeneWord Remover
			//
			String removerName="DeneWord Remover For MOhter Deneword in Simple Micro Controller";
			JSONObject removerDeneJSONObject= new JSONObject();
			denesJSONArray.put(removerDeneJSONObject);
			JSONArray remverDeneWordsJSONArray = new JSONArray();
			removerDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_DENEWORD_REMOVER);
			removerDeneJSONObject.put("DeneWords", remverDeneWordsJSONArray);

			String componentsDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS ).toString();
			
			removerDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, removerName);
			removerDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneChainTargetPointer);
			
			
			String removeActionDeneWordPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS,"Simple Micro Controller","Mother Microcontroller"   ).toString();
			deneword = Utils.createDeneWordJSONObject("Remove Mother DeneWord", removeActionDeneWordPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneChainTargetPointer);
			remverDeneWordsJSONArray.put(deneword);

		}
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
				deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
				
			deneWordsJSONArray.put(deneword);

		}
		
		//
		// now run it again to create the actual denes
		//
		JSONObject configParamDene;
		Object paramValue;
		for(int i=0;i<parameters.length();i++) {
			parameterJSONObject = parameters.getJSONObject(i);
			
			
			logger.debug(parameterJSONObject);
			//
			// first the data
			//
			parameterName = parameterJSONObject.getString("Name");
			// 2026-10-31: the value as written, with its own Value Type (a Pi's microcontroller has text
			// settings, e.g. a server address); it was read as a number and written as a String. Range Minimum
			// and Maximum stay in the part for the factory's Avocado page; they were read here but never used.
			paramValue = parameterJSONObject.get("Value");
			parameterDataType = parameterJSONObject.getString("Value Type");

			configParamDene = new JSONObject();
			denesJSONArray.put(configParamDene);
			configParamDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneTergetPointer);


			configParamDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, parameterName );
			configParamDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);

			deneWordsJSONArray = new JSONArray();
			configParamDene.put("DeneWords", deneWordsJSONArray);
			
			deneword = Utils.createDeneWordJSONObject("Codon", componentName, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			
			deneword = Utils.createDeneWordJSONObject(parameterName, paramValue, null, parameterDataType, true);
			deneWordsJSONArray.put(deneword);
			
			
		}
		
		
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
	public ArrayList<String> getExternalTeleonomeNames() {
		return null;
	}
}
