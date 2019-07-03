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

public class MutationBasedActionHomeoBoxGenerator extends HomeboxGenerator {
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
		String mutationName = homeboxSourceDataElement.getString(TeleonomeConstants.SPERM_ACTION_DENEWORD_MUTATION_NAME);
		String microcontrollerPointer = homeboxSourceDataElement.getString("Microcontroller Pointer");
		String actuatorName = homeboxSourceDataElement.getString("Actuator Name");
		String actionName = homeboxSourceDataElement.getString("Action Name");
		String actuatorCommandTrueExpression = homeboxSourceDataElement.getString(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION);
		int evaluationPosition = homeboxSourceDataElement.getInt("Evaluation Position");
		
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
		// create an action that will create a mutation
		//
		JSONObject actionDene = new JSONObject();
		actionJSONArray.put(actionDene);
		JSONArray actionsDeneWordsJSONArray = new JSONArray();
		actionDene.put("DeneWords", actionsDeneWordsJSONArray);

		actionDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, mutationName + " Mutation Action");
		actionDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.SPERM_DENE_TYPE_CREATE_MUTATION);
		String mutationsPointer = new Identity("Egg", TeleonomeConstants.SPERM_HYPOTHALAMUS_MUTATIONS ).toString();
		actionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, mutationsPointer);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		actionsDeneWordsJSONArray.put(deneword);
		nextActionValue++;
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, nextActionValue, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actionsDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT,TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actionsDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_MUTATION_NAME,mutationName , null, TeleonomeConstants.DATATYPE_STRING, true);
		actionsDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.MUTATION_TYPE_ATTRIBUTE,TeleonomeConstants.MUTATION_TYPE_STATE , null, TeleonomeConstants.DATATYPE_STRING, true);
		actionsDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("Execution Mode",TeleonomeConstants.MUTATION_EXECUTION_MODE_IMMEDIATE , null, TeleonomeConstants.DATATYPE_STRING, true);
		actionsDeneWordsJSONArray.put(deneword);
		
		/*
		 * Create the dene that would go in the on Load
		 */
		String mutationOnLoadDeneTargetPointer = new Identity("Egg",mutationName, TeleonomeConstants.DENECHAIN_ON_LOAD_MUTATION ).toString();

		JSONObject mutationOnLoadDeneJSONObject= new JSONObject();
		denesJSONArray.put(mutationOnLoadDeneJSONObject);

		JSONArray mutationOnLoadDeneJSONObjectDeneWordsJSONArray = new JSONArray();
		mutationOnLoadDeneJSONObject.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, "Update DeneWord");
		mutationOnLoadDeneJSONObject.put("DeneWords", mutationOnLoadDeneJSONObjectDeneWordsJSONArray);
		mutationOnLoadDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, mutationOnLoadDeneTargetPointer);
		mutationOnLoadDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.MUTATION_COMMAND_SET_DENEWORD);
		deneword = Utils.createDeneWordJSONObject( TeleonomeConstants.MUTATION_COMMAND_SET_DENEWORD, actuatorCommandTrueExpression, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		mutationOnLoadDeneJSONObjectDeneWordsJSONArray.put(deneword);
		logger.debug("mutationOnLoadDeneJSONObject=" + mutationOnLoadDeneJSONObject);
		
		/*
		 *create the action list dene that will go into the Actions ToExecite denechain of the mutation 
		 */

		String mutationActionDeneTargetPointer = new Identity("Egg",mutationName, TeleonomeConstants.DENECHAIN_ACTIONS_TO_EXECUTE ).toString();

		JSONObject mutationActionDeneJSONObject= new JSONObject();
		denesJSONArray.put(mutationActionDeneJSONObject);

		JSONArray mutationActionDeneJSONObjectDeneWordsJSONArray = new JSONArray();
		mutationActionDeneJSONObject.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, actionName + " List");
		mutationActionDeneJSONObject.put("DeneWords", mutationActionDeneJSONObjectDeneWordsJSONArray);
		mutationActionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, mutationActionDeneTargetPointer);
		mutationActionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_ACTION_LIST);
		
		String mutationActuatorPointer = new Identity("Egg",TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actuatorName).toString();
		deneword = Utils.createDeneWordJSONObject( actuatorName, mutationActuatorPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_POINTER);
		mutationActionDeneJSONObjectDeneWordsJSONArray.put(deneword);


		String mutationActionPointer = new Identity("Egg",TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actionName ).toString();
		deneword = Utils.createDeneWordJSONObject(actionName, mutationActionPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_ACTION);
		mutationActionDeneJSONObjectDeneWordsJSONArray.put(deneword);
		
		logger.debug("mutationActionDeneJSONObject=" + mutationActionDeneJSONObject);
	
		
		
		//
		// next the action dene in Actuators
		//
		JSONObject actuatorActionDene = new JSONObject();
		denesJSONArray.put(actuatorActionDene);
		String actuatorChainPointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS).toString();
		
		JSONArray actuatorActionDeneWordsJSONArray = new JSONArray();
		actuatorActionDene.put("DeneWords", actuatorActionDeneWordsJSONArray);
		actuatorActionDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorChainPointer);
		actuatorActionDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, actionName);
		actuatorActionDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		actuatorActionDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		actuatorActionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION, evaluationPosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actuatorActionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXPRESSION , actuatorCommandTrueExpression, null, TeleonomeConstants.DATATYPE_STRING, true);
		actuatorActionDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION , actuatorCommandTrueExpression, null, TeleonomeConstants.DATATYPE_STRING, true);
		actuatorActionDeneWordsJSONArray.put(deneword);
		
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
		String  deneType, deneName;
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
	logger.debug("line 200, returning denesJSONArray=" + denesJSONArray.length());
	return homeBoxProcessingResultJSONObject;
	}

	@Override
	public ArrayList<String> getExternalTeleonomeNames() {
		return null;
	}

}
