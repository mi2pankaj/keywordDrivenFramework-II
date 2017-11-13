/**
 * Summary: This class is written for keyword definitions
 * Last Changes Done on Feb 2, 2015 3:56:34 PM
 * Purpose of change: Added keywords to be used while executing test cases.
 */


package framework.core.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mysql.jdbc.Connection;

import framework.utilities.CaptureScreenShotLib;
import framework.utilities.CustomException;
import framework.utilities.CustomExceptionStopExecution;
import framework.utilities.DBLib;
import framework.utilities.FileLib;
import framework.utilities.GenericMethodsLib;
import framework.utilities.IntegerLib;
import framework.utilities.JsonParserHelper;
import framework.utilities.KeyBoardActionsUsingRobotLib;
import framework.utilities.httpClientWrap;
import lenskart.tests.OrderDetails;
import lenskart.tests.TestSuiteClass;
import net.lightbody.bmp.proxy.ProxyServer;


// TODO: Auto-generated Javadoc
/**
 * @author Pankaj Katiyar
 *
 */

@SuppressWarnings("deprecation")
public class Keywords {

	Logger logger = Logger.getLogger(Keywords.class.getName());

	String passed_status;
	String failed_status;
	String skip_status;

	/**
	 * Defining Variables
	 */
	String warning_status;
	WebDriver driver;
	WebElement webelement;
	String noObjectSuppliedMessage;
	String noDataSuppliedMessage;

	GetObjects getObject = new GetObjects();
	HandlerLib handler = new HandlerLib();
	String locationToSaveSceenShot;

	JSONObject jsonObjectRepo;
	static Connection connection;
	

	ProxyServer proxyServer;
	
	
	/**
	 * Constructor initialization.
	 */
	public Keywords(Connection connection, JSONObject jsonObjectRepo, ProxyServer proxyServer)
	{
		this.passed_status = "Pass: ";
		this.failed_status = "Fail: ";
		this.skip_status = "Skip: ";
		this.warning_status = "Warning: ";

		this.noObjectSuppliedMessage = failed_status + "Please supply the desired object from object repository.";
		this.noDataSuppliedMessage = failed_status + "Please supply the desired test data.";
		this.locationToSaveSceenShot = TestSuiteClass.AUTOMATION_HOME.concat("/screenshots/").concat("ErrorKeywords").concat("/");

		this.jsonObjectRepo = jsonObjectRepo;
		Keywords.connection = connection;
		this.proxyServer = proxyServer;
	}


	/**
	 * This keyword launches browser.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String launchbrowser(WebDriver driver, String objectName, String data)
	{
		String result;

		try{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received browser name is: "+data);

			if(data.isEmpty())
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Empty browser is received. ");
				result = failed_status + "Browser type: "+data +" can't be empty, please supply the supported browser: chrome or firefox.";
			}
			else if(data.equalsIgnoreCase("chrome") || data.equalsIgnoreCase("firefox"))
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Setting up browser: "+data);

				driver = GenericMethodsLib.WebDriverSetUp(data, null);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Launched browser name is: "+data + " browsername "+ driver );			
				//Bring browser in focus, normally chrome opens in background.
				handler.getBrowserInFocus(driver);

				result = passed_status+ "Browser launched successfully";
			}
			else
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Supplied browser: "+data + " is not supported. ");
				result = failed_status + "Supplied browser type: "+data +" is not supported, supported ones are chrome and firefox.";
			}
		}catch(Exception e)
		{
			result = failed_status + "Couldn't launch browser";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while launching browser: "+data, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}



	/** This keyword closes the browsers opened by automation code.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String closebrowser  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Closing browser. ");

			driver.quit();

			//			for(String window : driver.getWindowHandles())
			//			{
			//				driver.switchTo().window(window).close();
			//			}

			result = passed_status+ "Browser closed successfully.";
		}catch(Exception e)
		{
			result = failed_status + "Couldn't close browser. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while closing browser. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword navigates to a URL.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String navigateurl(WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			/** putting a random delay to avoid simultaneous load on server */
			int delay = IntegerLib.GetRandomNumber(2000, 1000);
			Thread.sleep(delay);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : random delay of: "+delay);

			if(driver != null)
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received url is: "+data+ " and browser is: " +driver);
				try{driver.get(data);}catch(Exception e)
				{
					Thread.sleep(IntegerLib.GetRandomNumber(1000, 500));
					driver.navigate().to(data);
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred, reloading again : "+data);
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Navigated url is: "+data);
				result = passed_status+ "Navigated url successfully.";
			}
			else
			{
				result = failed_status + "Couldn't navigate to url. ";
			}
		}catch(Exception e)
		{
			result = failed_status + "Couldn't navigate to url";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while navigating url : "+data+ "at browser : " +driver, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/**
	 * This keyword navigates back on step
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String navigateback(WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			/** putting a random delay to avoid simultaneous load on server */
			int delay = IntegerLib.GetRandomNumber(5000, 1000);
			Thread.sleep(delay);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : random delay of: "+delay);

			if(driver != null)
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received url is: "+data+ " and browser is: " +driver);
				try{driver.navigate().back();;}catch(Exception e)
				{
					Thread.sleep(IntegerLib.GetRandomNumber(1500, 500));
					driver.navigate().back();
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred, reloading again : "+data);
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Navigated url is: "+data);
				result = passed_status+ "Navigated back successfully.";
			}
			else
			{
				result = failed_status + "Couldn't navigate to url. ";
			}
		}catch(Exception e)
		{
			result = failed_status + "Couldn't navigate back";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while navigating back url : "+data+ "at browser : " +driver, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword will move the driver to new browser window.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String movetonewbrowserwindow  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Moving to new browser window" );

			String currentState = driver.getWindowHandle().toString();

			/** This code will explicitly wait for max 5 sec to appear multiple window 
			 * for driver to switch on */
			int i=0;
			while(driver.getWindowHandles().size()<2){
				Thread.sleep(3000);
				i++;

				if(i==5){
					break;
				}
			}

			for(String handles : driver.getWindowHandles())
			{
				if(!handles.equalsIgnoreCase(currentState))
				{
					driver.switchTo().window(handles);
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Switched window has title: "+driver.getTitle());
				}else if(handles.equalsIgnoreCase(currentState)){
					driver.switchTo().window(currentState);
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Switched window has title: "+driver.getTitle());
				}
			}
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Moved to new window: "+driver.getCurrentUrl());
			result = passed_status+ "Moved to new window successfully";
		}catch(Exception e)
		{
			result = failed_status + "Couldn't moved to new window";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while moving to new window ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword is used to upload the creative.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String uploadimage(WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received file name with location is: " +data);

			/**
			 * Getting relative image file
			 */
			data = handler.getUploadImageLocation(data);
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Uploading image file: "+data);

			Thread.sleep(500);

			WebElement upload = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
			upload.click();

			Thread.sleep(1500);

			KeyBoardActionsUsingRobotLib.ChooseFileToUpload(data, driver);

			/**
			 * Wait until image is uploaded successfully. Currently hardcoding this 
			 * later on need to get from OR
			 */
			By byLocator = By.xpath("//div[@class='ui-progressbar ui-widget ui-widget-content ui-corner-all'][@aria-valuenow='100']");
			handler.applyExplicitWait(driver, byLocator, new NoSuchElementException(""));

			result = passed_status+ "Creative uploaded successfully";
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't upload creative";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while uploading file : "+data, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword will select the date.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String selectdate  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received date is: " +data);

			//finding the calendar icon
			webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);

			//Selecting the dates from calendar
			handler.selectDateFromCalendar(driver, webelement, data, jsonObjectRepo);

			result = passed_status+ "Selected date successfully";
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't selected date";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while selecting date : "+data, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword will select the date range in Placements / Sub-placement screen,
	 * This keyword accepts input data in comma separated or semicolon separated format,
	 * If desired date range is select, then input can be like select, Date1, Date2 or Date1, Date2
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String selectdaterange  (WebDriver driver, String objectName, String data)
	{
		String result;

		try{
			data = data.trim().toLowerCase();
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received date range is: " +data);

			/** finding the calendar icon */
			webelement = getObject.getWebElementFromRepository("Placements_DateRange",  driver, jsonObjectRepo);

			/** 
			 * Putting sleep for 1 sec to sync the application.
			Thread.sleep(1000);
			webelement.click();
			Thread.sleep(1000);
			 */

			String javaScript = "arguments[0].click()";
			handler.executeJavaScript(driver, javaScript, webelement);

			if(data.matches("^today.*"))
			{
				webelement = getObject.getWebElementFromRepository("Placement_DateRange_Today",  driver, jsonObjectRepo);
				webelement.click();
			}
			else if(data.matches("^yesterday.*"))
			{
				webelement = getObject.getWebElementFromRepository("Placement_DateRange_Yesterday",  driver, jsonObjectRepo);
				webelement.click();
			}
			else if(data.matches("^last 7 days.*"))
			{
				webelement = getObject.getWebElementFromRepository("Placement_DateRange_Last7days",  driver, jsonObjectRepo);
				webelement.click();
			}
			else if(data.matches("^last 30 days.*"))
			{
				webelement = getObject.getWebElementFromRepository("Placement_DateRange_Last30days",  driver, jsonObjectRepo);
				webelement.click();
			}
			else if(data.matches("^this month.*"))
			{
				webelement = getObject.getWebElementFromRepository("Placement_DateRange_ThisMonth",  driver, jsonObjectRepo);
				webelement.click();
			}
			else if(data.matches("^last month.*"))
			{
				webelement = getObject.getWebElementFromRepository("Placement_DateRange_LastMonth",  driver, jsonObjectRepo);
				webelement.click();
			}
			else if(data.matches("^select.*") || data.contains(",") || data.contains(";"))
			{
				webelement = getObject.getWebElementFromRepository("Placement_DateRange_Select",  driver, jsonObjectRepo);
				webelement.click();

				//Pick calendar date, here data has to be splitted 

				List<String> inputdata = new ArrayList<String>();

				if(data.contains(";"))
				{
					inputdata = Arrays.asList(data.split(";"));
				}
				else if(data.contains(","))
				{
					inputdata = Arrays.asList(data.split(","));
				}

				/** Keeping a check if some one forgets to enter the data like select, <left date>, < right date>
				 * and enters data like <left date>, < right date> then consider this case.
				 */

				//String option = "";
				String leftDate = "";
				String rightDate = "";

				if(inputdata.size() == 2)
				{
					leftDate = inputdata.get(0).trim();
					rightDate = inputdata.get(1).trim();
				}
				else if(inputdata.size() == 3)
				{
					leftDate = inputdata.get(1).trim();
					rightDate = inputdata.get(2).trim();
				}

				//Select dates from left and right calendar
				handler.selectDateRangeFromCalendar(driver, leftDate, rightDate, jsonObjectRepo);

				//Click Submit button finally
				webelement = getObject.getWebElementFromRepository("Placement_DateRange_Submit_Button",  driver, jsonObjectRepo);
				webelement.click();
			}
			else
			{
				throw new CustomException("Invalid date supplied. ");
			}

			result = passed_status+ "Selected date range successfully";
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't selected date range";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while selecting date range : "+data, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword is for click on button.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String clickbutton  (WebDriver driver, String objectName, String data)
	{
		String result = "";
		By byLocator = null;
		try{
			if(!objectName.isEmpty())
			{
				try{
					/** create dynamic element */
					byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
					webelement = driver.findElement(byLocator);
				}catch(CustomException e)
				{
					/** create element normally */
					webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);

				}
				if(!webelement.isEnabled())
				{
					By bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);

					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Element is not clickable");
					handler.applyExplicitWait(driver, bylocator, new WebDriverException());
				}

				try{webelement.click();}catch(StaleElementReferenceException w){System.out.println("Nothing to worry - ");}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Clicking element :" + webelement );
				result = passed_status+ "Button clicked successfully";

			}
			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Object received to click.");
				result = failed_status+ "No object was provided to click. ";
			}

		}

		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't click on button";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while clicking button element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/** Entering the power prescription for contact lenses.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String enterprescriptioncontactlenses(WebDriver driver, String objectName, String data){
		String result=null;
		String[] objectArray=null;
		String[] dataArray=null;
		try{
			dataArray=data.split("\n");
			objectArray=objectName.split("\n");
			for(int i=0; i<dataArray.length; i++){
				selectdropdownlisting(driver, objectArray[i], dataArray[i]);
			}
		}catch(Exception e){
			result = failed_status + "Couldn't able to click enter prescription";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while performing action: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword is used to click link.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String clicklink(WebDriver driver, String objectName, String data)
	{
		String result;

		try{
			boolean staleExceptionHandleFlag = true;
			int staleExceptionAttempt=0;

			/** Adding a check on staleExceptionHandleFlag exception, in case this occurs then find the element again until the max attempt = 5.
			 */
			while(staleExceptionHandleFlag)
			{
				try
				{					
					/** First check if this a dynamic element, if not then catch customexception and find element conventionally --> 
					 * Now putting condition on data, if data is empty then get element from object repository using objectName 
					 * else find element using objectLabel --> to be used in keyword clickmenu.
					 */
					try{
						webelement = handler.createDynamicWebElement(driver, objectName, data, jsonObjectRepo);
					}catch(CustomException c)
					{
						webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
					}

					/** Wait until link is visible and clickable, if its not enabled.*/
					By bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);

					if(!webelement.isEnabled() && webelement != null)
					{
						handler.applyExplicitWait(driver, bylocator, new WebDriverException());
					}

					/** if there is any exception thrown while clicking link, then reattempt after catching that exception */
					try{
						webelement.click();
					}catch(WebDriverException w){
						handler.applyExplicitWait(driver, bylocator, new WebDriverException());
						webelement.click();
					}

					staleExceptionHandleFlag = false;

				}catch(StaleElementReferenceException e){
					staleExceptionAttempt ++;
				}

				if(staleExceptionAttempt ==5){
					break;
				}
			}

			Thread.sleep(2500);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Clicking element: " + webelement );
			result = passed_status+ "Clicked link successfully";

		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't click link";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while clicking link: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword is used to click link.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String evaluate_expression (WebDriver driver, String objectName, String data)
	{
		String result = "";
		String actionCondition=null;
		String keyword=null;
		String value=null;
		boolean status=false;

		try{

			if(!objectName.isEmpty())
			{
				/** parse the received data sample - when ifelementpresent then getText with data */
				actionCondition = data.substring(data.indexOf("when")+4, data.indexOf("then")).trim();
				keyword=data.substring(data.indexOf("then")+4, data.indexOf("with")).trim();
				value=data.substring(data.indexOf("with")+4, data.length()).trim();

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Condition from data is ="+actionCondition );
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : keywordAction from data is ="+keyword );
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : value from data is ="+value);

				Keywords keywordsObj=new Keywords(Keywords.connection, jsonObjectRepo, proxyServer);

				if(!actionCondition.equalsIgnoreCase("null")){

					Method conditionMethod=keywordsObj.getClass().getMethod(actionCondition, WebDriver.class, String.class, String.class);

					/** check condition */
					Object obj=conditionMethod.invoke(keywordsObj, driver, objectName, value);

					if(!obj.toString().contains("Fail")){
						status=true;
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : condition ="+actionCondition+" is not verified");
					}

					/** perform action */
					if(status){
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Performing action "+keyword+" on the data");
						Method method = keywordsObj.getClass().getMethod(keyword, WebDriver.class, String.class, String.class);

						Object resultObj = method.invoke(keywordsObj, driver, objectName, value);
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : action "+keyword+" invoked successfully");

						result = (String) resultObj;

					}else{
						result = skip_status + " Supplied Condition Failed. ";
						logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : action "+keyword+" is not invoked");
					}
				}else{
					result = skip_status + " No condition was supplied with when keyword. ";
					logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : action "+keyword+" is not invoked as not condition is specified");
				}
			}
			else
			{
				result = skip_status+ " No Object was supplied for action. ";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to perform action");
			}
		}
		catch(Exception e)
		{
			result = skip_status + "Exception occurred while evaluating expression.";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Element is not available" +e.getMessage());

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}

	/**
	 * This keyword is to select radio button.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String selectradiobutton  (WebDriver driver, String objectName, String data)
	{
		String result;
		By byLocator = null;

		try{
			/** create dynamic element */
			byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
			webelement = driver.findElement(byLocator);
		}catch(CustomException e)
		{
			/** create element normally */
			try {
				webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
			} catch (CustomException e1) { logger.error(e.getMessage()); }
		}

		try{

			/** apply explicit wait */
			WebDriverWait wait = new WebDriverWait(driver, 45);
			wait.until(ExpectedConditions.elementToBeClickable(webelement));

			if(!webelement.isSelected())
			{
				webelement.click();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Selected radio button option is: " +webelement);
				result = passed_status+ "Selected radio button successfully";
			}
			else
			{
				result = passed_status+ "Desired radio button was already selected. ";
			}
		}
		catch(Exception e)
		{
			//Get by locator and apply external wait
			try {
				byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
			} catch (CustomException e2) {
				try {
					byLocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
				} catch (CustomException e3) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			try{


				handler.applyExplicitWait(driver, byLocator, e);
				webelement.click();

				result = passed_status+ "Selected radio button successfully";
			}
			catch(Exception ex)
			{
				result = failed_status + "Couldn't select radio button";
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while selecting radio button option : " +webelement, e);

				/** Taking screenshot during exception */
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
			}
		}

		return result;
	}


	/** This keyword clicks any check box if its not already checked. this keyword supports multiple check box selection.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String selectcheckbox  (WebDriver driver, String objectName, String data)
	{
		String result = "";

		/** if object is supplied by comma separated then select every check box */
		if(objectName.contains(","))
		{
			List<String> objectList = new ArrayList<>(Arrays.asList(objectName.split(",")));
			for(int i=0; i<objectList.size(); i++)
			{
				result = result + "\n" + handler.selectCheckbox(driver, objectList.get(i).trim(), webelement, data, 
						locationToSaveSceenShot, passed_status, failed_status, jsonObjectRepo);
			}
		}
		else if(data.contains(","))
		{
			List<String> dataList = new ArrayList<>(Arrays.asList(data.split(",")));
			for(int i=0; i<dataList.size(); i++)
			{
				result = result + "\n" + handler.selectCheckbox(driver, objectName, webelement, dataList.get(i).trim(), 
						locationToSaveSceenShot, passed_status, failed_status, jsonObjectRepo);
			}
		}
		else
		{
			result = handler.selectCheckbox(driver, objectName, webelement, data, locationToSaveSceenShot, passed_status, failed_status, jsonObjectRepo);
		}

		return result;
	}


	/** This keyword unselect any selected check box.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String unselectcheckbox  (WebDriver driver, String objectName, String data)
	{
		String result;
		By byLocator = null;
		try{
			/** create dynamic element */
			byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
			webelement = driver.findElement(byLocator);
		}catch(CustomException e)
		{
			/** create element normally */
			try {
				webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
			} catch (CustomException e1) 
			{ logger.error(e.getMessage()); 
			result = failed_status + e.getMessage();
			}
		}
		try{


			if(webelement.isSelected()){
				webelement.click();
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Checkbox is cleared. ");
			result = passed_status+ "Checkbox cleared successfully";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't clear checkbox. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while clearing checkbox : " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword verifies if supplied check box is selected.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String ischeckboxselected  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
			if(webelement.isSelected())
			{
				return passed_status + "Checkbox is selected. "; 
			}
			else
			{
				return failed_status + "Checkbox is not selected. ";
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't check the supplied checkbox selection. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while checking selection of checkbox : " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

		}
		return result;
	}


	/**
	 * This keyword is for select an option from radio button.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String selectdropdownvalue  (WebDriver driver, String objectName, String data)
	{
		String result = "";
		/** if object is supplied by comma separated then select every check box */
		try{
			if(objectName.contains(","))
			{
				List<String> objectList = new ArrayList<>(Arrays.asList(objectName.split(",")));
				for(int i=0; i<objectList.size(); i++)
				{
					result = result + "\n" + selectdropdownvalue(driver, objectList.get(i).trim(), data);
				}
			}
			else if(data.contains(","))
			{
				List<String> dataList = new ArrayList<>(Arrays.asList(data.split(",")));
				for(int i=0; i<dataList.size(); i++)
				{
					result = result + "\n" + selectdropdownvalue(driver, objectName, dataList.get(i).trim());
				}
			}
			else{
				webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
				Select select = new Select(webelement);
				select.selectByVisibleText(data.trim());

				result = passed_status+ "Selected dropdown value successfully";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Selected drop down value: " +data);
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Exception occurred while selecting drop down option. "+data;
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while selecting drop down option : " +data, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword will be used to select the desired value(s) out of pre populated list by searched records based on 
	 *  user input, like Select Placement in Assign Placement to Client screen / select channel in Generate Tag screen etc.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String chooseinlist  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{
			if(!objectName.isEmpty() && !data.isEmpty())
			{
				/** In case of stale element exception, find element again to do operation.
				 *  repeatAction parameter will keep the code in loop and attemptCount parameter will limit the 
				 *  number of attempt to 5 to avoid infinite loop.
				 */
				boolean repeatAction = true;
				int attemptCount = 0;

				while(repeatAction)
				{
					try{
						Thread.sleep(1000);
						webelement = handler.createDynamicWebElement(driver, objectName, data, jsonObjectRepo);
						webelement.click();
						Thread.sleep(1000);

						repeatAction = false;
					}catch(StaleElementReferenceException e){
						repeatAction = true;
						attemptCount++;
					}

					if(attemptCount ==5){
						break;
					}
				}

				result = passed_status+ "Selected value: "+data+" successfully";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Selected value: "+data+" successfully");
			}
			else
			{
				result = noDataSuppliedMessage + "   " + noObjectSuppliedMessage;
			}
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't select "+data +" from list. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while selecting value: "+data +" from list. ");

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword is for type the value in text field/area.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String typevalue  (WebDriver driver, String objectName, String data)
	{
		String result;
		try{

			if(!objectName.isEmpty())
			{
				webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);

				//Clearing element before typing, if exception occurs while clearing then ignore this and proceed.
				try{
					if(objectName.equalsIgnoreCase("EditApplication_MarketingName_TextBox"))
					{
						Thread.sleep(1000);

						/** Clearing the text of text box */
						webelement.sendKeys(Keys.CONTROL + "a");
						webelement.sendKeys(Keys.DELETE);
					}
					else
					{
						Thread.sleep(2000);
						webelement.clear();
					}

					/** executing js to clear text if not cleared by above code */
					handler.executeJavaScript(driver, " arguments[0].value=\"\" ", webelement);

				}catch(Exception e){
					/** executing js to clear text if not cleared by above code */
					handler.executeJavaScript(driver, " arguments[0].value=\"\" ", webelement);

					logger.error(e.getMessage());
				}

				//This code will type the text in search box -- slowly key by key
				if(objectName.equalsIgnoreCase("Create_Sub_placement_SearchApplication_textBox") || 
						objectName.equalsIgnoreCase("GenerateTag_SearchChannel_TextBox")
						|| objectName.equalsIgnoreCase("AddNewAppOrSite_AddResultGoogle_WebResult_DropDown")
						|| objectName.equalsIgnoreCase("CreateANewDeal_Package_Chip"))
				{
					handler.typeSlowly(webelement, data);
					Thread.sleep(1000);
				}
				else
				{
					Thread.sleep(250);
					webelement.sendKeys(data);
					Thread.sleep(250);
				}
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Typing the value : " + data + " in the element: " + webelement );
				result = passed_status+ "Value typed successfully";
			}
			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No object was provided to type value. " );
				result = failed_status+ "No object was provided to type value. ";
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't type value";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while Typing the value : " + data + " in the element: " + webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** This keyword is to upload video, automation code will directly type the received file location in choose file input field.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public  String choosefile  (WebDriver driver, String objectName, String data)
	{		
		String result;
		try{
			webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);

			/**
			 * Get video location relative to Automation_Home,
			 * in case of banner sub placement, choosefile keyword is used but image is uploaded 
			 */
			String uploadFile = data;
			uploadFile = handler.getUploadVideoLocation(data);

			/** If video file is empty then check if image file needs to be uploaded. */
			if(uploadFile.isEmpty())
			{
				uploadFile = handler.getUploadImageLocation(data);
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Uploading media file: "+uploadFile);

			//webelement.sendKeys(uploadFile);
			webelement.click();
			Thread.sleep(1500);
			KeyBoardActionsUsingRobotLib.ChooseFileToUpload(uploadFile, driver);
			Thread.sleep(1000);

			//Get progress bar webelement 
			webelement = getObject.getWebElementFromRepository("Create_Sub_placement_Video_Upload_ProgressBar", driver, jsonObjectRepo);

			//wait until progress is 100, max time for wait = 600*1000 mili sec.
			boolean status = false;
			for(int i=0; i<600; i++)
			{
				//get the progress
				Thread.sleep(2000);
				String progress = webelement.getAttribute("aria-valuenow").toString().trim();

				if(progress.equalsIgnoreCase("100") || data.contains("jpg"))
				{
					status = true;
					break;
				}
				else
				{
					/** handling banner upload case where progress attribute becomes 0.1 uploading image */
					if(i==5 && progress.equalsIgnoreCase("0.1")) 
					{
						break;
					}
					continue;
				}
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Uploading media file: " + data + " in the element: " + webelement );

			if(status){
				result = passed_status+ "Media was uploaded successfully. ";
			}else{
				result = failed_status+ "Media wasn't uploaded. ";
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't upload media.";			
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while uploading video located at: " + data + " in the element: " + webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword is for verify the text of any supplied object.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 * @throws CustomExceptionStopExecution 
	 */	
	public String verifytext  (WebDriver driver, String objectName, String data) throws CustomExceptionStopExecution
	{
		String result = "";

		try{

			if(objectName.isEmpty())
			{
				result = failed_status + "Supplied object name is empty. ";
			}

			else
			{
				//Getting data after removing any flag like "must flag"
				if(data.contains(";")){
					data = data.split(";")[0].trim();
				}
				else if(data.contains(",")){
					data = data.split(",")[0].trim();
				}

				String actualValue = "";

				/** Handling stale element reference exception, in this exception retry to find the element, max attempt is 5
				 */
				boolean staleElementReferenceException = true;
				int staleElementReferenceExceptionCount = 0;
				By bylocator = null;

				while(staleElementReferenceException)
				{
					try
					{
						/** First the check if the supplied element is a dynamic object which needs data to create element definition, 
						 * if no, then createDynamicWebElement method will throw CustomExceptionsLib exception, and then find the element using 
						 * method: getWebElementFromRepository
						 */
						try{
							bylocator = getObject.getByLocatorFromRepository(objectName, data, driver, jsonObjectRepo);
						}catch(CustomException c){
							bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
						}

						/** apply explicit wait of 45 sec before finding the element */
						handler.applyExplicitWait(driver, bylocator, new NoSuchElementException(""), 45);
						webelement = driver.findElement(bylocator);

						for (int i=0; i<5; i++)
						{
							actualValue = driver.findElement(bylocator).getText().trim(); 
							if(actualValue.contains("#")){
								actualValue=actualValue.split("#")[1];
							}
							logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual text of element: " + actualValue);

							if(actualValue.equalsIgnoreCase(data))
							{
								break;
							}
							else
							{
								Thread.sleep(1000);
							}
						}

						staleElementReferenceException = false;
					}
					catch(StaleElementReferenceException s)
					{
						staleElementReferenceException = true;
						staleElementReferenceExceptionCount++;
					}

					if(staleElementReferenceExceptionCount == 5)
					{
						break;
					}
				}

				if(actualValue.equalsIgnoreCase(data))
				{
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Matched. ");
					result = passed_status + "Text is as expected.";
				}
				else 
				{
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Doesn't Match. ");
					result = failed_status + "The actual value is: " + actualValue + ", the expected value is: " + data;
				}
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch (TimeoutException e) 
		{
			result = failed_status + "Could not retrieve the text."; 
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Timed out while waiting for text to be present: "+data);
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " : " + failed_status + "Exception occurred while verifying the text: " + data + " of the element: " +webelement, e);
			result = failed_status + "Could not retrieve the text."; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Current url: "+driver.getCurrentUrl());
		return result;
	}


	/**
	 * This keyword will verify the details from mysql database, user has to
	 * supply the db query in objectName and expected comma separated result(s)
	 * in data column, usage example: [objectName = select ABC from campaign
	 * where id = "XYZ"][input = xyz].
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */	
	public String verifydbdetails  (WebDriver driver, String objectName, String data) 
	{
		String result = "";
		try{
			/** 
			 * Do not proceed if there is no query supplied.
			 */
			if(objectName.isEmpty())
			{
				result = failed_status + "No query was supplied. ";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ ": " + result);
			}
			else
			{
				/** parse received data */
				data = handler.dataParser(data, connection);

				/** Parsing the supplied sql query. */
				String sqlQuery = objectName.replace("\"", "'");
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Executing supplied query: "+sqlQuery);

				String [] records = GenericMethodsLib.ExecuteMySQLQueryReturns1DArray(connection, sqlQuery);

				/** proceed to test only if received records is not null */
				if(records != null)
				{
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received Number Of Records: "+records.length);

					boolean dataListFlag = false;
					List<String> dataList = new ArrayList<>();

					/**
					 * This is for a special case If there is only one supplied data but containing ',' into it.
					 */
					if(data.contains("Vpaid") || data.contains("Mraid"))
					{
						/**
						 *  Replace the provided data as per the saved data in database
						 */
						data.replace("Vpaid", "2");
						data.replace("Mraid1", "3");
						data.replace("Mraid2", "5");

						if(records[0].trim().equalsIgnoreCase(data.trim()))
						{
							result = passed_status + "Actual value is same as expected. ";
						}
						else
						{
							result = failed_status + "Expected value= "+data + " whereas actual value saved in db = "+records[0];
						}
					}

					/**
					 * Converting the comma / semi colon separated supplied data into a list 
					 * only when --> data contains must pass flag separated by , or ;
					 */

					else if(data.contains(";"))
					{
						if(data.toLowerCase().contains("must pass"))
						{
							dataListFlag = true;	

						}
						else
						{
							dataListFlag = false;
						}

						/** Recasting the splitted string as list to avoid unsupported operation exception. */
						dataList = new ArrayList<>(Arrays.asList(data.split(";")));
					}
					else if(data.contains(","))
					{
						if(data.toLowerCase().contains("must pass"))
						{
							dataListFlag = true;
						}
						else
						{
							dataListFlag = false;
						}
						/** Recasting the splitted string as list to avoid unsupported operation exception. */
						dataList = new ArrayList<>(Arrays.asList(data.split(",")));
					}
					else
					{
						/** If there is only one supplied data::: 
						 */
						if(records[0].trim().equalsIgnoreCase(data.trim()))
						{
							result = passed_status + "Actual value is same as expected. ";
						}
						else
						{
							result = failed_status + "Expected value= "+data + " whereas actual value saved in db = "+records[0];
						}
					}

					/** If the supplied data is a list then iterating it: */
					if(dataListFlag)
					{
						/** Remove any must pass flag from the supplied user data list, checking the only last item
						 * coz must pass can be used only at the last place. 
						 */
						if(dataList.get(dataList.size()-1).trim().equalsIgnoreCase("must pass"))
						{
							dataList.remove(dataList.size()-1);
						}
					}	
					if(!dataList.isEmpty())
					{
						/** This failFlag will be used to verify if there is any case of data mismatch */
						boolean failFlag = true;

						/** Iterating the supplied data list. */
						for(int i=0; i<dataList.size(); i++)
						{
							String suppliedExpectedValue = dataList.get(i).trim();
							String actualDBValue = records[i];

							/** Compare each supplied data with the retrieved value from database */
							if(!suppliedExpectedValue.equalsIgnoreCase(actualDBValue))
							{
								result = result + "Expected value= "+dataList.get(i) +" whereas actual value saved in db= "+records[i] + "  ";

								/** If there is even a single mismatch failFlag = false, later on to be determined if there was any mismatch. */
								if(failFlag)
								{
									failFlag = false;
								}
							}

							/** Check if the whole list is iterated yet, if yes then check the failFlag, if failFlag is true 
							 * then there was no mismatch else there was a mismatch in data.  
							 */
							if(i==dataList.size()-1)
							{
								if(failFlag)
								{
									result = passed_status + "All values are saved as expected in database.";
								}
								else
								{
									result = failed_status + result;
								}
							}
						}

					}
				}
				else
				{
					result = failed_status + "Received null in database. ";
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Records Received ... ");
				}
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " : " + failed_status + "Exception occurred while verifying the database details." , e);
			result = failed_status + "Could not get database details. "; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/**
	 * This keyword will execute the supplied insert / update query in mysql database, user has to
	 * supply the db query in objectName , usage example: [objectName = update abc where a = c].
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */	
	public String executedbquery  (WebDriver driver, String objectName, String data) 
	{
		String result = "";
		try{
			/** 
			 * Do not proceed if there is no query supplied.
			 */
			if(objectName.isEmpty())
			{
				result = failed_status + "No query was supplied. ";
			}
			else
			{
				/** Parsing the supplied sql query. */
				String sqlQuery = objectName.replace("\"", "'");
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Executing supplied query: "+sqlQuery);

				boolean flag = new DBLib().executeUpdateInsertQuery(connection, sqlQuery);
				if(flag)
				{
					result = passed_status + "Query was executed.";
				}
				else {
					result = failed_status + "Query was not executed.";
				}
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ " : " + failed_status + "Exception occurred while verifying the database details." , e);
			result = failed_status + "Could not get database details. "; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/**
	 * This keyword is little special, it will be used to verify the text of
	 * desired parameter corresponding to the given searched value. For example,
	 * in Marketplace Connections screen, user searches a connection by giving
	 * Connection Identifier = "SearchOnly_DontDelete" and verifies Connection
	 * Name = "searchonly_dontdelete_both_video_rtb22_all" corresponding to
	 * given Connection Identifier, then this keyword verifytextofsearchedrecord
	 * will be used with the below values in input data along with the object Name of Object to be verified
	 * (in this case objectName of Connection Name)
	 * INPUT DATA: SearchOnly_DontDelete, searchonly_dontdelete_both_video_rtb22_all
	 * 
	 * First Parameter is the Value Used To Perform Search, Second is the
	 * expected value of desired parameter which will be matched with the actual
	 * value. This is required to maintain the relation.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifytextofsearchedrecord  (WebDriver driver, String objectName, String data)
	{
		String result = "";
		try
		{
			if(objectName.isEmpty() || data.isEmpty())
			{
				result = failed_status + "Both object name and desired test in data should be supplied to use this keyword. ";
			}
			else
			{
				String searchParam = "";
				String expectedValue = "";

				/** Getting searched parameter and expected value from the supplied data. 
				 * First parameter will always be the data to be used in finding element and second one will be 
				 * the expected data.
				 */

				if(data.contains(";"))
				{
					searchParam = data.split(";")[0].trim();
					expectedValue = data.split(";")[1].trim();
				}
				else if(data.contains(","))
				{
					searchParam = data.split(",")[0].trim();
					expectedValue = data.split(",")[1].trim();
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Search parameter is: " + searchParam);

				/** Create the dynamic element using searchParam, putting sleep to handle sync
				 */
				Thread.sleep(2000);
				webelement = handler.createDynamicWebElement(driver, objectName, searchParam, jsonObjectRepo);
				if(webelement != null)
				{
					/** Wait until the expected text is present in the web element.  
					 */
					try{
						WebDriverWait wait = new WebDriverWait(driver, 2);
						wait.until(ExpectedConditions.textToBePresentInElement(webelement, expectedValue));		
					}catch(Exception e){
					}
					String actualValue = webelement.getText().trim();
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual text of element : " + actualValue);

					/** Matching expected and actual values */
					if(actualValue.equals(expectedValue))
					{
						logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Matched. ");
						result = passed_status + "Text is as expected.";
					}
					else 
					{
						logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Doesn't Match. ");
						result = failed_status + "The actual value is: " + actualValue + ", the expected value is: " + expectedValue;
					}
				}
				else
				{
					result = "FAIL: Couldn't find the supplied webelement therefore text couldn't be verified.";
				}
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the text: " + data + " of the element: " +webelement, e);
			result = failed_status + "Could not retrieve the text."; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword will be used to verify all the error elements/texts present on any screen.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String get_errormessages  (WebDriver driver, String objectName, String data)
	{

		/**
		 * //span[class='text-error'] and //label[class='text-error']
		 */
		try{
			//First check if there is any alert found.
			String message = "";
			message = message + get_alerttext(driver,objectName, data);

			//Secondly get any error message present on screen.
			List<WebElement> errorElementList = new ArrayList<WebElement>();

			//Get all the error text defined by tag label
			By labelerrorMessage = getObject.getByLocatorFromRepository("ScreenErrorMessages_DefinedByLabel_Text", driver, jsonObjectRepo);

			errorElementList = driver.findElements(labelerrorMessage);			

			//Get all the error text defined by tag span if no error found by tag label
			if(errorElementList == null)
			{
				By spanerrorMessage = getObject.getByLocatorFromRepository("ScreenErrorMessages_DefinedBySpan_Text", driver, jsonObjectRepo);
				errorElementList = driver.findElements(spanerrorMessage);
			}

			if(errorElementList != null)
			{
				for(WebElement element : errorElementList)
				{
					message = message + element.getText() +"\n";
				}
				return message;
			}
			else
			{
				return "No error message found. ";
			}
		}
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while verifying the error messages.", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not retrieve the error message."; 


		}
	}


	/**
	 * This keyword is for verify the title of the browser.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String verifybrowsertitle  (WebDriver driver, String objectName, String data)
	{
		try{
			//Getting data after removing any flag like "must pass flag"
			if(data.contains(";")){
				data = data.split(";")[0].trim();
			}
			else if(data.contains(",")){
				data = data.split(",")[0].trim();
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Verifying the title of browser : " + driver );
			String actualValue = driver.getTitle().trim();
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Title of browser is: "+actualValue);

			if(actualValue.equalsIgnoreCase(data))
			{
				return passed_status + " Title is as expected.";
			}
			else 
			{
				return failed_status + " the actual title is : " + actualValue + " but the expected title is : " + data;
			}
		}catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the title: " + data + " in the element: " +driver, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + ": could not retrive the browser title."; 
		}
	}




	/**
	 * This keyword is used to get the text of any element.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String storedata_gettext(WebDriver driver, String objectName, String data)
	{
		try 
		{
			/** create dynamic element first */
			webelement = handler.createDynamicWebElement(driver, objectName, data, jsonObjectRepo);
		} 
		catch (CustomException e1) 
		{
			/** if not dynamic element then create it normally */
			try {
				webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
			} catch (CustomException e) {
				logger.error(e.getMessage());
			}
		}

		try{

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Getting the text of element : " + webelement );
			String actualValue = webelement.getText().trim();
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Text of element is: " +actualValue);

			return actualValue;
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the text from the element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " : could not retrive the text";
		}
	}

	/** This keyword will get the text of alert, if any.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String get_alerttext  (WebDriver driver, String objectName, String data)
	{
		try{
			String actualText = driver.switchTo().alert().getText().toString();

			//Accepting alert
			acceptalert(driver,objectName, data);
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual text present in alert: " + actualText );
			return actualText;
		}
		catch(NoAlertPresentException e)
		{
			logger.warn("No Alert found.");
			return "No alert found.";
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the alert text. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not retrieve the alert text."; 
		}
	}


	/**
	 * This keyword is for get the title of browser.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String get_browsertitle  (WebDriver driver, String objectName, String data)
	{
		try{			
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Title of browser : " + driver );
			String actualValue = driver.getTitle().trim();
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Title of browser is: "+actualValue);
			return actualValue;
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred  "+e+" while getting the browser title from the element: " +driver, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + " : could not retrive the page title"; 
		}
	}


	/** this keyword is generic, will be used to move the driver focus to a window and handle the frame internally, 
	 * just pass the desired window name: 
	 * These screens are covered by this keyword
	 * Move to create/edit placement screen
	 * Move to Add Credit screen
	 * Move to Set Date screen
	 * Move to Create a New Marketplace Connection / Edit Connection screen
	 * Move to Assign Network screen
	 * Move to Clone Campaign screen
	 * Move to Edit Revenue screen
	 * Move to Alternate Ads / Upload Creative screen
	 * Move to Create New Account screen / Edit Account screen
	 * Move to Assign placements to Clients screen
	 * Move to Generate Reporting API screen
	 * Move to default content / move out of current iframe / frame
	 * Move to Generate Tag screen
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String movetowindow  (WebDriver driver, String objectName, String data)
	{
		String result;

		try{
			data = data.toLowerCase();

			if((data.contains("create placement") || data.contains("edit placement") || data.matches("^placement.*"))
					|| (data.contains("add credit")) 
					|| (data.contains("set date")) 
					|| (data.contains("create a new marketplace connection") || data.contains("edit connection") || data.matches("^edit connection.*"))
					|| (data.contains("assign network") || data.matches("^assign network.*"))
					|| (data.contains("clone campaign") || data.matches("^clone campaign.*"))
					|| (data.contains("edit revenue") || data.matches("^edit revenue.*"))
					|| (data.contains("create new account") || data.matches("^create new account.*") || data.contains("edit account") || data.matches("^edit account.*"))
					|| (data.contains("assign placements to clients".toLowerCase()) || data.matches("^assign placements.*"))
					|| (data.contains("Generate Reporting API".toLowerCase()) || data.matches("^generate reporting api.*"))
					|| (data.contains("generate tag") || data.matches("^generate tag.*"))
					|| (data.contains("media (whitelist)") || data.matches("^whitelist.*"))
					|| (data.contains("media (blacklist)") || data.matches("^blacklist.*"))
					|| (data.contains("add site list")) || (data.contains("tag generator")||data.contains("add app list"))
					)
			{
				//By by = getObject.getByLocatorFromRepository("create_placements_iframe", driver);
				handler.moveToFrame(driver, "modalIfrm");

				result = passed_status + "Moved to " +data + " window successfully. ";
			}
			else if(data.contains("alternate ads") || data.contains("upload creative"))
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Moved to "+data + " screen. ");
				result = passed_status + "Moved to " +data + " window successfully. ";
			}
			else if(data.equalsIgnoreCase("new browser window"))
			{
				result = movetonewbrowserwindow(driver, objectName, data);
			}
			else if(data.contains("default content") || data.matches("^default content.*") 
					|| data.contains("default screen") || data.matches("^default screen.*")
					|| data.contains("no frame") || data.matches("^no frame.*")
					|| data.contains("no iframe") || data.matches("^no iframe.*")
					|| data.startsWith("default") || data.isEmpty()
					|| data.contains("move out of window"))
			{
				driver.switchTo().defaultContent();
				result = passed_status + "Moved out of frame and switched to default content. ";
			}
			else
			{
				result = warning_status + "This screen wasn't found, please check again. ";
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Moved to iframe corresponding to supplied window: "+data);
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't move to "+ data +" window. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while moving to "+data + " window. " ,e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword will verify the existence of supplied web element(s), multiple elements 
	 * can be supplied separated by comma(,) or semicolon(;). 
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifyelementpresent  (WebDriver driver, String objectName, String data)
	{
		String result = "";

		try
		{
			/** checking if supplied data is q query, if yes then execute it and get the data, create webelement using it
			 * and call recursively verifyelementpresent */
			if(data.toLowerCase().trim().startsWith("select") && data.toLowerCase().trim().contains("from"))
			{
				//Connection con = MobileTestClass_Methods.CreateSQLConnection();
				String [] arrayData = GenericMethodsLib.ExecuteMySQLQueryReturns1DArray(connection, data);
				//con.close();

				/** iterating the received data */
				for(int i=0; i<arrayData.length; i++)
				{
					/** recursive call to keywords.verifyelementpresent method */
					String dataToBeUsed = arrayData[i].trim();
					result = result + "\n" + verifyelementpresent(driver, objectName, dataToBeUsed);
				}
			}
			else
			{
				result = handler.verifyElementPresent(objectName, data, driver, webelement, getObject, 
						handler, passed_status, failed_status, locationToSaveSceenShot, jsonObjectRepo);
			}
		}catch(Exception e)
		{
			if(e instanceof CustomException)
			{
				result = e.getMessage();
			}
			else
			{
				result = failed_status + "Couldn't check the presence of element. ";
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while checking the presence of element. ", e);

				/** Taking screenshot during exception */
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
			}
		}

		return result;
	}


	/** This keyword will verify the existence of supplied web element, if element not present then result is pass.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifyelementnotpresent  (WebDriver driver, String objectName, String data)
	{
		String result = "";
		List<String> notPresentObjectList = new ArrayList<String>();

		try{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Checking presence of supplied element: "+objectName);

			boolean listFlag = false;
			List<String> suppliedObjectList = new ArrayList<String>();

			/** if comma separated objects are supplied then splitting them into a list. */
			if(objectName.contains(","))
			{
				listFlag = true;
				suppliedObjectList = new ArrayList<String>(Arrays.asList(objectName.split(",")));
			}
			else if(objectName.contains(";"))
			{
				listFlag = true;
				suppliedObjectList = new ArrayList<String>(Arrays.asList(objectName.split(";")));
			}
			else
			{
				listFlag = false;

				boolean iselementDisplayed = false;

				/** if data is supplied with or without comma / semi colon then convert the data into list and get the first string 
				 * as data input to create the dynamic element, multiple data can be supplied like: xyz ; must pass 
				 * then last value needs to be separated out, other than this data can't be supplied.
				 */
				webelement = handler.parseObject_GetWebElement(driver, objectName, data, jsonObjectRepo);

				if(webelement == null){
					iselementDisplayed = false;
				}else
				{
					//Checking if element is displayed
					iselementDisplayed = webelement.isDisplayed();
				}

				if(iselementDisplayed)
				{
					result = failed_status + "Element is present. ";
				}
				else
				{
					result = passed_status + "Element is not present. ";
				}
			}

			/** Iterating list and collecting not present objects into notPresentObjectList list, in case of InvocationTargetException exception
			 * also, adding object into  notPresentObjectList list
			 */
			if(listFlag)
			{
				for(int i=0; i<suppliedObjectList.size(); i++)
				{
					/** Catching InvocationTargetException exception in case webelement is not found on web page.
					 */
					try{
						webelement = getObject.getWebElementFromRepository(suppliedObjectList.get(i),  driver, jsonObjectRepo);

						if(!webelement.isDisplayed())
						{
							notPresentObjectList.add(suppliedObjectList.get(i));
						}
					}catch(NullPointerException e)
					{
						notPresentObjectList.add(suppliedObjectList.get(i));
						logger.info(suppliedObjectList.get(i) + " wasn't found on web page. ");
					}
				}

				/** checking if notPresentObjectList's size, if this is equal to supplied one then pass
				 * else fail it.
				 */

				if(notPresentObjectList.size() == suppliedObjectList.size())
				{
					result = passed_status + "All supplied elements were not present. ";
				}
				else
				{
					/**
					 * Removing not present objects from supplied list and display only present elements in results.
					 */
					suppliedObjectList.removeAll(notPresentObjectList);
					result = failed_status + "Element(s): "+suppliedObjectList +" was(were) present. ";
				}
			}
		}
		catch(Exception e)
		{
			if(e instanceof CustomException)
			{
				result = e.getMessage();
			}
			else
			{
				result = failed_status + "Couldn't check the presence of element. ";
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while checking the presence of element. ", e);

				/** Taking screenshot during exception */
				CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
			}
		}

		return result;
	}


	/** This keyword will move the driver focus to alert, if any and accept it.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String acceptalert  (WebDriver driver, String objectName, String data)
	{
		String result;

		try{

			if(handler.waitForAlert(driver))
			{
				driver.switchTo().alert().accept();
				result = passed_status+ "Alert accepted.";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Accepted alert. ");
			}
			else
			{
				return warning_status + "No alert found.";
			}
		}
		catch(NoAlertPresentException e)
		{
			logger.warn("No Alert found.");
			return warning_status + "No alert found.";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't move to alert.";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while accepting alert. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword will move the driver focus to alert (if any) and dismiss it.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String dismissalert  (WebDriver driver, String objectName, String data)
	{
		String result;

		try{

			if(handler.waitForAlert(driver))
			{
				driver.switchTo().alert().dismiss();
				result = passed_status+ "Alert dismissed.";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Alert dismissed. ");
			}
			else
			{
				return warning_status + "No alert found.";
			}
		}
		catch(NoAlertPresentException e)
		{
			logger.warn("No Alert found.");
			return warning_status + "No alert found.";
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't move to alert.";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while dismissing alert. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** This keyword is to verify the text of any alert.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String verifyalerttext(WebDriver driver, String objectName, String data)
	{
		try{

			//Getting data after removing any flag like "must flag"
			if(data.contains(";")){
				data = data.split(";")[0].trim();
			}
			else if(data.contains(",")){
				data = data.split(",")[0].trim();
			}

			String actualText = driver.switchTo().alert().getText().toString();

			//Accepting alert
			acceptalert(driver,objectName, data);

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual text present in alert: " + actualText );

			if(actualText.equals(data))
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Matched. ");
				return passed_status;
			}
			else 
			{
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Doesn't Match. ");
				return failed_status + "The actual value is: " + actualText + " but the expected value is: " + data;
			}
		}
		catch(NoAlertPresentException e)
		{
			logger.warn("No Alert found.");
			return failed_status + "No alert found.";
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the alert text. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not retrieve the alert text."; 


		}
	}


	/** This keyword is to verify if any alert is present.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String verifyalertpresent(WebDriver driver, String objectName, String data)
	{
		try
		{
			/** Get alert text, if no alert then move to NoAlertPresentException exception. */

			String alertText = driver.switchTo().alert().getText().toString();

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : An alert was present having text = "+alertText);
			driver.switchTo().alert().accept();

			return passed_status + "An alert is present having text = "+alertText;
		}
		catch(NoAlertPresentException a)
		{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Alert Was Found.");
			return failed_status + "No alert was present."; 
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the presence of alert. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not verify the presence of alert.";
		}
	}


	/** This keyword is to verify if alert is not present.
	 *
	 * @param objectName 
	 * @param data 
	 * @return 
	 */	
	public String verifyalertnotpresent(WebDriver driver, String objectName, String data)
	{
		try
		{
			/** Get alert text, if no alert then move to NoAlertPresentException exception. */

			String alertText = driver.switchTo().alert().getText().toString();

			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : An alert was present having text = "+alertText);
			driver.switchTo().alert().accept();

			return failed_status + "An alert is present having text = "+alertText;
		}
		catch(NoAlertPresentException a)
		{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Alert Was Found.");
			return passed_status + "No alert was present."; 
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the presence of alert. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

			return failed_status + "could not verify the presence of alert.";
		}
	}


	/** This keyword is to verify the value of supplied element, it checks the text present in VALUE attribute of
	 * supplied element.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifyelementvalue(WebDriver driver, String objectName, String data)throws CustomExceptionStopExecution
	{
		String result = "";
		try
		{
			if(objectName.isEmpty())
			{
				result = failed_status + "Supplied object name is empty. ";
			}
			else
			{
				//Getting data after removing any flag like "must pass"
				if(data.contains(";")){
					data = data.split(";")[0].trim();
				}
				else if(data.contains(",")){
					data = data.split(",")[0].trim();
				}

				/** First the check if the supplied element is a dynamic object which needs data to create element definition, 
				 * if no, then createDynamicWebElement method will throw CustomExceptionsLib exception, and then find the element using 
				 * method: getWebElementFromRepository
				 */
				try{
					webelement = handler.createDynamicWebElement(driver, objectName, data, jsonObjectRepo);
				}catch(CustomException c){
					webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
				}

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Verifying the value of element: " + webelement );
				String actualValue = webelement.getAttribute("value").trim();

				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : actual value of element : " + actualValue);

				if(actualValue.equals(data))
				{
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Value Matched. ");
					result = passed_status + "Value is as expected.";
				}
				else 
				{
					logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Value Doesn't Match. ");
					result = failed_status + "The actual value is: " + actualValue + ", the expected value is: " + data;
				}
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			logger.error(failed_status + "Exception occurred while verifying the value: " + data + " of the element: " +webelement, e);
			result = failed_status + "Could not retrieve the value."; 

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

		}
		return result;
	}


	/**
	 * This keyword is completely responsible for login by google interface
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String googlelogin(WebDriver driver, String objectName, String data)
	{
		String result = "";
		String username = "";
		String password = "";
		try{

			if(!data.isEmpty())
			{
				//Getting data after removing any flag like "must pass"
				if(data.contains(";")){
					username = data.split(";")[0].trim();
					password = data.split(";")[1].trim();	
				}
				else if(data.contains(",")){
					username = data.split(",")[0].trim();
					password = data.split(",")[1].trim();
				}

				// Entering the username
				webelement = getObject.getWebElementFromRepository("gmail_username",  driver, jsonObjectRepo);
				webelement.sendKeys(username);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Typing the value : " + username + " in the element: " + webelement );

				// Clicking on Next button
				webelement = getObject.getWebElementFromRepository("gmail_next_button",  driver, jsonObjectRepo);
				webelement.click();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Clicked on the next button: " + webelement );

				/** dynamic wait unitl password text is present */
				By byLocator = getObject.getByLocatorFromRepository("gmail_password", driver, jsonObjectRepo);
				handler.applyExplicitWait(driver, byLocator, new ElementNotVisibleException(""), 120);

				// Entering the password
				webelement = getObject.getWebElementFromRepository("gmail_password",  driver, jsonObjectRepo);
				webelement.sendKeys(password);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Typing the value : " + password + " in the element: " + webelement );

				// Getting the URL before login  
				String urlBeforeLogin = driver.getCurrentUrl();

				webelement = getObject.getWebElementFromRepository("gmail_next_button",  driver, jsonObjectRepo);
				webelement.click();

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Clicked on the sign button: " + webelement );

				/** putting a dynamic wait until browser'title is not vdopia */
				try{
					int count = 0;
					while(!driver.getTitle().contains("Vdopia") && count < 90)
					{
						Thread.sleep(1000);
						count ++;
					}}catch(TimeoutException t){driver.navigate().refresh();}

				// Getting the URL after successful login
				String urlAfterLogin = driver.getCurrentUrl();

				if (!urlBeforeLogin.equalsIgnoreCase(urlAfterLogin))
				{
					result = passed_status+ "user logged in successfully";
					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Page Source: "+driver.getPageSource());
				}
				else
				{
					result = failed_status + "Google login was unsuccessful. ";
				}
			}

			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No login credentials were provided. " );
				result = failed_status+ "No login credentials were provided. ";
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Google login was unsuccessful. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while google sign in. ", e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);

		}
		return result;
	}


	/** This keyword will be used to scroll objects.
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String scrollobject(WebDriver driver, String objectName, String data) 
	{
		String result = "";
		By byLocator = null;
		try
		{	
			try{
				/** create dynamic locator */
				byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
			}catch(CustomException e)
			{
				/** create locator normally */
				byLocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
			}

			//apply explicit wait
			handler.applyExplicitWait(driver, byLocator, new ElementNotVisibleException(""), 7);
			webelement = driver.findElement(byLocator);

			//execute js to bring element into view
			String javaScript = "arguments[0].scrollIntoView(false);";
			handler.executeJavaScript(driver, javaScript, webelement);

			result = passed_status+ "Scrolled the bar successfully";
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Scrolled the bar successfully " );
		}
		catch(Exception e){
			result = failed_status + ": Unable to scroll the bar";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while scrolling the bar ", e);
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}

	/** Waiting for element visibility
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String waitforelementvisibility(WebDriver driver, String objectName, String data) 
	{
		String result = "";
		By byLocator = null;
		try
		{	
			try{
				/** create dynamic locator */
				byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
			}catch(CustomException e)
			{
				/** create locator normally */
				byLocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
			}

			//apply explicit wait
			handler.applyExplicitWait(driver, byLocator, new ElementNotVisibleException(""), 45);
			webelement = driver.findElement(byLocator);
			if(webelement.isDisplayed()){
				result = passed_status+ "successfully waited for the element";
			}
		}
		catch(Exception e){
			result = failed_status + "Unable to wait for the element visibility";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while waiting for the element visibility ", e);
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}
		return result;
	}


	/** Waiting for element visibility
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String selectdropdownlisting(WebDriver driver, String objectName, String data) 
	{
		String result = "";
		By byLocator = null;
		List<WebElement> elements=null;
		String optionValue=null;
		try
		{	
			try{
				/** create dynamic locator */
				byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
			}catch(CustomException e)
			{
				/** create locator normally */
				byLocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
			}

			//apply explicit wait
			handler.applyExplicitWait(driver, byLocator, new ElementNotVisibleException(""), 10);
			webelement = driver.findElement(byLocator);
			elements=webelement.findElements(By.tagName("option"));
			for(WebElement element:elements){
				optionValue=element.getText().trim();
				if(optionValue.equalsIgnoreCase(data)){
					element.click();
					result = passed_status + "Clicked on the element matched";
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Selected the option successfully -- Actual ="+optionValue+" and Expected ="+data);
					break;
				}
			}
		}
		catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception in selecting from the dropdown", e);
			result = failed_status + e.getMessage();
		}
		return result;
	}

	/** This is used to escape the auto fill --- pages like package and deal screen
	 * 
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String escapeautofill  (WebDriver driver, String objectName, String data)
	{
		String result = "";
		try
		{	Actions action = new Actions(driver);
		action.sendKeys(Keys.ESCAPE).build().perform();
		result = passed_status+"Escaped the object successfully";
		}
		catch(Exception e){
			result = failed_status + e.getMessage();
		}
		return result;	
	}


	/** This keyword will bring the desired element in focus.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String getfocusonelement(WebDriver driver, String objectName, String data)
	{
		String result = "";
		By byLocator = null;

		try{
			if(!objectName.isEmpty())
			{
				try{
					/** create dynamic element */
					byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
					webelement = driver.findElement(byLocator);
				}catch(CustomException e)
				{
					/** create element normally */
					webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
					if(webelement.equals(null))
					{
						Thread.sleep(5000);
						webelement = getObject.getWebElementFromRepository(objectName,  driver, jsonObjectRepo);
					}
				}
				if(!webelement.isEnabled())
				{
					By bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);

					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Element is not enabled");
					handler.applyExplicitWait(driver, bylocator, new WebDriverException());
				}

				Thread.sleep(1000);
				String javaScript = "arguments[0].scrollIntoView(false);";
				Thread.sleep(1000);

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Executing java script: " +javaScript);
				handler.executeJavaScript(driver, javaScript, webelement);

				result = passed_status+ "script executed successfully";
			}
			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Object received to bring into focus.");
				result = failed_status+ "No Object received to bring into focus. ";
			}
		}
		catch(CustomException e)
		{
			result = failed_status + e.getMessage();
		}
		catch(Exception e)
		{
			result = failed_status + "Couldn't bring the element in focus. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while moving foucs on element: " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}


	/** This keyword has no use in this class, it is just a flag which is read by ReadTestCases class even before coming to this class,
	 * this is declared here because, if its not declared here then Test Case will try to find this keyword here and that would throw
	 * an error saying this - on_error_resume_next doesn't exist. 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String on_error_resume_next(WebDriver driver, String objectName, String data)
	{
		if(data.equalsIgnoreCase("no") || data.equalsIgnoreCase("false"))
		{
			return passed_status+"After encountering first failure, subsequent steps won't be executed.";
		}
		else
		{
			return passed_status +"After encountering first failure, subsequent steps will still be executed.";
		}
	}

	/** this keyword will wait until the supplied element is disappeared.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String waitforelementtodisappear(WebDriver driver, String objectName, String data)
	{
		String result = "";
		try{
			By bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
			WebDriverWait wait = new WebDriverWait(driver, 60);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(bylocator));

			result = passed_status +" Success. ";
		}
		catch(Exception e)
		{
			result = failed_status +" error occurred while waiting for disappearance of supplied object. ";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :Exception occured when select category"+e);
		}
		return result;

	}

	/** this keyword will wait until the supplied element is disappeared.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	/** This keyword will bring the desired element in focus.
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String reloadpage(WebDriver driver, String objectName, String data)
	{
		String result = "";
		By byLocator = null;

		try{
			if(!objectName.isEmpty())
			{
				try{
					/** create dynamic element */
					byLocator = handler.createDynamicByLocator(driver, objectName, data, jsonObjectRepo);
					webelement = driver.findElement(byLocator);
				}catch(CustomException e)
				{
					/** create element normally */
					webelement = getObject.getWebElementFromRepository(objectName, driver, jsonObjectRepo);
					if(webelement.equals(null))
					{
						Thread.sleep(5000);
						webelement = getObject.getWebElementFromRepository(objectName, driver, jsonObjectRepo);
					}
				}
				if(webelement.isEnabled())
				{
					int count=0;
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Criteo block is displayed and refreshing the page");
					do{
						driver.navigate().refresh();
						count++;
					}while(webelement.isEnabled() && count<5);

					result = passed_status +" page is refreshed";
				}else{
					result = passed_status +" No need to refresh";
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No criteo header found");
				}
			}
			else
			{
				result = passed_status +" No need to refresh";
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No criteo header found");
			}
		}
		catch(CustomException e)
		{
			result = passed_status +" No need to refresh" + e.getMessage();
		}
		catch(Exception e)
		{
			result = passed_status +" No need to refresh";
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred: "+e+" while checking for the availability of : " +webelement, e);

			/** Taking screenshot during exception */
			CaptureScreenShotLib.captureScreenShot(driver, locationToSaveSceenShot.concat(GenericMethodsLib.DateTimeStamp("MMdd_hhmmss") + ".png"));
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Screenshot is captured at: "+locationToSaveSceenShot);
		}

		return result;
	}



	/** This keyword is created specifically for e2e tests where we need the test data in test cases for further usage and the test data is supplied 
	 * after replacing the desired macros. 
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String donothing(WebDriver driver, String objectName, String data)
	{
		return "PASS: NOTHING WAS DONE HERE. ";
	}


	/** apply delay in seconds
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String applyrandomdelaysec(WebDriver driver, String objectName, String data)
	{
		int delay = 0;
		try
		{
			delay = IntegerLib.GetRandomNumber(6000, 3000);
			Thread.sleep(delay);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Applied Random Delay: "+delay);
		}catch(Exception e){}
		return "PASS: Applied Random Delay of: "+delay;
	}


	/**verify all links for footer and header
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String verifylinks(WebDriver driver, String objectName, String data) 
	{
		String url, text ="";
		int status=0;

		List<String> brokenlinks = new ArrayList<>();

		try{

			By bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);

			List<WebElement> alllinks = driver.findElements(bylocator);

			for (int i = 0; i < alllinks.size(); i++) {
				url = alllinks.get(i).getAttribute("href");
				text =  alllinks.get(i).getText();

				if (!url.contains("javascript:void")){
					status = httpClientWrap.getStatusCodeOfGetRequest("{"+url+"}", null);

					if (status!=200){
						brokenlinks.add(text + " - " +url);
					}
				}	

			}
		}
		catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+ "-" +e.getMessage(), e);
		}
		if (brokenlinks.size()==0){
			return passed_status+" No Broken Link on.";
		}
		else{
			return failed_status + "Links not working for "+brokenlinks.toString();
		}
	}


	/** Get all the links from the supplied bylocator 
	 * 
	 * @param driver
	 * @param bylocator
	 * @return
	 */
	public String get_linksofelement(WebDriver driver, String objectName, String data)
	{
		List<String> urlList = new ArrayList<>();
		try
		{
			By bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
			List<WebElement> listMatchingElements = driver.findElements(bylocator);

			for (int i = 0; i < listMatchingElements.size(); i++) {

				WebElement object = listMatchingElements.get(i);

				if(object.getAttribute("href") != null) {
					urlList.add(object.getAttribute("href"));
				}else if(object.getAttribute("src") != null) {
					urlList.add(object.getAttribute("src"));
				}
			}

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : Received URL List: " +urlList.toString());

		}catch (Exception e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting list: ", e);
		}

		/** just to maintain the same return format -- returning string */
		String urls = urlList.toString().replace("[", "").replace("]", "");
		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : " +" received urls : "+urls);

		return urls;
	}


	/** User will supply the data -- which will have comma separated urls 
	 * 
	 * @param urlList
	 * @return
	 */
	public String verifybrokenlinks(WebDriver driver, String objectName, String data)
	{
		List<String> urlList = Arrays.asList(data.split(","));

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get() + " : "+ " Received url list to verify: "+urlList.toString());

		if(urlList.size() < 1) {
			return skip_status+ " No Links Were Supplied To Check. ";
		}else {
			List<String> brokenList = handler.getBrokenLinks(urlList);

			if(brokenList.size() > 0) {
				return failed_status+" Broken Links: "+brokenList.toString();
			}else {
				return passed_status+" No Broken Links Found. ";
			}
		}
	}


	/** Keyword to execute java script
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String executejavascript(WebDriver driver, String objectName, String data)
	{
		/**
		 * Sample code, if javascript has to be executed on a webelement:
		 * js.executeScript("arguments[0].click()", webelement);
		 */

		Object objJsOutput = null;
		try
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : " +" Executing java script: "+data);

			/** for sync */
			Thread.sleep(1000);

			String javaScript = data;

			By bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
			webelement = driver.findElement(bylocator);

			JavascriptExecutor js = (JavascriptExecutor) driver;

			/** just to bring browser back in focus - can be removed later on */
			objJsOutput = js.executeScript("alert('Hi')");
			driver.switchTo().alert().accept();

			if(webelement != null)
			{
				objJsOutput = js.executeScript(javaScript, webelement);
			}
			else
			{
				objJsOutput = js.executeScript(javaScript);
			}

			Thread.sleep(1000);
		}
		catch(Exception ex)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while executing java script: "+data +" for supplied element: "+webelement, ex);
		}

		return passed_status + "Java Script Executed. "+objJsOutput;
	}

	/** get captcha code to be entered
	 * @author rishi
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String storedata_getcaptchacode(WebDriver driver, String objectName, String data){
		String result=null;
		String captchaText=null;
		try{
			boolean staleExceptionHandleFlag = true;
			int staleExceptionAttempt=0;

			/** Adding a check on staleExceptionHandleFlag exception, in case this occurs then find the element again until the max attempt = 5.
			 */
			while(staleExceptionHandleFlag)
			{
				try
				{					
					/** First check if this a dynamic element, if not then catch customexception and find element conventionally --> 
					 * Now putting condition on data, if data is empty then get element from object repository using objectName 
					 * else find element using objectLabel --> to be used in keyword clickmenu.
					 */
					try{
						webelement = handler.createDynamicWebElement(driver, objectName, data, jsonObjectRepo);
					}catch(CustomException c)
					{
						webelement = getObject.getWebElementFromRepository(objectName, driver, jsonObjectRepo);
					}

					/** Wait until link is visible and clickable, if its not enabled.*/
					By bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);

					if(!webelement.isEnabled() && webelement != null)
					{
						handler.applyExplicitWait(driver, bylocator, new WebDriverException());
					}

					captchaText = handler.getCaptcha(webelement, driver, bylocator);

					staleExceptionHandleFlag = false;

				}catch(StaleElementReferenceException e){
					staleExceptionAttempt ++;
				}

				if(staleExceptionAttempt ==5){
					break;
				}
			}

			Thread.sleep(2500);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" captcha text is" + captchaText);
			result = captchaText;

		}
		catch(Exception e)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" Unable to capture captcha text" + e);
		}
		return result;	
	}


	public String get_responsefieldasstring (WebDriver driver, String objectName, String data) {
		String result="";
		try {
			result =JsonParserHelper.getAsString(data, objectName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Exception occurred while prasing JSON response field", e);
			return failed_status + "Cannot get value for objectName specified" + objectName;
		}

		return  result;		
	}

//	public String get_apiresponse(WebDriver driver, String objectName, String data){
//		objectName = objectName.replace("{", "");
//		objectName = objectName.replace("}", "");
//		objectName = objectName.trim();
//
//		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received URL: "+objectName);
//
//		StringBuffer apiresult = new StringBuffer();
//		String result="";
//		CloseableHttpClient httpclient = null;
//		CloseableHttpResponse response = null;
//		String sessionToken1 = null;
//		String xSessionToken = null;
//		
//
//		try 
//		{
//			if(objectName.isEmpty() || objectName.equalsIgnoreCase(""))
//			{	
//				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+objectName);
//			}
//			else
//			{			
//				//Adding add custom headers in request
//				if(data != null && (!data.isEmpty()))
//				{
//<<<<<<< HEAD
//					httpclient = HttpClients.createDefault();
//					String method = JsonParserHelper.getAsString(data, "type");
//					if (method.equals("Post")){
//						HttpPost request = new HttpPost(objectName);
//						HttpResponse postSessionResponse=httpclient.execute(request);
//						HttpEntity sessionEntity=postSessionResponse.getEntity();
//						String sessionResponse=EntityUtils.toString(sessionEntity, "UTF-8");
//						JSONObject sessionObject=new JSONObject(sessionResponse);
//						JSONObject sessionResultObj=sessionObject.getJSONObject("result");
//						sessionToken1=sessionResultObj.get("id").toString();
//						if ((JsonParserHelper.getAsString(data,"cartClean").equals("yes"))){
//						request = new HttpPost("https://api.lenskart.com/v2/customers/authenticate");
//						//postSessionResponse=httpclient.execute(request);
//						request.setHeader("X-Api-Client", "desktop");
//						request.setHeader("X-Session-Token", sessionToken1);
//						request.setHeader("Content-Type", "application/json");
//						String stringBody="{\"username\":\""+JsonParserHelper.getAsString(data, "username")+"\",\"password\":\""+JsonParserHelper.getAsString(data, "password")+"\"}";
//						System.out.println(">>>>> "+stringBody);
//						StringEntity JsonEntityObj = new StringEntity(stringBody.toString());
//						request.setEntity(JsonEntityObj);
//						//postLoginRequest.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
//						response=httpclient.execute(request);
//						System.out.println("Check..."+response.getStatusLine().getStatusCode());
//						HttpEntity loginEntity=response.getEntity();
//						String loginRes=EntityUtils.toString(loginEntity,"UTF-8");
//						JSONObject loginJSONObj=new JSONObject(loginRes);
//						JSONObject loginResultObj=loginJSONObj.getJSONObject("result");
//						System.out.println(">>>>"+loginResultObj+"<<<<<<");
//						xSessionToken=loginResultObj.get("token").toString();
//						System.out.println(">>>>>>"+ xSessionToken);
//						HttpDelete deleteRequest = new HttpDelete("https://api.lenskart.com/v2/carts/items");
//						deleteRequest.setHeader("X-Api-Client", "desktop");
//						deleteRequest.setHeader("X-Session-Token", xSessionToken);
//						deleteRequest.setHeader("Content-Type", "application/json");
//			            System.out.println("  fhihfdhfd" + deleteRequest.getURI());
//			            response =httpclient.execute(deleteRequest);
//			            System.out.println("Check2..."+response.getStatusLine().getStatusCode());
//			            HttpEntity ent= response.getEntity();
//			            String res=EntityUtils.toString(ent,"UTF-8");
//			            System.out.println("Response.."+res);
//			            JSONObject result1 = new JSONObject(res);
//			            JSONObject resultObject=result1.getJSONObject("result");
//			            String remainingQty=resultObject.getString("itemsQty");
//			            System.out.println("Item remaining = " + remainingQty);
//			            httpclient.close();
//						}
//					}
//					else if (method.equals("Get")){
//						HttpGet request = new HttpGet(objectName);
//						response = httpclient.execute(request);
//	
//					}
//					else if (method.equals("Delete")){
//						HttpDelete request = new HttpDelete(objectName);
//						request.setHeader("X-Api-Client", "desktop");
//						request.setHeader("X-Session-Token", xSessionToken);
//			            System.out.println("  fhihfdhfd" + request.getURI());
//			            response =httpclient.execute(request);
//			            System.out.println("Check2..."+response.getStatusLine().getStatusCode());
//			            HttpEntity ent= response.getEntity();
//			            String res=EntityUtils.toString(ent,"UTF-8");
//			            System.out.println("Response.."+res);
//			            JSONObject result1 = new JSONObject(res);
//			            JSONObject resultObject=result1.getJSONObject("result");
//			            String remainingQty=resultObject.getString("itemsQty");
//			            System.out.println("Item remaining = " + remainingQty);
//			            httpclient.close();
//					}
//					 
//				}
//
//=======
//					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Adding custom headers... ");
//					/*for(Entry<String, String> map : headers.entrySet())
//					{
//						String name = map.getKey().trim();
//						String value = map.getValue().trim();
//						GetRequest.addHeader(name, value);
//					}*/
//
//					/*GetRequest.addHeader("X-Api-Client","desktop");
//					GetRequest.addHeader("Content-Type","application/json");*/
//					GetRequest.addHeader("sessiontoken", data);
//				}
//
//				response = httpclient.execute(GetRequest);
//
//				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : --------------------------RESPONSE ----------------------------");
//				logger.debug(response.getStatusLine());
//
//				BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent()));
//
//				String line = "";
//				while ((line = rd.readLine()) != null)
//				{
//					apiresult.append(line);
//				}
//				result = new String(apiresult.toString());
//				logger.info(result);
//>>>>>>> d947edce8be51c5cc5bb68f145d4b433274cba5e
//			}
//		} 
//		catch(Exception e)
//		{
//			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting response from url: "+objectName, e);
//		}
//		return result;
//	}


	public String get_apiresponse(WebDriver driver, String objectName, String data){
		objectName = objectName.replace("{", "");
		objectName = objectName.replace("}", "");
		objectName = objectName.trim();

		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received URL: "+objectName);

		//StringBuffer apiresult = new StringBuffer();
		String result="";
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		String sessionToken1 = null;
		String xSessionToken = null;
		

		try 
		{
			if(objectName.isEmpty() || objectName.equalsIgnoreCase(""))
			{	
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Provided server url is Either BLANK or EMPTY, Please check it again. "+objectName);
			}
			else
			{			
				//Adding add custom headers in request
				if(data != null && (!data.isEmpty()))
				{
					httpclient = HttpClients.createDefault();
					String method = JsonParserHelper.getAsString(data, "type");
					if (method.equals("Post")){
						HttpPost request = new HttpPost(objectName);
						HttpResponse postSessionResponse=httpclient.execute(request);
						HttpEntity sessionEntity=postSessionResponse.getEntity();
						String sessionResponse=EntityUtils.toString(sessionEntity, "UTF-8");
						JSONObject sessionObject=new JSONObject(sessionResponse);
						JSONObject sessionResultObj=sessionObject.getJSONObject("result");
						sessionToken1=sessionResultObj.get("id").toString();
						if ((JsonParserHelper.getAsString(data,"cartClean").equals("yes"))){
						request = new HttpPost("https://api.lenskart.com/v2/customers/authenticate");
						//postSessionResponse=httpclient.execute(request);
						request.setHeader("X-Api-Client", "desktop");
						request.setHeader("X-Session-Token", sessionToken1);
						request.setHeader("Content-Type", "application/json");
						String stringBody="{\"username\":\""+JsonParserHelper.getAsString(data, "username")+"\",\"password\":\""+JsonParserHelper.getAsString(data, "password")+"\"}";
						System.out.println(">>>>> "+stringBody);
						StringEntity JsonEntityObj = new StringEntity(stringBody.toString());
						request.setEntity(JsonEntityObj);
						//postLoginRequest.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
						response=httpclient.execute(request);
						System.out.println("Check..."+response.getStatusLine().getStatusCode());
						HttpEntity loginEntity=response.getEntity();
						String loginRes=EntityUtils.toString(loginEntity,"UTF-8");
						JSONObject loginJSONObj=new JSONObject(loginRes);
						JSONObject loginResultObj=loginJSONObj.getJSONObject("result");
						System.out.println(">>>>"+loginResultObj+"<<<<<<");
						xSessionToken=loginResultObj.get("token").toString();
						System.out.println(">>>>>>"+ xSessionToken);
						HttpDelete deleteRequest = new HttpDelete("https://api.lenskart.com/v2/carts/items");
						deleteRequest.setHeader("X-Api-Client", "desktop");
						deleteRequest.setHeader("X-Session-Token", xSessionToken);
						deleteRequest.setHeader("Content-Type", "application/json");
			            System.out.println("  fhihfdhfd" + deleteRequest.getURI());
			            response =httpclient.execute(deleteRequest);
			            System.out.println("Check2..."+response.getStatusLine().getStatusCode());
			            HttpEntity ent= response.getEntity();
			            String res=EntityUtils.toString(ent,"UTF-8");
			            System.out.println("Response.."+res);
			            JSONObject result1 = new JSONObject(res);
			            JSONObject resultObject=result1.getJSONObject("result");
			            String remainingQty=resultObject.getString("itemsQty");
			            System.out.println("Item remaining = " + remainingQty);
			            httpclient.close();
						}
					}
					else if (method.equals("Get")){
						HttpGet request = new HttpGet(objectName);
						response = httpclient.execute(request);
	
					}
					else if (method.equals("Delete")){
						HttpDelete request = new HttpDelete(objectName);
						request.setHeader("X-Api-Client", "desktop");
						request.setHeader("X-Session-Token", xSessionToken);
			            System.out.println("  fhihfdhfd" + request.getURI());
			            response =httpclient.execute(request);
			            System.out.println("Check2..."+response.getStatusLine().getStatusCode());
			            HttpEntity ent= response.getEntity();
			            String res=EntityUtils.toString(ent,"UTF-8");
			            System.out.println("Response.."+res);
			            JSONObject result1 = new JSONObject(res);
			            JSONObject resultObject=result1.getJSONObject("result");
			            String remainingQty=resultObject.getString("itemsQty");
			            System.out.println("Item remaining = " + remainingQty);
			            httpclient.close();
					}
					 
				}

			}
		} 
		catch(Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while getting response from url: "+objectName, e);
		}
		return result;
	}

	public String verifyresponse(WebDriver driver, String objectName, String data){

		String result="";
		if(objectName.equalsIgnoreCase(data))
		{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Matched. ");
			result = passed_status + "Text is as expected.";
		}
		else 
		{
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Actual and Expected Text Doesn't Match. ");
			result = failed_status + "The actual value is: " + objectName + ", the expected value is: " + data;
		}
		return result;

	}

	public String get_session(WebDriver driver, String objectName, String data){
		String session = driver.manage().getCookieNamed("frontend").getValue();
		logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Session id is :- ");
		return session;
	}

	public String get_randomproduct(WebDriver driver, String objectName, String data){

		By bylocator;
		try {
			bylocator = getObject.getByLocatorFromRepository(objectName, driver, jsonObjectRepo);
			List<WebElement> totalProduct = driver.findElements(bylocator);
			if(totalProduct.size()!=1){
				Random randomBrand = new Random();
				int randomBrandIndex = randomBrand.nextInt(totalProduct.size()-1);
				WebElement el1= totalProduct.get(randomBrandIndex);
				/*	productId= el1.getAttribute("unbxdparam_sku");
				logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Product ID is:" +productId);*/
				handler.performMouseAction(driver,el1);

				String javaScript = "arguments[0].click();";
				handler.executeJavaScript(driver, javaScript, el1);
				//clickByJS(driver,el1);

			} 
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" get_randomproduct Fail ");
			return failed_status;

		}

		return passed_status;

	}

	public String clickByJS(WebDriver driver,WebElement e1) throws Exception
	{
		try{
			((JavascriptExecutor)driver).executeScript("arguments[0].checked = true;", e1);
			((JavascriptExecutor)driver).executeScript("arguments[0].click()", e1);

			try{
				driver.switchTo().alert().accept();
			}catch(Throwable e){

			}


			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"clickByJS" +"passed");

		}catch(Throwable e){
			logger.debug(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" clickByJS Fail ");
			//Utilities.takeErrorScreenShot();		
			return failed_status;
		}
		return passed_status;
	}


	public String storedataobject(WebDriver driver, String objectName, String data){
		String objectPath ="/Users/rishi/Documents/myWorkSpace/Lenskart_Automation/conf/dataObject";
		File fileObj;
		JSONObject jsonObj;
		JSONArray jsonArrayObj;
		OrderDetails orderdetailObj;
		ObjectInputStream ois;
		FileOutputStream fos;
		ObjectOutputStream oos;
		String fileData;
		try{
			if(!data.isEmpty()){
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" data json is " + data);
				fileObj = new File(objectPath);
				if(fileObj.exists()){
					/**
					 * file exists hence first need to read the object data and then appending the input data
					 */
					ois = new ObjectInputStream(new FileInputStream(new File(objectPath)));
					orderdetailObj = (OrderDetails) ois.readObject();
					fileData = orderdetailObj.getOrderDetail();
					jsonArrayObj = new JSONArray(fileData);
					jsonArrayObj.put(new JSONObject(data));
					orderdetailObj.setOrderDetail(jsonArrayObj.toString());
				}else{
					/**
					 * file doesn't exists hence need to set the data object with the input data
					 */
					orderdetailObj= new OrderDetails();
					jsonObj=new JSONObject(data);
					jsonArrayObj= new JSONArray();
					jsonArrayObj.put(jsonObj);
					orderdetailObj.setOrderDetail(jsonArrayObj.toString());
				}
				fos = new FileOutputStream(objectPath);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(orderdetailObj);
				oos.flush();
				oos.close();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" data is added in the dataObject" + data);
				return passed_status;
			}
			else{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Data is empty hence nothing is added in json object" + data);
				return failed_status;
			}

		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured with writing data to object for "+data, e);
			return failed_status;
		}

	}


	/**
	 * Keyword is used to retrieve object based on grammer from the serialized object.
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String retrievedataobject(WebDriver driver, String objectName, String data){
		String object=null;
		String objectPath ="/Users/rishi/Documents/myWorkSpace/Lenskart_Automation/conf/dataObject";
		String jsonData;
		JSONArray jsonArrayObj;
		OrderDetails orderdetailObj;
		ObjectInputStream ois;
		int index=0;
		String objectParam=null;
		try{
			objectParam=data.substring(data.indexOf("GET")+3, data.indexOf("ON Index")).trim();
			index=Integer.parseInt(data.substring(data.indexOf("ON Index")+8, data.length()).trim());
			ois=new ObjectInputStream(new FileInputStream(new File(objectPath)));
			orderdetailObj = (OrderDetails) ois.readObject();
			jsonData=orderdetailObj.getOrderDetail();
			jsonArrayObj=new JSONArray(jsonData);
			JSONObject obj = (JSONObject) jsonArrayObj.get(index);
			object=obj.get(objectParam).toString();
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured with retriving data from the object "+data, e);
		}
		return object;
	}

	/** this keyword will return the network requests
	 * 
	 * @param driver
	 * @param objectName
	 * @param data
	 * @return
	 */
	public String getproxylog(WebDriver driver, String objectName, String data)
	{
		String result= "";

		String har = TestSuiteClass.AUTOMATION_HOME+"/har_"+new Date().getTime();
		try{
			proxyServer.getHar().writeTo(new File(har));
			JSONObject json = new JSONObject(FileLib.ReadContentOfFile(har));

			JSONArray jsonArray = json.getJSONObject("log").getJSONArray("entries");

			for(int i=0; i<jsonArray.length(); i++)
			{
				JSONObject object = jsonArray.getJSONObject(i);

				JSONObject request = object.getJSONObject("request");
				JSONObject response = object.getJSONObject("response");

				if(request.getString("url").contains("api.lenskart.com") 
						&& (response.getString("status").startsWith("5") || response.getString("status").startsWith("4")))
				{
					result = result + request.getString("url") + " --- " + response.getString("status") + "\n";
				}	
			}
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+"Logs are generated for the test case");	
		
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : error occured while getting the log file", e);
		}
		
		/** stopping server after getting har file */
		proxyServer.stop();
		
		return result;
	}

}