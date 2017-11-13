/**
 * Last Changes Done on Jan 20, 2015 12:41:21 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */

package lenskart.tests;


import java.io.File;
import java.util.TreeMap;

import org.apache.log4j.Logger; 
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeSuite;

import framework.utilities.FileLib;
import framework.utilities.GenericMethodsLib;
import framework.utilities.XlsLib;
import lenskart.tests.TestSuiteClass;

import org.testng.annotations.AfterSuite;


public class TestSuiteClass
{

	public static String executionResult;

	public static String resultFileLocation;
	public static TreeMap<String, Integer> totalTC = new TreeMap<String, Integer>();
	public static String AUTOMATION_HOME;

	/** setting up unique execution id to be used in case of threads */
	public static ThreadLocal<Object> UNIQ_EXECUTION_ID = new ThreadLocal<>();

	/** Declaring logger */
	Logger logger = Logger.getLogger(TestSuiteClass.class.getName());

	//@Parameters({"logFileLocation", "logFileName", "ReRun"})
	@BeforeSuite
	public void beforeSuite()
	{
		try
		{
			/** setting up automation_home */
			AUTOMATION_HOME=System.getProperty("user.dir");

			System.out.println("AUTOMATION_HOME is: "+AUTOMATION_HOME);

			/** 1. Initialize configuration */
			GenericMethodsLib.InitializeConfiguration();

			/** Loading log4j.properties file for logger and creating logs folder in advance */
			PropertyConfigurator.configure(TestSuiteClass.AUTOMATION_HOME.concat("/conf/log4j.properties"));
			FileLib.CreateDirectory(TestSuiteClass.AUTOMATION_HOME.concat("/logs"));

			/** 4.Check if result file exists or not. */
			String resultFileName = "Main_Result";

			resultFileLocation = TestSuiteClass.AUTOMATION_HOME.concat("/results/Main_Result").toString();	
			File ResultFile = new File(resultFileLocation);

			/** delete and re create folders */
			if(!(ResultFile.exists())){
				ResultFile.mkdirs();
			}
			else {
				ResultFile.delete();
				ResultFile.mkdirs();
			}

			executionResult = resultFileLocation.concat("/").concat(resultFileName).concat(".xls");

			logger.info("Main Result file location : " + executionResult);

			XlsLib result = new XlsLib();

			/** 4. Create Empty Excel file */
			result.emptyExcel(executionResult);
		}
		catch(Exception e)
		{
			logger.error("Exception handled during execution of beforeTestSuite. ", e); 
		}
	}

	@AfterSuite
	public void afterSuite()  
	{
		try{

			/** Generate Summary of Results */ 
			XlsLib test = new XlsLib();
			test.generateFinalResult(executionResult);

			GenericMethodsLib.cleanProcesses();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}

	}

}
