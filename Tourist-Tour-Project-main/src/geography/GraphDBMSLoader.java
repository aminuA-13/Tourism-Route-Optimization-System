package geography;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import TailTour.MapGraph;


//Victor Habila

public class GraphDBMSLoader {
	
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tailouredtourdb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "";
    
    static GeographicPoint p1;
    static GeographicPoint p2;
    private static String regionn;
    private static String residType;
    private static int index=0;
	private static double dist=0.0;
	
	public static void loadRoadMap(MapGraph map)
	{
		loadRoadMap(map, null, null);
	}

	
	public static void loadRoadMap(MapGraph map,  
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments, 
			Set<GeographicPoint> intersectionsToLoad)
	{
		Collection<GeographicPoint> nodes = new HashSet<GeographicPoint>();
        HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap = 
        		buildPointMapOneWay();
		
        // Add the nodes to the graph
		List<GeographicPoint> intersections = findIntersections(pointMap);
		for (GeographicPoint pt : intersections) {
			map.addVertex(pt);
			if (intersectionsToLoad != null) {
				intersectionsToLoad.add(pt);
			}
			nodes.add(pt);
		}
		
		
		addEdgesAndSegments(nodes, pointMap, map, segments);
	}
	
	// Build the map from points to lists of lists of lines.
		// The map returned is indexed by a GeographicPoint.  The values
		// are lists of length two where each entry in the list is a list.
		// The first list stores the outgoing roads while the second 
		// stores the outgoing roads.
		private static HashMap<GeographicPoint, List<LinkedList<RoadLineInfo>>>
		buildPointMapOneWay()
		{
			
	        HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap = 
	        		new HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>>();
			
	           
	            // Read the lines out of the file and put them in a HashMap by points
	            while (index > 0) {
	            	RoadLineInfo line = splitInputString();
	            	addToPointsMapOneWay(line, pointMap);
	            }
	           
	       
			
			return pointMap;
		}
		
		// Add the next line read from the file to the points map.
		private static void 
		addToPointsMapOneWay(RoadLineInfo line,
							HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> map)
		{
			List<LinkedList<RoadLineInfo>> pt1Infos = map.get(line.point1);
			if (pt1Infos == null) {
				pt1Infos = new ArrayList<LinkedList<RoadLineInfo>>();
				pt1Infos.add(new LinkedList<RoadLineInfo>());
				pt1Infos.add(new LinkedList<RoadLineInfo>());
				map.put(line.point1, pt1Infos);
			}
			List<RoadLineInfo> outgoing = pt1Infos.get(0);
			outgoing.add(line);
			
			List<LinkedList<RoadLineInfo>> pt2Infos = map.get(line.point2);
			if (pt2Infos == null) {
				pt2Infos = new ArrayList<LinkedList<RoadLineInfo>>();
				pt2Infos.add(new LinkedList<RoadLineInfo>());
				pt2Infos.add(new LinkedList<RoadLineInfo>());
				map.put(line.point2, pt2Infos);
			}
			List<RoadLineInfo> incoming = pt2Infos.get(1);
			incoming.add(line);
			
		}
		
	
	
	// Find all the intersections.  Intersections are either dead ends 
		// (1 road in and 1 road out, which are the reverse of each other)
		// or intersections between two different roads, or where three
		// or more segments of the same road meet.
		private static List<GeographicPoint> 
		findIntersections(HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap) {
			// Now find the intersections.  These are roads that do not have
			// Exactly 1 or 2 roads coming in and out, where the roads in
			// match the roads out.
			List<GeographicPoint> intersections = new LinkedList<GeographicPoint>();
			for (GeographicPoint pt : pointMap.keySet()) {
				List<LinkedList<RoadLineInfo>> roadsInAndOut = pointMap.get(pt);
				LinkedList<RoadLineInfo> roadsOut = roadsInAndOut.get(0);
				LinkedList<RoadLineInfo> roadsIn = roadsInAndOut.get(1);
				
				boolean isNode = true;
				
				if (roadsIn.size() == 1 && roadsOut.size() == 1) {
					// If these are the reverse of each other, then this is
					// and intersection (dead end)
					if (!(roadsIn.get(0).point1.equals(roadsOut.get(0).point2) &&
							roadsIn.get(0).point2.equals(roadsOut.get(0).point1))
							&& roadsIn.get(0).region.equals(roadsOut.get(0).region)) {
						isNode = false;
					}
				}
				if (roadsIn.size() == 2 && roadsOut.size() == 2) {
					// If all the road segments have the same name, 
					// And there are two pairs of reversed nodes, then 
					// this is not an intersection because the roads pass
					// through.
				
					String name = roadsIn.get(0).region;
					boolean sameName = true;
					for (RoadLineInfo info : roadsIn) {
						if (!info.region.equals(name)) {
							sameName = false;
						}
					}
					for (RoadLineInfo info : roadsOut) {
						if (!info.region.equals(name)) {
							sameName = false;
						}
					}
					
					RoadLineInfo in1 = roadsIn.get(0);
					RoadLineInfo in2 = roadsIn.get(1);
					RoadLineInfo out1 = roadsOut.get(0);
					RoadLineInfo out2 = roadsOut.get(1);
			
					boolean passThrough = false;
					if ((in1.isReverse(out1) && in2.isReverse(out2)) ||
							(in1.isReverse(out2) && in2.isReverse(out1))) {
						
						passThrough = true;
					} 
					
					if (sameName && passThrough) {
						isNode = false;
					} 

				} 
				if (isNode) {
					intersections.add(pt);
				}
			}
			return intersections;
		}
		
		// Split the input string into the line information
		private static RoadLineInfo splitInputString()
		{	
			
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
	                regionn = resultSet.getString("region");
	                residType = resultSet.getString("residentType");
	               
	                double latt1 = Double.parseDouble(lat1);
	                double lonn1 = Double.parseDouble(lon1);
	                double latt2 = Double.parseDouble(lat2);
	                double lonn2 = Double.parseDouble(lon2);
	                
	                dist = getDist(latt1,lonn1,latt2,lonn2);
	                
	               
	    	       
	    	         p1 = new GeographicPoint(latt1, lonn1);
	    	         p2 = new GeographicPoint(latt2, lonn2);

	    	         index++;
	              
	            }

	           

	            // Close the database resources
	            resultSet.close();
	            statement.close();
	            connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

			return new RoadLineInfo(p1, p2, regionn, residType);
	    	
			
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

//Once you have built the pointMap and added the Nodes, 
	// add the edges and build the road segments if the segments
	// map is not null.
	private static void addEdgesAndSegments(Collection<GeographicPoint> nodes, 
			HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap,
			MapGraph map, 
			HashMap<GeographicPoint,HashSet<RoadSegment>> segments)
	{
	
		// Now we need to add the edges
		// This is the tricky part
		for (GeographicPoint pt : nodes) {
			// Trace the node to its next node, building up the points 
			// on the edge as you go.
			List<LinkedList<RoadLineInfo>> inAndOut = pointMap.get(pt);
			LinkedList<RoadLineInfo> outgoing = inAndOut.get(0);
			for (RoadLineInfo info : outgoing) {
				HashSet<GeographicPoint> used = new HashSet<GeographicPoint>();
				used.add(pt);
				
				List<GeographicPoint> pointsOnEdge = 
						findPointsOnEdge(pointMap, info, nodes);
				GeographicPoint end = pointsOnEdge.remove(pointsOnEdge.size()-1);
				double dist = getRoadLength(pt, end, pointsOnEdge);
				map.addEdge(pt, end, info.region, info.residentType, dist);

				// If the segments variable is not null, then we 
				// save the road geometry
				if (segments != null) {
					// Now create road Segments for each edge
					HashSet<RoadSegment> segs = segments.get(pt);
					if (segs == null) {
						segs = new HashSet<RoadSegment>();
						segments.put(pt,segs);
					}
					RoadSegment seg = new RoadSegment(pt, end, pointsOnEdge, 
							info.region, info.residentType, dist);
					segs.add(seg);
					segs = segments.get(end);
					if (segs == null) {
						segs = new HashSet<RoadSegment>();
						segments.put(end,segs);
					}
					segs.add(seg);
				}
			}
		}
	}
	
	
	
	// Calculate the length of this road segment taking into account all of the 
	// intermediate geographic points.
	private static double getRoadLength(GeographicPoint start, GeographicPoint end,
			List<GeographicPoint> path)
	{
		double dist = 0.0;
		GeographicPoint curr = start;
		for (GeographicPoint next : path) {
			dist += curr.distance(next);
			curr = next;
		}
		dist += curr.distance(end);
		return dist;
	}
	
	private static List<GeographicPoint>
	findPointsOnEdge(HashMap<GeographicPoint,List<LinkedList<RoadLineInfo>>> pointMap,
		RoadLineInfo info, Collection<GeographicPoint> nodes) 
	{
		List<GeographicPoint> toReturn = new LinkedList<GeographicPoint>();
		GeographicPoint pt = info.point1;
		GeographicPoint end = info.point2;
		List<LinkedList<RoadLineInfo>> nextInAndOut = pointMap.get(end);
		LinkedList<RoadLineInfo> nextLines = nextInAndOut.get(0);
		while (!nodes.contains(end)) {
			toReturn.add(end);
			RoadLineInfo nextInfo = nextLines.get(0);
			if (nextLines.size() == 2) {
				if (nextInfo.point2.equals(pt)) {
					nextInfo = nextLines.get(1);
				}
			}
			else if (nextLines.size() != 1) {
				System.out.println("Something went wrong building edges");
			}
			pt = end;
			end = nextInfo.point2;
			nextInAndOut = pointMap.get(end);
			nextLines = nextInAndOut.get(0);
		}
		toReturn.add(end);
		
		return toReturn;
	}
	
	
	// A class to store information about the lines in the road files.
	static class RoadLineInfo
	{
		GeographicPoint point1;
		GeographicPoint point2;
		
		String region;
		String residentType;
		
		
		/** Create a new RoadLineInfo object to store information about the line 
		 * read from the file
		 * @param p1 One of the points
		 * @param p2 The other point
		 * @param roadName The name of the road
		 * @param roadType The type of the road
		 */
		RoadLineInfo(GeographicPoint p1, GeographicPoint p2, String region, String residentType) 
		{
			point1 = p1;
			point2 = p2;
			this.region = region;
			this.residentType = residentType;
			
		}
		
		
		/** Get the other point from this roadLineInfo */
		public GeographicPoint getOtherPoint(GeographicPoint pt)
		{
			if (pt == null) throw new IllegalArgumentException();
			if (pt.equals(point1)) {
				return point2;
			}
			else if (pt.equals(point2)) {
				return point1;
			}
			else throw new IllegalArgumentException();
		}
		
		/** Two RoadLineInfo objects are considered equal if they have the same
		 * two points and the same roadName and roadType.
		 */
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof RoadLineInfo))
			{
				return false;
			}
			RoadLineInfo info = (RoadLineInfo)o;
			return info.point1.equals(this.point1) && info.point2.equals(this.point2)  &&
					info.residentType.equals(this.residentType) && info.region.equals(this.region);
					
		}
		
		/** Calculate the hashCode based on the hashCodes of the two points
		 * @return The hashcode for this object.
		 */
		public int hashCode()
		{
			return point1.hashCode() + point2.hashCode();
			
		}
		
		public static void main(String[] args)
		{
			 splitInputString();

		}
		
		/** Returns whether these segments are part of the same road in terms of
		 * road name and road type.
		 * @param info The RoadLineInfo to compare against.
		 * @return true if these represent the same road, false otherwise.
		 */
		public boolean sameRoad(RoadLineInfo info)
		{
			return info.region.equals(this.region) && info.residentType.equals(this.residentType);
		}
		
		/** Return a copy of this LineInfo in the other direction */
		public RoadLineInfo getReverseCopy()
		{
			return new RoadLineInfo(this.point2, this.point1, this.region, this.residentType);
		}
		
		/** Return true if this road is the same segment as other, but in reverse
		 *   Otherwise return false.
		 */
		public boolean isReverse(RoadLineInfo other)
		{
			return this.point1.equals(other.point2) && this.point2.equals(other.point1) &&
					this.region.equals(other.region) && this.residentType.equals(other.residentType);
		}
		
		/** Return the string representation of this LineInfo. */
		public String toString()
		{
			return this.point1 + " " + this.point2 + " " + this.region + " " + this.residentType;
			
		}
		
	}

}
