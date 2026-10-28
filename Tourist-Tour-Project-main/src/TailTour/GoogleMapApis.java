package TailTour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GoogleMapApis {
	
	
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tailouredtourdb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
	
 // Distance threshold to consider addresses as connected (in kilometers)
    private static double distanceThreshold = 20.0;
    private static double busCostPerKm = 0.1; // Cost per kilometer for bus
    private static double trainCostPerKm = 0.2; // Cost per kilometer for train
    private static double busSpeed = 60.0; // Average speed of bus in km/h
    private static double trainSpeed = 120.0; // Average speed of train in km/h
    private static int busCost, trainCost=0;
    private static double busTime, trainTime=0.0;
    private static double distance=0;
    
    
    public static void fetchDataFromTextFile() {
    	
    	String fileName = "C:\\Users\\1165575\\eclipse-workspace\\TailouredTouristToursProject\\src\\DATASETEST.txt"; // Specify the path and name of your text file

        try {
        	Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);
             BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" "); // Assuming the data is comma-separated

                // Prepare the SQL statement
                String sql = "INSERT INTO dataset (lat1, lon1, lat2,lon2,region,residentType) VALUES (?, ?, ?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(sql);

                // Set the values from the file into the prepared statement
                statement.setString(1, data[0]);
                statement.setString(2, data[1]);
                statement.setString(3, data[2]);
                statement.setString(4, data[3]);
                statement.setString(5, data[4]);
                statement.setString(6, data[5]);

                // Execute the statement
                statement.executeUpdate();
            }

            System.out.println("Data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	
    public static void fetchData() {
    	try {
            // Establish a database connection
    		Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

            // Create a SQL statement to fetch addresses, latitudes, and longitudes
            String sql = "SELECT lat1, lon1, lat2, lon2,region,residentType FROM dataset";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            //int rowCount = resultSet.getFetchSize();

            // Store the addresses, latitudes, and longitudes in separate arrays
            String[] addresses = new String[255];
            double[] latitudes = new double[255];
            double[] longitudes = new double[255];
            int index = 0;

            while (resultSet.next()) {
                String address = resultSet.getString("address");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");

                addresses[index] = address;
                latitudes[index] = latitude;
                longitudes[index] = longitude;
                index++;
            }

            // Calculate and compare distances between places
            for (int i = 0; i < index; i++) {
                for (int j = i + 1; j < index; j++) {
                    double distance = calculateDistance(latitudes[i], longitudes[i], latitudes[j], longitudes[j]);
                    estimateTransportationCostAndTime(distance);
                    
                    if (distance <= distanceThreshold) {
                    	
                    	
                        System.out.println(addresses[i] + " is connected to " + addresses[j] + " with distance of " + distance + "km " + " Bus cost = " + busCost + "euro " + " bus time = " + busTime + "hrs " + " Train cost = " + trainCost + "euros " + "train time = "+ trainTime + "hrs");
                    }
                    else {
                    	
                    	System.out.println(addresses[i] + " is connected to " + addresses[j] + " with distance of " + distance + "km " + " Bus cost = " + busCost + "euro " + " bus time = " + busTime + "hrs " + " Train cost = " + trainCost + "euros " + "train time = "+ trainTime + "hrs");
					}
                    
                 
                }
            }

            // Close the database resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    

    
 // Haversine formula to calculate distance between two points
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // Radius of the Earth in kilometers

        // Convert latitudes and longitudes to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the differences between latitudes and longitudes
        double latDiff = lat2Rad - lat1Rad;
        double lonDiff = lon2Rad - lon1Rad;

        // Apply the Haversine formula
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distans = earthRadius * c;
        distance =(int)distans;
        return distance;
    }
    	
    
    
    public static void estimateTransportationCostAndTime(double distance) {
      

        // Calculate transportation cost
        double busCos = distance * busCostPerKm;
        double trainCos = distance * trainCostPerKm;
        
        
        
        busCost =(int)busCos;
        trainCost = (int)trainCos;

        // Calculate time taken
        double busTim = distance / busSpeed;
        double trainTim = distance / trainSpeed;
        
        busTime =(int)busTim;
        trainTime = (int)trainTim;

    }

	
	

	public static void main(String[] args) {

		
		//fetchData();
		fetchDataFromTextFile();
		
    }


}
