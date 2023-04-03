package jdbc;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import Enum.Area;
import Enum.Instruction;
import Enum.ProductCategory;
import Enum.ProductStatus;
import Server.EkrutServer;
import entities.AreaManager;
import entities.Facility;
import entities.Product;
import entities.ProductInFacility;
import entities.ServerMessage;
import entities.User;


/**
 * The DataBaseAreaManagerController class appears to be responsible for retrieving information about an Area Manager from a database, 
 * specifically their details and the facilities in their area. It does this by using a provided username to query the database for the area the user
 *  is responsible for, and then it uses this area to query for all facilities in that area. It also generates notifications for the area manager and 
 *  package all the information into a ServerMessage object, which is returned to the client.
 */
public class DataBaseAreaManagerController {

	public static ServerMessage getAreaManagerDetails(Hashtable<String, Object> data) {
		User user = (User) data.get("user");
		String username = user.getUserName();
		ServerMessage sm = new ServerMessage();
		// setting all the information of the areaManager we got from the Client
		AreaManager areaManager = new AreaManager();
		areaManager.setUserName(user.getUserName());
		areaManager.setFirstName(user.getFirstName());
		areaManager.setLastName(user.getLastName());
		areaManager.setId(user.getId());
		areaManager.setEmail(user.getEmail());
		areaManager.setPassword(user.getPassword());
		areaManager.setUserName(user.getUserName());
		areaManager.setRole(user.getRole());
		areaManager.setTelephone(user.getTelephone());
		areaManager.setLoggedIn(user.getLoggedIn());
		Statement stmt;
		Area area;

		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs;
			rs = quarey_ForArea(stmt, username); // First Query -> Search For Area Name To Area Manager
			// query return empty ResultSet--> the username do not found in DB.
			if (!rs.next())
				return new ServerMessage(Instruction.User_not_found);
			area = getEnumUserArea(rs.getString(1));
			areaManager.setArea(area);
			// Set Area By Enum - From First Query
			Hashtable<String, Object> areaManagerDetails = new Hashtable<String, Object>();
			areaManagerDetails.put("Area", area);
			sm = getFacilitiesInArea(areaManagerDetails);
			ArrayList<Facility> facilitiesList = (ArrayList<Facility>) sm.getData().get("facilitiesInArea");
			areaManager.setFacilities(facilitiesList);
			Hashtable<String, Object> areaManagerTable = new Hashtable<String, Object>();
			areaManagerTable.put("AreaManager", areaManager);
			areaManagerTable.put("Notification", generate_AreaManager_Notification(areaManager));
			return new ServerMessage(Instruction.Area_Manager_Details_Found, areaManagerTable);

		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query Get_Area_Manager_Details failed");
			return null;
		}
	}


	/**
	 * @return all facilities in area with all the details of the facilities .This method returns all facilities in area with all the details of the . 
	 */
	public static ServerMessage getFacilitiesInArea(Hashtable<String, Object> data) {
		Statement stmt;
		Area area = (Area) data.get("Area");
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs;
			rs = stmt.executeQuery(String.format("SELECT * FROM facilities WHERE facilityArea = '%s';", area));
			ArrayList<Facility> facilitiesList = new ArrayList<Facility>();
			while (rs.next()) {
				Facility facility = new Facility();
				facility.setFacilityId(rs.getString(1));
				facility.setFacilityArea(area);
				ArrayList<ProductInFacility> ProductInFacility = getFacilityProductsList(facility.getFacilityId());
				facility.setProductsInFacility(ProductInFacility);
				facilitiesList.add(facility);
			}

			Hashtable<String, Object> facilitiesTable = new Hashtable<String, Object>();
			facilitiesTable.put("facilitiesInArea", facilitiesList);
			return new ServerMessage(Instruction.Facilities_In_Area_List, facilitiesTable);
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query getFacilitiesInArea failed");
			return null;
		} 
	}


	/**
	 * This method returns arrayList of products in facility with the product in
	 * facility details.
	 * @param facilityId
	 * @return
	 */
	private static ArrayList<ProductInFacility> getFacilityProductsList(String facilityId) {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			ResultSet rs = stmt.executeQuery(
					String.format("SELECT * FROM productinfacility WHERE facilityId = '%s';", facilityId));
			ArrayList<ProductInFacility> facilitiesList = new ArrayList<ProductInFacility>();
			while (rs.next()) {
				ProductInFacility productInFacility = new ProductInFacility();
				productInFacility.setProductCode(rs.getInt("productCode"));
				productInFacility.setQuantity(rs.getInt("quantity"));
				productInFacility.setProductStatus(getEnumProductStatus(rs.getString("productStatus")));
				productInFacility.setTresholdLevel(rs.getInt("tresholdlevel"));
				productInFacility.setPaseedThreshold((rs.getInt("paseedThreshold")));
				Product p = getProductDtails(productInFacility.getProductCode());
				productInFacility.setCategory(p.getCategory());
				productInFacility.setPhoto(p.getPhoto());
				productInFacility.setProductPrice(p.getProductPrice());
				productInFacility.setProtductName(p.getProtductName());

				facilitiesList.add(productInFacility);
			}
			rs.close();
			return facilitiesList;
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query getFacilityProductsList failed");
			return null;
		} 
	}


	/**
	 * @param productCode
	 * @return product details for product in facility.
	 */
	private static Product getProductDtails(int productCode) {
		Statement stmt;
		try {
			stmt = DataBaseController.getConn().createStatement();
			String string_productCode = String.valueOf(productCode);
			ResultSet rs = stmt
					.executeQuery(String.format("SELECT * FROM product WHERE productCode = '%s';", string_productCode));
			if (!rs.next())
				return null;
			Product p = new Product();
			p.setProtductName(rs.getString("productName"));
			p.setProductPrice(rs.getFloat("productPrice"));
			Blob blob =rs.getBlob("photo");
			p.setPhoto (blob.getBytes(1, (int)blob.length()));
			p.setCategory(getEnumCategory(rs.getString("category")));
			
			return p;
		} catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query getProductDtails failed");
			return null;
		}
	}

	/**	
	 * @param stmt to send query
	 * @param username of area manager
	 * @return ResultSet of area for area manager
	 * @throws SQLException
	 */
	private static ResultSet quarey_ForArea(Statement stmt, String username) throws SQLException {
		ResultSet rs = stmt.executeQuery(String.format("SELECT area FROM user WHERE userName = \"%s\";", username));
		return rs;
	}


	/**
	 * @param stmt to send query
	 * @param area to search facilities
	 * @return ResultSet Of Facilities By Area Of Area Manager
	 * @throws SQLException
	 */
	private static ResultSet quarey_ForCreate_Facility_List_By_Area(Statement stmt, Area area) throws SQLException {
		ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM facilities WHERE facilityArea = \"%s\";", area));
		return rs;
	}

	/**
	 * @param stmt to send query
	 * @param facility in area manager facilities list
	 * @return ResultSet  For Each Facility Search Product In Facility
	 * @throws SQLException
	 */
	private static ResultSet quarey_For_Product_In_Facility(Statement stmt, Facility facility) throws SQLException {
		ResultSet rs = stmt.executeQuery(
				String.format("SELECT * FROM productinfacility WHERE facilityId = \"%s\";", facility.getFacilityId()));
		return rs;
	}

	/**
	 * @param stmt to send query
	 * @param productCode 
	 * @return ResultSet Each Product In Facility set details from prodcut table
	 * @throws SQLException
	 */
	private static ResultSet query_For_Set_Details_Each_Product_In_Facility(Statement stmt, int productCode)
			throws SQLException {
		String string_productCode = String.valueOf(productCode);
		ResultSet rs = stmt
				.executeQuery(String.format("SELECT * FROM product WHERE productCode = \"%s\";", string_productCode));
		return rs;
	}

	/**
	 * @param rs
	 * @return Facility Array List
	 * @throws SQLException
	 */
	private static ArrayList<Facility> createArrayList(ResultSet rs) throws SQLException {
		ArrayList<Facility> facilityArrayList = new ArrayList<Facility>();
		Area area;
		Facility facility;
		while (rs.next()) {
			// Retrieve data from the current row using methods such as getInt, getString,
			// etc.
			String facilityId = rs.getString(1);
			String facilityArea = rs.getString(2);
			area = getEnumUserArea(facilityArea);
			facility = new Facility(facilityId, area);
			facilityArrayList.add(facility);
		}
		return facilityArrayList;
	}

	/**
	 * @param rs
	 * @return Product In Faiclity ArrayList
	 * @throws SQLException
	 */
	private static ArrayList<ProductInFacility> create_Product_In_Facilities_Array(ResultSet rs, Statement stmt)
			throws SQLException {
		ArrayList<ProductInFacility> array_productInFacility = new ArrayList<ProductInFacility>();
		ProductInFacility productInFacility;
		ProductStatus productStatus;
		ProductCategory productCategory = ProductCategory.Drinks;
		while (rs.next()) {
			int productCode = rs.getInt(2);
			int quantity = rs.getInt(3);
			String tempproductStatus = rs.getString(4);
			int tresholdLevel = rs.getInt(5);
			productStatus = getEnumProductStatus(tempproductStatus);

			productInFacility = new ProductInFacility(productCode, "", 0 , new byte[0] , productCategory, quantity, productStatus,
					tresholdLevel);
			array_productInFacility.add(productInFacility);
		}
		return array_productInFacility;

	}
	// set the Threshol dLevel
	public static ServerMessage setThresholdLevel(Hashtable<String, Object> data) {
		String facilityName= (String) data.get("facilityName");
		int ProductCode= (int) data.get("ProductCode");
		String ThresholdLevel= (String) data.get("ThresholdLevel");
		int ThresholdLevel_Num = Integer.parseInt(ThresholdLevel);
		PreparedStatement stmt;
		try {
			stmt = DataBaseController.getConn().prepareStatement("UPDATE productinfacility SET tresholdlevel = ? WHERE facilityId = ? AND productCode = ?");
			stmt.setInt(1,ThresholdLevel_Num); 
			stmt.setString(2,facilityName);
			stmt.setInt(3,ProductCode);
		    stmt.executeUpdate();
		    
			return new ServerMessage(Instruction.Nothing_To_Get);

		}catch (SQLException e) {
			EkrutServer.getServer().getServerGui().printToConsole("The query get user details failed");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 *  Run and vertify we are using one of the Areas defined
	 * @param userArea
	 * @return Area from enum
	 */
	private static Area getEnumUserArea(String userArea) {
		for (Area i : Area.values()) {
			if (userArea.equals(i.name())) {
				return i;
			}
		}
		return null;
	}


	/**
	 * Run and vertify we are using one of the Areas defined
	 * @param productsStatus
	 * @return ProductStatus from enum
	 */
	private static ProductStatus getEnumProductStatus(String productsStatus) {
		for (ProductStatus i : ProductStatus.values()) {
			if (productsStatus.equals(i.name())) {
				return i;
			}
		}
		return null;
	}

	/**
	 * 	 Run and verify we are using one of the ProductCategory defined
	 * @param category
	 * @return ProductCategory from enum
	 */
	private static ProductCategory getEnumCategory(String category) {
		if (category == null) {
			return null;
		}

		for (ProductCategory i : ProductCategory.values()) {
			if (category.equals(i.name())) {
				return i;
			}
		}
		return null;
	}

	/*-----Generate notiftication*/
	/**
	 * @param areaManager
	 * @return simulation of nofitication and email and sms  send
	 */
	public static String generate_AreaManager_Notification(AreaManager areaManager) {
		String displayMessegeMissingInventory = "";
		String finalString = "";
		String FinalMessege = "Hey " + areaManager.getFirstName() + " " + areaManager.getLastName()
				+ "\nEKRut System Sent:\nEmail To : " + areaManager.getEmail() + "\nSMS to "
				+ areaManager.getTelephone()
				+ "\nabout some products in your area that their inventory passed threshold level :\n";
		boolean flag = false;
		ArrayList<Facility> facilitiesList = areaManager.getFacilities();
		for (Facility facility : facilitiesList) {
			// NO PRODUCT IN FACILITIES
			if (!facility.getProductsInFacility().isEmpty()) {
				for (ProductInFacility productInFacility : facility.getProductsInFacility()) {
					if (productInFacility.getQuantity() < productInFacility.getTresholdLevel()) {
						if (flag == false) {
							displayMessegeMissingInventory += "\nIn Facilty " + facility.getFacilityId() + ":  ";
							flag = true;
						}
						displayMessegeMissingInventory += productInFacility.getProtductName() + ", ";
					}
				}
				if (displayMessegeMissingInventory.equals("")) {
					finalString = displayMessegeMissingInventory;
				} else {
					StringBuilder sb = new StringBuilder(displayMessegeMissingInventory);
					sb.deleteCharAt(sb.length() - 1);
					sb.deleteCharAt(sb.length() - 1);
					displayMessegeMissingInventory = sb.toString();
					displayMessegeMissingInventory += "\n";
					flag = false;
					finalString = FinalMessege + displayMessegeMissingInventory;
				}
			}
		}
		return finalString;
	}
}
