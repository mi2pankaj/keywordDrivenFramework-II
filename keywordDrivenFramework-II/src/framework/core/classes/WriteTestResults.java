/**
 * Last Changes Done on 5 Mar, 2015 12:07:46 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: 
 */
package framework.core.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lenskart.tests.TestSuiteClass;


public class WriteTestResults {

	/**
	 * @param args
	 */

	Logger logger = Logger.getLogger(WriteTestResults.class.getName());

	public void addResultColumn(File testResultFile, String sheetName, String resultLabel)
	{
		try{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Adding label: "+resultLabel +" column in file: "+testResultFile + " in sheet: "+sheetName);

			Workbook book = Workbook.getWorkbook(testResultFile);
			WritableWorkbook copiedBook = Workbook.createWorkbook(testResultFile, book);
			WritableSheet sheet = copiedBook.getSheet(sheetName);

			Label lblColumnName = new Label(sheet.getColumns(), 0, resultLabel);
			sheet.addCell(lblColumnName);

			copiedBook.write();
			copiedBook.close();
			book.close();
		}catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while adding Test Result column in file: "+testResultFile, e);
		}
	}

	/** This is a synchronized method to be used by Threads and Non-Threads to write test case results in excel sheet.
	 * 
	 * @param testResultFile
	 * @param testCaseObject
	 * @return
	 */
	public synchronized boolean writeTestCaseObjectResult(WritableWorkbook copiedBook, TestCaseObjects testCaseObject)
	{
		boolean flag;
		try{

			ReadTestCases readTest = new ReadTestCases();
			String summarySheet = readTest.testCaseSummarySheet;
			WritableSheet sheet = copiedBook.getSheet(summarySheet);

			int tcIDcolumn = sheet.findCell(readTest.tcSummaryTCIdColumn, 0, 0, sheet.getColumns(),0, false).getColumn();
			int tcResultscolumn = sheet.findCell(readTest.tcSummaryResultColumn, 0, 0, sheet.getColumns(),0, false).getColumn();

			/** get test case details from received test case object */
			String receivedTestCaseId = testCaseObject.getTestCaseId();
			int testCaseIdRowNumber = testCaseObject.getTestCaseIdRowNumber();
			String testCaseResult = testCaseObject.getTestCaseResult();

			/** get the test case id from the sheet, from the Test Case Id Column and match again -- just to double sure
			 * before putting the results */
			String testCaseIDFromSheet = sheet.getCell(tcIDcolumn, testCaseIdRowNumber).getContents().toString();

			if(testCaseIDFromSheet.equalsIgnoreCase(receivedTestCaseId)){
				/** adding result at the row number of received test case id --- therefore avoiding the iteration of whole sheet */
				Label lblColumnName = new Label(tcResultscolumn, testCaseIdRowNumber, testCaseResult);
				sheet.addCell(lblColumnName);

				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writitng Result: "+testCaseResult + " For Test Case Id: "+receivedTestCaseId);
			}else{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Strange Case Found Here: "+testCaseResult + " For Test Case Id: "+receivedTestCaseId);
			}

			flag = true; 
		}catch(Exception e)
		{
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while writting resutls in test summary file: ", e);
		}

		return flag;
	}

	/** This method is also special and innovative, this will start writing test results of received test step object in synchronized way 
	 * 
	 * @param fileName
	 * @param testCaseObject
	 * @return
	 */
	public synchronized boolean writeTestStepObjectResult(WritableWorkbook copiedBook, TestCaseObjects testCaseObject)
	{
		boolean flag = false;
		try
		{
			ReadTestCases readTestCase = new ReadTestCases();
			String teststepsSheet = readTestCase.testStepSheet;

			/** get the test step objects from the received test case object */
			List<TestStepObjects> testStepObjectsList = testCaseObject.gettestStepObjectsList();

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writing test step result in sheet: "+teststepsSheet);

			/** Get the existing workbook - sheet */	
			WritableSheet sheet = copiedBook.getSheet(teststepsSheet);

			/** get test step id and test result column number */
			int testStepIDcolumn = sheet.findCell(readTestCase.tcStepTCStepIDColumn, 0, 0, sheet.getColumns(),0, false).getColumn();
			int testStepResultscolumn = sheet.findCell(readTestCase.tcStepResultColumn, 0, 0, sheet.getColumns(),0, false).getColumn();

			/** iterate the test step objects list */
			for(TestStepObjects testStepObject : testStepObjectsList)
			{
				int row = testStepObject.getTestStepIdRowNumber();
				String receivedTestStepId = testStepObject.getTestStepId();
				String testStepIdFromSheet = sheet.getCell(testStepIDcolumn, row).getContents().trim();

				/** write test step results only if the received test step is matching the test step from the sheet, at the row */
				if(receivedTestStepId.equalsIgnoreCase(testStepIdFromSheet))
				{
					String result = testStepObject.getTestStepResult();
					Label lblTestStepResult = new Label(testStepResultscolumn, row, result);
					sheet.addCell(lblTestStepResult);
				}				
			}

			flag = true;
		}
		catch(Exception e)
		{
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while writing test steps results. ", e);
		}
		return flag;

	}

	/** This one is the only method to write the test results in test case.
	 * 
	 * @param testResultFile
	 * @param testCaseObject
	 * @return
	 */
	public synchronized boolean writeTestObjectResults_UsingJxl(File testResultFile, TestCaseObjects testCaseObject)
	{
		boolean flag = false;

		try
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writting test results in test case file "+testResultFile);

			Workbook book = Workbook.getWorkbook(testResultFile);
			WritableWorkbook copiedBook = Workbook.createWorkbook(testResultFile, book);

			/** write test case result */
			boolean a = writeTestCaseObjectResult(copiedBook, testCaseObject);

			/** write test step result */
			boolean b = writeTestStepObjectResult(copiedBook, testCaseObject);

			/** get the final flag */
			flag = a && b;

			copiedBook.write();
			copiedBook.close();
			book.close();
		}catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : "+"Error occurred while writting resutls", e);
		}

		return flag;
	}

	/** write execution control sheet contents
	 * 
	 * @param executionControlSheet
	 * @param updatedTestCasesList
	 */
	public void writeExecutionControlSheetContent(XSSFSheet executionControlSheet, List<TestCaseObjects> updatedTestCasesList)
	{
		int i=0;
		for (TestCaseObjects testcaseObject : updatedTestCasesList)
		{
			XSSFRow row = executionControlSheet.createRow(i+1);

			row.createCell(0).setCellValue(testcaseObject.getTestCaseId());
			row.createCell(1).setCellValue(testcaseObject.getTestDataID());
			row.createCell(2).setCellValue(testcaseObject.getTestCaseDescription());

			row.createCell(3).setCellValue(testcaseObject.getTestCaseSupportedBrowserType());
			row.createCell(4).setCellValue(testcaseObject.getTestCaseDataDriven());
			row.createCell(5).setCellValue(testcaseObject.getTestCaseResult());

			i++;
		}
	}

	/** Write execution control header labels
	 * 
	 * @param executionControlSheet
	 */
	public void addHeader_ExecutionControlSheet(XSSFSheet executionControlSheet)
	{
		//create header
		XSSFRow rowhead = executionControlSheet.createRow((short)0);

		rowhead.createCell(0).setCellValue("TC_ID");
		rowhead.createCell(1).setCellValue("TD_ID");
		rowhead.createCell(2).setCellValue("Description");

		rowhead.createCell(3).setCellValue("Supported_Browser_Type");
		rowhead.createCell(4).setCellValue("Data_Driven");
		rowhead.createCell(5).setCellValue("Result");
	}

	/** write test case object results 
	 * 
	 * @param updatedTestCasesList
	 * @return
	 */
	public synchronized boolean writeTestObjectResults_UsingPoI (List<TestCaseObjects> updatedTestCasesList)  
	{
		boolean flag;
		try
		{
			logger.info("Writing Test Results ******* ");

			FileOutputStream out = new FileOutputStream(new File(System.getProperty("user.dir").concat("/result.xlsx")));

			XSSFWorkbook workbook = new XSSFWorkbook();		

			/** add execution control sheet */
			XSSFSheet executionControlSheet = workbook.createSheet("executionControl");

			/** add test case step sheet */
			XSSFSheet testCaseStepsSheet = workbook.createSheet("testCaseSteps");

			/** Write execution control header labels */
			addHeader_ExecutionControlSheet(executionControlSheet);

			/** Write test step sheet header labels */
			addHeader_TestCaseStepsSheet(testCaseStepsSheet);

			/** write execution control data */
			writeExecutionControlSheetContent(executionControlSheet, updatedTestCasesList);

			/** write test step data */
			writeTestCaseStepsSheetContent(testCaseStepsSheet, updatedTestCasesList);

			workbook.write(out);
			workbook.close();

			flag = true;

		}catch (Exception e) {
			flag = false;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + " Exception while writing results ." , e);
		}

		return flag;
	}



	/** Write testCaseSteps header labels
	 * 
	 * @param executionControlSheet
	 */
	public void addHeader_TestCaseStepsSheet(XSSFSheet testCaseStepsSheet)
	{
		//create header
		XSSFRow rowhead = testCaseStepsSheet.createRow((short)0);

		rowhead.createCell(0).setCellValue("TC_ID");
		rowhead.createCell(1).setCellValue("TD_ID");

		rowhead.createCell(2).setCellValue("Step_ID");
		rowhead.createCell(3).setCellValue("Description");

		rowhead.createCell(4).setCellValue("Keyword");
		rowhead.createCell(5).setCellValue("objectName");

		rowhead.createCell(6).setCellValue("inputData");
		rowhead.createCell(7).setCellValue("Test_Results");
	}


	/** write test case step sheet contents
	 * 
	 * @param executionControlSheet
	 * @param updatedTestCasesList
	 */
	public void writeTestCaseStepsSheetContent(XSSFSheet testCaseStepsSheet, List<TestCaseObjects> updatedTestCasesList)
	{
		int i=1;

		for (TestCaseObjects testcaseObject : updatedTestCasesList)
		{
			/** get test step object list from each test case object */
			List<TestStepObjects> testStepObjectList = testcaseObject.gettestStepObjectsList();

			XSSFRow row;
			
			/** iterate each test step and write results */
			for(TestStepObjects testStepObject : testStepObjectList)
			{
				row = testCaseStepsSheet.createRow(i);
				
				/** write test step objects */
				getTestCaseSteps_RowContent(row, testStepObject.getTestCaseId(), testcaseObject.getTestDataID(), 
						testStepObject.getTestStepId(), testStepObject.getTestStepDescription(), 
						testStepObject.getKeyword(), testStepObject.getObjectName(), testStepObject.getData(), 
						testStepObject.getTestStepResult());
				i++;
			}
			
			/** add a blank row after each test case in test step sheet */
			row = testCaseStepsSheet.createRow(i);
			getTestCaseSteps_RowContent(row, "", "", "", "", "", "", "","");
			i++;
		}
	}


	/** write test case step sheet contents
	 * 
	 * @param executionControlSheet
	 * @param updatedTestCasesList
	 */
	public void getTestCaseSteps_RowContent(XSSFRow row, String getTestCaseId, String getTestDataID, 
			String getTestStepId, String getTestStepDescription, String getKeyword, 
			String getObjectName, String getData, String getTestStepResult)
	{
		row.createCell(0).setCellValue(getTestCaseId);
		row.createCell(1).setCellValue(getTestDataID);

		row.createCell(2).setCellValue(getTestStepId);
		row.createCell(3).setCellValue(getTestStepDescription);

		row.createCell(4).setCellValue(getKeyword);
		row.createCell(5).setCellValue(getObjectName);

		row.createCell(6).setCellValue(getData);
		row.createCell(7).setCellValue(getTestStepResult);				
	}

}
