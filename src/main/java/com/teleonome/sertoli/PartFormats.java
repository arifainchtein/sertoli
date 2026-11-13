package com.teleonome.sertoli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;

/**
 * The format of every kind of part in Avocado (Avocado design §3.1, phase 1), in one place: what each generator
 * reads, and what the factory's Avocado page needs to offer a part in its palette. Used by the page before it
 * shows a part, and from the command line:
 *
 *   PartFormats check <avocado folder>     every part's problems
 *   PartFormats markdown                   the formats, for docs/avocado-part-formats.md
 *
 * Errors are what makes Sertoli fail on the part. Palette warnings are what the page needs and Sertoli does not
 * (a name to show, a description, a category); a part with warnings works in a recipe but is not offered.
 */
public class PartFormats {

	public static class Problems {
		public final List<String> errors = new ArrayList<String>();
		public final List<String> paletteWarnings = new ArrayList<String>();

		public boolean usable() {
			return errors.isEmpty();
		}

		public boolean offeredInPalette() {
			return errors.isEmpty() && paletteWarnings.isEmpty();
		}
	}

	// one field of a part: its name, its JSON type, whether it must be there, what a list holds or an object contains
	static class Field {
		final String name, type, description;
		final boolean required;
		final Field[] children;

		Field(String name, String type, boolean required, String description, Field... children) {
			this.name = name;
			this.type = type;
			this.required = required;
			this.description = description;
			this.children = children;
		}
	}

	static Field req(String name, String type, String description, Field... children) {
		return new Field(name, type, true, description, children);
	}

	static Field opt(String name, String type, String description, Field... children) {
		return new Field(name, type, false, description, children);
	}

	static final String STRING = "string", INT = "int", NUMBER = "number", BOOLEAN = "boolean", OBJECT = "object", LIST = "list";

	// every part, whatever its kind
	static final Field[] COMMON = {
			req(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE, STRING, "names the generator: <type>HomeoBoxGenerator (not needed in a .hud of panels)"),
			req("Homeobox Name", STRING, "the homeobox's name in the sperm"),
			opt("Configurable", LIST, "what the page lets a recipe change",
					req("Name", STRING, "the setting's path in the part, A:B for B inside A; Software:Parameters:<name> for a parameter's Value"),
					req("Value Type", STRING, "int, double, String, boolean"),
					opt("Required", BOOLEAN, ""),
					opt("Default", "any", ""),
					opt("Per Unit", BOOLEAN, "true: differs from one unit to the next; never set in a recipe (the factory writes it at Send To DS)"),
					opt("Secret", BOOLEAN, "true: a password or key; never in a recipe or a Denome")) };

	// what the page needs to offer a part
	static final Field[] PALETTE = {
			req("Display Name", STRING, "the name on the part's card"),
			req("Description", STRING, "one line: what the part adds"),
			req("Category", STRING, "the palette group: Microcontrollers, Sensors, Actuators, Publishers, Telepathons, Human Interface"),
			opt("Requires", LIST, "file names of parts this one needs in the same recipe") };

	static final Map<String, Field[]> BY_TYPE = new LinkedHashMap<String, Field[]>();
	static {
		BY_TYPE.put("MicroController", new Field[] {
				req("Component Name", STRING, "the component dene's name"),
				req("Class", STRING, "the framework's MicroController class"),
				req("Is Mother", BOOLEAN, ""),
				req("Software", OBJECT, "",
						req("Processing Queue Position", INT, ""),
						opt("Async Request Delay Millis", INT, "the asynchronous cycle's pause between requests"),
						opt("Async Execution Mode", STRING, "Continuous or Single"),
						req("Parameters", LIST, "the microcontroller's config parameters",
								req("Name", STRING, ""), req("Value", "any", "of its Value Type"), req("Value Type", STRING, "int, double, String, boolean"),
								opt("Range Minimum", NUMBER, ""), opt("Range Maximum", NUMBER, ""))),
				opt("Hardware", OBJECT, "Arduino boards only",
						opt("Storage Manager", STRING, "e.g. SDCardManager"), opt("Time Manager", STRING, "e.g. GravityRTC")) });
		BY_TYPE.put("Sensor", new Field[] {
				req("Sensor Name", STRING, ""), req("Port", STRING, ""),
				req("Microcontroller Pointer", STRING, "the microcontroller that reads it"),
				req("Provider", STRING, ""), req("Reporting Value Dene Name", STRING, ""),
				req("Values", LIST, "one per value the sensor reports",
						req("Name", STRING, ""), req("Units", STRING, ""), req("Value Type", STRING, ""),
						req("Sensor Request Queue Position", INT, "the order the microcontroller reports the values in"),
						opt("Initial Value", "any", "of its Value Type; default false for a boolean, 0 otherwise"),
						opt("Range Minimum", NUMBER, ""), opt("Range Maximum", NUMBER, "")),
				opt("Container Page Identity Pointer", STRING, "the page its panel goes on; none: no panel"),
				opt(TeleonomeConstants.HUMAN_INTERFACE_PANEL, STRING, "the panel's name") });
		BY_TYPE.put("Actuator", new Field[] {
				req("Actuator Name", STRING, "the actuator dene's name; its action list is \"<Actuator Name> Actions\""),
				req("Microcontroller Pointer", STRING, "the microcontroller the actions' commands go to"),
				opt("Execution Position", INT, "default 1") });
		BY_TYPE.put("SFTPPublisher", new Field[] {
				req("SFTP Server Ip Address", STRING, ""), req("SFTP Key File Name", STRING, ""),
				req("Processing Queue Position", INT, "") });
		BY_TYPE.put("DigitalStablesPublisher", new Field[] {
				req("Processing Queue Position", INT, ""),
				req("Upload To DigitalStables", BOOLEAN, "false: nothing is queued or sent"),
				req("DigitalStables Server", STRING, "devicesheart's address, wss://.../mqtt"),
				req("DigitalStables Settings File", STRING, "the file the factory writes at Send To DS (login, serial number, password)"),
				opt("Prepare Evaluation Position", INT, "default 25"), opt("Publish Evaluation Position", INT, "default 26"),
				opt("Keep Command Requests Days", INT, "the Mnemosycon rule; default 7") });
		BY_TYPE.put("TelepathonSupport", new Field[] {
				req("Actuator Name", STRING, "the receiver's actuator the delete commands go to (Annabelle Actuator)"),
				opt("Panel In Page Position", INT, "the Telepathon View Panel on the Home Page; default 2"),
				opt("Delete Telepathon Evaluation Position", INT, "default 1"),
				opt("Delete Stale Telepathons Evaluation Position", INT, "default 1") });
		BY_TYPE.put("HumanInterface", new Field[] {
				req("Container Page Identity", STRING, "the page"),
				req("Panels", LIST, "", req("Processor", STRING, "the panel generator"), opt("Values", LIST, "")) });
		BY_TYPE.put("HumanInterfacePages", new Field[] {
				opt("Panel In Page Position", INT, "the Home Page Pills Panel on the Home Page; default 3"),
				opt("Show Map", BOOLEAN, "the Map pill (devices placed by their telepathons' Latitude and Longitude); default true"),
				opt("Allow New Pages", BOOLEAN, "offer + New tab; default true") });
		BY_TYPE.put("PiFourValuesFourDigitDisplays", new Field[] {
				req("Processing Queue Position", INT, ""), req("Action Group Name", STRING, ""), req("Provider", STRING, ""),
				req("Displays", LIST, "",
						req("Name", STRING, ""), req("Clock Pin", INT, ""), req("Data Pin", INT, ""),
						req("Data Source", OBJECT, "", req("Data Source Pointer", STRING, ""))) });
		BY_TYPE.put("ActionExecutedEveryNPulses", new Field[] {
				req("Counter Increment", INT, ""), req("Counter Limit", INT, ""), req("Execution Position", INT, ""),
				req("Action Group Name", STRING, ""), req("Action Name", STRING, ""),
				req("Actuator Action List Pointer", STRING, ""), req("Actuator Name", STRING, ""),
				req("Condition Name", STRING, ""), req("Counter Suffix", STRING, ""), req("Microcontroller Pointer", STRING, "") });
		BY_TYPE.put("MutationBasedAction", new Field[] {
				req("Evaluation Position", INT, ""), req("Action Name", STRING, ""), req("Actuator Name", STRING, ""),
				req("Expression", STRING, ""), req("Microcontroller Pointer", STRING, "") });
		BY_TYPE.put("ExternalDataMultiColorLED", new Field[] {
				req("LED Position", INT, ""), req("Cases", LIST, ""), req("Thresholds", LIST, ""),
				req("Data Source", OBJECT, "", req("Data Source Pointer", STRING, "")),
				req("Action Group Name", STRING, ""), req("Actuator Action List Pointer", STRING, ""),
				req("External Data Source Pointer", STRING, ""), req("MicroControllerBaseCommand", STRING, ""),
				req("Microcontroller Pointer", STRING, ""), req("Provider", STRING, "") });
		BY_TYPE.put("AddDataTransformation", new Field[] {
				req("Data Source", STRING, ""), req("Destination", STRING, ""),
				req("Destination Data Value Type", STRING, ""), req("Function Name", STRING, "") });
	}

	public static Problems check(JSONObject part, String partFileName) {
		Problems problems = new Problems();
		String type;
		if (FilenameUtils.getExtension(partFileName).equalsIgnoreCase("hud") && part.optString(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE, "").trim().isEmpty()) {
			type = "HumanInterface";
			checkFields(part, new Field[] { COMMON[1], COMMON[2] }, "", problems.errors);
		} else {
			checkFields(part, COMMON, "", problems.errors);
			type = part.optString(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE, "");
		}
		Field[] fields = BY_TYPE.get(type);
		if (fields == null) {
			if (!type.isEmpty()) problems.errors.add("no format (and no generator) for type " + type);
		} else {
			checkFields(part, fields, "", problems.errors);
		}
		checkConfigurable(part, problems.errors);
		checkFields(part, PALETTE, "", problems.paletteWarnings);
		return problems;
	}

	private static void checkFields(JSONObject object, Field[] fields, String where, List<String> out) {
		for (Field field : fields) {
			String path = where + field.name;
			if (!object.has(field.name)) {
				if (field.required) out.add("missing " + path + " (" + field.type + ")");
				continue;
			}
			Object value = object.get(field.name);
			if (!hasType(value, field.type)) {
				out.add(path + " should be " + field.type + ", is " + describe(value));
				continue;
			}
			if (field.children.length == 0) continue;
			if (value instanceof JSONObject) {
				checkFields((JSONObject) value, field.children, path + ":", out);
			} else if (value instanceof JSONArray) {
				JSONArray items = (JSONArray) value;
				for (int i = 0; i < items.length(); i++) {
					Object item = items.get(i);
					if (!(item instanceof JSONObject)) {
						out.add(path + "[" + (i + 1) + "] should be an object, is " + describe(item));
					} else {
						checkFields((JSONObject) item, field.children, path + "[" + (i + 1) + "]:", out);
					}
				}
			}
		}
	}

	// every Configurable path must lead to a value in the part
	private static void checkConfigurable(JSONObject part, List<String> out) {
		JSONArray configurable = part.optJSONArray("Configurable");
		if (configurable == null) return;
		for (int i = 0; i < configurable.length(); i++) {
			JSONObject setting = configurable.optJSONObject(i);
			if (setting == null || !setting.has("Name")) continue;
			if (SertoliEngine.resolve(part, setting.getString("Name")) == null) {
				out.add("Configurable " + setting.getString("Name") + " leads to nothing in the part");
			}
		}
	}

	private static boolean hasType(Object value, String type) {
		switch (type) {
		case STRING: return value instanceof String;
		case INT: return value instanceof Integer || value instanceof Long;
		case NUMBER: return value instanceof Number;
		case BOOLEAN: return value instanceof Boolean;
		case OBJECT: return value instanceof JSONObject;
		case LIST: return value instanceof JSONArray;
		default: return true;
		}
	}

	private static String describe(Object value) {
		if (value instanceof JSONObject) return OBJECT;
		if (value instanceof JSONArray) return LIST;
		if (value instanceof String) return STRING + " \"" + value + "\"";
		return value.getClass().getSimpleName().toLowerCase() + " " + value;
	}

	static String markdown() {
		StringBuilder md = new StringBuilder();
		md.append("# Avocado part formats\n\n*Generated by `PartFormats markdown` (sertoli); edit PartFormats.java, not this file.*\n\n");
		md.append("A part's folder in Avocado comes from its extension: `.mcu` mcu/, `.hsd` hsd/, `.had` had/, `.hud` hud/, `.pub` pub/, `.tph` telepathons/.\n\n");
		md.append("## Every part\n\n");
		table(md, COMMON);
		md.append("## For the palette (the page offers a part only with these)\n\n");
		table(md, PALETTE);
		for (Map.Entry<String, Field[]> entry : BY_TYPE.entrySet()) {
			md.append("## ").append(entry.getKey()).append("\n\n");
			table(md, entry.getValue());
		}
		return md.toString();
	}

	private static void table(StringBuilder md, Field[] fields) {
		md.append("| Field | Type | Required | What |\n|---|---|---|---|\n");
		rows(md, fields, "");
		md.append("\n");
	}

	private static void rows(StringBuilder md, Field[] fields, String prefix) {
		for (Field field : fields) {
			md.append("| `").append(prefix).append(field.name).append("` | ").append(field.type)
					.append(" | ").append(field.required ? "yes" : "no").append(" | ").append(field.description).append(" |\n");
			rows(md, field.children, prefix + field.name + (field.type.equals(LIST) ? "[]:" : ":"));
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 1 && args[0].equals("markdown")) {
			System.out.print(markdown());
			return;
		}
		if (args.length != 2 || !args[0].equals("check")) {
			System.out.println("Usage: PartFormats check <avocado folder> | PartFormats markdown");
			System.exit(2);
		}
		File avocado = new File(args[1]);
		int usable = 0, offered = 0, total = 0;
		for (String folder : Arrays.asList("mcu", "hsd", "had", "hud", "pub", "telepathons")) {
			File[] files = new File(avocado, folder).listFiles();
			if (files == null) continue;
			Arrays.sort(files);
			for (File file : files) {
				if (!file.isFile() || SertoliEngine.folderFor(file.getName()) == null) continue;
				total++;
				Problems problems;
				try {
					problems = check(new JSONObject(FileUtils.readFileToString(file, "UTF-8")), file.getName());
				} catch (JSONException e) {
					problems = new Problems();
					problems.errors.add("not valid JSON: " + e.getMessage());
				}
				if (problems.usable()) usable++;
				if (problems.offeredInPalette()) offered++;
				String status = problems.offeredInPalette() ? "OK" : problems.usable() ? "usable, not in palette" : "BROKEN";
				System.out.println(folder + "/" + file.getName() + ": " + status);
				for (String error : problems.errors) System.out.println("    error: " + error);
				if (problems.usable()) {
					for (String warning : problems.paletteWarnings) System.out.println("    palette: " + warning);
				}
			}
		}
		System.out.println(total + " parts: " + usable + " usable in a recipe, " + offered + " offered in the palette");
	}
}
