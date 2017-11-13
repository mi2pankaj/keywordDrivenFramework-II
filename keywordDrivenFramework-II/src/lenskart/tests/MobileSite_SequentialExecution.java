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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;

public class MobileSite_SequentialExecution {

	static String suiteName = ""; 
	String testCaseFile;
	String testResultFile;
	File resultFile;
	HashMap<String, Boolean> map = new HashMap<>();
	Logger logger = Logger.getLogger(MobileSite_SequentialExecution.class.getName());

	static Connection connectionServe;
	static JSONObject jsonObjectRepo = new JSONObject();
	static List<TestCaseObjects> testCaseObjectList;

	/** store result in two D array to get testNg results for jenkins */
	static Object [][] resultData;


	/** setting up configuration before test */
	@SuppressWarnings("unused")
	@Parameters("SuiteType")
	@BeforeClass
	public void beforeClass(String suiteType) 
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
			if(suiteType.equalsIgnoreCase("msite")){
				testCaseFile = TestSuiteClass.AUTOMATION_HOME.toString().concat("/tc_cases/mobileSite/mobileSite_Test_Cases.xls");

				logger.debug(" : Test Cases File Located at: "+testCaseFile);
				testResultFile = TestSuiteClass.resultFileLocation.concat("/mobileSite_TestResults");
			}else if(suiteType.equalsIgnoreCase("vsm")){
				testCaseFile = TestSuiteClass.AUTOMATION_HOME.toString().concat("/tc_cases/vsmSite/vsm_Test_Cases.xls");

				logger.debug(" : Test Cases File Located at: "+testCaseFile);
				testResultFile = TestSuiteClass.resultFileLocation.concat("/vsm_TestResults");
			}
//			testCaseFile = TestSuiteClass.AUTOMATION_HOME.toString().concat("/tc_cases/vsmSite/vsm_Test_Cases.xls");
//
//			logger.debug(" : Test Cases File Located at: "+testCaseFile);
//			testResultFile = TestSuiteClass.resultFileLocation.concat("/mobileSite_TestResults");

			resultFile = FileLib.CopyExcelFile(testCaseFile, testResultFile);
			logger.debug(" : Test Cases Result File Located at: "+resultFile);

			/** get object repository as json object */
			String objectRepo = TestSuiteClass.AUTOMATION_HOME.concat("/object_repository/mobileSiteObjectRepository/mobileSite_ObjectRepository.xls");
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

			resultData = new Object[testCaseObjectList.size()][2];
		}
		catch (Exception e)
		{
			logger.error(" : Error occurred before starting the portal test", e);
		}
	}


	/** running tests */
	@Test(priority=1)
	public void launchTests()
	{
		ReadTestCases readTestCases = new ReadTestCases();

		for(int i=0; i<testCaseObjectList.size(); i++)
		{
			TestCaseObjects testCaseObject = testCaseObjectList.get(i);

			testCaseObject = readTestCases.executeTestCaseObject(testCaseObject, connectionServe, jsonObjectRepo);
			new WriteTestResults().writeTestObjectResults_UsingJxl(resultFile, testCaseObject);

			/** get the result in a 2d array to use as data provider later on -- to get testng results */
			resultData[i][0] = testCaseObject.getTestCaseId()+" : "+testCaseObject.getTestCaseDescription();
			resultData[i][1] = testCaseObject.getTestCaseResult();
		}
	}


	@Test(priority=2, dataProvider="getResults")
	public void testResults(Object testCaseDescription, Object testCaseResult)
	{
		if(testCaseResult.toString().toLowerCase().contains("skip"))
		{
			throw new SkipException(testCaseResult.toString());
		}
		else if(testCaseResult.toString().toLowerCase().contains("fail"))
		{
			Assert.fail(testCaseResult.toString());
		}
		else if(testCaseResult.toString().toLowerCase().contains("pass"))
		{
			Assert.assertTrue(true, testCaseResult.toString());
		}
	}


	@DataProvider
	public Object [][] getResults()
	{
		return resultData;
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


	//	@Override
	//	public String getTestName() {
	//
	//		StringBuilder builder = new StringBuilder();
	//		builder.append("name=").append(testCaseName);
	//		return builder.toString();
	//
	//	}

}
