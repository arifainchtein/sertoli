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
 * Human Interface pages (Avocado hud/HumanInterfacePages.hud, 2026-11-10): the Home Page's row of pills, as on
 * devices.digitalstables.com, and the mutations that let the web page make pages for it.
 * <ul>
 * <li>the Home Page's "Home Page Pills Panel", pointing at the new chain "Home Page Pills", which holds the
 * built-in pills (one dene each: "Map", the devices placed by their telepathons' Configuration Latitude and
 * Longitude) and "Pill Settings" (whether "+ New tab" is offered);</li>
 * <li>the State mutations "Create Human Interface Page", "Update Human Interface Page" and "Delete Human
 * Interface Page". They hold nothing to do themselves: the page's payload carries the Denes and DeneChains
 * (the framework's structural payloads, StructuralPayload). Each one's Mutation Configuration says "Allowed
 * Payload Nucleus" = Human Interface, the only nucleus such a payload may touch.</li>
 * </ul>
 * A page made this way is a Dene in Human Interface Control Parameters with "Home Page Pill" true and "Include in
 * Navigation" false (a pill, not a footer page), plus its own DeneChains; all of it marked "Injected By Payload",
 * so Delete can remove only what a payload made. Pointers are written with Egg, as the other generators; Fertilizer
 * names them after the Denome it fertilizes.
 */
public class HumanInterfacePagesHomeoBoxGenerator extends HomeboxGenerator {

	public static final String CREATE_MUTATION = "Create Human Interface Page";
	public static final String UPDATE_MUTATION = "Update Human Interface Page";
	public static final String DELETE_MUTATION = "Delete Human Interface Page";
	public static final String PILLS_CHAIN = "Home Page Pills";
	public static final String PILLS_PANEL = "Home Page Pills Panel";
	public static final String PILLS_STYLE = "Home Page Pills";
	static final String EGG = "Egg";

	public JSONObject process(String teleonomeName, JSONObject part, int currentActionIndex, ArrayList externalDataDenesCreated) {
		String homeBoxName = part.getString("Homeobox Name");

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
		// the sperm actions: the pills' dene chain and the three mutations
		//
		int nextAction = currentActionIndex;
		JSONObject createChain = new JSONObject();
		actions.put(createChain);
		createChain.put(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE, "Create " + PILLS_CHAIN + " DeneChain");
		createChain.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, new Identity(EGG, TeleonomeConstants.NUCLEI_HUMAN_INTERFACE).toString());
		createChain.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_CREATE_DENE_CHAIN);
		JSONArray createChainWords = new JSONArray();
		createChain.put("DeneWords", createChainWords);
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, ++nextAction, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT, TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_DENECHAIN_NAME, PILLS_CHAIN, null, TeleonomeConstants.DATATYPE_STRING, true));

		for (String mutation : new String[] { CREATE_MUTATION, UPDATE_MUTATION, DELETE_MUTATION }) {
			actions.put(createMutation(mutation, ++nextAction));
			// where the mutation's payload may work
			JSONObject configuration = dene(denes, mutation + " Payload Configuration", null,
					new Identity(EGG, mutation, TeleonomeConstants.DENECHAIN_MUTATION_CONFIGURATION).toString());
			word(configuration, TeleonomeConstants.DENEWORD_ALLOWED_PAYLOAD_NUCLEUS, TeleonomeConstants.NUCLEI_HUMAN_INTERFACE, TeleonomeConstants.DATATYPE_STRING, null);
		}

		//
		// the Home Page panel and the built-in pills
		//
		String pillsChain = new Identity(EGG, TeleonomeConstants.NUCLEI_HUMAN_INTERFACE, PILLS_CHAIN).toString();
		JSONObject panel = dene(denes, PILLS_PANEL, null, new Identity(EGG, TeleonomeConstants.NUCLEI_HUMAN_INTERFACE, "Home Page").toString());
		word(panel, "Pills Panel Pointer", pillsChain, TeleonomeConstants.DATATYPE_DENE_POINTER, TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER);
		word(panel, "Pills Panel Style", PILLS_STYLE, TeleonomeConstants.DATATYPE_STRING, TeleonomeConstants.DENEWORD_TYPE_PANEL_VISUALIZATION_STYLE);
		word(panel, TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION, part.optInt("Panel In Page Position", 3), TeleonomeConstants.DATATYPE_INTEGER,
				TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION);
		word(panel, "Visible", true, TeleonomeConstants.DATATYPE_BOOLEAN, null);

		JSONObject map = dene(denes, "Map", "Built In Pill", pillsChain);
		word(map, "Pill Name", "Map", TeleonomeConstants.DATATYPE_STRING, null);
		word(map, "Pill Position", 1, TeleonomeConstants.DATATYPE_INTEGER, null);
		word(map, "Pill View", "Telepathon Map", TeleonomeConstants.DATATYPE_STRING, null);
		word(map, "Visible", part.optBoolean("Show Map", true), TeleonomeConstants.DATATYPE_BOOLEAN, null);

		JSONObject settings = dene(denes, "Pill Settings", null, pillsChain);
		word(settings, "Allow New Pages", part.optBoolean("Allow New Pages", true), TeleonomeConstants.DATATYPE_BOOLEAN, null);
		word(settings, "Create Mutation", CREATE_MUTATION, TeleonomeConstants.DATATYPE_STRING, null);
		word(settings, "Update Mutation", UPDATE_MUTATION, TeleonomeConstants.DATATYPE_STRING, null);
		word(settings, "Delete Mutation", DELETE_MUTATION, TeleonomeConstants.DATATYPE_STRING, null);

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

	// a sperm action that creates a State mutation, run at once, by command
	private static JSONObject createMutation(String name, int executionPosition) {
		JSONObject createMutation = new JSONObject();
		createMutation.put(TeleonomeConstants.DENE_NAME_ATTRIBUTE, name);
		createMutation.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_CREATE_MUTATION);
		createMutation.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, new Identity(EGG, TeleonomeConstants.SPERM_HYPOTHALAMUS_MUTATIONS).toString());
		JSONArray words = new JSONArray();
		createMutation.put("DeneWords", words);
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true));
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, executionPosition, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT, TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_MUTATION_NAME, name, null, TeleonomeConstants.DATATYPE_STRING, true));
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.MUTATION_TYPE_ATTRIBUTE, TeleonomeConstants.MUTATION_TYPE_STATE, null, TeleonomeConstants.DATATYPE_STRING, true));
		words.put(Utils.createDeneWordJSONObject("Execution Mode", TeleonomeConstants.MUTATION_EXECUTION_MODE_IMMEDIATE, null, TeleonomeConstants.DATATYPE_STRING, true));
		return createMutation;
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
