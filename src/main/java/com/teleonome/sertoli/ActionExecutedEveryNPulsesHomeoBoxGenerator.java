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

public class ActionExecutedEveryNPulsesHomeoBoxGenerator extends HomeboxGenerator {
	Logger logger;
	@Override
	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndicator,
			ArrayList externalDataDenesCreated) {
		//
		// Getting the data
		//
		logger = Logger.getLogger(getClass());
		int nextActionValue=currentActionIndicator;
		JSONObject sensorValueDene;
		
		String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
		String actionGroupName = homeboxSourceDataElement.getString("Action Group Name");
		String microcontrollerPointer = homeboxSourceDataElement.getString("Microcontroller Pointer");
		String actuatorActionListPointer = homeboxSourceDataElement.getString("Actuator Action List Pointer");

		String counterSuffix = homeboxSourceDataElement.getString("Counter Suffix");
		String actuatorName = homeboxSourceDataElement.getString("Actuator Name");
		String conditionName = homeboxSourceDataElement.getString("Condition Name");
		String actionName = homeboxSourceDataElement.getString("Action Name");
		String actuatorCommandTrueExpression = homeboxSourceDataElement.getString(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION);
		
		int executionPosition = homeboxSourceDataElement.getInt("Execution Position");
		int counterLimit = homeboxSourceDataElement.getInt("Counter Limit");
		int counterIncrement = homeboxSourceDataElement.getInt("Counter Increment");
		
		String counterDeneName = "Pulse Count Since " + counterSuffix;
		
	     
	     
	     

		
		// get the
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
		
		//
		// Create the Mnemosyne Counter
		//
		JSONObject mnemosynePulseCounterDeneJSONObject= new JSONObject();
		denesJSONArray.put(mnemosynePulseCounterDeneJSONObject);
		JSONArray mnemosynePulseCounterDeneWordsJSONArray = new JSONArray();
		mnemosynePulseCounterDeneJSONObject.put("DeneWords", mnemosynePulseCounterDeneWordsJSONArray);
		String mnemosynePulseCounterDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_MNEMOSYNE,TeleonomeConstants.MNEMOSYNE_DENECHAIN_PULSE_COUNT ).toString();
		mnemosynePulseCounterDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, counterDeneName);
		mnemosynePulseCounterDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET,  mnemosynePulseCounterDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_MNEMOSYNE_COUNTER, 0, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		mnemosynePulseCounterDeneWordsJSONArray.put(deneword);

		//
		// Create the Actuator 
		//
		JSONObject actuatorDeneJSONObject= new JSONObject();
		denesJSONArray.put(actuatorDeneJSONObject);
		JSONArray actuatorDeneWordsJSONArray = new JSONArray();
		actuatorDeneJSONObject.put("DeneWords", actuatorDeneWordsJSONArray);
		String actuatorDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS ).toString();
		actuatorDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, actuatorName);
		actuatorDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET,  actuatorDeneChainTargetPointer);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_MICROCONTROLLER_POINTER, microcontrollerPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_MICROCONTROLLER_POINTER);
		actuatorDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXECUTION_POSITION, executionPosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actuatorDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(actuatorName + " Actions", actuatorActionListPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTION_LIST);
		actuatorDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actuatorDeneWordsJSONArray.put(deneword);

		String actuatorListName = actuatorName + " Actions";
		String actuatorListPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS ,actuatorListName).toString();
		deneword = Utils.createDeneWordJSONObject(actuatorName + " Actions", actuatorListPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION_LIST);
		actuatorDeneWordsJSONArray.put(deneword);

		//
		// now create the action list dene
		//
		JSONObject actionListDene = new JSONObject();
		denesJSONArray.put(actionListDene);

		JSONArray actionListDeneWordsJSONArray = new JSONArray();
		actionListDene.put("DeneWords", actionListDeneWordsJSONArray);

		actionListDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, actuatorListName);
		actionListDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION_LIST);
		actionListDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorDeneChainTargetPointer);

		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actuatorDeneWordsJSONArray.put(deneword);
		
		
		String actionPointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actionName ).toString();
		deneword = Utils.createDeneWordJSONObject(actionName, actionPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_ACTION);
		actionListDeneWordsJSONArray.put(deneword);
		
		//
		// next the action dene represented by actionInPulseName
		//
		JSONObject actionDene = new JSONObject();
		denesJSONArray.put(actionDene);

		JSONArray actionDeneWordsJSONArray = new JSONArray();
		actionDene.put("DeneWords", actionDeneWordsJSONArray);
		actionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorDeneChainTargetPointer);
		actionDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, actionName);
		actionDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		actionDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		actionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION, 1, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION , actuatorCommandTrueExpression, null, TeleonomeConstants.DATATYPE_STRING, true);
		actionDeneWordsJSONArray.put(deneword);
		
		
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXPRESSION , "(!"+conditionName +")", null, TeleonomeConstants.DATATYPE_STRING, true);
		actionDeneWordsJSONArray.put(deneword);
		
		String conditionPointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, conditionName ).toString();
		deneword = Utils.createDeneWordJSONObject(conditionName, conditionPointer , null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_CONDITION_POINTER);
		actionDeneWordsJSONArray.put(deneword);
		
		//
		// next the action dene Increment Pulse Count
		//
		JSONObject incrementCounterActionDene = new JSONObject();
		denesJSONArray.put(incrementCounterActionDene);
		String deneName = "Increment Pulse Count " + actuatorName;
		
		JSONArray incrementCounterActionDeneWordsJSONArray = new JSONArray();
		incrementCounterActionDene.put("DeneWords", incrementCounterActionDeneWordsJSONArray);
		incrementCounterActionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorDeneChainTargetPointer);
		incrementCounterActionDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, deneName);
		incrementCounterActionDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		incrementCounterActionDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		incrementCounterActionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION, 2, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		incrementCounterActionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION , TeleonomeConstants.COMMANDS_DO_NOTHING, null, TeleonomeConstants.DATATYPE_STRING, true);
		incrementCounterActionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXPRESSION , "("+conditionName +")", null, TeleonomeConstants.DATATYPE_STRING, true);
		incrementCounterActionDeneWordsJSONArray.put(deneword);
		
		String mnemosyneTrueExpressionName = deneName + " " + counterSuffix + " Mnemosyne Operations True Expression";
		String mnemosyneNamePointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, mnemosyneTrueExpressionName ).toString();
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_MNEMOSYNE_OPERATION_TRUE_EXPRESSION , mnemosyneNamePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		incrementCounterActionDeneWordsJSONArray.put(deneword);
		
		
		//
		// next the condition dene
		//
		JSONObject conditionDene = new JSONObject();
		denesJSONArray.put(conditionDene);
		
		JSONArray conditionnDeneWordsJSONArray = new JSONArray();
		conditionDene.put("DeneWords", conditionnDeneWordsJSONArray);
		conditionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorDeneChainTargetPointer);
		conditionDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, conditionName);
		conditionDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTUATOR_CONDITION);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		conditionnDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		conditionnDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject("On Lack of Data", false, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		conditionnDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXPRESSION , "(Counter != 0)", null, TeleonomeConstants.DATATYPE_STRING, true);
		conditionnDeneWordsJSONArray.put(deneword);
		
		String counterDeneWordTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_MNEMOSYNE,TeleonomeConstants.MNEMOSYNE_DENECHAIN_PULSE_COUNT , counterDeneName, TeleonomeConstants.DENEWORD_MNEMOSYNE_COUNTER).toString();
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_MNEMOSYNE_COUNTER , counterDeneWordTargetPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_CONDITION_VARIABLE_POINTER);
		conditionnDeneWordsJSONArray.put(deneword);
		
		
		//
		// next the mnemosyne operartons index
		//
		JSONObject mnemosyneTrueExpressionDene = new JSONObject();
		denesJSONArray.put(mnemosyneTrueExpressionDene);
		
		JSONArray mnemosyneTrueExpressionDeneWordsJSONArray = new JSONArray();
		mnemosyneTrueExpressionDene.put("DeneWords", mnemosyneTrueExpressionDeneWordsJSONArray);
		mnemosyneTrueExpressionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorDeneChainTargetPointer);
		mnemosyneTrueExpressionDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, mnemosyneTrueExpressionName);
		String mnemosyneOperationName= deneName + " " + counterSuffix;
		String mnemosyneOperationPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS , mnemosyneOperationName).toString();
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_MNEMOSYNE_COUNTER , mnemosyneOperationPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_MNEMOSYNE_OPERATION);
		mnemosyneTrueExpressionDeneWordsJSONArray.put(deneword);
		
		
		//
		// next the mnemosyne operation
		//
		JSONObject mnemosyneOperationDene = new JSONObject();
		denesJSONArray.put(mnemosyneOperationDene);
		
		JSONArray mnemosyneOperationDeneWordsJSONArray = new JSONArray();
		mnemosyneOperationDene.put("DeneWords", mnemosyneOperationDeneWordsJSONArray);
		mnemosyneOperationDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorDeneChainTargetPointer);
		mnemosyneOperationDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, mnemosyneOperationName);
		
		mnemosyneOperationDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		mnemosyneOperationDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXECUTION_POSITION, 1, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		mnemosyneOperationDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_MNEMOSYNE_COUNTER , counterDeneWordTargetPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_MNEMOSYNE_OPERATION_COUNTER_POINTER);
		mnemosyneOperationDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_COUNTER_LIMIT, counterLimit, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_COUNTER_LIMIT);
		mnemosyneOperationDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_COUNTER_INCREMENT, counterIncrement, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_MNEMOSYNE_OPERATION);
		mnemosyneOperationDeneWordsJSONArray.put(deneword);
		
		
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
		String  deneType;
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
