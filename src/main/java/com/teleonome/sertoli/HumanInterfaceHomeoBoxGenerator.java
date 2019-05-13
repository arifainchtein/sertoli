package com.teleonome.sertoli;

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
import com.teleonome.sertoli.humaninterfacegenerators.HumanInterfaceGenerator;

public class HumanInterfaceHomeoBoxGenerator extends HomeboxGenerator {
	Logger logger;
	@Override
	public JSONObject process(String teleonomeName, JSONObject homeboxSourceDataElement, int currentActionIndicator, ArrayList externalDataDenesCreated) {
		logger = Logger.getLogger(getClass());
		int nextActionValue=currentActionIndicator;
		//
		// read data in
		//
		String homeBoxName  = homeboxSourceDataElement.getString("Homeobox Name");
		String containerPageIdentity  = homeboxSourceDataElement.getString("Container Page Identity");
		JSONArray panels = homeboxSourceDataElement.getJSONArray("Panels");
		
		
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
		
		JSONObject panel,processingResultJSONObject;
		String panelName, panelStyle, panelDeneChainPointer, processor;
		int panelInPagePosition;
		boolean visible=false;
		String processorClassName;
		Class clazz;
		Constructor constructor;
		HumanInterfaceGenerator anHumanInterfaceGenerator;
		String pageIdentity;
		JSONArray panelValues;
		for(int i=0;i<panels.length();i++){
			panel = panels.getJSONObject(i);
			panelName = panel.getString(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE);
			panelStyle = panel.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_VISUALIZATION_STYLE);
			panelInPagePosition = panel.getInt(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION);
			panelDeneChainPointer = new Identity("Egg", TeleonomeConstants.NUCLEI_HUMAN_INTERFACE,panelName ).toString();
			
			
			if(panel.has("Values")) {
				panelValues = panel.getJSONArray("Values");
			}else {
				panelValues = new JSONArray();
			}
			processor =  panel.getString("Processor");
			
			JSONObject data = new JSONObject();
			
			
			data.put(TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER, panelDeneChainPointer);
			data.put(TeleonomeConstants.DENE_TYPE_VISUALIZATION_STYLE, panelStyle);
			data.put(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION, panelInPagePosition);
			data.put(TeleonomeConstants.DENEWORD_VISIBLE, visible);
			data.put("Container Page Identity", containerPageIdentity);
			data.put(TeleonomeConstants.DENEWORD_TYPE_PANEL_DATA_DISPLAY_NAME, panelName);
			data.put("Values", panelValues);
			//
			// invoke the generarator
			//
			processorClassName = "com.teleonome.sertoli.humaninterfacegenerators." + processor ;
			try {
				clazz = Class.forName(processorClassName);
				constructor = clazz.getConstructor();
				anHumanInterfaceGenerator = (HumanInterfaceGenerator) constructor.newInstance();
				
				processingResultJSONObject = anHumanInterfaceGenerator.process(data, nextActionValue);
				nextActionValue++;
				JSONArray denes = processingResultJSONObject.getJSONArray("Denes");
				JSONArray actions = processingResultJSONObject.getJSONArray("Actions");
				
				for(int j=0;j<denes.length();j++) {
					denesJSONArray.put(denes.get(j));
				}
				for(int j=0;j<actions.length();j++) {
					actionJSONArray.put(actions.get(j));
				}
			} catch (ClassNotFoundException e) {
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
		for(int i=0;i<denesJSONArray.length();i++) {
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
		
		return homeBoxProcessingResultJSONObject;
		
	}
	@Override
	public ArrayList getExternalTeleonomeNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
