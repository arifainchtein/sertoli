package com.teleonome.sertoli;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;

public class ExternalDataMultiColorLEDHomeoBoxGenerator extends HomeboxGenerator {

	Logger logger;

	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndex) {
		//
		// Getting the data
		//
		logger = Logger.getLogger(getClass());
		int nextActionValue=currentActionIndex;
		JSONObject sensorValueDene;
		String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
		String actionGroupName = homeboxSourceDataElement.getString("Action Group Name");
		//
		// where the exteral data lives in the denome, ie "@Egg:Purpose:External Data:Ra:BatteryVoltage"
		JSONObject dataSourceJSONObject = homeboxSourceDataElement.getJSONObject("Data Source");
		String dataSourcePointer = dataSourceJSONObject.getString("Data Source Pointer");
		String dataSourceUnits = dataSourceJSONObject.getString(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE);
		String dataSourceValueType = dataSourceJSONObject.getString(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE);
		//
		// use the dataSourcePointer to extract one of the comparators, which will be the deneword of the pointer
		// ie if datapointer is  "@Egg:Purpose:External Data:Ra:BatteryVoltage", then the maincomparator will be BatteryVoltage
		//
		String externalDataSourcePointer = homeboxSourceDataElement.getString("External Data Source Pointer");
		Identity externalDataSourceIdentity = new Identity(externalDataSourcePointer);
		String externalTeleonomeName= externalDataSourceIdentity.getTeleonomeName();
		String mainComparator = (new Identity(dataSourcePointer)).deneWordName;
		
		int ledPosition = homeboxSourceDataElement.getInt("LED Position");
		String pointerToMicroController = homeboxSourceDataElement.getString("Microcontroller Pointer");
		String actuatorActionListPointer = homeboxSourceDataElement.getString("Actuator Action List Pointer");
		String microControllerBaseCommand = homeboxSourceDataElement.getString("MicroControllerBaseCommand");
		String sourceAddress = homeboxSourceDataElement.getString("Provider");
		JSONArray thresholds = homeboxSourceDataElement.getJSONArray("Thresholds");
		JSONArray cases = homeboxSourceDataElement.getJSONArray("Cases");
		
		//
		// processing
		//


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
		// Create the DeneWordCarrier Dene for the Thresholds

		/* The Thresholds become Control Parameters
			//  at @Egg:Internal:Descriptive:Control Parameters
			//
		 */	
		JSONObject denewordCarrierForThresholdsDene = new JSONObject();
		denesJSONArray.put(denewordCarrierForThresholdsDene);
		denewordCarrierForThresholdsDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, actionGroupName);
		denewordCarrierForThresholdsDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_DENEWORD_CARRIER);
		String thresholdTargetPointer=new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_DESCRIPTIVE, TeleonomeConstants.DENE_CONTROL_PARAMETERS ).toString();
		denewordCarrierForThresholdsDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, thresholdTargetPointer);
		deneWordsJSONArray = new JSONArray();
		denewordCarrierForThresholdsDene.put("DeneWords", deneWordsJSONArray);
		JSONObject threshold;
		String thresholdName;
		
		Object thresholdValue;
		
		
		Hashtable<String, JSONObject> thresholdNameIndex = new Hashtable();
		for(int i=0;i<thresholds.length();i++){
			threshold = thresholds.getJSONObject(i);
			thresholdName = threshold.getString(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE);
			thresholdValue = threshold.get("Threshold Value");
			
			thresholdNameIndex.put(thresholdName, threshold);
			deneword = Utils.createDeneWordJSONObject(thresholdName , thresholdValue, dataSourceUnits, TeleonomeConstants.DATATYPE_DOUBLE, true);
			deneWordsJSONArray.put(deneword);
		}
		/*
		 * Create the Dene that will have the dataSource, which will be in Egg:Purpose:External Data:$externalTeleonomeName:$mainComparator
		 * ie @Cleo:Purpose:External Data:Ra:BatteryVoltage
		 * The dene will have the following  denewords
		 * 1)ExternalDataStatus 
		 * 2)Pulse Timestamp
		 * 3)Pulse Timestamp Millis
		 * 4) the actual value $mainComparator
		 * 
		 *  The first three are required by the spec for all External Data denes
		 *  
		 *  finally a deneword has to be 
		 */
		
		JSONObject externalDataDeneJSONObject = new JSONObject();
		denesJSONArray.put(externalDataDeneJSONObject);
		JSONArray externalDataDeneWordsJSONArray = new JSONArray();
		externalDataDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_EXTERNAL_DATA_SOURCE);
		externalDataDeneJSONObject.put("DeneWords", externalDataDeneWordsJSONArray);
		String externalDataDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_PURPOSE,TeleonomeConstants.DENECHAIN_EXTERNAL_DATA, externalTeleonomeName ).toString();
		externalDataDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, externalTeleonomeName);
		externalDataDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, externalDataDeneChainTargetPointer);
		

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EXTERNAL_DATA_STATUS, TeleonomeConstants.BOOTSTRAP_DANGER, null, TeleonomeConstants.DATATYPE_STRING, true);
		externalDataDeneWordsJSONArray.put(deneword);
		
		// the status, pulsetimestamp and pulsetimestamp millis always have the same address
		//
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_STATUS, "NA", null, TeleonomeConstants.DATATYPE_STRING, true);
	    String statusDataLocationPointer = (new Identity(externalTeleonomeName, TeleonomeConstants.NUCLEI_PURPOSE,TeleonomeConstants.DENECHAIN_OPERATIONAL_DATA, TeleonomeConstants.DENE_VITAL, TeleonomeConstants.DENEWORD_STATUS )).toString();
		deneword.put(TeleonomeConstants.DENEWORD_DATA_LOCATION_ATTRIBUTE,statusDataLocationPointer);
		externalDataDeneWordsJSONArray.put(deneword);
		
		
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.PULSE_TIMESTAMP,"", null, TeleonomeConstants.DATATYPE_STRING, true);
		statusDataLocationPointer = new Identity(externalTeleonomeName, TeleonomeConstants.PULSE_TIMESTAMP).toString();
		deneword.put(TeleonomeConstants.DENEWORD_DATA_LOCATION_ATTRIBUTE,statusDataLocationPointer);
		externalDataDeneWordsJSONArray.put(deneword);
		
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.PULSE_TIMESTAMP_MILLISECONDS, 0, null, TeleonomeConstants.DATATYPE_LONG, true);
		statusDataLocationPointer = new Identity(externalTeleonomeName, TeleonomeConstants.PULSE_TIMESTAMP_MILLISECONDS).toString();
		deneword.put(TeleonomeConstants.DENEWORD_DATA_LOCATION_ATTRIBUTE,statusDataLocationPointer);
		externalDataDeneWordsJSONArray.put(deneword);
		
		
		//
		// the main value represented by $mainComparator with a data location of externalDataSourcePointer
		Object initial;
		if(dataSourceValueType.equals(TeleonomeConstants.DATATYPE_DOUBLE)) {
			initial=0.0;
		}else if(dataSourceValueType.equals(TeleonomeConstants.DATATYPE_INTEGER)) {
			initial=0;
		}else if(dataSourceValueType.equals(TeleonomeConstants.DATATYPE_LONG)) {
			initial=0;
		}else if(dataSourceValueType.equals(TeleonomeConstants.DATATYPE_STRING)) {
			initial="";
		}else {
			initial="";
		}
		deneword = Utils.createDeneWordJSONObject(mainComparator, initial, dataSourceUnits, dataSourceValueType, true);
		deneword.put(TeleonomeConstants.DENEWORD_DATA_LOCATION_ATTRIBUTE,externalDataSourcePointer);
		externalDataDeneWordsJSONArray.put(deneword);
		
		
		
		
		
		
		
		
		/*
		   Create the DeneWordCarrier Dene for the ActionList
			Every Dene of type action needs to have a deneword added to the 
			actuatorActionListPointer
			
		 */	

		JSONObject denewordCarrierForActionListDene = new JSONObject();
		denesJSONArray.put(denewordCarrierForActionListDene);
		
		denewordCarrierForActionListDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "DeneWord Carrier for ActionList");
		denewordCarrierForActionListDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_DENEWORD_CARRIER);
		denewordCarrierForActionListDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorActionListPointer);
		JSONArray actionListDeneWordsJSONArray = new JSONArray();
		denewordCarrierForActionListDene.put("DeneWords", actionListDeneWordsJSONArray);
		
		//
		// The cases, every case produces 
		// three denes, an action dene, ansuccess task and a condition
		
		JSONObject caseSourceInfo, actionDeneJSONObject, successTaskDeneJSONObject, conditionDeneJSONObject;
		String caseName, expression, conditionName, conditionDeneName, conditionDenePointer, actionSuccessTaskDenePointer, actionSuccessTaskDeneName;
		int evalPos;
		
		String actuatorsDeneChainTargetPointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS ).toString();
		String microControllerCommand = null, caseValue, caseThreshold;
		JSONObject caseThresholdJSONObject;
		String updateDeneIdentityPointer, actionDenePointer;
		for(int i=0;i<cases.length();i++){
			caseSourceInfo = cases.getJSONObject(i);
			caseName = caseSourceInfo.getString(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE);
			expression = caseSourceInfo.getString(TeleonomeConstants.DENEWORD_EXPRESSION);
			conditionName = caseSourceInfo.getString(TeleonomeConstants.CONDITION_NAME);
			evalPos = caseSourceInfo.getInt(TeleonomeConstants.EVALUATION_POSITION);
			caseThreshold=caseSourceInfo.getString("Threshold");
			caseThresholdJSONObject = (JSONObject)thresholdNameIndex.get(caseThreshold);
			caseValue=caseThresholdJSONObject.getString("Status Value");
			microControllerCommand = microControllerBaseCommand + "#" + ledPosition + "#" +  caseValue + "#Ok";

			conditionDeneName = caseName+ "Condition";
			actionSuccessTaskDeneName = caseName + " Success Tasks";
			conditionDenePointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, conditionDeneName ).toString();
			
			actionSuccessTaskDenePointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actionSuccessTaskDeneName ).toString();
			actionDenePointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, caseName ).toString();

			//
			// create the action dene
			//
			actionDeneJSONObject = new JSONObject();
			denesJSONArray.put(actionDeneJSONObject);
			
			//
			// add this dene as a deneword to the action list
			//
            deneword = Utils.createDeneWordJSONObject(caseName, actionDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_ACTION);
			actionListDeneWordsJSONArray.put(deneword); 	
			
			
			actionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, caseName);
			actionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTION);
			actionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
			deneWordsJSONArray = new JSONArray();
			actionDeneJSONObject.put("DeneWords", deneWordsJSONArray);

			deneword = Utils.createDeneWordJSONObject("Codon", caseName, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION, evalPos, null, TeleonomeConstants.DATATYPE_INTEGER, true);
			deneWordsJSONArray.put(deneword);
			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
			deneWordsJSONArray.put(deneword);
			deneword = Utils.createDeneWordJSONObject("Actuator Command Code True Expression", TeleonomeConstants.COMMANDS_DO_NOTHING, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			deneword = Utils.createDeneWordJSONObject("Expression", "("+conditionName +")", null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);

			deneword = Utils.createDeneWordJSONObject(conditionName, conditionDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_CONDITION_POINTER);
			deneWordsJSONArray.put(deneword);
			
			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_ACTION_SUCCESS_TASK_TRUE_EXPRESSION, actionSuccessTaskDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_ACTION_SUCCESS_TASKS);
			deneWordsJSONArray.put(deneword);
			
			//
			// create the Success tasks
			//
			successTaskDeneJSONObject = new JSONObject();
			denesJSONArray.put(successTaskDeneJSONObject);
			successTaskDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, actionSuccessTaskDeneName);
			successTaskDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
			deneWordsJSONArray = new JSONArray();
			successTaskDeneJSONObject.put("DeneWords", deneWordsJSONArray);
			deneword = Utils.createDeneWordJSONObject(actionSuccessTaskDeneName, microControllerCommand, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_UPDATE_DENEWORD_VALUE);
			//
			// the target attribute is "Update " + mainComparator
			updateDeneIdentityPointer =  (new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS,"Update " + mainComparator, TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION)).toString();
			deneword.put(TeleonomeConstants.DENEWORD_TARGET_ATTRIBUTE, updateDeneIdentityPointer);
			deneWordsJSONArray.put(deneword);
			
			
			//
			// create the Condition tasks
			//
			conditionDeneJSONObject = new JSONObject();
			denesJSONArray.put(conditionDeneJSONObject);
			conditionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, conditionDeneName);
			conditionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTUATOR_CONDITION);
			conditionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
			deneWordsJSONArray = new JSONArray();
			conditionDeneJSONObject.put("DeneWords", deneWordsJSONArray);
			deneword = Utils.createDeneWordJSONObject("Codon", caseName, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			
			deneword = Utils.createDeneWordJSONObject("Expression", expression, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneWordsJSONArray.put(deneword);
			
			deneword = Utils.createDeneWordJSONObject("On Lack Of Data", false, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
			deneWordsJSONArray.put(deneword);
			//
			// now create the conditional variable pointers by analysing the expression,
			// by design the expression will be of the form
			//
			//     "Expression":"BatteryVoltage>YellowVoltageThreshold && BatteryVoltage<GreenVoltageThreshold"  
            //
			// you need to identify every one of the elements in the expression and create a deneword.  One element is always
			// the mainComparator defined above (in this example the maincomparator is BatteryVoltage
			//
			// first do the one for the mainComparator
			//
			deneword = Utils.createDeneWordJSONObject(mainComparator, dataSourcePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_CONDITION_VARIABLE_POINTER);
			deneWordsJSONArray.put(deneword);
			//
			// now loop over all the thresholds and see if the expression contains those terms, if so add a deneword
			caseThresholdJSONObject = (JSONObject)thresholdNameIndex.get(caseThreshold);
			logger.debug("line 331 caseThreshold=" + caseThreshold);
			logger.debug("line 331 caseThresholdJSONObject=" + caseThresholdJSONObject.toString(4));
			
			for(Enumeration<String> keys = thresholdNameIndex.keys();keys.hasMoreElements();) {
				
				thresholdName = keys.nextElement();
				logger.debug("line 335 thresholdName=" + thresholdName + " expression=" + expression);
				if(expression.contains(thresholdName)) {
					logger.debug("line 339 Creating extra");
					thresholdTargetPointer=new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_DESCRIPTIVE, TeleonomeConstants.DENE_CONTROL_PARAMETERS, thresholdName ).toString();
					deneword = Utils.createDeneWordJSONObject(thresholdName, thresholdTargetPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
					deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_CONDITION_VARIABLE_POINTER);
					deneWordsJSONArray.put(deneword);
				}
			}
		}
		//
		// because this is the external data, there are three  more denes needed to account for when the data is stale
		// an action dene, a condition dene and a success tasks false expression dene, 
		//
		// do the action first
		//
		JSONObject staleDataActionDeneJSONObject = new JSONObject();
		denesJSONArray.put(staleDataActionDeneJSONObject);
		
		
		String staleDataActionDeneName = externalTeleonomeName  + " " + mainComparator + " Is Stale Action";
		
		//
		// add this dene as a deneword to the action list
		//
		actionDenePointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, staleDataActionDeneName ).toString();
        deneword = Utils.createDeneWordJSONObject(staleDataActionDeneName, actionDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_ACTION);
		actionListDeneWordsJSONArray.put(deneword); 	
		
		
		staleDataActionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, staleDataActionDeneName);
		staleDataActionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTION);
		staleDataActionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
		deneWordsJSONArray = new JSONArray();
		staleDataActionDeneJSONObject.put("DeneWords", deneWordsJSONArray);
		deneword = Utils.createDeneWordJSONObject("Codon", staleDataActionDeneName, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		deneWordsJSONArray.put(deneword);
		
		//
		// this action should be the last one of the group to be executed
		// therefore it should be equal to 1 + total cases
		evalPos = 1 + cases.length();
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION,evalPos, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		deneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION, TeleonomeConstants.COMMANDS_DO_NOTHING, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_FALSE_EXPRESSION, TeleonomeConstants.COMMANDS_DO_NOTHING, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		//
		// the expression is made out f externalTeleonomeName and ExternalDataStatusCondition
		conditionName = externalTeleonomeName +"ExternalDataStatusCondition";
		deneword = Utils.createDeneWordJSONObject("Expression", "("+conditionName +")", null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		String externalDataStatusConditionDeneName = externalTeleonomeName  + " External Data Status Condition";
		
		conditionDenePointer=thresholdTargetPointer=new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, externalDataStatusConditionDeneName ).toString();
		deneword = Utils.createDeneWordJSONObject(conditionName, conditionDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_CONDITION_POINTER);
		deneWordsJSONArray.put(deneword);
		
		String actionSuccessFalseExpressionDeneName = staleDataActionDeneName + " Success Tasks False Expression";
		actionSuccessTaskDenePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actionSuccessFalseExpressionDeneName ).toString();
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_ACTION_SUCCESS_TASK_FALSE_EXPRESSION, actionSuccessTaskDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_ACTION_SUCCESS_TASKS);
		deneWordsJSONArray.put(deneword);
		
		
		//
		// next the condition dene
		//
		conditionDeneJSONObject = new JSONObject();
		denesJSONArray.put(conditionDeneJSONObject);
		// the condition name needs to be:
		// $externalteleonomeName External Data Status Condition"
		//
		// the conditionDeneName
		String updateCodon = (new Identity(pointerToMicroController)).deneWordName;
		
		conditionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, externalDataStatusConditionDeneName);
		conditionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTUATOR_CONDITION);
		conditionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
		deneWordsJSONArray = new JSONArray();
		conditionDeneJSONObject.put("DeneWords", deneWordsJSONArray);
		deneword = Utils.createDeneWordJSONObject("Codon", updateCodon, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject("Expression", "(ExternalDataStatus== SuccessValue)", null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject("On Lack Of Data", false, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		deneWordsJSONArray.put(deneword);
		
		//
		//Since the condition expression is always the same, ie  ExternalDataStatus==SuccessValue) crearte denewords for each term
		// For ExternalDataStatus the dataSourcePointer would be something like @Egg:Purpose:External Data:XXX:ExternalDataStatus
		// where XXX would be the external teleonome name which is given by the variable externalTeleonomeName
		dataSourcePointer = thresholdTargetPointer=new Identity("Egg", TeleonomeConstants.NUCLEI_PURPOSE,TeleonomeConstants.DENECHAIN_EXTERNAL_DATA, externalTeleonomeName, TeleonomeConstants.EXTERNAL_DATA_STATUS ).toString();
		deneword = Utils.createDeneWordJSONObject("ExternalDataStatus", dataSourcePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_CONDITION_VARIABLE_POINTER);
		deneWordsJSONArray.put(deneword);
		//
		//succesvalue
		//
		dataSourcePointer = thresholdTargetPointer=new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_REFERENCE, TeleonomeConstants.DENE_STATUS, TeleonomeConstants.BOOTSTRAP_SUCCESS ).toString();
		deneword = Utils.createDeneWordJSONObject("SuccessValue", dataSourcePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_CONDITION_VARIABLE_POINTER);
		deneWordsJSONArray.put(deneword);
		
		
		//
		// finally the action success tass false expression
		//
		
		successTaskDeneJSONObject = new JSONObject();
		denesJSONArray.put(successTaskDeneJSONObject);
		successTaskDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, actionSuccessFalseExpressionDeneName);
		successTaskDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
		deneWordsJSONArray = new JSONArray();
		successTaskDeneJSONObject.put("DeneWords", deneWordsJSONArray);
		String microControllerCommandValue = microControllerBaseCommand + "#" + ledPosition + "#stale#Ok";
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_ACTION_SUCCESS_TASK_FALSE_EXPRESSION, microControllerCommandValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_UPDATE_DENEWORD_VALUE);
		//
		// the target attribute is "Update " + mainComparator
		updateDeneIdentityPointer =  (new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS,"Update " + mainComparator, TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION)).toString();
		deneword.put(TeleonomeConstants.DENEWORD_TARGET_ATTRIBUTE, updateDeneIdentityPointer);
		deneWordsJSONArray.put(deneword);
		
		
		
		//
		// end of denes for stale data condition
		//
		
		
		
		
		//
		// After the cases, add one more dene
		// for the one that actually updates the 
		// microcontroller


		JSONObject updateMicroControllerActionDene = new JSONObject();
		denesJSONArray.put(updateMicroControllerActionDene);
		updateMicroControllerActionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainTargetPointer);
		updateMicroControllerActionDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "Update " + mainComparator);
		updateMicroControllerActionDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTION);
		
		//
		// add this dene as a deneword to the action list
		//
		actionDenePointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, "Update " + mainComparator ).toString();
        deneword = Utils.createDeneWordJSONObject("Update " + mainComparator, actionDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_ACTION);
		actionListDeneWordsJSONArray.put(deneword); 
		
		
		deneWordsJSONArray = new JSONArray();
		updateMicroControllerActionDene.put("DeneWords", deneWordsJSONArray);
		//
		// in this the codon is the deneword of the MicroController
		//
		
		
		deneword = Utils.createDeneWordJSONObject("Codon", updateCodon, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		//
		// the evaluation position is calculated based on 2+totalNumberOfCases
		int updateActionEvaluationPosition = 2+thresholdNameIndex.size();
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION, updateActionEvaluationPosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		deneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("Active", true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		deneWordsJSONArray.put(deneword);
		//
		// this "Actuator Command Code True Expression" is the ne that will change as the other actions are exeecuted
		// so the value here is just something to start with, so using the variable microControllerCommand
		// which was calculated for every case means that the deneword below will have the expression of the last case
		// which by convention is the data is stale
	
		deneword = Utils.createDeneWordJSONObject("Actuator Command Code True Expression", microControllerCommand, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("Expression", "1==1", null, TeleonomeConstants.DATATYPE_STRING, true);
		deneWordsJSONArray.put(deneword);
		
		

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

}
