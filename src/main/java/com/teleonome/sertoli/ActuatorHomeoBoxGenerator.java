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
 * An actuator of a microcontroller with an empty action list "<Actuator Name> Actions" (2026-10-31, Avocado
 * design §3.2): the parts that act through it (a device type's commands, telepathon support) add their actions
 * to that list with a DeneWord carrier. Annabelle's actuator first; Chinampa's is the same kind.
 */
public class ActuatorHomeoBoxGenerator extends HomeboxGenerator {

	public JSONObject process(String teleonomeName, JSONObject part, int currentActionIndex, ArrayList externalDataDenesCreated) {
		String homeBoxName = part.getString("Homeobox Name");
		String actuatorName = part.getString("Actuator Name");
		String actionListName = actuatorName + " Actions";
		String actuators = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS).toString();

		JSONObject result = new JSONObject();
		JSONObject homeBox = new JSONObject();
		result.put("Homeobox", homeBox);
		result.put("Actions", new JSONArray());
		homeBox.put("Name", homeBoxName);
		JSONArray denes = new JSONArray();
		homeBox.put("Denes", denes);

		JSONObject metaData = dene(denes, "Meta Data", TeleonomeConstants.SPERM_DENE_TYPE_HOMEOBOX_METADATA, "");
		word(metaData, "Created On", new SimpleDateFormat(TeleonomeConstants.SPERM_DATE_FORMAT).format(new Date()), TeleonomeConstants.DATATYPE_DENE_POINTER, null);
		word(metaData, "Description", part.optString("Description", ""), TeleonomeConstants.DATATYPE_STRING, null);

		JSONObject actuator = dene(denes, actuatorName, TeleonomeConstants.DENE_TYPE_ACTUATOR, actuators);
		word(actuator, TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_MICROCONTROLLER_POINTER, part.getString("Microcontroller Pointer"),
				TeleonomeConstants.DATATYPE_DENE_POINTER, TeleonomeConstants.DENEWORD_TYPE_ACTUATOR_MICROCONTROLLER_POINTER);
		word(actuator, TeleonomeConstants.DENEWORD_EXECUTION_POSITION, part.optInt("Execution Position", 1), TeleonomeConstants.DATATYPE_INTEGER, null);
		word(actuator, TeleonomeConstants.CODON, actuatorName, TeleonomeConstants.DATATYPE_STRING, null);
		word(actuator, actionListName, new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL, TeleonomeConstants.DENECHAIN_ACTUATORS, actionListName).toString(),
				TeleonomeConstants.DATATYPE_DENE_POINTER, TeleonomeConstants.DENE_TYPE_ACTION_LIST);

		dene(denes, actionListName, TeleonomeConstants.DENE_TYPE_ACTION_LIST, actuators);

		JSONObject index = new JSONObject();
		index.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, "");
		index.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, TeleonomeConstants.SPERM_HOMEOBOX_INDEX);
		index.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.SPERM_HOMEOBOX_INDEX);
		index.put("DeneWords", new JSONArray());
		for (String name : new String[] { actuatorName, actionListName }) {
			word(index, name, "@Sperm:Hypothalamus:" + homeBoxName + ":" + name, TeleonomeConstants.DATATYPE_DENE_POINTER, TeleonomeConstants.DENEWORD_TYPE_HOX_DENE_POINTER);
		}
		denes.put(index);
		return result;
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
