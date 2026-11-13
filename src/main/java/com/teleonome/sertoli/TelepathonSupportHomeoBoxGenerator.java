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
 * Telepathon support (Avocado telepathons/TelepathonSupport.tph; design §3.2): what lets a Teleonome that hears
 * devices show and forget their telepathons. Taken from Annabelle's Denome (2026-11-01):
 * <ul>
 * <li>two actions of the receiver's actuator: "Delete Telepathon Action" (command DeleteTelepathon#name, the name
 * set by the mutation's On Load from the web page's payload, DeleteTelepathonProcessingHandler) and "Hourly
 * Delete Stale Telepathons Action" (DeleteStaleTelepathons#Hourly). Neither goes into the actuator's own action
 * list: that list runs every pulse; only the mutations run these;</li>
 * <li>the mutations "Delete Telepathon" (invoked by the page) and "Hourly Delete Stale Telepathons" (Invocation
 * Mode Time, Execution Time Hourly);</li>
 * <li>the Home Page's "Telepathon View Panel" and the empty dene chain "Telepathons View" it points at.</li>
 * </ul>
 * Not here: the telepathons themselves and "Annabelle Queue Status" (what she grows: MappedBusThread writes them),
 * and the Cerebellum's registry task per device type (the device-type parts).
 */
public class TelepathonSupportHomeoBoxGenerator extends HomeboxGenerator {

	static final String DELETE_ACTION = "Delete Telepathon Action";
	static final String STALE_ACTION = "Hourly Delete Stale Telepathons Action";
	static final String DELETE_MUTATION = "Delete Telepathon";
	static final String STALE_MUTATION = "Hourly Delete Stale Telepathons";
	static final String VIEW_CHAIN = "Telepathons View";
	static final String PANEL = TeleonomeConstants.PANEL_VISUALIZATION_TELEPHATON_VIEW;

	public JSONObject process(String teleonomeName, JSONObject part, int currentActionIndex, ArrayList externalDataDenesCreated) {
		String homeBoxName = part.getString("Homeobox Name");
		String actuatorName = part.getString("Actuator Name");
		String actuators = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS).toString();
		String actuatorPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS, actuatorName).toString();

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
		// the sperm actions: the view's dene chain and the two mutations
		//
		int nextAction = currentActionIndex;
		JSONObject createChain = new JSONObject();
		actions.put(createChain);
		createChain.put(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE, "Create " + VIEW_CHAIN + " DeneChain");
		createChain.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, new Identity("Egg", TeleonomeConstants.NUCLEI_HUMAN_INTERFACE).toString());
		createChain.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_CREATE_DENE_CHAIN);
		JSONArray createChainWords = new JSONArray();
		createChain.put("DeneWords", createChainWords);
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, ++nextAction, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT, TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_DENECHAIN_NAME, VIEW_CHAIN, null, TeleonomeConstants.DATATYPE_STRING, true));

		actions.put(createMutation(DELETE_MUTATION, ++nextAction, null));
		actions.put(createMutation(STALE_MUTATION, ++nextAction, TeleonomeConstants.MUTATION_INVOCATION_MODE_TIME));

		//
		// the two actions of the receiver's actuator
		//
		action(denes, DELETE_ACTION, actuators, actuatorName, part.optInt("Delete Telepathon Evaluation Position", 1), TeleonomeConstants.DELETE_TELEPATHON + "#");
		action(denes, STALE_ACTION, actuators, actuatorName, part.optInt("Delete Stale Telepathons Evaluation Position", 1),
				TeleonomeConstants.DELETE_STALE_TELEPATHONS + "#" + TeleonomeConstants.MNEMOSYNE_HOURLY_MUTATION);

		//
		// "Delete Telepathon": On Load writes the command with the telepathon's name (the page's payload targets
		// @On Load:Update DeneWord:Set DeneWord), then runs the action
		//
		JSONObject onLoad = dene(denes, "Update DeneWord", TeleonomeConstants.MUTATION_COMMAND_SET_DENEWORD,
				new Identity("Egg", DELETE_MUTATION, TeleonomeConstants.DENECHAIN_ON_LOAD_MUTATION).toString());
		JSONObject setWord = Utils.createDeneWordJSONObject(TeleonomeConstants.MUTATION_COMMAND_SET_DENEWORD, TeleonomeConstants.DELETE_TELEPATHON + "#", null,
				TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		setWord.put(TeleonomeConstants.MUTATION_TARGET, new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS, DELETE_ACTION,
				TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION).toString());
		onLoad.getJSONArray("DeneWords").put(setWord);

		mutationActionList(denes, DELETE_MUTATION, DELETE_ACTION, actuatorName, actuatorPointer);
		mutationActionList(denes, STALE_MUTATION, STALE_ACTION, actuatorName, actuatorPointer);

		JSONObject timeConfiguration = dene(denes, "Time Mutation Configuration", TeleonomeConstants.MUTATION_COMMAND_SET_DENEWORD,
				new Identity("Egg", STALE_MUTATION, "Mutation Configuration").toString());
		word(timeConfiguration, TeleonomeConstants.DENEWORD_TYPE_TIME_BASED_MUTATION_EXECUTION_TIME, TeleonomeConstants.MNEMOSYNE_HOURLY_MUTATION,
				TeleonomeConstants.DATATYPE_STRING, null);

		//
		// the Home Page panel
		//
		JSONObject panel = dene(denes, PANEL, null, new Identity("Egg", TeleonomeConstants.NUCLEI_HUMAN_INTERFACE, "Home Page").toString());
		word(panel, "Now Panel Pointer", new Identity("Egg", TeleonomeConstants.NUCLEI_HUMAN_INTERFACE, VIEW_CHAIN).toString(), TeleonomeConstants.DATATYPE_DENE_POINTER,
				TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER);
		word(panel, "Now Panel Style", PANEL, TeleonomeConstants.DATATYPE_STRING, TeleonomeConstants.DENEWORD_TYPE_PANEL_VISUALIZATION_STYLE);
		word(panel, TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION, part.optInt("Panel In Page Position", 2), TeleonomeConstants.DATATYPE_INTEGER,
				TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION);
		word(panel, "Visible", true, TeleonomeConstants.DATATYPE_BOOLEAN, null);

		//
		// the homeobox index: every dene but the metadata
		//
		JSONObject index = new JSONObject();
		index.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "");
		index.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, TeleonomeConstants.SPERM_HOMEOBOX_INDEX);
		index.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_HOMEOBOX_INDEX);
		index.put("DeneWords", new JSONArray());
		for (int i = 0; i < denes.length(); i++) {
			JSONObject d = denes.getJSONObject(i);
			if (d == metaData) continue;
			String name = d.getString(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE);
			word(index, name, "@Sperm:Hypothalamus:" + homeBoxName + ":" + name, TeleonomeConstants.DATATYPE_DENE_POINTER,
					TeleonomeConstants.DENEWORD_TYPE_HOX_DENE_POINTER);
		}
		denes.put(index);
		return result;
	}

	// a sperm action that creates a State mutation, run at once when invoked; invocationMode null: by command
	private static JSONObject createMutation(String name, int executionPosition, String invocationMode) {
		JSONObject createMutation = new JSONObject();
		createMutation.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, name);
		createMutation.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_CREATE_MUTATION);
		createMutation.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, new Identity("Egg", TeleonomeConstants.SPERM_HYPOTHALAMUS_MUTATIONS).toString());
		JSONArray words = new JSONArray();
		createMutation.put("DeneWords", words);
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true));
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, executionPosition, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT, TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_MUTATION_NAME, name, null, TeleonomeConstants.DATATYPE_STRING, true));
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.MUTATION_TYPE_ATTRIBUTE, TeleonomeConstants.MUTATION_TYPE_STATE, null, TeleonomeConstants.DATATYPE_STRING, true));
		words.put(Utils.createDeneWordJSONObject("Execution Mode", TeleonomeConstants.MUTATION_EXECUTION_MODE_IMMEDIATE, null, TeleonomeConstants.DATATYPE_STRING, true));
		if (invocationMode != null) {
			words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.MUTATION_INVOCATION_MODE_ATTRIBUTE, invocationMode, null, TeleonomeConstants.DATATYPE_STRING, true));
		}
		return createMutation;
	}

	// a mutation's "Actions To Execute": the actuator and the one action
	private static void mutationActionList(JSONArray denes, String mutation, String actionName, String actuatorName, String actuatorPointer) {
		JSONObject list = dene(denes, actionName + " List", TeleonomeConstants.DENE_TYPE_ACTION_LIST,
				new Identity("Egg", mutation, TeleonomeConstants.DENECHAIN_ACTIONS_TO_EXECUTE).toString());
		word(list, actuatorName, actuatorPointer, TeleonomeConstants.DATATYPE_DENE_POINTER, TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_POINTER);
		word(list, actionName, new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS, actionName).toString(),
				TeleonomeConstants.DATATYPE_DENE_POINTER, TeleonomeConstants.DENEWORD_TYPE_ACTION);
	}

	// an action of the receiver's actuator: always true, gives the command
	private static void action(JSONArray denes, String name, String actuators, String actuatorName, int evaluationPosition, String command) {
		JSONObject action = dene(denes, name, TeleonomeConstants.DENE_TYPE_ACTION, actuators);
		word(action, TeleonomeConstants.CODON, actuatorName, TeleonomeConstants.DATATYPE_STRING, null);
		word(action, TeleonomeConstants.DENEWORD_ACTIVE, true, TeleonomeConstants.DATATYPE_BOOLEAN, null);
		word(action, TeleonomeConstants.EVALUATION_POSITION, evaluationPosition, TeleonomeConstants.DATATYPE_INTEGER, null);
		word(action, TeleonomeConstants.DENEWORD_ACTUATOR_COMMAND_CODE_TRUE_EXPRESSION, command, TeleonomeConstants.DATATYPE_STRING, null);
		word(action, TeleonomeConstants.DENEWORD_EXPRESSION, "1==1", TeleonomeConstants.DATATYPE_STRING, null);
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

	@Override
	public ArrayList getExternalTeleonomeNames() {
		return null;
	}
}
