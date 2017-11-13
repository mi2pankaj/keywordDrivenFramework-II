/**
 * Last Changes Done on Jan 16, 2015 12:13:22 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */


package framework.core.classes;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger; 
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import com.mysql.jdbc.Connection;

import framework.utilities.GenericMethodsLib;
import jxl.Sheet;
import jxl.Workbook;
import lenskart.tests.TestSuiteClass;
import net.lightbody.bmp.proxy.ProxyServer;


@SuppressWarnings("deprecation")
public class ReadTestCases implements Cloneable
{
	public String tcSummaryRunColumn;
	public String tcSummaryLabelColumn;
	public String tcSummaryTCIdColumn;
	public String tcSummarySupportedBrowserTypeColumn;
	public String tcSummaryDescription;
	public String tcSummaryDataDriven;

	public String tcStepTCIdColumn;
	public String tcStepTCStepIDColumn;
	public String tcStepKeywordColumn;
	public String tcStepObjectColumn;
	public String tcStepDataColumn;
	public String tcStepDescriptionColumn;

	String keyword;
	String objectName;
	String data;
	public String tcSummaryResultColumn;
	public String tcStepResultColumn;
	String separator;
	public String testCaseSummarySheet;
	public String testStepSheet;
	String haltedTestStepResult;
	String haltFlag;

	static Logger logger = Logger.getLogger(ReadTestCases.class.getName());


	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public ReadTestCases()
	{	
		this.tcSummaryRunColumn = "Run" ;
		this.tcSummaryTCIdColumn = "TC_ID" ;
		this.tcSummaryResultColumn = "Test_Results";
		this.tcSummarySupportedBrowserTypeColumn = "Supported_Browser_Type";

		this.tcSummaryLabelColumn = "Label";
		this.tcSummaryDataDriven = "Data_Driven";
		this.tcSummaryDescription = "Description";

		this.tcStepTCIdColumn = "TC_ID";
		this.tcStepResultColumn = "Test_Results";
		this.tcStepTCStepIDColumn = "Step_ID";
		this.tcStepKeywordColumn = "Keyword";
		this.tcStepObjectColumn = "objectName";
		this.tcStepDescriptionColumn = "Description";

		/** Choosing input data column based on current test environment */
		String currentTestEnv;
		try{
			currentTestEnv = GenericMethodsLib.propertyConfigFile.getProperty("currentTestEnvironment").toString().trim();
		}catch (NullPointerException e) {
			currentTestEnv = "qa";
		}

		if(currentTestEnv.equalsIgnoreCase("qa"))
		{
			this.tcStepDataColumn = "inputData_QA";
		}
		else
		{
			this.tcStepDataColumn = "inputData_Production";
		}

		this.separator = "####";

		this.testCaseSummarySheet = "executionControl";
		this.testStepSheet = "testCaseSteps";	

		this.haltedTestStepResult = "Not Executed.";
		this.haltFlag = "must pass";
	}


	/** getter to return testStepSheet name
	 * @return
	 */
	public String gettestStepSheet()
	{
		return testStepSheet;
	}


	/** getter to return testCaseSummarySheet name
	 * @return
	 */
	public String gettestCaseSummarySheet()
	{
		return testCaseSummarySheet;
	}


	/** what this does - this will first read the execution control sheet and then find those test cases which are set to yes and get the test step details 
	 * from teststep sheet and put those details in objects and finally return the map -- like -- <TestCaseID, <TestStepDetailsObject>>
	 * 
	 * @param fileNameWithLocation
	 * @return
	 */
	public List<TestCaseObjects> getRunnableTestCaseObjects(String fileNameWithLocation)		
	{	
		logger.info("Test Case Summary File is : "+fileNameWithLocation);
		List<String> tc_id = new ArrayList<String>();
		Sheet testCaseSummarySheetObj = null;
		Sheet testStepSheetObj = null;
		Workbook book = null;
		boolean flag = true;

		/** create a list of LoadTestCaseObjects Objects - which contains the runnable testcase id and corresponding testStep details in form of objects */
		List<TestCaseObjects> getRunnableTestCaseObjectsList = new ArrayList<>();

		try{
			book = Workbook.getWorkbook(new File(fileNameWithLocation));
			testCaseSummarySheetObj = book.getSheet(testCaseSummarySheet);
			testStepSheetObj = book.getSheet(testStepSheet);
		}catch(Exception e){
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exiting -- Please check the file location, Error occurred while loading file: "+fileNameWithLocation, e);
		}

		try
		{
			/** if no exception, then proceed */
			if(flag){

				logger.info("****** loading objects ***** ");

				/** get the column numbers from test step sheet - to retrieve the details later on */
				int tc_id_column = testStepSheetObj.findCell(tcStepTCIdColumn, 0, 0,testStepSheetObj.getColumns(), 0 , false).getColumn();
				int tc_step_id_column = testStepSheetObj.findCell(tcStepTCStepIDColumn, 0, 0, testStepSheetObj.getColumns(),0, false).getColumn();
				int keyword_column = testStepSheetObj.findCell(tcStepKeywordColumn, 0, 0,testStepSheetObj.getColumns(), 0 , false).getColumn();
				int object_column = testStepSheetObj.findCell(tcStepObjectColumn, 0, 0, testStepSheetObj.getColumns(),0, false).getColumn();
				int data_column = testStepSheetObj.findCell(tcStepDataColumn, 0, 0, testStepSheetObj.getColumns(),0, false).getColumn();
				int description_column = testStepSheetObj.findCell(tcStepDescriptionColumn, 0, 0, testStepSheetObj.getColumns(),0, false).getColumn();

				/** Finding "Run" and "TC_ID" column in the Test Case Summary -- which is executionControl sheet */
				int run_column = testCaseSummarySheetObj.findCell(tcSummaryRunColumn, 0, 0, testCaseSummarySheetObj.getColumns(), 0, false).getColumn();
				int id_column = testCaseSummarySheetObj.findCell(tcSummaryTCIdColumn, 0, 0, testCaseSummarySheetObj.getColumns(), 0, false).getColumn();

				/** get supported browser type - like chrome, mobile etc. */
				int supportedBrowserType_column = testCaseSummarySheetObj.findCell(tcSummarySupportedBrowserTypeColumn, 0, 0, testCaseSummarySheetObj.getColumns(),0, false).getColumn();
				int testcase_datadriven_column = testCaseSummarySheetObj.findCell(tcSummaryDataDriven, 0, 0, testCaseSummarySheetObj.getColumns(),0, false).getColumn();

				/** get test case description */
				int testCaseDescription_column = testCaseSummarySheetObj.findCell(tcSummaryDescription, 0, 0, testCaseSummarySheetObj.getColumns(),0, false).getColumn();

				/** find those test steps from test cases steps sheet, for those test cases which are set to RUN=Yes in executionControl sheet */
				for(int row=1;row<testCaseSummarySheetObj.getRows();row++)
				{
					String runMode = testCaseSummarySheetObj.getCell(run_column, row).getContents().trim();
					if (runMode.equalsIgnoreCase("yes")) 
					{
						/** create an object of LoadTestCaseObjects and set all the details of testStep Objects in this object */
						TestCaseObjects testCaseObject = new TestCaseObjects();

						/** create a List of TestCaseObjects and all the step details in that object */
						List<TestStepObjects> testStepObjectsList = new ArrayList<>();

						/** get runnable test case id */
						String testSummary_TCID = testCaseSummarySheetObj.getCell(id_column, row).getContents().trim();
						tc_id.add(testSummary_TCID);

						/** get runnable test case - supported browser type and description */
						String testCaseSupportedBrowserType = testCaseSummarySheetObj.getCell(supportedBrowserType_column, row).getContents().trim();
						String testCaseDescription = testCaseSummarySheetObj.getCell(testCaseDescription_column, row).getContents().trim();						
						String testCaseDataDriven = testCaseSummarySheetObj.getCell(testcase_datadriven_column, row).getContents().trim();

						/** iterate the test step sheet -- to find those test steps for which there is a runnable test case id */
						for(int row_testStep =1; row_testStep<testStepSheetObj.getRows(); row_testStep++)
						{
							String testStep_TCID = testStepSheetObj.getCell(tc_id_column , row_testStep).getContents().trim();
							if(testSummary_TCID.equalsIgnoreCase(testStep_TCID))
							{
								/** get the test steps details corresponding to above test case id */
								String testStepID = testStepSheetObj.getCell(tc_step_id_column, row_testStep).getContents().trim();
								String keyword = testStepSheetObj.getCell(keyword_column, row_testStep).getContents().trim();
								String objectName = testStepSheetObj.getCell(object_column, row_testStep).getContents().trim();
								String data = testStepSheetObj.getCell(data_column, row_testStep).getContents().trim();
								String testStepDescription = testStepSheetObj.getCell(description_column, row_testStep).getContents().trim();

								/** create object -- and set all the details for this object */
								TestStepObjects tcStepObject = new TestStepObjects();
								tcStepObject.setData(data);
								tcStepObject.setKeyword(keyword);
								tcStepObject.setObjectName(objectName);
								tcStepObject.setTestCaseId(testStep_TCID);
								tcStepObject.setTestStepId(testStepID);
								tcStepObject.setTestStepIdRowNumber(row_testStep);
								tcStepObject.setTestStepDescription(testStepDescription);

								/** add object in list */
								testStepObjectsList.add(tcStepObject);
								//logger.info("loaded object: "+testStep_TCID+"-"+testStepID+"-"+keyword+"-"+objectName+"-"+data);
							}
						}

						/** loading all the test case objects in object of new LoadTestCaseObjects() */
						testCaseObject.settestStepObjectsList(testStepObjectsList);
						testCaseObject.setIfTestCaseQueued(new AtomicBoolean(false));
						testCaseObject.setTestCaseExecutionProgressStatus(new AtomicInteger(0));

						testCaseObject.setTestCaseId(testSummary_TCID);
						testCaseObject.setTestCaseIdRowNumber(row);
						testCaseObject.setTestCaseSupportedBrowserType(testCaseSupportedBrowserType);
						testCaseObject.setTestCaseDescription(testCaseDescription);
						testCaseObject.setTestCaseDataDriven(testCaseDataDriven);

						/** add test case object in a list finally */
						getRunnableTestCaseObjectsList.add(testCaseObject);
					}
				}
				book.close();
			}
		}
		catch(Exception e)
		{
			logger.error(" Exception occurred while reading Test Case Summary File :" +fileNameWithLocation, e);
		}

		return getRunnableTestCaseObjectsList; 				
	}


	/** This method is the innovative one, this gonna execute the received test case object and get it loaded with test results 
	 * and later on, can do anything with this single method.  
	 *  
	 * @param testCaseObject
	 * @param connection
	 * @param jsonObjectRepo
	 * @return
	 */
	public TestCaseObjects executeTestCaseObject(TestCaseObjects testCaseObject, Connection connection, JSONObject jsonObjectRepo)
	{
		String result;

		boolean resultFlag;
		try{

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"executeTestCaseObject Method -- iterating test case object ... ");

			/** This hashmap contains the test step id and corresponding test step data, will not reset for each test case id.
			 * this will be used when user wants to give same (dynamic) data in multiple test steps for one test case.
			 */
			HashMap<String, String> testStepID_InputData = new HashMap<String, String>();

			/** this test step object list will contain the updated test test stp objects with result, and finally this will be
			 * put into test case object */
			List<TestStepObjects> finalTestStepsObjectList = new ArrayList<>();

			/** Launching a driver here -- for every test case, it will be launched in every test case id
			 * --> Making this change to make it work --> End to End Test - Threads Framework */
			ProxyServer proxyServer=GenericMethodsLib.startProxyServer(testCaseObject.getTestCaseSupportedBrowserType());

			WebDriver driver=GenericMethodsLib.invokeBrowser(testCaseObject.getTestCaseSupportedBrowserType(), proxyServer);

			/** This resultFlag is set to false if any of test step of a test case is failed. 
			 * This flag sets the result either PASS or FAIL for the test case id in executionControl sheet.
			 */
			resultFlag = true;

			/** This haltExecution is set to false by default, in case any result is fail and corresponding input data has 
			 * must pass flag, then subsequent steps, for that test case id, will not be executed and default result will set = "Not Executed"  
			 */
			boolean haltExecution = false;

			/** Setting up new feature --> on_error_resume_next, if this flag is found no in the first step of Test Case, then in case of any test step 
			 * failure, subsequent steps won't be executed, similar to must pass flag, but difference is --> must pass flag can be used only with 
			 * verify keywords like verifyText, verifyTitle etc. not with other keywords like typeValue or clickButton etc. 
			 */
			boolean on_error_resume_next = false;

			/** get the list of test step objects - which need to be executed, iterating this...  */
			List<TestStepObjects> testSteps = testCaseObject.gettestStepObjectsList();

			for(int row=0; row<testSteps.size(); row++){

				result = "";

				/** get test step object and getting all data from this object */
				TestStepObjects testStepObject = testSteps.get(row);

				String testStepID = testStepObject.getTestStepId();
				String keyword = testStepObject.getKeyword();
				String objectName = testStepObject.getObjectName();
				String data = testStepObject.getData();

				/**
				 * get date for the days given in the input sheet
				 */
				if(data.contains("#currentdate#")){
					data=GenericMethodsLib.getDateInString(data);

				}

				/** if found in the first step as no --> that means upon first failure, 
				 * subsequent steps won't be executed. -- set on_error_resume_next flag to true */
				if(row == 0 && keyword.equalsIgnoreCase("on_error_resume_next") && data.equalsIgnoreCase("no")){
					on_error_resume_next = true;
				}

				/** If the supplied data has #time# then replace #time# with the time stamp. */
				/** First of all store each step step id and corresponding data in a hashmap for each test step id. */
				/** 1. If user supplies the input data like #TC_01_03# in test step id TC_01_06 then this means the input data for 
				 * step 06 is the same as data given in step 03, in this case hashmap stores value like (TC_01_06,#TC_01_03#),
				 * Now first of all get the input data from testStepID_InputData hashmap for id(key) = TC_01_03 after removing # from it.
				 * If hashmap has this value then update hashmap as (TC_01_06,Value) and pass this value for further processing, else data = ""
				 */
				data = new HandlerLib().dataParser(data, keyword, testStepID_InputData, connection);
				objectName = new HandlerLib().dataParser(objectName, keyword, testStepID_InputData, connection);
				testStepID_InputData.put(testStepID.toLowerCase().trim(), data);

				/** Check if execution needs to be halted, if yes then set Result = "Not Executed."
				 * for keyword = closebrowser, getproxylog execution will not be halted. 
				 */
				if(!haltExecution || keyword.equalsIgnoreCase("closebrowser") || keyword.equalsIgnoreCase("getproxylog")){

					/** Performing action based on received keyword, object and input data */
					PerformAction action = new PerformAction();
					result = action.performAction(driver, keyword, objectName, data, connection, jsonObjectRepo, proxyServer);

					/** Removing string "; must pass" from results */
					if(result.contains(haltFlag)){
						result = new HandlerLib().resultParser(result, haltFlag);
					}


					/** adding a custom step - like if there is a requirement to store the data at step 1 and use that data at step 2
					 * then use keyword like storeData_keyword so that after execution of this keyword, result will be stored
					 * in a map testStepID_InputData corresponding to test step id */
					if(keyword.toLowerCase().trim().startsWith("storedata_") ||
							keyword.toLowerCase().trim().startsWith("get_") || keyword.toLowerCase().trim().startsWith("retrievedataobject")) {
						testStepID_InputData.put(testStepID.toLowerCase().trim(), result);	
					}

					/** Check if execution needs to be halted based on received above test step result. */
					haltExecution = new HandlerLib().haltTestExecution(data, result);
				}
				else{
					result = haltedTestStepResult;
					logger.debug("halting steps now, test step: "+testStepID);
				}

				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"Keyword: "+keyword + ", Object Name: "+objectName + ", Input Data: "+data + ", Test Step: "+testStepID+ ", Result: "+result);

				/** Setting up haltExecution flag to true if on_error_resume_next is found to be true and any fail result. 
				 * If on_error_resume_next is found in first row as no and if there is any Fail result found then set haltExecution = true.
				 */
				if(on_error_resume_next && (result.toLowerCase().startsWith("fail: ") || result.toLowerCase().contains("fail: "))){
					haltExecution = true;
				}

				/** Setting result of each test case id using flag resultFlag, test case id result will be Fail 
				 * if any of the test step is failed or if any test step result = Not Executed
				 */
				if(resultFlag){
					if(result.toLowerCase().trim().matches("^fail.*") || result.equalsIgnoreCase(haltedTestStepResult)
							|| result.toLowerCase().trim().startsWith("fail:") || result.toLowerCase().contains("fail:")){
						resultFlag = false;
						logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"Found Test Step: "+testStepID +" = Failed:");
					}
				}

				/** Putting results of each test step in respective test step object */
				testStepObject.setTestStepResult(result);

				/** add updated test step object into a final object list */
				finalTestStepsObjectList.add(testStepObject);
			}

			/** finally add the updated test steps object list in the received test case object and return this */
			testCaseObject.settestStepObjectsList(finalTestStepsObjectList);

			/** set the test case result based on resultFlag -- if its true then Pass else Fail */
			if(resultFlag){
				testCaseObject.setTestCaseResult("PASS");
			}
			else{
				testCaseObject.setTestCaseResult("FAIL");
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"Exception occurred: ", e);
		}
		return testCaseObject;
	}

}
