package lenskart.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import framework.utilities.FileLib;
import framework.utilities.GenericMethodsLib;

public class UnitTestClass {

	public static void main(String[] args) throws JSONException, FileNotFoundException, IOException, ClassNotFoundException {

		String x = FileLib.ReadContentOfFile("/Users/pankaj.katiyar/Desktop/Automation/Lenskart_Automation/f");

		List<HashMap<String, String>> list=GenericMethodsLib.dataObjectToListOfMap(x);

		System.out.println(list.toString());
	}

}
