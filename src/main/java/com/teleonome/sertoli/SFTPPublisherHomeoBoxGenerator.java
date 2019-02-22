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

public class SFTPPublisherHomeoBoxGenerator extends HomeboxGenerator{

	Logger logger;

	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndex, ArrayList externalDataDenesCreated) {
		//
		// Getting the data
		//
		logger = Logger.getLogger(getClass());
		int nextActionValue=currentActionIndex;
		JSONObject sensorValueDene;
		String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
		String SFTPServerIpAddress = homeboxSourceDataElement.getString("SFTP Server Ip Address");
		String privateKeyFileName = homeboxSourceDataElement.getString("SFTP Key File Name");
		int processingQueuePosition = homeboxSourceDataElement.getInt("Processing Queue Position");

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
		// Create the Microcontroller Dene
		//
		String controllerName="SFTP Publishing Controller";
		JSONObject microControllerDeneJSONObject= new JSONObject();
		denesJSONArray.put(microControllerDeneJSONObject);
		JSONArray microControllerDeneWordsJSONArray = new JSONArray();
		microControllerDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER);
		microControllerDeneJSONObject.put("DeneWords", microControllerDeneWordsJSONArray);


		String className = "com.teleonome.framework.microcontroller.sftppublisher.SFTPPublisherMicroController";
		String componentsDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS ).toString();
		microControllerDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, controllerName);
		microControllerDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneChainTargetPointer);

		deneword = Utils.createDeneWordJSONObject("Processing Queue Position", processingQueuePosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		microControllerDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(controllerName + " Class Name",className, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_MICROCONTROLLER_PROCESSING_CLASSNAME);
		microControllerDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, controllerName, null, TeleonomeConstants.DATATYPE_STRING, true);
		microControllerDeneWordsJSONArray.put(deneword);

		String configParamDeneName=controllerName + " Config Parameter List";
		String configParamListPointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, controllerName + " Config Parameter List").toString();
		deneword = Utils.createDeneWordJSONObject(configParamDeneName,configParamListPointer, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_MICROCONTROLLER_CONFIG_PARAM_LIST);
		microControllerDeneWordsJSONArray.put(deneword);

		//
		// create the "Dene Type": "Microcontroller Config Parameter List",
		//
		JSONObject microControllerConfigParamListDeneJSONObject= new JSONObject();
		denesJSONArray.put(microControllerConfigParamListDeneJSONObject);
		JSONArray microControllerConfigParamListDeneWordsJSONArray = new JSONArray();
		microControllerConfigParamListDeneJSONObject.put("DeneWords", microControllerConfigParamListDeneWordsJSONArray);
		
		microControllerConfigParamListDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneChainTargetPointer);
		microControllerConfigParamListDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "SFTP Publisher Controller Config Parameter List");
		microControllerConfigParamListDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER_LIST);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, controllerName, null, TeleonomeConstants.DATATYPE_STRING, true);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String publishContentPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "SFTP Publish Contents" ).toString();
		deneword = Utils.createDeneWordJSONObject("Publish Content",publishContentPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String sftpKeyFileNamePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "SFTP Key File Name" ).toString();
		deneword = Utils.createDeneWordJSONObject("SFTP Key File Name",sftpKeyFileNamePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String sftpServerIpAddressPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "SFTP Server IP Address" ).toString();
		deneword = Utils.createDeneWordJSONObject("SFTP Server IP Address",sftpServerIpAddressPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String settingsUpdatePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "Settings Update" ).toString();
		deneword = Utils.createDeneWordJSONObject("Settings Update",settingsUpdatePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String processingQueuePositionPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "Processing Queue Position" ).toString();
		deneword = Utils.createDeneWordJSONObject("Processing Queue Position",processingQueuePositionPointer, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);
		//
		// now create the denes for each of the config param 
		//	
		JSONObject microControllerConfigParamDeneJSONObject = getConfigParamDene(componentsDeneChainTargetPointer,controllerName, "SFTP Publish Contents",TeleonomeConstants.COMMANDS_PUBLISH_TELEONOME_PULSE);
		denesJSONArray.put(microControllerConfigParamDeneJSONObject);

		microControllerConfigParamDeneJSONObject = getConfigParamDene(componentsDeneChainTargetPointer,controllerName, "SFTP Key File Name",privateKeyFileName);
		denesJSONArray.put(microControllerConfigParamDeneJSONObject);

		microControllerConfigParamDeneJSONObject = getConfigParamDene(componentsDeneChainTargetPointer,controllerName, "SFTP Server IP Address",SFTPServerIpAddress);
		denesJSONArray.put(microControllerConfigParamDeneJSONObject);

		microControllerConfigParamDeneJSONObject = getConfigParamDene(componentsDeneChainTargetPointer,controllerName, "Processing Queue Position",processingQueuePosition);
		denesJSONArray.put(microControllerConfigParamDeneJSONObject);

		// dont use getConfigParams with Settings update because one of the denewords needs a target attribute
		// and also it has a denetype
		
		microControllerConfigParamDeneJSONObject= new JSONObject();
		JSONArray microControllerConfigParamDeneWordsJSONArray = new JSONArray();
		microControllerConfigParamDeneJSONObject.put("DeneWords", microControllerConfigParamDeneWordsJSONArray);
		microControllerConfigParamDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneChainTargetPointer);
		microControllerConfigParamDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "Settings Update");
		microControllerConfigParamDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_DENOMIC_OPERATION);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, controllerName, null, TeleonomeConstants.DATATYPE_STRING, true);
		microControllerConfigParamDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject( "Settings Update", false, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_UPDATE_DENEWORD_VALUE);
		String target =  new Identity("Egg", TeleonomeConstants.NUCLEI_HUMAN_INTERFACE,TeleonomeConstants.DENECHAIN_TYPE_HUMAN_INTERFACE_CONTROL_PARAMETERS, "Settings", TeleonomeConstants.DENEWORD_TYPE_HUMAN_INTERFACE_WEB_PAGE_INCLUDE_IN_NAVIGATION ).toString();
		deneword.put(TeleonomeConstants.DENEWORD_TARGET_ATTRIBUTE,target);
		
		microControllerConfigParamDeneWordsJSONArray.put(deneword);
			
		denesJSONArray.put(microControllerConfigParamDeneJSONObject);
		/*
		 *  create the actuator and the actions
		 */
		String actuatorName = "SFTP Publisher Actuator";
		String actionInPulseName = "SFTP Publisher Prepare Publish";
		String actionByMutationName = "SFTP Publisher Publish Pulse";
		String actuatorActionList = "SFTP Publisher Actuator Actions";

		String actuatorsDeneChainPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS).toString();
		//
		// Create the actuator Dene
		//
		JSONObject actuatorDene = new JSONObject();
		denesJSONArray.put(actuatorDene);

		JSONArray actuatorDeneWordsJSONArray = new JSONArray();
		actuatorDene.put("DeneWords", actuatorDeneWordsJSONArray);

		actuatorDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, actuatorName);
		actuatorDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTUATOR);
		actuatorDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainPointer);

		String micrControllerPointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, controllerName ).toString();
		deneword = Utils.createDeneWordJSONObject("Pointer to Microcontroller", micrControllerPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_MICROCONTROLLER_POINTER);
		actuatorDeneWordsJSONArray.put(deneword);

		nextActionValue++;
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXECUTION_POSITION, nextActionValue, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actuatorDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		actuatorDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		actuatorDeneWordsJSONArray.put(deneword);


		String actuatorActionsPointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actuatorActionList ).toString();
		deneword = Utils.createDeneWordJSONObject(actuatorActionsPointer, actuatorActionsPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION_LIST);
		actuatorDeneWordsJSONArray.put(deneword);

		//
		// now create the action list dene
		//
		JSONObject actionListDene = new JSONObject();
		denesJSONArray.put(actionListDene);

		JSONArray actionListDeneWordsJSONArray = new JSONArray();
		actionListDene.put("DeneWords", actionListDeneWordsJSONArray);

		actionListDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, actuatorActionList);
		actionListDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION_LIST);
		actionListDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainPointer);

		String actionPointer =  new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actionInPulseName ).toString();
		deneword = Utils.createDeneWordJSONObject(actuatorActionList, actionPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_ACTION);
		actionListDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		actionListDeneWordsJSONArray.put(deneword);

		//
		// next the action dene represented by actionInPulseName
		//
		JSONObject preparePublishDene = new JSONObject();
		denesJSONArray.put(preparePublishDene);

		JSONArray preparePublishDeneWordsJSONArray = new JSONArray();
		preparePublishDene.put("DeneWords", preparePublishDeneWordsJSONArray);

		preparePublishDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, actionInPulseName);
		preparePublishDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION);
		preparePublishDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainPointer);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTION_EXECUTION_POINT, TeleonomeConstants.DENEWORD_ACTION_EXECUTION_POINT_POST_PULSE, null, TeleonomeConstants.DATATYPE_STRING, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION, 1, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION , "Publish", null, TeleonomeConstants.DATATYPE_STRING, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXPRESSION , "1==1", null, TeleonomeConstants.DATATYPE_STRING, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_CODE_TYPE, TeleonomeConstants.TELEONOME_SECURITY_CODE , null, TeleonomeConstants.DATATYPE_STRING, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		
		//
		// next the action dene represented by actionByMutationName
		//
		JSONObject actionByMutationDene = new JSONObject();
		denesJSONArray.put(actionByMutationDene);

		JSONArray actionByMutationDeneWordsJSONArray = new JSONArray();
		actionByMutationDene.put("DeneWords", actionByMutationDeneWordsJSONArray);

		actionByMutationDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, actionByMutationName);
		actionByMutationDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE,TeleonomeConstants.DENE_TYPE_ACTION);
		actionByMutationDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, actuatorsDeneChainPointer);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, actuatorName, null, TeleonomeConstants.DATATYPE_STRING, true);
		actionByMutationDeneWordsJSONArray.put(deneword);
	
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		actionByMutationDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.EVALUATION_POSITION, 5, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		actionByMutationDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION , "Publish", null, TeleonomeConstants.DATATYPE_STRING, true);
		actionByMutationDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXPRESSION , "1==1", null, TeleonomeConstants.DATATYPE_STRING, true);
		actionByMutationDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_CODE_TYPE, TeleonomeConstants.TELEONOME_SECURITY_CODE , null, TeleonomeConstants.DATATYPE_STRING, true);
		actionByMutationDeneWordsJSONArray.put(deneword);
		
		/*
		 *  end of the creating the actuator and the actions
		 */


		//
		// create an action that will create a mutation
		//
		JSONObject actionDene = new JSONObject();
		actionJSONArray.put(actionDene);
		JSONArray actionsDeneWordsJSONArray = new JSONArray();
		actionDene.put("DeneWords", actionsDeneWordsJSONArray);

		actionDene.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, "Publish Via SFTP");
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

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_MUTATION_NAME,"Publish Via SFTP" , null, TeleonomeConstants.DATATYPE_STRING, true);
		actionsDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.MUTATION_TYPE_ATTRIBUTE,TeleonomeConstants.MUTATION_TYPE_STATE , null, TeleonomeConstants.DATATYPE_STRING, true);
		actionsDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("Execution Mode",TeleonomeConstants.MUTATION_EXECUTION_MODE_IMMEDIATE , null, TeleonomeConstants.DATATYPE_STRING, true);
		actionsDeneWordsJSONArray.put(deneword);
		
		/*
		 * finally create a dene where the target is 
		 */

		String mutationActionDeneTargetPointer = new Identity("Egg","Publish Via SFTP", "Action To Execute" ).toString();

		JSONObject mutationActionDeneJSONObject= new JSONObject();
		denesJSONArray.put(mutationActionDeneJSONObject);

		JSONArray mutationActionDeneJSONObjectDeneWordsJSONArray = new JSONArray();
		mutationActionDeneJSONObject.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, "Publish Via SFTP");
		mutationActionDeneJSONObject.put("DeneWords", mutationActionDeneJSONObjectDeneWordsJSONArray);
		mutationActionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, mutationActionDeneTargetPointer);

		String mutationActuatorPointer = new Identity("Egg",TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actuatorName).toString();
		deneword = Utils.createDeneWordJSONObject( actuatorName, mutationActuatorPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_POINTER);
		mutationActionDeneJSONObjectDeneWordsJSONArray.put(deneword);


		String mutationActionPointer = new Identity("Egg",TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_ACTUATORS, actionByMutationName ).toString();
		deneword = Utils.createDeneWordJSONObject("SFTP Publisher Publish", mutationActionPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE,TeleonomeConstants.DENEWORD_TYPE_ACTION);
		mutationActionDeneJSONObjectDeneWordsJSONArray.put(deneword);

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

	private JSONObject getConfigParamDene(String componentsDeneChainTargetPointer,String controllerName, String configName, Object configValue ) {
		JSONObject microControllerConfigParamDeneJSONObject= new JSONObject();
		JSONArray microControllerConfigParamDeneWordsJSONArray = new JSONArray();
		microControllerConfigParamDeneJSONObject.put("DeneWords", microControllerConfigParamDeneWordsJSONArray);
		microControllerConfigParamDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneChainTargetPointer);
		microControllerConfigParamDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, configName);
		JSONObject deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, controllerName, null, TeleonomeConstants.DATATYPE_STRING, true);
		microControllerConfigParamDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(configName, configValue, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		microControllerConfigParamDeneWordsJSONArray.put(deneword);
		return microControllerConfigParamDeneJSONObject;
	}
}
