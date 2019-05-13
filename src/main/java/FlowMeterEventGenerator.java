import org.json.JSONArray;
import org.json.JSONObject;

import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity;
import com.teleonome.framework.utils.Utils;
import com.teleonome.sertoli.EventGenerator;

public class FlowMeterEventGenerator extends EventGenerator {

	/*
	 * 
	 * @see com.teleonome.sertoli.EventGenerator#process(java.lang.String)
	 */
	public JSONArray process(String teleonomeName) {
		JSONArray toReturn = new JSONArray();
		
		
		JSONObject eventValueDefinitionListDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionListDeneJSONObject);
		String telepathonsDeneChainTargetPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS ).toString();

		eventValueDefinitionListDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, "FlowMeter Event Value List");

		JSONArray eventValueDefinitionListDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionListDeneJSONObject.put("DeneWords", eventValueDefinitionListDeneWordsJSONArray);
		eventValueDefinitionListDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		String codonValue="FlowMeter Event";
		JSONObject deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);

		String[] valuesName = {"Event Start Time", "Event End Time", "Event Group Start Time","Average Flow", "Total Volume","Flow Meter Id","Sample Frequency Milliseconds","Number Of Samples In Event","Samples"};
		for(int i=0;i<valuesName.length;i++) {
			String valueName = valuesName[i];
			String pointerToEventvalueDefinition = new Identity("Egg", TeleonomeConstants.NUCLEI_INTERNAL,TeleonomeConstants.DENECHAIN_TELEPATHONS, valueName).toString();
			deneword = Utils.createDeneWordJSONObject(valueName , pointerToEventvalueDefinition, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
			deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
			eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		}
		//
		// "Event Start Time"
		//
		String prefix = "FlowMeter ";
		String valueName = prefix + "Event Start Time";
		JSONObject eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		JSONArray eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"seconds", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,"long", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,1, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","Start Time", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		//
		// "Event End Time"
		//
		valueName = prefix + "Event End Time";
		eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"seconds", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,"long", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,2, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","End Time", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		//
		// "Event End Time"
		//
		valueName = prefix + "Group Start Time";
		
		eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"seconds", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,"long", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,3, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","Group Start Time", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		//
		// "Average Flow"
		//
		valueName = prefix + "Average Flow";
		eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"lit/min", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,TeleonomeConstants.DATATYPE_DOUBLE, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,4, null, TeleonomeConstants.DATATYPE_DOUBLE, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","Average Flow", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		//
		// "Event Volume"
		//
		valueName = prefix + "Event Volume";
		eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"lit", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,TeleonomeConstants.DATATYPE_DOUBLE, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,5, null, TeleonomeConstants.DATATYPE_DOUBLE, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","Event Volume", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		//
		// "Flow Meter Id"
		//
		valueName = prefix + "Flow Meter Id";
		eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,TeleonomeConstants.DATATYPE_INTEGER, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,6, null, TeleonomeConstants.DATATYPE_DOUBLE, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","Flow Meter Id", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		//
		// "Sample Frequency Milliseconds"
		//
		valueName = prefix + " Sample Frequency Milliseconds";
		eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"millisec", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,TeleonomeConstants.DATATYPE_INTEGER, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,7, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","Sample Frequency Milliseconds", null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		//
		// "Number of Samples"
		//
		valueName = prefix + " Number of Samples";
		eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,TeleonomeConstants.DATATYPE_INTEGER, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,8, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","Number of Samples", null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		//
		// "Samples"
		//
		valueName = prefix + " Samples";
		eventValueDefinitionDeneJSONObject= new JSONObject();
		toReturn.put(eventValueDefinitionDeneJSONObject);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_NAME_ATTRIBUTE, valueName);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_EVENT_VALUE_DEFINITION);
		eventValueDefinitionDeneWordsJSONArray = new JSONArray();
		eventValueDefinitionDeneJSONObject.put("DeneWords", eventValueDefinitionDeneWordsJSONArray);
		eventValueDefinitionDeneJSONObject.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, telepathonsDeneChainTargetPointer);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.CODON, codonValue, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_UNIT_ATTRIBUTE,"", null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VALUETYPE_ATTRIBUTE,TeleonomeConstants.DATATYPE_TIME_SERIES, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_SERIESTYPE_ATTRIBUTE,TeleonomeConstants.DATATYPE_DOUBLE, null, TeleonomeConstants.DATATYPE_STRING, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_EVENT_STRING_QUEUE_POSITION,9, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		deneword = Utils.createDeneWordJSONObject("DeneWord Name","Samples", null, TeleonomeConstants.DATATYPE_INTEGER, true);
		eventValueDefinitionListDeneWordsJSONArray.put(deneword);
		
		return toReturn;
	}

}
