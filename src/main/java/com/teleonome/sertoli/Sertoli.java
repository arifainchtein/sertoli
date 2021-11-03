package com.teleonome.sertoli;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.teleonome.framework.TeleonomeConstants;
import com.teleonome.framework.denome.Identity; 
import com.teleonome.framework.exception.MissingDenomeException;
import com.teleonome.framework.utils.Utils;


public class Sertoli 
{
	public final static String BUILD_NUMBER="14/05/2018 08:26";
	static Logger logger;
	static String dataDirectory = Utils.getLocalDirectory() + "avocado/";
	static String hsdDirectoryName = Utils.getLocalDirectory() + "avocado/hsd/";
	static String hadDirectoryName = Utils.getLocalDirectory() + "avocado/had/";
	static String spermDirectoryName = Utils.getLocalDirectory() + "avocado/sperm/";
	static String sertoliDirectoryName = Utils.getLocalDirectory() + "avocado/sertoli/";

	public Sertoli() {
	}

	public void process(String selectedSpermFileName, String teleonomeName) {
		//
		// Load the Sperm
		//String selectedDenomeFileName = "/home/pi/Teleonome.denome";

		String stringFormSperm="";


		File selectedSpermFile = new File(spermDirectoryName + selectedSpermFileName);
		try {

			logger.debug("reading sperm from " +selectedSpermFileName);

			stringFormSperm = FileUtils.readFileToString(selectedSpermFile);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn(Utils.getStringException(e));
		}

		if(stringFormSperm.equals("")){
			Hashtable info = new Hashtable();
			logger.warn("The sperm file was not found in " + dataDirectory + " , ending.");
			System.exit(0);
		}
		JSONObject spermJSONObject = new JSONObject(stringFormSperm);
		JSONObject purposeJSONObject = spermJSONObject.getJSONObject(TeleonomeConstants.SPERM).getJSONObject(TeleonomeConstants.SPERM_PURPOSE);
		JSONObject hypothalamusJSONObject = spermJSONObject.getJSONObject(TeleonomeConstants.SPERM).getJSONObject(TeleonomeConstants.SPERM_HYPOTHALAMUS);
		JSONObject medulaJSONObject = spermJSONObject.getJSONObject(TeleonomeConstants.SPERM).getJSONObject(TeleonomeConstants.SPERM_MEDULA);


		JSONArray actionsJSONArray = hypothalamusJSONObject.getJSONArray(TeleonomeConstants.SPERM_HYPOTHALAMUS_ACTIONS);
		int currentActionValue=actionsJSONArray.length();
		JSONObject homeBoxProcessingResultJSONObject,homeoBoxJSONObject,actionJSONObject;
		JSONArray homeBoxProcessingActionsJSONArray;
		String newActionName="", newActionTarget="", newActionDeneType="";
		ArrayList<String> existing = new ArrayList();

		for(int j=0;j<actionsJSONArray.length();j++) {
			actionJSONObject = actionsJSONArray.getJSONObject(j);
			newActionName = actionJSONObject.getString(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE);
			newActionTarget = actionJSONObject.getString(TeleonomeConstants.SPERM_HOX_DENE_TARGET);
			newActionDeneType = actionJSONObject.getString(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE);
			existing.add(newActionName.trim() + newActionTarget.trim() + newActionDeneType.trim());
		}


		purposeJSONObject.put("Teleonome Name", teleonomeName);
		String stringFormHDS="";
		moveFiles(selectedSpermFileName);
		//File dir = new File(dataDirectory );


		//
		// now read the .sertoli file which will contain the Containers definition and the HmeBoxDefinition
		//
		File sertoliFile = new File(sertoliDirectoryName + teleonomeName + ".sertoli");
		String stringFormSertoli="";
		try {

			logger.debug("reading sertoliFile from " +sertoliFile.getAbsolutePath());

			stringFormSertoli = FileUtils.readFileToString(sertoliFile);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn(Utils.getStringException(e));
		}

		if(stringFormSertoli.equals("")){
			Hashtable info = new Hashtable();
			logger.warn("The sertoli file was not found in " + dataDirectory + " , ending.");
			System.exit(0);
		}
		JSONObject sertoliJSONObject = new JSONObject(stringFormSertoli);
		JSONArray containers = sertoliJSONObject.getJSONArray("Containers");
		
		JSONObject homeBoxDefinitions = sertoliJSONObject.getJSONObject("HomeBoxDefinitions");
		JSONArray componentsDefinitions = homeBoxDefinitions.getJSONArray("Components");
		
		JSONArray sensorsHomeBoxDefiitions = homeBoxDefinitions.getJSONArray("Sensors");
		JSONArray actuatorsHomeBoxDefiitions = homeBoxDefinitions.getJSONArray("Actuators");
		JSONArray humanInterfaceHomeBoxDefiitions = homeBoxDefinitions.getJSONArray("Human Interface");
		
		
		String hsdFileName;
		String homeboxDefinitionType;
		Class<?> clazz;

		JSONObject homeboxSourceDataElement;

		Constructor<?> constructor;
		HomeboxGenerator anHomeboxGenerator;


		JSONArray hypothalamusHomeoboxes = hypothalamusJSONObject.getJSONArray("Homeoboxes");
		JSONArray actions;
		//
		// first process each container and store the resulting homeobox
		JSONObject container;

		for(int i=0;i<containers.length();i++) {
			container = containers.getJSONObject(i);
			homeoBoxJSONObject = renderContainer(container);
			hypothalamusHomeoboxes.put(homeoBoxJSONObject);
		}
		
		//
		// nw process compnents
		JSONObject component;
		String mcuFileName;
		ArrayList<String> externalDataDenesCreated = new ArrayList();
		
		for(int i=0;i<componentsDefinitions.length();i++) {
			try {
				mcuFileName=hsdDirectoryName + componentsDefinitions.getString(i).trim();
				logger.debug("reading  " +mcuFileName);
				stringFormHDS = FileUtils.readFileToString(new File(mcuFileName));
				//logger.debug("stringFormHDS  " +stringFormHDS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			}
			homeboxSourceDataElement = new JSONObject(stringFormHDS);
			homeboxDefinitionType = "com.teleonome.sertoli." + homeboxSourceDataElement.getString(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE) + "HomeoBoxGenerator";
			try {
				clazz = Class.forName(homeboxDefinitionType);
				constructor = clazz.getConstructor();
				anHomeboxGenerator = (HomeboxGenerator) constructor.newInstance();
				homeBoxProcessingResultJSONObject = anHomeboxGenerator.process(teleonomeName, homeboxSourceDataElement,currentActionValue, externalDataDenesCreated);
				homeoBoxJSONObject = homeBoxProcessingResultJSONObject.getJSONObject("Homeobox");
				hypothalamusHomeoboxes.put(homeoBoxJSONObject);
				
			}catch(Exception e) {
				logger.warn(Utils.getStringException(e));
			}
			
			
			
			
			
			
		}
		//
		// Now Process the Sensor Definitions
		//
		
		for(int i=0;i<sensorsHomeBoxDefiitions.length();i++) {


			try {
				hsdFileName=hsdDirectoryName + sensorsHomeBoxDefiitions.getString(i).trim();
				logger.debug("reading  " +hsdFileName);
				stringFormHDS = FileUtils.readFileToString(new File(hsdFileName));
				//logger.debug("stringFormHDS  " +stringFormHDS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			}
			homeboxSourceDataElement = new JSONObject(stringFormHDS);
			homeboxDefinitionType = "com.teleonome.sertoli." + homeboxSourceDataElement.getString(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE) + "HomeoBoxGenerator";
			try {
				clazz = Class.forName(homeboxDefinitionType);
				constructor = clazz.getConstructor();
				anHomeboxGenerator = (HomeboxGenerator) constructor.newInstance();
				homeBoxProcessingResultJSONObject = anHomeboxGenerator.process(teleonomeName, homeboxSourceDataElement,currentActionValue, externalDataDenesCreated);
				homeoBoxJSONObject = homeBoxProcessingResultJSONObject.getJSONObject("Homeobox");
				homeBoxProcessingActionsJSONArray = homeBoxProcessingResultJSONObject.getJSONArray("Actions");
				//
				// now add the homebox to the sperm


				hypothalamusHomeoboxes.put(homeoBoxJSONObject);

				actions = hypothalamusJSONObject.getJSONArray("Actions");

				for(int j=0;j<homeBoxProcessingActionsJSONArray.length();j++) {
					actionJSONObject = homeBoxProcessingActionsJSONArray.getJSONObject(j);
					newActionName = actionJSONObject.getString(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE).trim();
					newActionTarget = actionJSONObject.getString(TeleonomeConstants.SPERM_HOX_DENE_TARGET).trim();
					newActionDeneType = actionJSONObject.getString(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE).trim();

					if(!existing.contains(newActionName + newActionTarget + newActionDeneType)) {
						actionsJSONArray.put(actionJSONObject);
						currentActionValue=actionsJSONArray.length();

						existing.add(newActionName + newActionTarget + newActionDeneType);
					}
				}



			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//
		// Now Process the Action Definitions
		//
		String hadFileName="", stringFormHAS="";
		ArrayList newExternalTeleonomeNames;
		String externalTeleonomeName;
		for(int i=0;i<actuatorsHomeBoxDefiitions.length();i++) {


			try {
				hadFileName=hadDirectoryName + actuatorsHomeBoxDefiitions.getString(i);
				logger.debug("reading  " +hadFileName);
				stringFormHAS = FileUtils.readFileToString(new File(hadFileName));
				//logger.debug("stringFormHDS  " +stringFormHDS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			}
			homeboxSourceDataElement = new JSONObject(stringFormHAS);
			homeboxDefinitionType = "com.teleonome.sertoli." + homeboxSourceDataElement.getString(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE) + "HomeoBoxGenerator";
			try {
				clazz = Class.forName(homeboxDefinitionType);
				constructor = clazz.getConstructor();
				anHomeboxGenerator = (HomeboxGenerator) constructor.newInstance();
				
				homeBoxProcessingResultJSONObject = anHomeboxGenerator.process(teleonomeName, homeboxSourceDataElement,currentActionValue, externalDataDenesCreated);
				//
				// After processing, check to see if externalDataDenesCreated needs to be updated
				
				newExternalTeleonomeNames = anHomeboxGenerator.getExternalTeleonomeNames();
				if(newExternalTeleonomeNames!=null && newExternalTeleonomeNames.size()>0) {
					for(int j=0;j<newExternalTeleonomeNames.size();j++) {
						externalTeleonomeName = (String)newExternalTeleonomeNames.get(j);
						if(!externalDataDenesCreated.contains(externalTeleonomeName)) {
							externalDataDenesCreated.add(externalTeleonomeName);
						}
					}
				}
				
				
				homeoBoxJSONObject = homeBoxProcessingResultJSONObject.getJSONObject("Homeobox");
				homeBoxProcessingActionsJSONArray = homeBoxProcessingResultJSONObject.getJSONArray("Actions");
				//
				// now add the homebox to the sperm


				hypothalamusHomeoboxes.put(homeoBoxJSONObject);

				actions = hypothalamusJSONObject.getJSONArray("Actions");

				for(int j=0;j<homeBoxProcessingActionsJSONArray.length();j++) {
					actionJSONObject = homeBoxProcessingActionsJSONArray.getJSONObject(j);
					newActionName = actionJSONObject.getString(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE).trim();
					newActionTarget = actionJSONObject.getString(TeleonomeConstants.SPERM_HOX_DENE_TARGET).trim();
					newActionDeneType = actionJSONObject.getString(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE).trim();

					if(!existing.contains(newActionName + newActionTarget + newActionDeneType)) {
						actionsJSONArray.put(actionJSONObject);
						currentActionValue=actionsJSONArray.length();

						existing.add(newActionName + newActionTarget + newActionDeneType);
					}
				}



			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//
		// now process the human interface elements
		//
		String hudFileName="", stringFormHUS="";
		
		for(int i=0;i<humanInterfaceHomeBoxDefiitions.length();i++) {


			try {
				hudFileName=hsdDirectoryName + humanInterfaceHomeBoxDefiitions.getString(i);
				logger.debug("reading  " +hudFileName);
				stringFormHUS = FileUtils.readFileToString(new File(hudFileName));
				//logger.debug("stringFormHDS  " +stringFormHDS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn(Utils.getStringException(e));
			}
			homeboxSourceDataElement = new JSONObject(stringFormHUS);
			homeboxDefinitionType = "com.teleonome.sertoli.HumanInterfaceHomeoBoxGenerator";
			try {
				clazz = Class.forName(homeboxDefinitionType);
				constructor = clazz.getConstructor();
				anHomeboxGenerator = (HomeboxGenerator) constructor.newInstance();
				
				homeBoxProcessingResultJSONObject = anHomeboxGenerator.process(teleonomeName, homeboxSourceDataElement,currentActionValue, externalDataDenesCreated);
				homeoBoxJSONObject = homeBoxProcessingResultJSONObject.getJSONObject("Homeobox");
				homeBoxProcessingActionsJSONArray = homeBoxProcessingResultJSONObject.getJSONArray("Actions");
				//
				// now add the homebox to the sperm

				logger.debug("line 343, homeoBoxJSONObject=" + homeoBoxJSONObject.length());
				hypothalamusHomeoboxes.put(homeoBoxJSONObject);

				actions = hypothalamusJSONObject.getJSONArray("Actions");
				
				for(int j=0;j<homeBoxProcessingActionsJSONArray.length();j++) {
					actionJSONObject = homeBoxProcessingActionsJSONArray.getJSONObject(j);
					newActionName = actionJSONObject.getString(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE).trim();
					newActionTarget = actionJSONObject.getString(TeleonomeConstants.SPERM_HOX_DENE_TARGET).trim();
					newActionDeneType = actionJSONObject.getString(TeleonomeConstants.DENE_DENE_TYPE_ATTRIBUTE).trim();

					if(!existing.contains(newActionName + newActionTarget + newActionDeneType)) {
						actionsJSONArray.put(actionJSONObject);
						currentActionValue=actionsJSONArray.length();

						existing.add(newActionName + newActionTarget + newActionDeneType);
					}
				}



			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//
		// now process the telepathons
		// start the counter from 


		try {
			File newSpermFile = new File(dataDirectory + teleonomeName + ".sperm");
			FileUtils.writeStringToFile(newSpermFile, spermJSONObject.toString(4));
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hypothalamusJSONObject = spermJSONObject.getJSONObject(TeleonomeConstants.SPERM).getJSONObject(TeleonomeConstants.SPERM_HYPOTHALAMUS);
		medulaJSONObject = spermJSONObject.getJSONObject(TeleonomeConstants.SPERM).getJSONObject(TeleonomeConstants.SPERM_MEDULA);
		int actionsLength = hypothalamusJSONObject.getJSONArray("Actions").length();
		int homeoBoxesLength = hypothalamusJSONObject.getJSONArray("Homeoboxes").length();

		logger.info("Sperm Analysis");
		logger.info(" ");
		logger.info("Hypothalamus:");
		logger.info("\tActions:" + actionsLength);
		logger.info("\tHomeoBoxes:" + homeoBoxesLength);
		JSONArray hbs= hypothalamusJSONObject.getJSONArray("Homeoboxes");
		JSONObject hb;
		for(int i=0;i<hbs.length();i++) {
			hb = hbs.getJSONObject(i);
			logger.info("\t" + hb.getString("Name"));
		}
		logger.info(" ");
		logger.warn("Process Completed");
	}



	private JSONObject renderContainer(JSONObject container) {
		//
		// now look at containers for a container that has the name equal to the value of humanInterfacePanel
		//

		String containerTarget = container.getString("Target");
		String containerName = container.getString("Name");
		//
		// now check to see if this exists already in the container target


		//
		// the container information to create 4 denewords, 

		//
		//		"Panel Visualization Style":"Single Value Complete Width",
		//        "Panel In Page Position ":1,
		//        "Visible":true,
		//"Panel Target Pointer":"@Egg:Human interface:Home Page"
		//
		JSONObject homeBoxJSONObject = new JSONObject();
		homeBoxJSONObject.put("Name", containerName);
		JSONArray denesJSONArray = new JSONArray();
		homeBoxJSONObject.put("Denes", denesJSONArray);

		JSONObject newContainerDene = new JSONObject();
		denesJSONArray.put(newContainerDene);
		newContainerDene.put(TeleonomeConstants.DENEWORD_NAME_ATTRIBUTE, containerName);
		newContainerDene.put(TeleonomeConstants.SPERM_HOX_DENE_TARGET, containerTarget);
		JSONArray newContainerDeneDeneWordsJSONArray = new JSONArray();
		newContainerDene.put("DeneWords", newContainerDeneDeneWordsJSONArray);

		String visualizationStyle = container.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_VISUALIZATION_STYLE);
		JSONObject deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, visualizationStyle, null, TeleonomeConstants.DATATYPE_STRING, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_VISUALIZATION_STYLE);
		newContainerDeneDeneWordsJSONArray.put(deneword);

		int panelInPagePosition = container.getInt(TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, panelInPagePosition, null, TeleonomeConstants.DATATYPE_INTEGER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_IN_PAGE_POSITION);
		newContainerDeneDeneWordsJSONArray.put(deneword);

		boolean visible = container.getBoolean(TeleonomeConstants.DENEWORD_VISIBLE);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_VISIBLE, true, null, TeleonomeConstants.DATATYPE_BOOLEAN, true);
		newContainerDeneDeneWordsJSONArray.put(deneword);

		String containerDeneChainPointer=container.getString(TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER);
		deneword = Utils.createDeneWordJSONObject(TeleonomeConstants.DENEWORD_ACTIVE, containerDeneChainPointer, null, TeleonomeConstants.DATATYPE_DENE_POINTER, true);
		deneword.put(TeleonomeConstants.DENEWORD_DENEWORD_TYPE_ATTRIBUTE, TeleonomeConstants.DENEWORD_TYPE_PANEL_DENECHAIN_POINTER);
		newContainerDeneDeneWordsJSONArray.put(deneword);
		return homeBoxJSONObject;
	}

	private void moveFiles(String spermFileName) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(TeleonomeConstants.SPERM_DATE_FORMAT);

		String destFolderName=dataDirectory + "Sertolization/" + dateFormat.format(new Timestamp(System.currentTimeMillis())) + "/";
		File destFolder = new File(destFolderName);
		destFolder.mkdirs();
		File srcFile = new File(spermDirectoryName + spermFileName);
		File destFile =  new File(destFolderName + "PreSertoli_" + spermFileName );
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getLastSertolizationDate() {
		File srcFolder= new File( dataDirectory +"Sertolization");
		File[] files = srcFolder.listFiles();
		Arrays.sort(files, new Comparator<File>(){
			public int compare(File f1, File f2)
			{
				return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			} });

		// take the first element of the array
		SimpleDateFormat dateFormat = new SimpleDateFormat(TeleonomeConstants.SPERM_DATE_FORMAT);
		return files[0].getName();

	}


	private static void undoSertolization(String spermFileName) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(TeleonomeConstants.SPERM_DATE_FORMAT);
		String destFolderName=dataDirectory;//"/home/pi/Teleonome/" ;

		//
		// first identify the folrders 
		File srcFolder= new File(dataDirectory + "Sertolization"); //"/home/pi/Teleonome/Sperm_Fert");

		File[] files = srcFolder.listFiles();

		Arrays.sort(files, new Comparator<File>(){
			public int compare(File f1, File f2)
			{
				return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
			} });

		// take the first element of the array
		File selectedSourceFolder = files[0];
		String srcFolderName=selectedSourceFolder.getAbsolutePath();

		logger.debug("The last Sertolization is " + selectedSourceFolder.getAbsolutePath());
		File destFolder = new File(destFolderName);
		//
		// copy the Teleonome.denome from fertilizatin back to main Teleonome
		//
		File srcFile =  new File(srcFolderName +  "/PreSertoli_" + spermFileName );
		File destFile = new File(dataDirectory+ spermFileName);
		//
		// First delete the file
		if(destFile.isFile()) {
			logger.debug("Erasing existing " + spermFileName);
			destFile.delete();
		}

		try {
			FileUtils.copyFile(srcFile, destFile);
			logger.debug("copying " +  srcFile + " " + destFile);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//
		// finally remove the directory
		logger.debug("Erasing Sertolization directory " + selectedSourceFolder.getAbsolutePath());
		try {
			FileUtils.deleteDirectory(selectedSourceFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn(Utils.getStringException(e));
		}

	}
	public static void main( String[] args )
	{
		String fileName =  Utils.getLocalDirectory() + "lib/Log4J.properties";
		PropertyConfigurator.configure(fileName);
		logger = Logger.getLogger(com.teleonome.sertoli.Sertoli.class);


		if(args.length>0 && args[0].equals("-v")) {
			System.out.println("Sertoli Build " + BUILD_NUMBER);
			System.exit(0);
		}else if(args.length>0 && args[0].equals("-u")) {
			String previousStateDate = getLastSertolizationDate();
			Scanner scanner = new Scanner(System.in);
			System.out.println("Type the sperm file name and enter  ");
			String spermFileName = scanner.nextLine();
			String line;

			if(spermFileName.length()>0) {
				System.out.println("Reverting to previous state");
				undoSertolization(spermFileName);
			}else {
				System.out.println("Goodbye");
				System.exit(0);
			}
			scanner.close();
		}else {
			if(args.length!=2){
				System.out.println("Usage: Sertoli localSpermFileName TeleonomeName");
				System.exit(-1);
			}

			String spermFileName=args[0];
			String teleonomeName=args[1];

			File f = new File(spermDirectoryName + spermFileName);
			if(!f.isFile()){
				System.out.println("Sperm file is invalid: " + dataDirectory + spermFileName);
				System.exit(-1);
			}

			new Sertoli().process(spermFileName, teleonomeName);
		}
	}
}
