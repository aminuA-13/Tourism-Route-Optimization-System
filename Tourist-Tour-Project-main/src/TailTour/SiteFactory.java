package TailTour;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class SiteFactory {

	 	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tailouredtourdb";
	    private static final String DB_USERNAME = "root";
	    private static final String DB_PASSWORD = "";
	    private static final String CSV_FILE_PATH = "C:\\Users\\1165575\\eclipse-workspace\\TailouredTouristToursProject\\src\\Dataset2.csv";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 try {
	            // Establish database connection
	            Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

	            // Create SQL insert statement
	            String insertQuery = "INSERT INTO sitedata (id, Names,latitude,longitude,address) VALUES (?, ?, ?, ?,?)";

	            // Create prepared statement
	            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

	            // Read and parse CSV file
	            @SuppressWarnings("deprecation")
				CSVParser csvParser = new CSVParser(new FileReader(CSV_FILE_PATH), CSVFormat.DEFAULT.withHeader());

	            for (CSVRecord csvRecord : csvParser) {
	                // Extract data from CSV fields
	            	String id = csvRecord.get("id");
	                String names = csvRecord.get("Names");
	                String lat = csvRecord.get("Latitude");
	                String lon = csvRecord.get("Longitude");
	                String address = csvRecord.get("Address");

	                // Set values in prepared statement
	                preparedStatement.setString(1, id);
	                preparedStatement.setString(2, names);
	                preparedStatement.setString(3, lat);
	                preparedStatement.setString(4, lon);
	                preparedStatement.setString(5, address);

	                // Execute the insert statement
	                preparedStatement.executeUpdate();
	            }

	            // Close resources
	            csvParser.close();
	            preparedStatement.close();
	            connection.close();

	            System.out.println("Data imported successfully from CSV to MySQL database.");
	        } catch (SQLException | IOException e) {
	            e.printStackTrace();
	        }
	    }
		

	}


