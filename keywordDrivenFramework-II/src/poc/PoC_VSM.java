package poc;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import framework.core.classes.ReadTestCases;
import framework.core.classes.TestCaseObjects;
import framework.core.classes.TestDataObjects;
import framework.utilities.FileLib;
import framework.utilities.GenericMethodsLib;


public class PoC_VSM {

	@Test
	public static void main() 
	{
		TestDataObjects dataSheet = new TestDataObjects();

		String x=FileLib.ReadContentOfFile(System.getProperty("user.dir").concat("/vsm.json"));

		List<HashMap<String, String>> testDataList = GenericMethodsLib.listOfMapFromJson(x);

		List<TestCaseObjects> list = new ReadTestCases().getRunnableTestCaseObjects(System.getProperty("user.dir").concat("/tc_cases/vsmSite/vsm_Test_Cases.xls"));
		
		List<TestCaseObjects> ts = dataSheet.getUpdatedTestCasesObjectList(testDataList, list);
		
		System.out.println(ts.toString());
	}

}