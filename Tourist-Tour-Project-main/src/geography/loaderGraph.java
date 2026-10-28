package geography;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import TailTour.MapGraph;
import TailTour.MapNode;


public class loaderGraph {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tailouredtourdb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
 // Connect to the database and retrieve monument geographic points
    private static List<GeographicPoint> monumentPoints = new ArrayList<>();
    public static void loadRoadMapFromDatabase(MapGraph map) {
    	
    	try {
            // Establish a database connection
    		Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

            // Create a SQL statement to fetch addresses, latitudes, and longitudes
            String sql = "SELECT lat1, lon1, lat2, lon2,region,residentType FROM dataset";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            //int rowCount = resultSet.getFetchSize();

            // Store the addresses, latitudes, and longitudes in separate arrays
            
            
            while (resultSet.next()) {
                String lat1 = resultSet.getString("lat1");
                String lon1 = resultSet.getString("lon1");
                String lat2 = resultSet.getString("lat2");
                String lon2 = resultSet.getString("lon2");
                String regionn = resultSet.getString("region");
                String residType = resultSet.getString("residentType");
               
                double latt1 = Double.parseDouble(lat1);
                double lonn1 = Double.parseDouble(lon1);
                double latt2 = Double.parseDouble(lat2);
                double lonn2 = Double.parseDouble(lon2);
                
                double dist = getDist(latt1,lonn1,latt2,lonn2);
                
               
    	       
                GeographicPoint p1 = new GeographicPoint(latt1, lonn1);
                GeographicPoint p2 = new GeographicPoint(latt2, lonn2);

                map.addVertex(p1);
                map.addVertex(p2);
                map.addEdge(p1, p2, regionn, residType,dist);
            }
            
              
            

           

            // Close the database resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

		
    	
		
	}
    
public static void siteFetch(MapGraph map) {
	
	
    
    //Map<GeographicPoint, MapNode> monumentPoints = new HashMap<GeographicPoint, MapNode>();
    	try {
            // Establish a database connection
    		Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

            // Create a SQL statement to fetch addresses, latitudes, and longitudes
            String sql = "SELECT * FROM dataset WHERE residentType='Monument'";
            PreparedStatement statement = connection.prepareStatement(sql);

                ResultSet resultSet = statement.executeQuery(sql);
            
            //int rowCount = resultSet.getFetchSize();

            // Store the addresses, latitudes, and longitudes in separate arrays
            
            
            while (resultSet.next()) {
                String lat1 = resultSet.getString("lat1");
                String lon1 = resultSet.getString("lon1");
               
               
               
                double latt1 = Double.parseDouble(lat1);
                double lonn1 = Double.parseDouble(lon1);
               
                
                GeographicPoint monumentPoint = new GeographicPoint(latt1, lonn1);
                monumentPoints.add(monumentPoint);
                
                //double dist = getDist(latt1,lonn1,latt2,lonn2);
           
            }
         
            // Close the database resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	
    	
	       }

		public static GeographicPoint closeMonument() {
			// User's dwelling geographic point
	        GeographicPoint dwellingPoint = new GeographicPoint(32.8694205, -117.2205748); // Example coordinates for Eiffel Tower
	    	// Find the closest monument to the dwelling
	        GeographicPoint closestMonument = findClosestMonument(monumentPoints, dwellingPoint);

	        if (closestMonument != null) {
	            System.out.println("Closest Monument: Latitude - " + closestMonument.getLatitude() +
	                    ", Longitude - " + closestMonument.getLongitude());

	            // Calculate and display the shortest path to the closest monument using A* algorithm
	          
	           
	            
	            
	            
	            // ...
	        } else {
	            System.out.println("No monuments found in the database.");
	        }
	    	return closestMonument;
	    	
			
		}

			//Method to find the closest monument to the user's dwelling
			private static GeographicPoint findClosestMonument(List<GeographicPoint> monumentPoints, GeographicPoint dwellingPoint) {
			    double minDistance = Double.MAX_VALUE;
			    GeographicPoint closestMonument = null;
			
			    for (GeographicPoint monumentPoint : monumentPoints) {
			        double distance = calculateDistance(monumentPoint, dwellingPoint);
			        if (distance < minDistance) {
			            minDistance = distance;
			            closestMonument = monumentPoint;
			        }
			    }
			    
			    
			   
			
			    return closestMonument;
			}

			
			// Method to calculate the distance between two geographic points
		    private static double calculateDistance(GeographicPoint point1, GeographicPoint point2) {
		        double lat1 = Math.toRadians(point1.getLatitude());
		        double lon1 = Math.toRadians(point1.getLongitude());
		        double lat2 = Math.toRadians(point2.getLatitude());
		        double lon2 = Math.toRadians(point2.getLongitude());

		        double earthRadius = 6371.0; // Earth's radius in kilometers

		        double dlon = lon2 - lon1;
		        double dlat = lat2 - lat1;

		        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
		        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		        return earthRadius * c;
		    }

    
    private static double getDist(double lat1, double lon1, double lat2, double lon2)
    {
    	int R = 6373; // radius of the earth in kilometres
    	double lat1rad = Math.toRadians(lat1);
    	double lat2rad = Math.toRadians(lat2);
    	double deltaLat = Math.toRadians(lat2-lat1);
    	double deltaLon = Math.toRadians(lon2-lon1);

    	double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
    	        Math.cos(lat1rad) * Math.cos(lat2rad) *
    	        Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    	double d = R * c;
    	return d;
    }
	public static void main(String[] args) {
		 MapGraph map = new MapGraph();
	        loadRoadMapFromDatabase(map);
	        
	}

}
