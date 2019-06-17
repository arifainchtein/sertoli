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

public class AddDataTransformationHomeboxGenerator extends HomeboxGenerator {

	Logger logger;
	@Override
	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndex,ArrayList externalDataDenesCreated) {
		// TODO Auto-generated method stub
		//
		// Getting the data
		//
		logger = Logger.getLogger(getClass());
		int nextActionValue=currentActionIndex;
		JSONObject sensorValueDene;
		String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
		String functionName = homeboxSourceDataElement.getString("Function Name");
		String dataSource = homeboxSourceDataElement.getString("Data Source");
		String destination = homeboxSourceDataElement.getString("Destination");
		Object defaultValue =  homeboxSourceDataElement.get("Default Value");
		String destinationValueType = homeboxSourceDataElement.getString("Destination Data Value Type");
		
		//
		// now get the deneword name of the source, and for the destination
		// because they  will be used
		// get the String dataSource
		Identity dataSourceDeneWordIdentity = new Identity(dataSource);
		String dataSourceDeneWordName = dataSourceDeneWordIdentity.getDeneWordName();
		Identity destinationDeneWordIdentity = new Identity(destination);
		String destinationeDeneWordName = dataSourceDeneWordIdentity.getDeneWordName();
		
		
		String dataTransformationDeneName="Transform " + dataSourceDeneWordName;
		String actionDeneName="Transform " + dataSourceDeneWordName + " Action";
		
		String dataTransformationDenePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, dataTransformationDeneName ).toString();
		String actuatorsDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS ).toString();
		String analysisDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_PURPOSE,TeleonomeConstants.DENECHAIN_ANALYSIS ).toString();
		
		
		
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
		deneword = Utils.createDeneWordJSONObject("Description", "Adds a Data Transformation", null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		
		//
		// Create the DeneWord Carrier For Virtual Actuator Actions
		//
		String carrierName="DeneWord Carrier For Virtual Actuator Actions";
		JSONObject carrierDeneJSONObject= new JSONObject();
		denesJSONArray.put(carrierDeneJSONObject);
		JSONArray carrierDeneWordsJSONArray = new JSONArray();
		carrierDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_DENEWORD_CARRIER);
		carrierDeneJSONObject.put("DeneWords", carrierDeneWordsJSONArray);


		carrierDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, carrierName);
		carrierDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
		
		String transformActionDenePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS,actionDeneName  ).toString();
		deneword = Utils.createDeneWordJSONObject(actionDeneName, transformActionDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
		carrierDeneWordsJSONArray.put(deneword);

		//
		// Create the actionDene
		//
		JSONObject actionDeneJSONObject= new JSONObject();
		denesJSONArray.put(actionDeneJSONObject);
		JSONArray actionDeneWordsJSONArray = new JSONArray();
		actionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTION);
		actionDeneJSONObject.put("DeneWords", actionDeneWordsJSONArray);


		actionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, actionDeneName);
		actionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		actionDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actionDeneName, null, TeleonomeConstants.DATATYPE_STRING, true);
		actionDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION, 1, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION, dataTransformationDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_TRANSFORMATION_FUNCTION);
		actionDeneWordsJSONArray.put(deneword);
	
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_OPERATION_VARIABLE, dataSource, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		actionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXPRESSION, "1==1", null, TeleonomeConstants.DATATYPE_STRING, true);
		actionDeneWordsJSONArray.put(deneword);
		
		
		
		
		//
		// Create the dataTransformationDene
		//
		
		JSONObject transformActionDeneJSONObject= new JSONObject();
		denesJSONArray.put(transformActionDeneJSONObject);
		JSONArray transformActionDeneWordsJSONArray = new JSONArray();
		transformActionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_DENEWORD_OPERATION_DATA_TRANSFORMATION);
		transformActionDeneJSONObject.put("DeneWords", transformActionDeneWordsJSONArray);


		transformActionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, dataTransformationDeneName);
		transformActionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		transformActionDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, dataTransformationDeneName, null, TeleonomeConstants.DATATYPE_STRING, true);
		transformActionDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_TRANSFORMATION_FUNCTION, functionName, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_TRANSFORMATION_FUNCTION);
		transformActionDeneWordsJSONArray.put(deneword);
	
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_OPERATION_VARIABLE, dataSource, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_OPERATION_VARIABLE);
		transformActionDeneWordsJSONArray.put(deneword);
		
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_OPERATION_DESTINATION, destination, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_OPERATION_DESTINATION);
		transformActionDeneWordsJSONArray.put(deneword);
		
		
		//
		// Create the destination dene
		//
		
		JSONObject destinationDeneJSONObject= new JSONObject();
		denesJSONArray.put(destinationDeneJSONObject);
		JSONArray destinationDeneWordsJSONArray = new JSONArray();
		destinationDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTION);
		destinationDeneJSONObject.put("DeneWords", destinationDeneWordsJSONArray);


		destinationDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, destinationeDeneWordName);
		destinationDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(destinationeDeneWordName, defaultValue, null, destinationValueType, true);
		destinationDeneWordsJSONArray.put(deneword);
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
		// TODO Auto-generated method stub
		return null;
	}

}
