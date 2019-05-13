package com.teleonome.sertoli;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.teleonome.framework.*;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;

public class AquaBubblerXbeeTelepathonGenerator extends HomeboxGenerator {
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
		String telepathonName = homeboxSourceDataElement.getString("AquaBubbler Name");
		String microcontrollerPointer = homeboxSourceDataElement.getString("Microcontroller Pointer");
		JSONArray events = homeboxSourceDataElement.getJSONArray("Events");
		//
		//
		JSONObject xBeeParams = homeboxSourceDataElement.getJSONObject("XBee 64 Bit Address");
		String msb = xBeeParams.getString("MSB");
		String lsb = xBeeParams.getString("LSB");
		int executionPosition = homeboxSourceDataElement.getInt("Execution Position");
		String aquaBubblerModel = homeboxSourceDataElement.getString("AquaBubbler Model");
		String aquaBubblerBoardVersion = homeboxSourceDataElement.getString("AquaBubbler Board Version");
		int numberOfDrinkingPoints = homeboxSourceDataElement.getInt("Number Of Water Points");



		logger.debug("line 31, events=" + events.toString(4));
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
		// Create the Telepathon Dene
		//
		JSONObject telepathonDeneJSONObject= new JSONObject();
		denesJSONArray.put(telepathonDeneJSONObject);
		telepathonDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, telepathonName);
		telepathonDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENE_TYPE_TELEPATHON);

		JSONArray telepathonDeneWordsJSONArray = new JSONArray();
		telepathonDeneJSONObject.put("DeneWords", telepathonDeneWordsJSONArray);
		String telepathonsDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS ).toString();

		telepathonDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, telepathonName, null, TeleonomeConstants.DATATYPE_STRING, true);
		telepathonDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject("Pointer to Microcontroller", microcontrollerPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_TELEPATHON_MICROCONTROLLER_POINTER);
		telepathonDeneWordsJSONArray.put(deneword);


		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_TELEPATHON_TYPE, TeleonomeConstants.TELEPATHON_TYPE_XBEE, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_TELEPATHON_TYPE);
		telepathonDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXECUTION_POSITION, executionPosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		telepathonDeneWordsJSONArray.put(deneword);

		String commProfileName=telepathonName + " Communication Profile";
		String communicationProfilePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS , commProfileName).toString();
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_TELEPATHON_COMMUNCATION_PROFILE,communicationProfilePointer , null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_TELEPATHON_COMMUNCATION_PROFILE);
		telepathonDeneWordsJSONArray.put(deneword);


		String eventListPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS , telepathonName + " Event List").toString();
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_TELEPATHON_EVENT_LIST_POINTER, eventListPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_TELEPATHON_EVENT_LIST_POINTER);
		telepathonDeneWordsJSONArray.put(deneword);




		//
		// Create the Comm Profile Dene
		//
		JSONObject commProfileDeneJSONObject= new JSONObject();
		denesJSONArray.put(commProfileDeneJSONObject);
		commProfileDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, commProfileName);

		JSONArray commProfileDeneWordsJSONArray = new JSONArray();
		commProfileDeneJSONObject.put("DeneWords", commProfileDeneWordsJSONArray);
		commProfileDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, telepathonName, null, TeleonomeConstants.DATATYPE_STRING, true);
		commProfileDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject("64 Bit Address MSB", msb, null, TeleonomeConstants.DATATYPE_STRING, true);
		commProfileDeneWordsJSONArray.put(deneword);

		deneword = Utils.createDeneWordJSONObject("64 Bit Address LSB", lsb, null, TeleonomeConstants.DATATYPE_STRING, true);
		commProfileDeneWordsJSONArray.put(deneword);
		//
		// create the event list dene
		// loop over each event
		//
		JSONObject eventListDeneJSONObject= new JSONObject();
		denesJSONArray.put(eventListDeneJSONObject);
		eventListDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, telepathonName + " Event List");

		JSONArray eventListDeneWordsJSONArray = new JSONArray();
		eventListDeneJSONObject.put("DeneWords", eventListDeneWordsJSONArray);
		eventListDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, telepathonName, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventListDeneWordsJSONArray.put(deneword);

		//
		// becasue we are only creating denewords all we need is the name of the event
		// check also for Event Data Structure and create a list of unique ones
		//
		JSONObject eventDataJSONObject;
		String eventName;
		String  eventDataStructure;
		ArrayList<String> uniqueEventDataStructures = new ArrayList();
		for (int i=0;i<events.length();i++) {
			eventDataJSONObject = events.getJSONObject(i);
			eventName=eventDataJSONObject.getString("Name");
			eventDataStructure=eventDataJSONObject.getString("Event Data Structure");
			if(!uniqueEventDataStructures.contains(eventDataStructure))uniqueEventDataStructures.add(eventDataStructure);
			String eventDenePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS , eventName).toString();
			deneword = Utils.createDeneWordJSONObject(eventName, eventDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_DEFINITION);
			eventListDeneWordsJSONArray.put(deneword);
		}
		
		//
		// loop over the unique datastructures and generate the
		// Event Value List and all the event value definition
		String eventDefinitionType;
		Class clazz;
		Constructor constructor;
		EventGenerator eventGenerator;
		JSONObject eventProcessingResultJSONObject;
		JSONArray eventResultJSONArray;
		for(int i=0;i<uniqueEventDataStructures.size();i++) {
			eventDataStructure = uniqueEventDataStructures.get(i);
			eventDefinitionType = "com.teleonome.sertoli." + eventDataStructure + "EventGenerator";
			try {
				clazz = Class.forName(eventDefinitionType);
				constructor = clazz.getConstructor();
				eventGenerator = (EventGenerator) constructor.newInstance();
				eventResultJSONArray = eventGenerator.process(teleonomeName);
				for(int j=0;j<uniqueEventDataStructures.size();j++) {
					denesJSONArray.put(eventResultJSONArray.getJSONObject(j));
				}
				
				
			}catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			}
			
		}
		
		//
		// now loop over all the events again to build the denes for each event
		//
		String  mnemosyneDestination, aggregateOn;
		JSONArray aggregatorsJSONArray;
		for (int i=0;i<events.length();i++) {
			eventDataJSONObject = events.getJSONObject(i);
			eventName=eventDataJSONObject.getString("Name");
			eventDataStructure=eventDataJSONObject.getString("Event Data Structure");
			mnemosyneDestination=eventDataJSONObject.getString("Event Mnemosyne Destination");
			aggregateOn = eventDataJSONObject.getString("Aggregate Value");
			aggregatorsJSONArray = null;
			if(eventDataJSONObject.has("Aggregators")) {
				aggregatorsJSONArray = eventDataJSONObject.getJSONArray("Aggregators");
			}


			//
			// add the event dene
			//
			JSONObject eventtDeneJSONObject= new JSONObject();
			denesJSONArray.put(eventtDeneJSONObject);
			eventtDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, eventName);

			JSONArray eventDeneWordsJSONArray = new JSONArray();
			eventtDeneJSONObject.put("DeneWords", eventDeneWordsJSONArray);
			eventtDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, telepathonName, null, TeleonomeConstants.DATATYPE_STRING, true);
			eventDeneWordsJSONArray.put(deneword);

			String eventMnemosyneDestinationDenePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_MNEMOSYNE,mnemosyneDestination, eventName).toString();
			deneword = Utils.createDeneWordJSONObject(eventName + "  Mnemosyne Destination", eventMnemosyneDestinationDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_DEFINITION);
			eventDeneWordsJSONArray.put(deneword);
			String eventMnemosyneOperationsName="";
			if(aggregatorsJSONArray!=null && aggregatorsJSONArray.length()>0) {
				eventMnemosyneOperationsName = eventName + " Mnemosyne Operations";
				String mnemosyneOperationDestinationDenePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS , eventMnemosyneOperationsName).toString();
				deneword = Utils.createDeneWordJSONObject("Mnemosyne Operations", mnemosyneOperationDestinationDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
				deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.MNEMOSYNE_OPERATION_INDEX_LABEL);
				eventDeneWordsJSONArray.put(deneword);
			}

			deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_TYPE_EVENT_DATA_STRUCTURE, TeleonomeConstants.EVENT_DATA_STRUCTURE_FLOWMETER, null, TeleonomeConstants.DATATYPE_STRING, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_DATA_STRUCTURE);
			eventDeneWordsJSONArray.put(deneword);

			String flowMeterEventValueListName = "FlowMeter Event Value List";
			String eventDataStructureValueListPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS , flowMeterEventValueListName).toString();
			deneword = Utils.createDeneWordJSONObject( TeleonomeConstants.DENEWORD_TYPE_EVENT_DATA_STRUCTURE_VALUE_LIST, eventDataStructureValueListPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_DATA_STRUCTURE_VALUE_LIST);
			eventDeneWordsJSONArray.put(deneword);


			//
			// the Event Mnemosyne Operations dene
			//
			if(aggregatorsJSONArray!=null && aggregatorsJSONArray.length()>0) {
				JSONObject eventMnemosyneOperationsDeneJSONObject= new JSONObject();
				denesJSONArray.put(eventMnemosyneOperationsDeneJSONObject);
				eventMnemosyneOperationsDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, eventMnemosyneOperationsName);

				JSONArray eventMnemosyneOperationsDeneWordsJSONArray = new JSONArray();
				eventMnemosyneOperationsDeneJSONObject.put("DeneWords", eventMnemosyneOperationsDeneWordsJSONArray);
				eventMnemosyneOperationsDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
				deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, telepathonName, null, TeleonomeConstants.DATATYPE_STRING, true);
				eventMnemosyneOperationsDeneWordsJSONArray.put(deneword);
				//
				// run the aggregator array to generate the denewords
				//
				for(int j=0;j<aggregatorsJSONArray.length();j++) {
					//
					// aggergatortext will be something like "Mnemosyne Current Hour", ie the denechain name in the mnemosyne
					// so remove the word Mnemosyne when creating the name to make it shorter
					String aggregatorTimeText = aggregatorsJSONArray.getString(j).replace("Mnemosyne ", "") ;
					String aggregatorName="Aggregate " + aggregateOn + " for " + aggregatorTimeText + " " + eventName;
					String eventMnemosyneOperationDenePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS , aggregatorName).toString();
					deneword = Utils.createDeneWordJSONObject(aggregatorName, eventMnemosyneOperationDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
					deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_MNEMOSYNE_OPERATION);
					eventMnemosyneOperationsDeneWordsJSONArray.put(deneword);

				}
				//
				// now run the aggregators for loop again to create the actual denes
				for(int j=0;j<aggregatorsJSONArray.length();j++) {
					JSONObject anEventMnemosyneOperationDeneJSONObject= new JSONObject();
					denesJSONArray.put(anEventMnemosyneOperationDeneJSONObject);
					String aggregatorDeneChainName = aggregatorsJSONArray.getString(j);
					String aggregatorTimeText = aggregatorDeneChainName.replace("Mnemosyne ", "") ;
					String aggregatorName="Aggregate " + aggregateOn + " for " + aggregatorTimeText + " " + eventName;
					anEventMnemosyneOperationDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, aggregatorName);
					anEventMnemosyneOperationDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.MNEMOSYNE_UPDATE_VALUE_OPERATION);

					JSONArray anEventMnemosyneOperationDeneWordsJSONArray = new JSONArray();
					anEventMnemosyneOperationDeneJSONObject.put("DeneWords", anEventMnemosyneOperationDeneWordsJSONArray);
					anEventMnemosyneOperationDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, telepathonName, null, TeleonomeConstants.DATATYPE_STRING, true);
					anEventMnemosyneOperationDeneWordsJSONArray.put(deneword);
					
					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EXECUTION_POSITION, (j+1), null, TeleonomeConstants.DATATYPE_INTEGER, true);
					anEventMnemosyneOperationDeneWordsJSONArray.put(deneword);
					
					deneword = Utils.createDeneWordJSONObject("Operation", TeleonomeConstants.MNEMOSYNE_DENEWORD_AGGREGATION_OPERATION, null, TeleonomeConstants.DATATYPE_STRING, true);
					anEventMnemosyneOperationDeneWordsJSONArray.put(deneword);
					
					String mnemosyneSourceAndTargetDenePointer = new Identity("Egg", TeleonomeConstants.NUCLEI_MNEMOSYNE,aggregatorDeneChainName , aggregatorName).toString();
					deneword = Utils.createDeneWordJSONObject("Target", mnemosyneSourceAndTargetDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
					deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.MNEMOSYNE_DENE_WORD_TYPE_TARGET);
					eventMnemosyneOperationsDeneWordsJSONArray.put(deneword);

					deneword = Utils.createDeneWordJSONObject("Aggregate From", mnemosyneSourceAndTargetDenePointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
					eventMnemosyneOperationsDeneWordsJSONArray.put(deneword);

					String aggregateValueDeneWordPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_MNEMOSYNE,mnemosyneDestination , eventName, aggregateOn).toString();
					deneword = Utils.createDeneWordJSONObject("Aggregate Value", aggregateValueDeneWordPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
					eventMnemosyneOperationsDeneWordsJSONArray.put(deneword);

					deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.MNEMOSYNE_DENEWORD_TARGET_POSITION, TeleonomeConstants.COMMAND_MNEMOSYNE_LAST_DENE_POSITION, null, TeleonomeConstants.DATATYPE_STRING, true);
					eventMnemosyneOperationsDeneWordsJSONArray.put(deneword);

				}
			}

		}


		//
		// automatically add aggregatrs
		//

		//		"Mnemosyne":[
		//		             {
		//		                 "Prunning Strategy":"Reset",
		//		                 "Name":"AquaBubbler1 aggregated volume Current Hour",
		//		                 "Destination":"@Egg:Mnemosyne:Mnemosyne Current Hour"
		//		                 "Units":"Liters",
		//		                 "Value Type":"double"
		//		             },
		//		             {
		//		                 "Prunning Strategy":"Reset",
		//		                 "Name":"AquaBubbler1 aggregated volume Today",
		//		                 "Destination":"@Egg:Mnemosyne:Mnemosyne Today"
		//		                 "Units":"Liters",
		//		                 "Value Type":"double"
		//		             }
		//		         ]


		return null;
	}

	@Override
	public ArrayList<String> getExternalTeleonomeNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
