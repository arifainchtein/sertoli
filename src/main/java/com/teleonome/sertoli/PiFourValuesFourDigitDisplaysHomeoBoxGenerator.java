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

public class PiFourValuesFourDigitDisplaysHomeoBoxGenerator extends HomeboxGenerator{
	Logger logger;
	ArrayList<String> newExternalTeleonomeNames = new ArrayList<String>();
	
	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndex, ArrayList externalDataDenesCreated) {
		//
		// Getting the data
		//
		logger = Logger.getLogger(getClass());
		int nextActionValue=currentActionIndex;
		JSONObject sensorValueDene;
		String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
		String actionGroupName = homeboxSourceDataElement.getString("Action Group Name");
		String provider = homeboxSourceDataElement.getString("Provider");
		JSONArray displays = homeboxSourceDataElement.getJSONArray("Displays");
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
		// create the microcontroller dene and its config params
		//

		
		//
		// Create the Microcontroller Dene
		//
		String controllerName="Pi Four Values Four Digit Displays Controller";
		JSONObject microControllerDeneJSONObject= new JSONObject();
		denesJSONArray.put(microControllerDeneJSONObject);
		JSONArray microControllerDeneWordsJSONArray = new JSONArray();
		microControllerDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER);
		microControllerDeneJSONObject.put("DeneWords", microControllerDeneWordsJSONArray);


		String className = "com.teleonome.framework.microcontroller.pifourvaluesfourdigitdisplaysmicrocontroller.PiFourValuesFourDigitDisplaysMicroController";
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
		microControllerConfigParamListDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "Pi Four Values Four Digit Displays Controller Config Parameter List");
		microControllerConfigParamListDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER_LIST);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, controllerName, null, TeleonomeConstants.DATATYPE_STRING, true);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String publishContentPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "Display 1" ).toString();
		deneword = Utils.createDeneWordJSONObject("Display 1",publishContentPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String sftpKeyFileNamePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "Display 2" ).toString();
		deneword = Utils.createDeneWordJSONObject("Display 2",sftpKeyFileNamePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String sftpServerIpAddressPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "Display 3" ).toString();
		deneword = Utils.createDeneWordJSONObject("Display 3",sftpServerIpAddressPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		String settingsUpdatePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_COMPONENTS, "Display 4" ).toString();
		deneword = Utils.createDeneWordJSONObject("Display 4",settingsUpdatePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		microControllerConfigParamListDeneWordsJSONArray.put(deneword);

		
		//
		// now create the denes for each of the config param 
		//	
		JSONObject microControllerConfigParamDeneJSONObject;
		JSONObject display;
		for(int i=0;i<displays.length();i++){
			display = displays.getJSONObject(i);
			microControllerConfigParamDeneJSONObject = getConfigParamDene(componentsDeneChainTargetPointer,controllerName,display);
			denesJSONArray.put(microControllerConfigParamDeneJSONObject);
		}
		

		
			
	
		/*
		 *  create the actuator and the actions
		 */
		String actuatorName = "Pi Four Values Four Digit Displays Actuator";
		String actionInPulseName = "Pi Four Values Four Digit Displays Update";
		
		String actuatorActionList = "Pi Four Values Four Digit Displays Actuator Actions";

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
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION , "Publish Via SFTP", null, TeleonomeConstants.DATATYPE_STRING, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXPRESSION , "1==1", null, TeleonomeConstants.DATATYPE_STRING, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_CODE_TYPE, TeleonomeConstants.TELEONOME_SECURITY_CODE , null, TeleonomeConstants.DATATYPE_STRING, true);
		preparePublishDeneWordsJSONArray.put(deneword);
		
		//
		// create the denes in "actuators"
		//
		String actuatorTarget = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS).toString();


		//
		// Create the external data
		//

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
		 *  Because a .sertoli file can have more than one .had that writes to the external data denechain of a teleonome,
		 *  ie you can have an .had that looks at Sento's tub temperature and anther .had that looks at Sento's
		 *  pump status, and both have to ednd up in the same external data:sento dene, keep tracks of the ones 
		 *  already created to avoid creating duplicates, check if it has already been created
		 *   if the dene has been created,then do a deneword carrier to add the value deneword to the existing 
		 *   dene
		 *   if it has not been created, create it
		 */
		JSONObject externalDataDeneJSONObject;
		String externalTeleonomeName;



		
		Identity externalDataDeneIdentity, dataSourcePointerIdentity, externalDataSourcePointerIdentity;
		String dataSourcePointer, externalDataSourcePointer, dataSourceUnits, dataSourceValueType;
		String displayVariableName;
		
		for(int i=0;i<displays.length();i++){
			display = displays.getJSONObject(i);
			externalDataSourcePointer= display.getString("External Data Source Pointer");
			externalDataSourcePointerIdentity = new Identity(externalDataSourcePointer);
			externalDataDeneIdentity = new Identity(externalDataSourcePointerIdentity.getTeleonomeName(), externalDataSourcePointerIdentity.getNucleusName(), externalDataSourcePointerIdentity.getDenechainName(), externalDataSourcePointerIdentity.getDeneName());
			externalTeleonomeName= externalDataDeneIdentity.getTeleonomeName();

			JSONObject dataSourceJSONObject = display.getJSONObject("Data Source");
			 dataSourcePointer = dataSourceJSONObject.getString("Data Source Pointer");
			 dataSourceUnits = dataSourceJSONObject.getString(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE);
			 dataSourceValueType = dataSourceJSONObject.getString(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE);
			 dataSourcePointerIdentity = new Identity(dataSourcePointer);
			 displayVariableName = dataSourcePointerIdentity.deneWordName;
			 
			if(!externalDataDenesCreated.contains(externalTeleonomeName) &&  !newExternalTeleonomeNames.contains(externalTeleonomeName) ) {

				/*
			   Create the DeneWordCarrier Dene to store the actual value $mainComparator
				Every Dene of type action needs to have a deneword added to the 
				actuatorActionListPointer

				 */	

				JSONObject denewordCarrierForMainComparatorDene = new JSONObject();
				denesJSONArray.put(denewordCarrierForMainComparatorDene);
				JSONArray mainComparatorDeneWordsJSONArray = new JSONArray();
				denewordCarrierForMainComparatorDene.put("DeneWords", mainComparatorDeneWordsJSONArray);

				denewordCarrierForMainComparatorDene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "DeneWord Carrier for " + externalTeleonomeName + " " + displayVariableName);
				denewordCarrierForMainComparatorDene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_DENEWORD_CARRIER);
				denewordCarrierForMainComparatorDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, externalDataDeneIdentity.toString());


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
				deneword = Utils.createDeneWordJSONObject(displayVariableName, initial, dataSourceUnits, dataSourceValueType, true);
				deneword.put(TeleonomeConstants.DENEWORD_DATA_LOCATION_ATTRIBUTE,externalDataSourcePointer);
				mainComparatorDeneWordsJSONArray.put(deneword);



			}else {
				//
				// this is the first time , so create the dene
				//
				externalDataDeneJSONObject= new JSONObject();
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
				deneword = Utils.createDeneWordJSONObject(displayVariableName, initial, dataSourceUnits, dataSourceValueType, true);
				deneword.put(TeleonomeConstants.DENEWORD_DATA_LOCATION_ATTRIBUTE,externalDataSourcePointer);
				externalDataDeneWordsJSONArray.put(deneword);

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
			for( i=0;i<denesJSONArray.length();i++) {
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
		}
		return homeBoxProcessingResultJSONObject;

	}
	
	private JSONObject getConfigParamDene(String componentsDeneChainTargetPointer,String controllerName, JSONObject displayJSONObject ) {
		JSONObject microControllerConfigParamDeneJSONObject= new JSONObject();
		JSONArray microControllerConfigParamDeneWordsJSONArray = new JSONArray();
		String configName = displayJSONObject.getString("Name");
		microControllerConfigParamDeneJSONObject.put("DeneWords", microControllerConfigParamDeneWordsJSONArray);
		microControllerConfigParamDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, componentsDeneChainTargetPointer);
		microControllerConfigParamDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, configName);
		
		JSONObject dataSourceJSONObject = displayJSONObject.getJSONObject("Data Source");
		String dataSourcePointer = dataSourceJSONObject.getString("Data Source Pointer");
		
		
		JSONObject deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, controllerName, null, TeleonomeConstants.DATATYPE_STRING, true);
		microControllerConfigParamDeneWordsJSONArray.put(deneword);
		 
		deneword = Utils.createDeneWordJSONObject("Identity", dataSourcePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		microControllerConfigParamDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject("Clock Pin", displayJSONObject.getInt("Clock Pin"), null, TeleonomeConstants.DATATYPE_INTEGER, true);
		microControllerConfigParamDeneWordsJSONArray.put(deneword);
		
		deneword = Utils.createDeneWordJSONObject("Data Pin", displayJSONObject.getInt("Data Pin"), null, TeleonomeConstants.DATATYPE_INTEGER, true);
		microControllerConfigParamDeneWordsJSONArray.put(deneword);
		
		return microControllerConfigParamDeneJSONObject;
	}

	@Override
	public ArrayList<String> getExternalTeleonomeNames() {
		// TODO Auto-generated method stub
		return  newExternalTeleonomeNames;
	}
}
