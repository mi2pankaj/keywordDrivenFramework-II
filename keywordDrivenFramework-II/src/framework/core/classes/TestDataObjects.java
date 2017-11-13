package framework.core.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import lenskart.tests.TestSuiteClass;


public class TestDataObjects {

	Logger logger = Logger.getLogger(TestDataObjects.class.getName());


	@Test
	public static void main() throws IOException 
	{
		TestDataObjects dataSheet = new TestDataObjects();

		Recordset recordset=dataSheet.readTestDataSheet
				(System.getProperty("user.dir").concat("/tc_data/desktopSite/desktopSite_Test_Data.xls"), 
						"Select * from Test_Data");

		List<HashMap<String, String>> testDataList = new TestDataObjects().getTestDatasheet(recordset);

		List<TestCaseObjects> updatedTestCasesList = dataSheet.getUpdatedTestCasesObjectList(testDataList, 
				new ReadTestCases().getRunnableTestCaseObjects(
						System.getProperty("user.dir").concat("/tc_cases/desktopSite/desktopSite_Test_Cases_Data_Driven.xls")
						));

		new WriteTestResults().writeTestObjectResults_UsingPoI(updatedTestCasesList);

	}


	/** read the supplied sheet based on query --> get the record set
	 * 
	 * @param file
	 * @param query
	 * @return
	 */
	public Recordset readTestDataSheet(String file, String query) 
	{
		Recordset record = null;

		try {
			Fillo fillo=new Fillo();
			Connection connection =fillo.getConnection(file);
			record = connection.executeQuery(query);

		}catch (Exception e) {
			logger.error("Exception while loading test data from sheet: "+file, e);
		}
		return record;
	}


	/** Get the list of test data map which is each row of test data sheet --- like first row will be key of map and subsequent row will be values. 
	 * 
	 * @param recordset
	 * @return
	 */
	public  List<HashMap<String, String>> getTestDatasheet(Recordset recordset)
	{
		List<HashMap<String, String>> listOftestDataMaps = new ArrayList<>();

		try
		{
			ArrayList<String> dataColumnNames =recordset.getFieldNames();

			while(recordset.next())
			{				
				HashMap<String, String> rowHashMap = new HashMap<>();

				for(int j=0; j<dataColumnNames.size(); j++)
				{
					String fieldName = dataColumnNames.get(j);
					String value = recordset.getField(fieldName);

					rowHashMap.put(fieldName, value);
				}

				listOftestDataMaps.add(rowHashMap);
			}
		}catch (Exception e) {
			logger.error(" : Exception occurred while getting test data map : ", e);
		}

		return listOftestDataMaps;
	}


	/** Get the test case object which is data driven.
	 * 
	 * @param testCaseObjectList
	 * @param testCaseId
	 * @return
	 */
	public TestCaseObjects getDataDrivenTestCaseObject(List<TestCaseObjects> testCaseObjectList, String testCaseId)  
	{
		TestCaseObjects testCaseObject = new TestCaseObjects();

		try
		{
			for(int i=0; i<testCaseObjectList.size();i++) {

				if(testCaseObjectList.get(i).getTestCaseId().equalsIgnoreCase(testCaseId)) {
					testCaseObject = (TestCaseObjects) testCaseObjectList.get(i).clone();
					break;
				}
			}
		}catch (Exception e) {
			logger.error(" Exception occurred : ", e);
		}
		return testCaseObject;
	}


	/** Update the data in received test step object from - Test Data Map.
	 * 
	 * @param rowHashMap
	 * @param testcaseobj
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws CloneNotSupportedException 
	 */
	public TestCaseObjects updateDataInTestCaseObject(HashMap<String, String> rowHashMap, TestCaseObjects testcaseobj){

		try
		{
			List<TestStepObjects> teststepobjlist= testcaseobj.gettestStepObjectsList();
			List<TestStepObjects> updatedTestStepobjlist = new ArrayList<>();

			for(int i =0; i<teststepobjlist.size(); i++) {

				/** create a clone of object before updating it otherwise this update statement will update the received test step object list in test case object */
				TestStepObjects teststepobject =(TestStepObjects) teststepobjlist.get(i).clone();
				String testData=teststepobject.getData();

				if(rowHashMap.containsKey(testData)) {
					String testValue = (String) rowHashMap.get(testData);
					teststepobject.setData(testValue);
				}

				/** updating the test step objects in updated object list */
				updatedTestStepobjlist.add(teststepobject);
			}

			/** update test case object with test steps */
			testcaseobj.settestStepObjectsList(updatedTestStepobjlist);

			/** set test data id in test case object */
			testcaseobj.setTestDataID(rowHashMap.get("TD_ID"));
			
		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " + e.getMessage(), e);
		}
		return testcaseobj;
	}


	/** get the updated the Test Case Object List --> supply the actual test case object list, this will read the test data sheet and 
	 * create the as number of test case object as number of test data is available -- data driven tests
	 * 
	 * @param testDataMap
	 * @param testCaseObjectList
	 * @return
	 */
	public List<TestCaseObjects> getUpdatedTestCasesObjectList(List<HashMap<String, String>> testDataMap, List<TestCaseObjects> testCaseObjectList){

		List<TestCaseObjects> updatedTestCasesList = new ArrayList<>();

		try{
			/** iterate test data map and create that many data driven test case object and them into final list to be executed later on */
			for(HashMap<String, String> rowHashMap : testDataMap){

				String testCaseId = rowHashMap.get("TC_ID");

				/** check if test case id is data driven or not, clone method is required not the copy because in case of copy same object is being created
				 * whereas we want a new object reference all the time- to achieve this, implemented clonable interface */				
				TestCaseObjects testCaseObject = (TestCaseObjects) getDataDrivenTestCaseObject(testCaseObjectList, testCaseId).clone();

				/** if not null then update test case object */ 
				if(testCaseObject != null && testCaseObject.getTestCaseDataDriven().equalsIgnoreCase("Yes")){

					testCaseObject = updateDataInTestCaseObject(rowHashMap, testCaseObject);

					/** add the final test case object in updatedTestCases list */
					updatedTestCasesList.add(testCaseObject);
				}
			}

			/** now iterate the received testCaseObjectList and find those test case objects which are have data_driven = no 
			 * and add them in updatedTestCases list */
			for(TestCaseObjects testCaseObjects : testCaseObjectList) {

				if(testCaseObjects.getTestCaseDataDriven().equalsIgnoreCase("No")) {
					updatedTestCasesList.add(testCaseObjects);
				}
			}

		}catch (Exception e) {
			logger.error(" Error occurred while getting updated test case object list: ", e);
		}

		return updatedTestCasesList;
	}

}
