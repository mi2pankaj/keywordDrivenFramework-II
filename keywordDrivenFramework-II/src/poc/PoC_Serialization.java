package poc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lenskart.tests.OrderDetails;


public class PoC_Serialization {

	public static void main(String[] args) throws IOException, ClassNotFoundException, JSONException {
		boolean fileExists=false;
		String data ="{\"age\": 130}";
		JSONArray jsonArray;
		ObjectOutputStream oos;
		FileOutputStream fos;
		JSONObject jsonObj;
		String path = "/Users/rishi/Documents/myWorkSpace/Lenskart_Automation/conf/qaconfsss";
		File fileObj = new File(path);
		fileExists=fileObj.exists();
		if(fileExists){
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(path)));
			OrderDetails  orderObj = (OrderDetails) in.readObject();
			String fileData=orderObj.getOrderDetail();
			jsonObj=new JSONObject(data);
			jsonArray=new JSONArray(fileData);
			jsonArray.put(jsonObj);
			orderObj.setOrderDetail(jsonArray.toString());
			fos= new FileOutputStream(path);
			oos= new ObjectOutputStream(fos);
			oos.writeObject(orderObj);
			oos.flush();
			oos.close();
			in.close();
		}else{
			OrderDetails orderObj= new OrderDetails();
			jsonObj=new JSONObject("{\"age\": 110}");
			
			jsonArray= new JSONArray();
			
			jsonArray.put(jsonObj);
			orderObj.setOrderDetail(jsonArray.toString());
			fos= new FileOutputStream(path);
			oos= new ObjectOutputStream(fos);
			oos.writeObject(orderObj);
			oos.flush();
			oos.close();
		}

//		OrderDetails orderObj= new OrderDetails();
//
//		HashMap<String, String> map = new HashMap<>();
//		map.put("1", "2");
		
		
//		orderObj.setOrderDetail(map);
//		FileOutputStream fos= new FileOutputStream(path);
//		ObjectOutputStream oos= new ObjectOutputStream(fos);
//		oos.writeObject(orderObj);
//		oos.flush();
//		oos.close();

		System.out.println("Done");


		//de-serialize
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(path)));
		OrderDetails  obj = (OrderDetails) in.readObject();
		
		in.close();
		System.out.println(obj.getOrderDetail());
	}

}
