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
 * Water networks (Avocado hud/WaterNetworks.hud, 2026-11-19): sources (a pump's flow meter, a tank, a stream with no
 * meter), measuring points (telepathons with flow meters), physical and defined sinks, and the pipes between them;
 * drawn live as a pill on the Home Page, the same on the Teleonome and on devices.digitalstables.com
 * (deviceviews-waternetwork.js).
 * <ul>
 * <li>the Purpose chain "Water Networks", holding "Water Network Settings": the mutations' names, whether "+ Water
 * network" is offered and the defaults of the leak checks (balance tolerance, minimum flow, how long before an alert,
 * where alerts go);</li>
 * <li>the State mutations "Create Water Network", "Update Water Network" and "Delete Water Network". Like the Human
 * Interface pages they hold nothing to do themselves: the page's payload carries the network as a DeneChain
 * ("Water Network &lt;name&gt;", one Dene per source, measuring point, sink and pipe), and their Mutation
 * Configuration says "Allowed Payload Nucleus" = Purpose. A network is therefore made by a payload and marked
 * "Injected By Payload", so Update (delete and inject in one payload) and Delete work on it, and never on what
 * the Denome was born with;</li>
 * <li>"Water Networks" in the Home Page Pills chain (made by HumanInterfacePages, which this part requires): it
 * tells the web page that each "Water Network ..." chain of Purpose is a pill, and where those pills go.</li>
 * </ul>
 * The networks themselves are what a Teleonome grows (Avocado design §2, kind 3): never in a recipe. Pointers are
 * written with Egg, as the other generators; Fertilizer names them after the Denome it fertilizes.
 */
public class WaterNetworksHomeoBoxGenerator extends HomeboxGenerator {

	public static final String CREATE_MUTATION = "Create Water Network";
	public static final String UPDATE_MUTATION = "Update Water Network";
	public static final String DELETE_MUTATION = "Delete Water Network";
	public static final String NETWORKS_CHAIN = "Water Networks";
	public static final String SETTINGS_DENE = "Water Network Settings";
	public static final String NETWORK_CHAIN_PREFIX = "Water Network ";
	public static final String PILL_VIEW = "Water Network";
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
		word(metaData, "Created On", new SimpleDateFormat(TeleonomeConstants.SPERM_DATE_FORMAT).format(new Date()), TeleonomeConstants.DATATYPE_STRING, null);
		word(metaData, "Description", part.optString("Description", ""), TeleonomeConstants.DATATYPE_STRING, null);

		//
		// the sperm actions: the Purpose chain and the three mutations
		//
		int nextAction = currentActionIndex;
		JSONObject createChain = new JSONObject();
		actions.put(createChain);
		createChain.put(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE, "Create " + NETWORKS_CHAIN + " DeneChain");
		createChain.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, new Identity(EGG, TeleonomeConstants.NUCLEI_PURPOSE).toString());
		createChain.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_DENE_TYPE_CREATE_DENE_CHAIN);
		JSONArray createChainWords = new JSONArray();
		createChain.put("DeneWords", createChainWords);
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POSITION, ++nextAction, null, TeleonomeConstants.DATATYPE_INTEGER, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT, TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_STRING, true));
		createChainWords.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_DENECHAIN_NAME, NETWORKS_CHAIN, null, TeleonomeConstants.DATATYPE_STRING, true));

		for (String mutation : new String[] { CREATE_MUTATION, UPDATE_MUTATION, DELETE_MUTATION }) {
			actions.put(createMutation(mutation, ++nextAction));
			// where the mutation's payload may work: Purpose only
			JSONObject configuration = dene(denes, mutation + " Payload Configuration", null,
					new Identity(EGG, mutation, TeleonomeConstants.DENECHAIN_MUTATION_CONFIGURATION).toString());
			word(configuration, TeleonomeConstants.DENEWORD_ALLOWED_PAYLOAD_NUCLEUS, TeleonomeConstants.NUCLEI_PURPOSE, TeleonomeConstants.DATATYPE_STRING, null);
		}

		//
		// Purpose:Water Networks:Water Network Settings
		//
		String networksChain = new Identity(EGG, TeleonomeConstants.NUCLEI_PURPOSE, NETWORKS_CHAIN).toString();
		JSONObject settings = dene(denes, SETTINGS_DENE, SETTINGS_DENE, networksChain);
		word(settings, "Allow New Water Networks", part.optBoolean("Allow New Water Networks", true), TeleonomeConstants.DATATYPE_BOOLEAN, null);
		word(settings, "Create Mutation", CREATE_MUTATION, TeleonomeConstants.DATATYPE_STRING, null);
		word(settings, "Update Mutation", UPDATE_MUTATION, TeleonomeConstants.DATATYPE_STRING, null);
		word(settings, "Delete Mutation", DELETE_MUTATION, TeleonomeConstants.DATATYPE_STRING, null);
		word(settings, "Network DeneChain Prefix", NETWORK_CHAIN_PREFIX, TeleonomeConstants.DATATYPE_STRING, null);
		word(settings, "Default Balance Tolerance", part.optDouble("Balance Tolerance", 1.0), TeleonomeConstants.DATATYPE_DOUBLE, null, "l/m");
		word(settings, "Default Minimum Flow", part.optDouble("Minimum Flow", 0.5), TeleonomeConstants.DATATYPE_DOUBLE, null, "l/m");
		word(settings, "Default Alert After Seconds", part.optInt("Alert After Seconds", 90), TeleonomeConstants.DATATYPE_INTEGER, null, "s");
		word(settings, "Alert Channel", part.optString("Alert Channel", "Emergency Channel"), TeleonomeConstants.DATATYPE_STRING, null);

		//
		// the pills: each Water Network chain of Purpose is a pill on the Home Page
		//
		String pillsChain = new Identity(EGG, TeleonomeConstants.NUCLEI_HUMAN_INTERFACE, HumanInterfacePagesHomeoBoxGenerator.PILLS_CHAIN).toString();
		JSONObject pill = dene(denes, NETWORKS_CHAIN, "Pill Source", pillsChain);
		word(pill, "Pill View", PILL_VIEW, TeleonomeConstants.DATATYPE_STRING, null);
		word(pill, "Pill Position", part.optInt("Pill Position", 3), TeleonomeConstants.DATATYPE_INTEGER, null);
		word(pill, "Pill Source Pointer", networksChain, TeleonomeConstants.DATATYPE_DENE_POINTER, null);
		word(pill, "Visible", true, TeleonomeConstants.DATATYPE_BOOLEAN, null);

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
		words.put(Utils.createDeneWordJSONObject(TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT, TeleonomeConstants.SPERM_ACTION_DENEWORD_EXECUTION_POINT_PRE_HOMEBOX, null, TeleonomeConstants.DATATYPE_STRING, true));
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
		word(dene, name, value, valueType, deneWordType, null);
	}

	private static void word(JSONObject dene, String name, Object value, String valueType, String deneWordType, String units) {
		JSONObject word = Utils.createDeneWordJSONObject(name, value, units, valueType, true);
		if (deneWordType != null) word.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, deneWordType);
		dene.getJSONArray("DeneWords").put(word);
	}

	@Override
	public ArrayList getExternalTeleonomeNames() {
		return null;
	}
}
