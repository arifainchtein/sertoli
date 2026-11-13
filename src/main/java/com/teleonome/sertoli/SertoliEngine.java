package com.teleonome.sertoli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.utils.Utils;

/**
 * Turns a recipe (a .sertoli file) into a sperm, without files or System.exit, so the factory's Avocado page
 * can call it as well as Sertoli's command line (Avocado design §4, §6).
 *
 * A recipe lists parts under "Components", "Sensors", "Actuators", "Human Interface", "Telepathons" and
 * "Publishers", all optional ("MicroControllers" is the older name of "Components"). Each entry is a part's
 * file name, or {"Part": "<file name>", "Settings": {"<key or A:B path>": value}}, the settings overriding the
 * part's own values. A part's folder in Avocado comes from its file's extension, and its generator from its
 * "Homeobox Definition Type" (a .hud always uses HumanInterfaceHomeoBoxGenerator).
 *
 * Every problem is collected with the part it belongs to; if there is any, there is no sperm, so a recipe
 * never yields a half-made one (before 2026-10-31 a failed part was a warning and the run went on).
 */
public class SertoliEngine {

	// sections in the order their homeoboxes go into the sperm: microcontrollers before what points at them
	private static final String[] SECTIONS = { "Components", "Sensors", "Actuators", "Human Interface", "Telepathons", "Publishers" };
	private static final String OLD_COMPONENTS_SECTION = "MicroControllers";

	private final Logger logger = Logger.getLogger(getClass());

	public static class Result {
		public JSONObject sperm;
		public final List<String> errors = new ArrayList<String>();

		public boolean isOk() {
			return errors.isEmpty();
		}
	}

	/** The folder of a part in Avocado, from its file's extension; null for an unknown extension. */
	public static String folderFor(String partFileName) {
		switch (FilenameUtils.getExtension(partFileName).toLowerCase()) {
		case "mcu": return "mcu";
		case "hsd": return "hsd";
		case "had": return "had";
		case "hud": return "hud";
		case "pub": return "pub";
		case "tph": return "telepathons";
		default: return null;
		}
	}

	/**
	 * @param recipe the .sertoli file's JSON
	 * @param emptySperm the sperm to start from (Avocado's sperm/Empty.sperm); not changed
	 * @param avocadoDir Avocado's folder (the parts are in its mcu/, hsd/, had/... folders)
	 * @param teleonomeName written into the sperm's Purpose
	 */
	public Result sertolize(JSONObject recipe, JSONObject emptySperm, File avocadoDir, String teleonomeName) {
		Result result = new Result();
		JSONObject sperm = new JSONObject(emptySperm.toString());
		JSONObject spermBody = sperm.getJSONObject(TeleonomeConstants.SPERM);
		spermBody.getJSONObject(TeleonomeConstants.SPERM_PURPOSE).put("Teleonome Name", teleonomeName);
		JSONObject hypothalamus = spermBody.getJSONObject(TeleonomeConstants.SPERM_HYPOTHALAMUS);
		JSONArray actions = hypothalamus.optJSONArray(TeleonomeConstants.SPERM_HYPOTHALAMUS_ACTIONS);
		if (actions == null) {
			actions = new JSONArray();
			hypothalamus.put(TeleonomeConstants.SPERM_HYPOTHALAMUS_ACTIONS, actions);
		}
		JSONArray homeoboxes = hypothalamus.optJSONArray("Homeoboxes");
		if (homeoboxes == null) {
			homeoboxes = new JSONArray();
			hypothalamus.put("Homeoboxes", homeoboxes);
		}
		// an action is added once, whichever part brings it
		List<String> existingActions = new ArrayList<String>();
		for (int i = 0; i < actions.length(); i++) existingActions.add(actionKey(actions.getJSONObject(i)));

		JSONArray containers = recipe.optJSONArray("Containers");
		if (containers != null) {
			for (int i = 0; i < containers.length(); i++) {
				try {
					homeoboxes.put(Sertoli.renderContainer(containers.getJSONObject(i)));
				} catch (JSONException e) {
					result.errors.add("Container " + (i + 1) + ": " + e.getMessage());
				}
			}
		}

		JSONObject definitions = recipe.optJSONObject("HomeBoxDefinitions");
		if (definitions == null) {
			result.errors.add("The recipe has no HomeBoxDefinitions");
			return result;
		}
		ArrayList<String> externalDataDenesCreated = new ArrayList<String>();
		for (String section : SECTIONS) {
			JSONArray entries = definitions.optJSONArray(section);
			if (entries == null && section.equals("Components")) entries = definitions.optJSONArray(OLD_COMPONENTS_SECTION);
			if (entries == null) continue;
			for (int i = 0; i < entries.length(); i++) {
				String partName = section + " " + (i + 1);
				try {
					Object entry = entries.get(i);
					JSONObject settings = null;
					if (entry instanceof JSONObject) {
						JSONObject entryObject = (JSONObject) entry;
						// "Name" is how the 2020 recipes wrote it
						partName = entryObject.has("Part") ? entryObject.getString("Part") : entryObject.getString("Name");
						settings = entryObject.optJSONObject("Settings");
					} else {
						partName = entry.toString().trim();
					}
					JSONObject part = readPart(avocadoDir, partName);
					if (settings != null) applySettings(part, settings);
					JSONObject generated = generate(part, partName, teleonomeName, actions.length(), externalDataDenesCreated);
					homeoboxes.put(generated.getJSONObject("Homeobox"));
					JSONArray partActions = generated.optJSONArray("Actions");
					if (partActions != null) {
						for (int j = 0; j < partActions.length(); j++) {
							JSONObject action = partActions.getJSONObject(j);
							String key = actionKey(action);
							if (!existingActions.contains(key)) {
								actions.put(action);
								existingActions.add(key);
							}
						}
					}
				} catch (PartException e) {
					result.errors.add(partName + ": " + e.getMessage());
				} catch (Exception e) {
					// a generator that throws: say which part, keep the cause
					result.errors.add(partName + ": " + e.getClass().getSimpleName() + ": " + e.getMessage());
					logger.warn(partName + ": " + Utils.getStringException(e));
				}
			}
		}
		if (result.isOk()) result.sperm = sperm;
		return result;
	}

	private JSONObject readPart(File avocadoDir, String partFileName) throws PartException {
		String folder = folderFor(partFileName);
		if (folder == null) throw new PartException("unknown kind of part (extension ." + FilenameUtils.getExtension(partFileName) + ")");
		File file = new File(new File(avocadoDir, folder), partFileName);
		if (!file.isFile()) throw new PartException("not found in Avocado's " + folder + "/ folder");
		try {
			return new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
		} catch (IOException e) {
			throw new PartException("cannot be read: " + e.getMessage());
		} catch (JSONException e) {
			throw new PartException("is not valid JSON: " + e.getMessage());
		}
	}

	private JSONObject generate(JSONObject part, String partFileName, String teleonomeName, int currentActionIndicator,
			ArrayList<String> externalDataDenesCreated) throws Exception {
		String type;
		if (FilenameUtils.getExtension(partFileName).equalsIgnoreCase("hud") && part.optString(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE, "").trim().isEmpty()) {
			// a .hud without a type is a page's panels (HumanInterfaceHomeoBoxGenerator); one with a type has its
			// own generator (HumanInterfacePages, 2026-11-10)
			type = "HumanInterface";
		} else {
			type = part.optString(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE, "").trim();
			if (type.isEmpty()) throw new PartException("has no \"" + TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE + "\"");
		}
		HomeboxGenerator generator;
		try {
			generator = (HomeboxGenerator) Class.forName("com.teleonome.sertoli." + type + "HomeoBoxGenerator").getConstructor().newInstance();
		} catch (ClassNotFoundException e) {
			throw new PartException("no generator for type " + type + " (com.teleonome.sertoli." + type + "HomeoBoxGenerator)");
		}
		JSONObject generated = generator.process(teleonomeName, part, currentActionIndicator, externalDataDenesCreated);
		if (generated == null || !generated.has("Homeobox")) throw new PartException("the " + type + " generator made no homeobox");
		ArrayList<String> external = generator.getExternalTeleonomeNames();
		if (external != null) {
			for (String name : external) {
				if (!externalDataDenesCreated.contains(name)) externalDataDenesCreated.add(name);
			}
		}
		return generated;
	}

	/**
	 * Where a setting's path leads: "A:B:C" is part.A.B.C; a key that meets a list of named items (a
	 * microcontroller's Software:Parameters) picks the item of that Name, and the setting is its "Value".
	 * Returns {the object to put into, the key to put}, or null when the path leads nowhere.
	 */
	static Object[] resolve(JSONObject part, String path) {
		String[] keys = path.split(":");
		JSONObject target = part;
		for (int k = 0; k < keys.length; k++) {
			String key = keys[k].trim();
			boolean last = k == keys.length - 1;
			Object next = target.opt(key);
			if (next instanceof JSONArray && k + 1 < keys.length) {
				JSONObject named = named((JSONArray) next, keys[k + 1].trim());
				if (named == null) return null;
				if (k + 1 == keys.length - 1) return new Object[] { named, "Value" };
				target = named;
				k++;
			} else if (last) {
				return next == null ? null : new Object[] { target, key };
			} else if (next instanceof JSONObject) {
				target = (JSONObject) next;
			} else {
				return null;
			}
		}
		return null;
	}

	private static JSONObject named(JSONArray items, String name) {
		for (int i = 0; i < items.length(); i++) {
			JSONObject item = items.optJSONObject(i);
			if (item != null && name.equals(item.optString("Name"))) return item;
		}
		return null;
	}

	static void applySettings(JSONObject part, JSONObject settings) throws PartException {
		for (String path : settings.keySet()) {
			Object[] where = resolve(part, path);
			if (where == null) throw new PartException("setting " + path + " leads to nothing in the part");
			((JSONObject) where[0]).put((String) where[1], settings.get(path));
		}
	}

	private static String actionKey(JSONObject action) {
		return action.optString(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE).trim()
				+ action.optString(TeleonomeConstants.SPERM_HOX_DENE_TARGET).trim()
				+ action.optString(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE).trim();
	}

	static class PartException extends Exception {
		private static final long serialVersionUID = 1L;

		PartException(String message) {
			super(message);
		}
	}
}
