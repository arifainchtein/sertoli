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
	
	public Sertoli() {
	}

	public void process(String selectedSpermFileName) {
		//
		// Load the Sperm
		//String selectedDenomeFileName = "/home/pi/Teleonome.denome";
		
		String stringFormSperm="";
		
		
		File selectedSpermFile = new File(dataDirectory + selectedSpermFileName);
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
		
		String teleonomeName = purposeJSONObject.getString("Teleonome Name");

		
		
		String stringFormHDS="";
		moveFiles(selectedSpermFileName);
		File dir = new File(dataDirectory );
		FileFilter fileFilter = new WildcardFileFilter("*.hsd");
		File[] files = dir.listFiles(fileFilter);
		JSONObject homeBoxProcessingResultJSONObject,homeoBoxJSONObject,actionJSONObject;
		JSONArray homeBoxProcessingActionsJSONArray;
		
		if(files.length>0) {
			StringBuffer data1=new StringBuffer();;
			logger.info("found "+ files.length +" hsd files" );
			File destFile;
			for(int i=0;i<files.length;i++) {
				
				try {
					logger.debug("reading  " +files[i]);
					stringFormHDS = FileUtils.readFileToString(files[i]);
					logger.debug("stringFormHDS  " +stringFormHDS);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.warn(Utils.getStringException(e));
				}
				JSONObject homeboxSourceDataElement = new JSONObject(stringFormHDS);
				String homeboxDefinitionType = "com.teleonome.sertoli." + homeboxSourceDataElement.getString(TeleonomeConstants.HOMEOBOX_DEFINITION_TYPE) + "HomeoBoxGenerator";
				Class<?> clazz;
				try {
					clazz = Class.forName(homeboxDefinitionType);
					Constructor<?> constructor = clazz.getConstructor();
					HomeboxGenerator anHomeboxGenerator = (HomeboxGenerator) constructor.newInstance();
					homeBoxProcessingResultJSONObject = anHomeboxGenerator.process(teleonomeName, homeboxSourceDataElement,currentActionValue);
					homeoBoxJSONObject = homeBoxProcessingResultJSONObject.getJSONObject("Homeobox");
					homeBoxProcessingActionsJSONArray = homeBoxProcessingResultJSONObject.getJSONArray("Actions");
					//
					// now add the homebox to the sperm

					JSONArray homeoboxes = hypothalamusJSONObject.getJSONArray("Homeoboxes");
					homeoboxes.put(homeoBoxJSONObject);
					
					JSONArray actions = hypothalamusJSONObject.getJSONArray("Actions");
					
					for(int j=0;j<homeBoxProcessingActionsJSONArray.length();j++) {
						actionJSONObject = homeBoxProcessingActionsJSONArray.getJSONObject(j);
						actionsJSONArray.put(actionJSONObject);
						currentActionValue++;
					}
					
					
					FileUtils.writeStringToFile(selectedSpermFile, spermJSONObject.toString(4));
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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		logger.warn("Process Completed");
	}


	private void moveFiles(String spermFileName) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(TeleonomeConstants.SPERM_DATE_FORMAT);
		
		String destFolderName=dataDirectory + "Sertolization/" + dateFormat.format(new Timestamp(System.currentTimeMillis())) + "/";
		File destFolder = new File(destFolderName);
		destFolder.mkdirs();
		File srcFile = new File(dataDirectory + spermFileName);
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
		
		
		if(args.length!=1){
			System.out.println("Usage: Sertoli localSpermFileName ");
			System.exit(-1);
		}
		
		
		if(args.length>0 && args[0].equals("-v")) {
			System.out.println("Sertoli Build " + BUILD_NUMBER);
			System.exit(0);
		}else if(args[0].equals("-u")) {
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
			String spermFileName=args[0];
			File f = new File(dataDirectory + spermFileName);
			if(!f.isFile()){
				System.out.println("Sperm file is invalid: " + dataDirectory + spermFileName);
				System.exit(-1);
			}
			
			new Sertoli().process(spermFileName);
		}
	}
}
