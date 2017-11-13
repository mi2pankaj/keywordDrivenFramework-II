/**
 * Last Changes Done on Jan 16, 2015 12:04:40 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */

package lenskart.tests;

import java.awt.Robot;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger; 
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.mysql.jdbc.Connection;

import framework.core.classes.GetObjectRepoAsJson;
import framework.core.classes.ReadTestCases;
import framework.core.classes.TestCaseObjects;
import framework.core.classes.WriteTestResults;
import framework.utilities.CaptureScreenShotLib;
import framework.utilities.FileLib;
import framework.utilities.GenericMethodsLib;
import framework.utilities.KeyBoardActionsUsingRobotLib;
import framework.utilities.XlsLib;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterClass;

public class DesktopSite_SequentialExecution {

	static String suiteName = ""; 
	String testCaseFile;
	String testResultFile;
	File resultFile;
	HashMap<String, Boolean> map = new HashMap<>();
	Logger logger = Logger.getLogger(DesktopSite_SequentialExecution.class.getName());

	static Connection connectionServe;
	static JSONObject jsonObjectRepo = new JSONObject();
	static List<TestCaseObjects> testCaseObjectList;

	/** setting up configuration before test */
	@SuppressWarnings("unused")
	
	@Parameters("flag")
	@BeforeClass
	public void beforeClass(String flag) 
	{
		try
		{
			GenericMethodsLib.InitializeConfiguration();

			/** Initializing constructor of KeyBoardActionsUsingRobotLib and CaptureScreenShotLib here, 
			 * so that focus on chrome browser is not disturbed. 
			 */
			Robot rt = new Robot();
			KeyBoardActionsUsingRobotLib keyBoard = new KeyBoardActionsUsingRobotLib(rt);
			CaptureScreenShotLib captureScreenshot = new CaptureScreenShotLib(rt);

			//connectionServe = GenericMethodsLib.CreateServeSQLConnection();

			testCaseFile = TestSuiteClass.AUTOMATION_HOME.toString().concat("/tc_cases/desktopSite/desktopSite_Test_Cases.xls");

			logger.debug(" : Test Cases File Located at: "+testCaseFile);
			testResultFile = TestSuiteClass.resultFileLocation.concat("/desktopSite/TestResults");

			resultFile = FileLib.CopyExcelFile(testCaseFile, testResultFile);
			logger.debug(" : Test Cases Result File Located at: "+resultFile);

			/** get object repository as json object */
			String objectRepo;
			if(flag.trim().equalsIgnoreCase("php")){
				objectRepo = TestSuiteClass.AUTOMATION_HOME.concat("/object_repository/desktopSiteObjectRepository/desktopSite_ObjectRepository_PHP.xls");
			}
			else{
				objectRepo = TestSuiteClass.AUTOMATION_HOME.concat("/object_repository/desktopSiteObjectRepository/desktopSite_ObjectRepository_Desktop_Revamp.xls");
			}
			
			jsonObjectRepo = new GetObjectRepoAsJson().getObjectRepoAsJson(objectRepo);

			ReadTestCases readTest = new ReadTestCases();
			String testStepResultColumnLabel = readTest.tcStepResultColumn;
			String testStepSheetName = readTest.testStepSheet;

			WriteTestResults writeResult = new WriteTestResults();
			writeResult.addResultColumn(resultFile, testStepSheetName, testStepResultColumnLabel);

			String testSummaryResultColumnLabel = readTest.tcSummaryResultColumn;
			String testSummarySheetName = readTest.testCaseSummarySheet;
			writeResult.addResultColumn(resultFile, testSummarySheetName, testSummaryResultColumnLabel);

			/** load test case objects */
			testCaseObjectList = readTest.getRunnableTestCaseObjects(testCaseFile);
		}
		catch (Exception e)
		{
			logger.error(" : Error occurred before starting the portal test", e);
		}
	}


	/** running tests */
	@Test
	public void runTests()
	{
		ReadTestCases readTestCases = new ReadTestCases();

		/** iterate the test case object list and execute test case and write results */
		for(TestCaseObjects testCaseObject : testCaseObjectList)
		{
			readTestCases.executeTestCaseObject(testCaseObject, connectionServe, jsonObjectRepo);
			new WriteTestResults().writeTestObjectResults_UsingJxl(resultFile, testCaseObject);
		}
	}


	/** finishing tests, writing results and saving in db */
	@AfterClass
	public void afterClass()  
	{
		try {
			//connectionServe.close();

			/** Get Total number of test cases executed */
			File f = new File(resultFile.toString());
			int totalTestCase = (XlsLib.getTotalRowOfExcelWorkbook(f))-1;

			totalTestCase = map.size();
			TestSuiteClass.totalTC.put(new ReadTestCases().gettestCaseSummarySheet(), totalTestCase);

			/** Updating portal execution summary and test steps results to main results sheet */
			String summaryData[][] = new XlsLib().dataFromExcel(resultFile.toString(), new ReadTestCases().gettestCaseSummarySheet());
			new XlsLib().updateResultInNewSheet(TestSuiteClass.executionResult, new ReadTestCases().gettestCaseSummarySheet(), summaryData);

			String stepsResultData[][] = new XlsLib().dataFromExcel(resultFile.toString(), new ReadTestCases().gettestStepSheet());
			new XlsLib().updateResultInNewSheet(TestSuiteClass.executionResult, new ReadTestCases().gettestStepSheet(), stepsResultData);

			logger.info(" : ################### Test Ended. ########################");

		}catch(Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

}
