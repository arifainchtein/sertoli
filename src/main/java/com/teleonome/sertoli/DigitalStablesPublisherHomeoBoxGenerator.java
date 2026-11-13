package com.teleonome.sertoli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;

/**
 * The DigitalStables Publisher (Avocado pub/DigitalStablesPublisher.pub; DevicesDigitalStables design doc §16.6):
 * the microcontroller that sends the outbox to devicesheart, its three config parameters, the actuator with the
 * action that queues "Publish To DigitalStables" after each pulse and the one the mutation runs, the mutation
 * itself, and the Mnemosycon rule that clears its CommandRequests. Wired like the SFTP publisher.
 *
 * Names are those of Annabelle's Denome (2026-10-31), where the publisher was first put by hand, so a recipe
 * reproduces her. The login, Annabelle's serial number and the password are not here: the factory writes them
 * to the settings file at Send To DS.
 */
public class DigitalStablesPublisherHomeoBoxGenerator extends HomeboxGenerator {

	static final String CONTROLLER = "DigitalStables Publisher Controller";
	static final String CODON = "DigitalStablesPublisherMicroController Controller";
	static final String CONFIG_LIST = "DigitalStables Publisher MicroController Processing Config Parameter List";
	static final String CLASS_NAME = "com.teleonome.framework.microcontroller.digitalstablespublisher.DigitalStablesPublisherMicroController";
	static final String ACTUATOR = "DigitalStables Publisher Actuator";
	static final String ACTUATOR_CODON = "DigitalStables PublisherActuator";
	static final String ACTION_LIST = "DigitalStables Publisher Actuator Actions";
	static final String PREPARE_ACTION = "DigitalStables Publisher Prepare Publish";
	static final String PUBLISH_ACTION = "DigitalStables Publisher Publish";
	static final String COMMAND = "Publish To DigitalStables";
	static final String RULE = "Publish To DigitalStables Rule";
	static final String[] SETTINGS = { "Upload To DigitalStables", "DigitalStables Server", "DigitalStables Settings File" };

	public JSONObject process(String teleonomeName, JSONObject part, int currentActionIndex, ArrayList externalDataDenesCreated) {
		String homeBoxName = part.getString("Homeobox Name");
		String components = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_COMPONENTS).toString();
		String actuators = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS).toString();
		String mnemosycons = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, "Mnemosycons").toString();

		JSONObject result = new JSONObject();
		JSONObject homeBox = new JSONObject();
		result.put("Homeobox", homeBox);
		JSONArray actions = new JSONArray();
		result.put("Actions", actions);
		homeBox.put("Name", homeBoxName);
		JSONArray denes = new JSONArray();
		homeBox.put("Denes", denes);

		JSONObject metaData = dene(denes, "Meta Data", TeleonomeConstants.SPERM_DENE_TYPE_HOMEOBOX_METADATA, "");
		word(metaData, "Created On", new SimpleDateFormat(TeleonomeConstants.SPERM_DATE_FORMAT).format(new Date()), TeleonomeConstants.DATATYPE_DENE_POINTER, null);
		word(metaData, "Description", part.optString("Description", ""), TeleonomeConstants.DATATYPE_STRING, null);

		//
		// the microcontroller, its config parameter list and one dene per parameter
		//
		JSONObject controller = dene(denes, CONTROLLER, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER, components);
		word(controller, "Processing Queue Position", part.getInt("Processing Queue Position"), TeleonomeConstants.DATATYPE_INTEGER, null);
		word(controller, "DigitalStablesPublisher MicroController Processing Class Name", CLASS_NAME, TeleonomeConstants.DATATYPE_STRING,
				TeleonomeConstants.DENEWORD_TYPE_MICROCONTROLLER_PROCESSING_CLASSNAME);
		word(controller, TeleonomeConstants.CODON, CODON, TeleonomeConstants.DATATYPE_STRING, null);
		word(controller, "DigitalStables Publisher Processing Config Parameter List", pointer(TeleonomeConstants.DENECHAIN_COMPONENTS, CONFIG_LIST),
				TeleonomeConstants.DATATYPE_DENE_POINTER, TeleonomeConstants.DENEWORD_TYPE_MICROCONTROLLER_CONFIG_PARAM_LIST);
		// one publish at a time: the writer works under a deadline and keeps the MQTT connection between pulses
		word(controller, TeleonomeConstants.DENEWORD_MICROCONTROLLER_ASYNC_EXECUTION_MODE, TeleonomeConstants.ASYNC_EXECUTION_MODE_SINGLE, TeleonomeConstants.DATATYPE_STRING, null);

		JSONObject configList = dene(denes, CONFIG_LIST, TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER_LIST, components);
		word(configList, TeleonomeConstants.CODON, CODON, TeleonomeConstants.DATATYPE_STRING, null);
		for (String setting : SETTINGS) {
			word(configList, setting, pointer(TeleonomeConstants.DENECHAIN_COMPONENTS, setting), TeleonomeConstants.DATATYPE_STRING,
					TeleonomeConstants.DENE_TYPE_MICROCONTROLLER_CONFIG_PARAMETER);
		}
		JSONObject upload = dene(denes, SETTINGS[0], null, components);
		word(upload, TeleonomeConstants.CODON, CODON, TeleonomeConstants.DATATYPE_STRING, null);
		word(upload, SETTINGS[0], part.getBoolean(SETTINGS[0]), TeleonomeConstants.DATATYPE_BOOLEAN, null);
		for (int i = 1; i < SETTINGS.length; i++) {
			JSONObject settingDene = dene(denes, SETTINGS[i], null, components);
			word(settingDene, TeleonomeConstants.CODON, CODON, TeleonomeConstants.DATATYPE_STRING, null);
			word(settingDene, SETTINGS[i], part.getString(SETTINGS[i]), TeleonomeConstants.DATATYPE_STRING, null);
		}

		//
		// the actuator: "Prepare" queues the command after each pulse (synchronous cycle), "Publish" is what
		// the mutation runs in the asynchronous cycle
		//
		JSONObject actuator = dene(denes, ACTUATOR, TeleonomeConstants.DENE_TYPE_ACTUATOR, actuators);
		word(actuator, "Pointer to Microcontroller", pointer(TeleonomeConstants.DENECHAIN_COMPONENTS, CONTROLLER), TeleonomeConstants.DATATYPE_DENE_POINTER,
				TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_MICROCONTROLLER_POINTER);
		word(actuator, TeleonomeConstants.DENEWORD_EXECUTION_POSITION, 1, TeleonomeConstants.DATATYPE_INTEGER, null);
		word(actuator, ACTION_LIST, pointer(TeleonomeConstants.DENECHAIN_ACTUATORS, ACTION_LIST), "Meme Pointer", TeleonomeConstants.DENE_TYPE_ACTION_LIST);
		word(actuator, TeleonomeConstants.CODON, ACTUATOR_CODON, TeleonomeConstants.DATATYPE_STRING, null);

		JSONObject actionList = dene(denes, ACTION_LIST, TeleonomeConstants.DENE_TYPE_ACTION_LIST, actuators);
		word(actionList, TeleonomeConstants.CODON, ACTUATOR_CODON, TeleonomeConstants.DATATYPE_STRING, null);
		word(actionList, PREPARE_ACTION, pointer(TeleonomeConstants.DENECHAIN_ACTUATORS, PREPARE_ACTION), TeleonomeConstants.DATATYPE_DENE_POINTER,
				TeleonomeConstants.DENEWORD_TYPE_ACTION);

		JSONObject prepare = action(denes, PREPARE_ACTION, actuators, part.optInt("Prepare Evaluation Position", 25));
		word(prepare, TeleonomeConstants.DENEWORD_CODE_TYPE, TeleonomeConstants.TELEONOME_SECURITY_CODE, TeleonomeConstants.DATATYPE_STRING, null);
		action(denes, PUBLISH_ACTION, actuators, part.optInt("Publish Evaluation Position", 26));

		//
		// the mutation "Publish To DigitalStables": created by a sperm action, filled by this dene
		//
		JSONObject createMutation = new JSONObject();
		actions.put(createMutation);
		createMutation.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, COMMAND);
		createMutation.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_CREATE_MUTATION);
		createMutation.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, new Identity("Egg", TeleonomeConstants.SPERM_HYPOTHALAMUS_MUTATIONS).toString());
		JSONArray createMutationWords = new JSONArray();
		createMutation.put("DeneWords", createMutationWords);
		createMutationWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true));
		createMutationWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, currentActionIndex + 1, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		createMutationWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT, TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		createMutationWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_MUTATION_NAME, COMMAND, null, TeleonomeConstants.DATATYPE_STRING, true));
		createMutationWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.MUTATION_TYPE_ATTRIBUTE, TeleonomeConstants.MUTATION_TYPE_STATE, null, TeleonomeConstants.DATATYPE_STRING, true));
		createMutationWords.put(Utils.createDeneWordJSONObject("Execution Mode", TeleonomeConstants.MUTATION_EXECUTION_MODE_IMMEDIATE, null, TeleonomeConstants.DATATYPE_STRING, true));

		JSONObject mutationActions = dene(denes, COMMAND + " Actions", TeleonomeConstants.DENE_TYPE_ACTION_LIST, new Identity("Egg", COMMAND, "Actions To Execute").toString());
		word(mutationActions, ACTUATOR, pointer(TeleonomeConstants.DENECHAIN_ACTUATORS, ACTUATOR), TeleonomeConstants.DATATYPE_DENE_POINTER,
				TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_POINTER);
		word(mutationActions, PUBLISH_ACTION, pointer(TeleonomeConstants.DENECHAIN_ACTUATORS, PUBLISH_ACTION), TeleonomeConstants.DATATYPE_DENE_POINTER,
				TeleonomeConstants.DENEWORD_TYPE_ACTION);

		//
		// the Mnemosycon rule that forgets the command's CommandRequests, and its pointer in the
		// Static Mnemosycon Configuration the Egg already has (a DeneWord carrier)
		//
		JSONObject rule = dene(denes, RULE, null, mnemosycons);
		word(rule, "Source", "Database", TeleonomeConstants.DATATYPE_STRING, TeleonomeConstants.MNEMOSYCON_RULE_SOURCE);
		word(rule, "Source", "CommandRequests", TeleonomeConstants.DATATYPE_STRING, TeleonomeConstants.MNEMOSYCON_RULE_LOCATION);
		word(rule, "Time Unit", "Day", TeleonomeConstants.DATATYPE_STRING, TeleonomeConstants.MNEMOSYCON_RULE_TIME_UNIT);
		word(rule, "Time Unit Value", part.optInt("Keep Command Requests Days", 7), TeleonomeConstants.DATATYPE_INTEGER, TeleonomeConstants.MNEMOSYCON_RULE_TIME_UNIT_VALUE);
		word(rule, "Command", COMMAND, TeleonomeConstants.DATATYPE_STRING, TeleonomeConstants.DENEWORD_TYPE_MNEMOSYCON_DATABASE_FIELD);
		word(rule, TeleonomeConstants.DENEWORD_EXECUTION_POSITION, 3, TeleonomeConstants.DATATYPE_INTEGER, null);

		JSONObject ruleCarrier = dene(denes, "Mnemosycon Rule Carrier", TeleonomeConstants.SPERM_DENE_TYPE_DENEWORD_CARRIER,
				new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, "Mnemosycons", "Static Mnemosycon Configuration").toString());
		word(ruleCarrier, COMMAND, pointer("Mnemosycons", RULE), TeleonomeConstants.DATATYPE_DENE_POINTER, TeleonomeConstants.DENEWORD_TYPE_MNEMOSYCON_RULE_POINTER);

		//
		// the homeobox index: every dene the Fertilizer adds (not the metadata, not the carrier)
		//
		JSONObject index = new JSONObject();
		index.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "");
		index.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, TeleonomeConstants.SPERM_HOMEOBOX_INDEX);
		index.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_HOMEOBOX_INDEX);
		index.put("DeneWords", new JSONArray());
		for (int i = 0; i < denes.length(); i++) {
			JSONObject d = denes.getJSONObject(i);
			String deneType = d.optString(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, "");
			if (d == metaData || deneType.equals(TeleonomeConstants.SPERM_DENE_TYPE_DENEWORD_CARRIER)) continue;
			String name = d.getString(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE);
			word(index, name, "@Sperm:Hypothalamus:" + homeBoxName + ":" + name, TeleonomeConstants.DATATYPE_DENE_POINTER,
					TeleonomeConstants.DENEWORD_TYPE_HOX_DENE_POINTER);
		}
		denes.put(index);
		return result;
	}

	private static String pointer(String deneChain, String deneName) {
		return new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, deneChain, deneName).toString();
	}

	private static JSONObject dene(JSONArray denes, String name, String deneType, String target) {
		JSONObject dene = new JSONObject();
		dene.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, name);
		if (deneType != null) dene.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, deneType);
		dene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, target);
		dene.put("DeneWords", new JSONArray());
		denes.put(dene);
		return dene;
	}

	private static void word(JSONObject dene, String name, Object value, String valueType, String deneWordType) {
		JSONObject word = Utils.createDeneWordJSONObject(name, value, null, valueType, true);
		if (deneWordType != null) word.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, deneWordType);
		dene.getJSONArray("DeneWords").put(word);
	}

	// an action of the publisher's actuator: runs after each pulse, always true, gives the command
	private static JSONObject action(JSONArray denes, String name, String actuators, int evaluationPosition) {
		JSONObject action = dene(denes, name, TeleonomeConstants.DENE_TYPE_ACTION, actuators);
		word(action, TeleonomeConstants.CODON, "VirtualActuator", TeleonomeConstants.DATATYPE_STRING, null);
		word(action, TeleonomeConstants.DENEWORD_ACTION_EXECUTION_POINT, TeleonomeConstants.DENEWORD_ACTION_EXECUTION_POINT_POST_PULSE, TeleonomeConstants.DATATYPE_STRING, null);
		word(action, TeleonomeConstants.DENEWORD_ACTIVE, true, TeleonomeConstants.DATATYPE_BOOLEAN, null);
		word(action, TeleonomeConstants.EVALUATION_POSITION, evaluationPosition, TeleonomeConstants.DATATYPE_INTEGER, null);
		word(action, TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION, COMMAND, TeleonomeConstants.DATATYPE_STRING, null);
		word(action, TeleonomeConstants.DENEWORD_EXPRESSION, "1==1", TeleonomeConstants.DATATYPE_STRING, null);
		return action;
	}

	@Override
	public ArrayList getExternalTeleonomeNames() {
		return null;
	}
}
