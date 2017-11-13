/**
 * Last Changes Done on Jan 23, 2015 3:45:57 PM
 * Last Changes Done by Pankaj Katiyar
 * Purpose of change: Implemented logger, added support for rtb_win and rtb_bp trackers for hudson requests
 */

package framework.utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lenskart.tests.OrderDetails;
import lenskart.tests.TestSuiteClass;
import net.lightbody.bmp.proxy.ProxyServer;

import org.apache.commons.configuration.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;


public class GenericMethodsLib 
{

	static Logger logger = Logger.getLogger(GenericMethodsLib.class.getName());

	public static PropertiesConfiguration propertyConfigFile;


	/***
	 * This method initialize the webdriver based on supplied browser type. New Way implemented for Chrome Driver:
	 * Now we'll start the chrome server and then wait until server is started and then create a remote driver.
	 * @param browser
	 * @param capabilities
	 * @return
	 */
	public static WebDriver WebDriverSetUp (String browser, String[] capabilities) 
	{

		WebDriver driver = null;
		try
		{
			logger.info(browser+" is being setup on " +System.getProperty("os.name"));

			if(browser.equalsIgnoreCase("FireFox"))
			{
				driver = new FirefoxDriver();
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Firefox is being setup");
			}
			else if (browser.equalsIgnoreCase("Chrome")) 
			{
				String chromeDriver;
				if(System.getProperty("os.name").matches("^Windows.*"))
				{
					chromeDriver = TestSuiteClass.AUTOMATION_HOME.concat("/tpt/drivers/chromedriver.exe");
				}else
				{
					//ExecuteCommands.ExecuteMacCommand_ReturnsExitStatus("killall chromedriver");
					chromeDriver = TestSuiteClass.AUTOMATION_HOME.concat("/tpt/drivers/chromedriver");
				}

				/** create chrome driver service */
				ChromeDriverService service = retryChromeDriverService(chromeDriver);				

				if(service != null && service.isRunning())
				{
					DesiredCapabilities cap = DesiredCapabilities.chrome();
					ChromeOptions options = new ChromeOptions();
					//options.addArguments("--kiosk");
					options.addArguments("disable-infobars");
					cap.setCapability(ChromeOptions.CAPABILITY, options);

					try{
						driver = new RemoteWebDriver(service.getUrl(), cap);
					}catch (SessionNotCreatedException e) 
					{
						/** if session is not created successfully then re-try to create it. Calling recursion */
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver session not setup, retrying ... ");

						driver = WebDriverSetUp(browser, capabilities);
					}
					catch (WebDriverException e) 
					{
						/** if session is not created successfully then re-try to create it. Calling recursion */
						logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver session not setup coz of webdriver exception, retrying ... ");

						driver = WebDriverSetUp(browser, capabilities);
					}
				}
				else
				{
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver service seems not started while setting up driver ... ");
				}

				/** browsing google.com to check if driver is launched successfully */
				try{driver.get("http://www.google.com");}catch(NoSuchWindowException n)
				{
					logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome browser was closed coz of unknown reason, retrying ... ");

					driver = WebDriverSetUp(browser, capabilities);
				}
			}
			else 
			{	
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No Support For: "+browser +" Browser. ");
			}			

			int driverImplicitDelay = Integer.parseInt(propertyConfigFile.getProperty("driverImplicitDelay").toString());

			/** setting up implicit driver delay */
			driver.manage().timeouts().implicitlyWait(driverImplicitDelay, TimeUnit.SECONDS);

			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
		}
		catch (Exception e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while setting up browser: " + browser, e);
		} 

		return driver;
	}


	/** This method will attempt to start chrome driver service, earler we were using recursion for retry that may result in
	 * infinite loops, now limiting max attempts to 10.
	 * 
	 * @param chromeDriver
	 * @return
	 */
	public static ChromeDriverService retryChromeDriverService(String chromeDriver) 
	{
		ChromeDriverService service = null;

		int i = 0;
		while(i <= 10)
		{
			if(service != null)
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver service is started yet, attempt: "+i);
				break;
			}
			else
			{
				service = getChromeDriverService(chromeDriver);
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver service is not started yet, attempt: "+i);
			}
		}

		/** wait for chrome driver to start */ 
		if(service != null)
		{
			waitForChromeDriverToStart(service);
		}

		return service;
	}


	/** Get chromedriver service instance.
	 * 
	 * @param chromeDriver
	 * @return
	 */
	public static ChromeDriverService getChromeDriverService(String chromeDriver)
	{
		ChromeDriverService service = null;
		try
		{
			service = new ChromeDriverService.Builder()
					.usingDriverExecutable(new File(chromeDriver))
					.usingAnyFreePort()
					.build();
			service.start();

			Thread.sleep(1000);

		}catch(Exception io){
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception occurred while starting the chrome driver service: "+ io);
		}

		return service;
	}


	/** This method waits for chrome driver to start, earlier we were putting infinite loop for wait, now limiting 10 attempts.
	 * 
	 * @param service
	 */
	public static void waitForChromeDriverToStart(ChromeDriverService service)
	{
		int i = 0;

		/** wait until chrome driver server is started -- maximum 10 attempts */
		while(i <= 10)
		{
			String output = httpClientWrap.sendGetRequest((service.getUrl().toString()));
			if(output.isEmpty())
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver is not started yet, attempt: "+i);
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}
			else
			{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Chrome driver is started, exiting loop at attempt: "+i);
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
				break;
			}
		}
	}


	/**
	 * Initializing Configuration File
	 */
	public static void InitializeConfiguration()  
	{	
		try
		{
			propertyConfigFile = new PropertiesConfiguration();
			String varAutomationHome = TestSuiteClass.AUTOMATION_HOME;

			// Now we will add path to conf folder and qaconf.properties is the file which will be needed to fetch the configurations.
			String config = varAutomationHome.concat("/conf/qaconf.properties");

			propertyConfigFile.load(config);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception Error occurred While Reading Config File, Ensure that Config file is at the mentioned path. ", e);
		}
	}


	//********** Establishing JDBC Connection to Mysql database: *********************************************//
	public static Connection CreateSQLConnection()  
	{
		Connection qaConnection = null;
		try
		{
			GenericMethodsLib.InitializeConfiguration();

			String dbClass = "com.mysql.jdbc.Driver";		
			Class.forName(dbClass);

			// Getting Values for dburl,dbUsername and dbPassword from configuration file
			String dburl = propertyConfigFile.getProperty("dbURL").toString();
			String dbuserName = propertyConfigFile.getProperty("dbUserName").toString();
			String dbpassword = propertyConfigFile.getProperty("dbPassword").toString();

			qaConnection = (Connection) DriverManager.getConnection (dburl,dbuserName,dbpassword);
		}
		catch(NullPointerException e)
		{
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : NullPointerException Handled By Method CreateSQLConnection, Plz check Config Values or Initialize Config by calling Method - InitializeConfiguration", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error occurred while creating sql connection. ", e);
		}
		//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : SQL Connection Was Made Successfully By Method CreateSQLConnection: " +url + " ; " + userName + " ; " +password);
		return qaConnection;
	}


	//********** Executing MySQL Query and Returning Result Set: *********************************************//
	public static ResultSet ExecuteMySQLQueryReturnsResultSet(Connection con, String sqlQuery) throws SQLException 
	{		
		try{
			Statement stmt = (Statement) con.createStatement();
			ResultSet rs = (ResultSet) stmt.executeQuery(sqlQuery);
			return rs;
		}catch(MySQLSyntaxErrorException m){
			logger.error(m.getMessage());
			return null;
		}
	}

	//********** Executing MySQL Query and Returning 2 D Array containing the Result Set without Column Name) *********************************************//
	public static String [][] ExecuteMySQLQueryReturnsArray(Connection con, String sqlQuery) 
	{		

		String [][]arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			rs.last();	// Setting the cursor at last
			int rows = rs.getRow();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Column Count: "+columns);

			arrayRecords = new String[rows][columns];

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getString(i).toString();
					arrayRecords[rs.getRow()-1][i-1] = strRecord;
					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturnsArray: " +strRecord);
					//}
				}
				//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : ");
			}			
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : MySQL Data Was Successfully Exported By Method ExecuteMySQLQueryReturnsArray. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsArray:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturnsArray", e);
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Expection Handled By: ExecuteMySQLQueryReturnsArray", e);
		}

		return arrayRecords;
	}



	//********** Executing MySQL Query and Returning 1 D Array containing the Result Set without Column Name *********************************************//
	public static String [] ExecuteMySQLQueryReturns1DArray(Connection con, String sqlQuery) 
	{		
		String []arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			if(rs !=null)
			{
				int columns = rs.getMetaData().getColumnCount();
				//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Column Count: "+columns);

				arrayRecords = new String[columns];

				rs.beforeFirst();	// Setting the cursor at first line	
				while (rs.next())
				{
					for(int i=1;i<=columns;i++)
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[i-1] = strRecord;
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturns1DArray: " +arrayRecords[i-1]);
					}
				}	
			}
			else
			{
				logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Received NULL record set for the supplied query: "+sqlQuery);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturns1DArray:", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception Handled By: ExecuteMySQLQueryReturns1DArray. ", e);
		}

		return arrayRecords;
	}


	/** Executing MySQL Query and Returning 1 D Array containing the Result Set without Column Name 
	 * 
	 * @param con
	 * @param sqlQuery
	 * @return
	 */
	@SuppressWarnings("finally")
	public static List<String> ExecuteMySQLQueryReturnsList(Connection con, String sqlQuery)
	{		
		List<String> recordList = new ArrayList<String>();

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			int columns = rs.getMetaData().getColumnCount();

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getString(i).toString().trim();
					recordList.add(strRecord);
				}
			}		
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturns1DArray:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturns1DArray", e);
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Expection Handled By: ExecuteMySQLQueryReturnsList. " ,e);
		}
		finally
		{
			return recordList;
		}
	}


	//********** Executing MySQL Query and Returning 1 D Array containing the Only Column Name Of Result Set *********************************************//
	public static String [] ExecuteMySQLQueryReturnsOnlyColumnNames(Connection con, String sqlQuery) throws SQLException
	{		
		String []arrayRecords = null;

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Column Count: "+columns);

			arrayRecords = new String[columns];

			rs.beforeFirst();	// Setting the cursor at first line	
			while (rs.next())
			{
				for(int i=1;i<=columns;i++)
				{
					String strRecord = rs.getMetaData().getColumnLabel(i).toString();
					arrayRecords[i-1] = strRecord;
					//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Writing Rows BY METHOD - ExecuteMySQLQueryReturnsOnlyColumnNames: " +strRecord);
				}
			}		
			con.close();			
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsOnlyColumnNames:", e);
		}
		catch (NullPointerException e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : NullPointerExpection Handled By: ExecuteMySQLQueryReturnsOnlyColumnNames", e);
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Used MySQL query may have returned a NULL column in Result Set, Therefore use IFNULL with that particular column in query.",e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Expection Handled By: ExecuteMySQLQueryReturnsOnlyColumnNames. ", e);
		}

		return arrayRecords;
	}



	//********** Executing MySQL Query and Returning 2 D Array containing the Result Set with Column Name) *********************************************//
	public static String [][] ExecuteMySQLQueryReturnsArrayWithColumnName(Connection con, String sqlQuery) 
	{		
		String [][]arrayRecords = null;
		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Running this query: "+sqlQuery);

		try
		{
			ResultSet rs = ExecuteMySQLQueryReturnsResultSet(con, sqlQuery);
			/*
			//Un-comment this for debugging
			while (rs.next())
			{
				for(int i=1;i<=rs.getMetaData().getColumnCount();i++)
				{
					String strRecord = rs.getString(i).toString();
					System.out.print(" : "+strRecord);
				}
				logger.info();
			}
			 */
			rs.last();	// Setting the cursor at last
			int rows = rs.getRow();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : rows in result set: " +rows);

			int columns = rs.getMetaData().getColumnCount();
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" :  Column Count: "+columns);

			arrayRecords = new String[rows+1][columns];

			rs.beforeFirst();	// Setting the cursor at first line

			while (rs.next())
			{
				int currentRow = rs.getRow();

				for(int i=1;i<=columns;i++)
				{
					if(currentRow == 1)
					{
						String strRecord = rs.getMetaData().getColumnLabel(i).toString();
						arrayRecords[currentRow-1][i-1] = strRecord;
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Column Label: " +strRecord);

						String strRecord_1 = rs.getString(i).toString();
						arrayRecords[currentRow][i-1] = strRecord_1;
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Record: " +strRecord_1);
					}
					else
					{
						String strRecord = rs.getString(i).toString();
						arrayRecords[currentRow][i-1] = strRecord;
						//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : record in result set: " +strRecord);
					}
				}

			}					
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : MySQL Data Was Successfully Exported By Method ExecuteMySQLQueryReturnsArray. Rows: " +arrayRecords.length + ", Columns: "+arrayRecords[0].length);

		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : There was no record found in the Result Set, Therefore returning a NULL array by Method : ExecuteMySQLQueryReturnsArray:", e);
		}
		catch (Exception e) 
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception Handled by Method : ExecuteMySQLQueryReturnsArray: ",e);
		}

		/*
		// Only for debugging
		for(int i=0; i<arrayRecords.length; i++)
		{
			for(int j=0; j<arrayRecords[0].length; j++)
			{
				System.out.print(" : " +arrayRecords[i][j]);
			}
			logger.info();
		}
		 */

		return arrayRecords;
	}



	//******************** Get Current Date Time Stamp *************************************************//
	public static String DateTimeStamp(String dateStampFormat)
	{
		try
		{
			//Sample: MMddyyyy_hhmmss
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}
		catch(Exception n)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Please check the supplied date format. " , n);
			return null;
		}
	}



	//******************** Writing The Date Time Stamp *************************************************//
	public static String DateTimeStampWithMiliSecond()
	{
		try
		{
			String dateStampFormat = "MMddyyyy_hhmmss_ms";
			//logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Date Time Stamp Format will be:" +dateStampFormat);

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(dateStampFormat);
			String formattedDate = sdf.format(date);
			return formattedDate;
		}
		catch(Exception n)
		{
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Exception handled by method: DateTimeStampWithMiliSecond. ", n);
			return null;
		}
	}



	/** This method cleans all the process.
	 * 
	 * @param suiteStartTime
	 */
	public static void cleanProcesses()
	{
		String strProcess = "chromedriver";
		String strCommand;

		/** Close all the remaining instance of the browser. */
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
		{
			strCommand = "taskkill /F /IM " + strProcess + ".exe";
		}
		else
		{
			strCommand = "killall " + strProcess;
		}

		/** Running Command */
		logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Running command to close Chromedriver instance if any remaining:");
		logger.info(strCommand);
		try {
			Runtime.getRuntime().exec(strCommand);
		} catch (IOException e) {
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error while cleaning up.", e);
		}
	}

	/**
	 * This method  will invoke set chrome mobile emulation
	 * @param objectName 
	 * @param data 
	 * @return chrome@@mobile@@Apple Iph 
	 */
	public static WebDriver mobileEmulation(String deviceName, ProxyServer proxyServer){
		WebDriver driver=null;

		DesiredCapabilities capabilities=null;
		HashMap<String, String> mobileEmulation=null;
		HashMap<String, Object> chromeOptions=null;
		try{
			mobileEmulation=new HashMap<String, String>();
			mobileEmulation.put("deviceName", deviceName);
			chromeOptions=new HashMap<String, Object>();
			chromeOptions.put("mobileEmulation", mobileEmulation);
			capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
			if(!proxyServer.equals(null)){
				capabilities.setCapability(CapabilityType.PROXY, proxyServer.seleniumProxy());
			}
			if(System.getProperty("os.name").matches("^Windows.*")){
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/tpt/drivers/chromedriver.exe");
			}else{
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/tpt/drivers/chromedriver");
			}
			driver=new ChromeDriver(capabilities);

			//driver = new ChromeDriver(capabilities);
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Error while creating mobile browser emulation. ", e);
		}
		return driver;
	}


	/** this is used to emukate the mobile browser on desktop 
	 * 
	 * @param deviceType
	 * @return
	 */
	public static WebDriver invokeBrowser(String browserInfoJson, ProxyServer proxyServer){
		WebDriver driver =null;
		JSONObject jsonObject=null;
		String deviceType=null;
		try{
			jsonObject=new JSONObject(browserInfoJson);
			deviceType=jsonObject.get("browser").toString();
			if(deviceType.equalsIgnoreCase("Desktop") || deviceType.equalsIgnoreCase("")){

				driver=WebDriverSetUp("chrome",null);
			}else{
				driver=mobileEmulation(deviceType, proxyServer);
			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to invoke browser", e);
		}
		return driver;

	}

	public static String getDateInString(String data){
		String date=null;
		Date currentDate=null;
		String daysToAdd=null;
		try{
			daysToAdd=data.substring(data.indexOf("add")+3, data.indexOf("days")).trim();
			DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
			currentDate= new Date();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(currentDate);
			calendar.add(Calendar.DATE, Integer.parseInt(daysToAdd));
			Date nextdate=calendar.getTime();
			date=dateFormat.format(nextdate);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Get the new date as per the input data :"+date);
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get the date", e);
		}
		return date;
	}


	/**
	 * Method is used to generate list of hashmap from the json string 
	 * @return
	 */
	public static List<HashMap<String, String>> listOfMapFromJson(String json){

		List<HashMap<String, String>> listOfMap=new ArrayList<>();		

		try{
			/** get json array from de-serialized object */
			JSONArray jsonArray =new JSONArray(json);

			for(int i=0; i<jsonArray.length(); i++){

				HashMap<String, String> hashmap=new HashMap<>();

				/** get each josn obj of json arr and store it in a hashmap */
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Iterator<?> keys = jsonObject.keys();

				while (keys.hasNext()) {
					String key = (String)keys.next();
					String value =jsonObject.getString(key);
					hashmap.put(key, value);
				}

				listOfMap.add(hashmap);
			}
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Succesfully stored dataobject into list Of hashmap:");	
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get the list of hashmap", e);
		}
		return listOfMap;
	}


	/** return the de-serialized object
	 * 
	 * @param objectPath
	 * @return
	 */
	public static OrderDetails deserializeObject(String objectPath) {

		OrderDetails orderDetailObj = null;

		try{
			/** de-serailze object */
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(new File(objectPath)));
			orderDetailObj =(OrderDetails) ois.readObject();
			ois.close();

			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Succesfully deserializeObject:");	
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to deserializeObject", e);
		}
		return orderDetailObj;
	}


	/** return the de-serialized object
	 * 
	 * @param objectPath
	 * @return
	 */
	public static List<HashMap<String, String>> dataObjectToListOfMap(String objectPath) {

		List<HashMap<String, String>> listOfMap=new ArrayList<>();	
		try{
			/** de-serailze object */
			OrderDetails orderDetailObj=deserializeObject(objectPath);

			String jsonString=orderDetailObj.getOrderDetail();

			/** get list of map from json*/
			listOfMap=listOfMapFromJson(jsonString);
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Succesfully deserializeObject:");	
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to deserializeObject", e);
		}
		return listOfMap;
	}


	/**
	 * Method is to start proxy server to capture network traffic.
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ProxyServer startProxyServer(String browerInfoJson){

		ProxyServer proxyServer=null;
		String isProxy=null;
		try{
			isProxy=getProxyInfo(browerInfoJson);
			if(isProxy.equalsIgnoreCase("yes")){
				proxyServer=new ProxyServer();
				proxyServer.start();
				proxyServer.setCaptureHeaders(true);
				proxyServer.setCaptureContent(true);
				proxyServer.newHar("requests_"+new Date().getTime());

				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Port Started On:"+proxyServer.getPort());
			}else{
				logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : No need to set proxy server:");
			}
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to start proxy server", e);
		}
		return proxyServer;
	}

	/**
	 * Method is to get the device type from the testcaseobject.
	 * @return
	 */
	public static String getDeviceName(String browserInfoJson){
		String deviceType=null;
		JSONObject jsonObject=null;
		try{
			jsonObject=new JSONObject(browserInfoJson);
			deviceType=jsonObject.getString("browser");
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Device type is :"+deviceType);
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get device type", e);
		}
		return deviceType;
	}

	/**
	 * Method is to get the device type from the testcaseobject.
	 * @return
	 */
	public static String getProxyInfo(String browserInfoJson){
		String isProxy=null;
		JSONObject jsonObject=null;
		try{
			jsonObject=new JSONObject(browserInfoJson);
			isProxy=jsonObject.getString("proxy");
			logger.info(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Proxy is :"+isProxy);
		}catch(Exception e){
			logger.error(TestSuiteClass.UNIQ_EXECUTION_ID.get()+" : Unable to get Proxy info", e);
		}
		return isProxy;
	}

}


