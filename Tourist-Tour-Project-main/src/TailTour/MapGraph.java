package TailTour;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import Constraint.Sites;
import Constraint.constraint;
import geography.GeographicPoint;
import geography.GraphDBMSLoader;
import geography.loaderGraph;




public class MapGraph {
	
	private Map<GeographicPoint, MapNode> intersections;
	private int numVertices;
	private int numEdges;
	private static double busCostPerKm = 4.5; // Cost per kilometer for bus
    private static double trainCostPerKm = 2.5; // Cost per kilometer for train
    private static double busSpeed = 60.0; // Average speed of bus in km/h
    private static double trainSpeed = 120.0; // Average speed of train in km/h
    private static int busCost, trainCost=0;
    private static double busTime, trainTime=0.0;
    private static String siteType;
    private static int days;
    private static double totalBudget=0.0;
	
	public MapGraph()
	{
		
		intersections = new HashMap<GeographicPoint, MapNode>();
		numVertices = 0;
		numEdges = 0;
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		return numVertices;
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		
		
		return intersections.keySet();
	}
	
	public int getNumEdges()
	{
		
		return numEdges;
	}
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{
		
		// return false if entry was not (the node
		// was already in the graph, or the parameter is null
		if (location == null || intersections.containsKey(location)) {
			return false;		
		}
		
		// Create a MapNode from a location and add it to the HashMap
		MapNode intersection = new MapNode(location);
		intersections.put(location, intersection);
		// Increment numVertices count
		//System.out.println("intersection: " + intersection + " is added!");
		numVertices++;
		return true;
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String region,
			String residentType, double distance) throws IllegalArgumentException {

		
		// Throwing Exceptions
		// Check for null arguments and -ve length
		
		if (from == null || to == null || region == null || residentType == null || distance < 0) {
			throw new IllegalArgumentException();
		}
		
		// check if vertices not been already added
		if (!intersections.containsKey(from) || !intersections.containsKey(to)) {
			throw new IllegalArgumentException();
		}
		
		// Create a MapEdge
		MapEdge road = new MapEdge(from, to, region, residentType, distance);
		
		// Add neighbors into neighbors list 
		intersections.get(from).getNeighbors().add(intersections.get(to));
		// Add MapEdge to the edge-list of start MapNode
		intersections.get(from).getRoadList().add(road);		
		
		// Increment numEdges count
		numEdges++;		
	}
	
	
	/** Print MapGraph Attributes
	 * @return s - information of MapGraph each intersection with
	 * list of all it's edge info	  
	 */
	public void printGraph() {
		for (GeographicPoint key: intersections.keySet()) {
			System.out.println("vert: " + key);			
			for (MapEdge e: intersections.get(key).getRoadList()) {
				System.out.println(e);
			}
			System.out.println("\n\n");			
		}
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		
		
		// Initialize Queue, Set & parent Map
		Set<MapNode> visited = new HashSet<MapNode>();
		Queue<MapNode> queue = new LinkedList<MapNode>();
		Map<MapNode, MapNode> parent = new HashMap<MapNode, MapNode>();
		
		// initialize pathFound boolean
		boolean pathFound = false;
		
		MapNode startNode = intersections.get(start);
		MapNode goalNode = intersections.get(goal);
		
		// Add start node to Queue and Set
		queue.add(startNode);
		visited.add(startNode);		
		
		while (!queue.isEmpty()) {
			MapNode currentNode = queue.poll();
			// Hook for visualization.  See writeup.
			nodeSearched.accept(currentNode.getLocation());
			
			// break the loop and set pathFound to true if we reach to the goal
			if (currentNode.toString().equals(goalNode.toString())) {
				pathFound = true;
				break;
			}
			
			for (MapNode neighbor: currentNode.getNeighbors()) {
				// Ensure visit only to non-visited nodes
				if (!visited.contains(neighbor)) {
					visited.add(neighbor);
					parent.put(neighbor, currentNode);
					queue.add(neighbor);
				}
			}
		}
		
		return getPath(startNode, goalNode, parent, pathFound);				
	}
	
	/** Helper private method that constructs a path 
	 * from start node to goal node by tracing back using 
	 * parent Map
	 * 
	 * @param start
	 * @param goal
	 * @param parent
	 * @param pathFound
	 * @return path from start to goal 
	 * 
	 */
	private List<GeographicPoint> getPath(MapNode start, MapNode goal, 
			Map<MapNode, MapNode> parent, boolean pathFound) {
		
		if (pathFound == false) {
			System.out.println("There is no path found from " + start + " to " + goal + ".");
			return null;
		}
		
		List<GeographicPoint> path = new LinkedList<GeographicPoint>();
		
		
		MapNode currNode = goal;
		
		// backtrace a node from goal until you reach to start
		while (true) {
			if (currNode.toString().equals(start.toString())) {
				break;
			}			
			MapNode prevNode = parent.get(currNode);
			path.add(currNode.getLocation());
			currNode = prevNode;			
		}
		
		path.add(start.getLocation());
		// reverse order to return a List from start to goal
		Collections.reverse(path);
		
		
		 // Calculate the total distance of the path
        double totalDistance = 0.0;
        for (int i = 1; i < path.size(); i++) {
            GeographicPoint current = path.get(i - 1);
            GeographicPoint next = path.get(i);
            totalDistance += current.distance(next);
        }

		
        estimateTransportationCostAndTime(totalDistance);
        
		
		System.out.println("Path: " + path);
		System.out.println("Total distance = " + totalDistance +"km");
		System.out.println("Bus cost = " + busCost +"euro," + " Time :" + busTime + "hrs");
		System.out.println("Train cost = " + trainCost +"euro," + " Time :" + trainTime + "hrs");
		return path;
	}
	
	
	public static void estimateTransportationCostAndTime(double distance) {
	      

        // Calculate transportation cost
        double busCos = distance * busCostPerKm;
        double trainCos = distance * trainCostPerKm;
        
        
        
        busCost =(int)busCos;
        trainCost = (int)trainCos;

        // Calculate time taken
        double busTim = (distance / busSpeed)*60;
        double trainTim = (distance / trainSpeed)*60;
        
        busTime =(int)busTim;
        trainTime = (int)trainTim;

    }
	
	public static void constraintApplications() {
		
		
		
	}
	
	
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		

		// Initialize PriorityQueue, Set, parent Map & distance Map
		HashSet<MapNode> visited = new HashSet<MapNode>();
		PriorityQueue<MapNode> priorityQueue = new PriorityQueue<>();
		HashMap<MapNode, MapNode> parent = new HashMap<MapNode, MapNode>();
		//HashMap<MapNode, Double> distances = new HashMap<MapNode, Double>(); 
		
		Integer inf = Integer.MAX_VALUE;
		boolean pathFound = false;
		MapNode startNode = intersections.get(start);
		MapNode goalNode = intersections.get(goal);
		
		// Set all distances to be inf for all intersections
		for (GeographicPoint location: intersections.keySet()) {
			//distances.put(intersections.get(location), inf.doubleValue());
			intersections.get(location).setDistance(inf.doubleValue());
		}
		
		// Set distance of start node to 0 and enqueue it
		intersections.get(start).setDistance(0.0);
		priorityQueue.add(startNode);
		
		int count = 0;
		
		while (!priorityQueue.isEmpty()) {
			//dequeue node from front of queue
			MapNode currentNode = priorityQueue.poll();
			count++;
			// Hook for visualization.  See writeup.
			nodeSearched.accept(currentNode.getLocation());
			
			if (!visited.contains(currentNode)) {
				visited.add(currentNode);
				
				//System.out.println("Node-visited: " + currentNode.getLocation());
				if (currentNode.toString().equals(goalNode.toString())) {
					pathFound = true;
					break;
				}
				
				for (MapEdge road: currentNode.getRoadList()) {					
					MapNode neighbor = intersections.get(road.getEnd());
					// Ensure visit only to non-visited nodes
					if (!visited.contains(neighbor)) {
						Double minDist = currentNode.getDistance() + road.getDistance();
						if (minDist < neighbor.getDistance()) {
							// Update neighbor's distance
							neighbor.setDistance(minDist);							
							parent.put(neighbor, currentNode);
							// enqueue neighbor in priorityQueue
							priorityQueue.add(neighbor);
						}
						
					}
				}				
			}
			
		}		
		System.out.println("Dijkstra total No. of Node-visited: " + count);
		return getPath(startNode, goalNode, parent, pathFound);		
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	

	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		
		
		
		//nodeSearched.accept(next.getLocation());
		// Initialize PriorityQueue, Set, parent Map & distance Map
		HashSet<MapNode> visited = new HashSet<MapNode>();
		PriorityQueue<MapNode> priorityQueue = new PriorityQueue<>();
		HashMap<MapNode, MapNode> parent = new HashMap<MapNode, MapNode>();
		 
		Integer inf = Integer.MAX_VALUE;
		boolean pathFound = false;
		MapNode startNode = intersections.get(start);
		MapNode goalNode = intersections.get(goal);
				
		// Set all distances and Heuristic Costs to be inf for all intersections
		for (GeographicPoint location: intersections.keySet()) {
			//distances.put(intersections.get(location), inf.doubleValue());
			intersections.get(location).setDistance(inf.doubleValue());
			// Add HCost for each node from Goal & activate astarFlag to modify compareTo 
			// function of MapNode such that priorityQueue entry will be based on HCost+GCost
			intersections.get(location).setHCost(goalNode);
			intersections.get(location).setAstar();
		}
		
		// Set distance of start node to 0 and enqueue it  
		intersections.get(start).setDistance(0.0);
		priorityQueue.add(startNode);	
		
		int count = 0;
				
		while (!priorityQueue.isEmpty()) {
			//dequeue node from front of queue
			MapNode currentNode = priorityQueue.poll();
			count++;
			// Hook for visualization.
			nodeSearched.accept(currentNode.getLocation());
					
			if (!visited.contains(currentNode)) {
				visited.add(currentNode);
				
				//System.out.println("Node-visited: " + currentNode.getLocation());
				if (currentNode.toString().equals(goalNode.toString())) {
					pathFound = true;
					break;
				}
						
				for (MapEdge road: currentNode.getRoadList()) {					
					MapNode neighbor = intersections.get(road.getEnd());
					System.out.println("Road-type: " + road.getResidentType());
					// Ensure visit only to non-visited nodes
					if (!visited.contains(neighbor)) {
						
						Double minDist = currentNode.getDistance() + road.getDistance();
						
						if (minDist < neighbor.getDistance()) {
							// Update neighbor's distance
							neighbor.setDistance(minDist);							
							parent.put(neighbor, currentNode);
							// enqueue neighbor in priorityQueue
							priorityQueue.add(neighbor);
						}
								
					}
				}				
			}
					
		}	
		
		System.out.println("Astar total No. of Node-visited: " + count);
				
		return getPath(startNode, goalNode, parent, pathFound);		
		
	}
	
	
	public static void main(String[] args) {
		
		
		//By assumption
		
				siteType= "Monument";
				// Define the start and end dates
		        LocalDate startDate = LocalDate.of(2023, 5, 1);
		        LocalDate endDate = LocalDate.of(2023, 5, 10);
		        
		        totalBudget=100;
				
		     // Calculate the number of days between the two dates
		        days = (int) ChronoUnit.DAYS.between(startDate, endDate);
		        
		        
		
		
		
		
		
		System.out.print("Making a new map...");
		MapGraph firstMap = new MapGraph();
		firstMap.printGraph();
		
		System.out.print("DONE. \nLoading the map...");
		//GraphDBMSLoader.loadRoadMap(firstMap);
		loaderGraph.loadRoadMapFromDatabase(firstMap);
		//firstMap.printGraph();
		System.out.println("DONE.");
		
		// You can use this method for testing.  
		
		
		
		/*
		MapGraph simpleTestMap = new MapGraph();
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);
		
		GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
		GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);
		
		System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
		List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart,testEnd);
		List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart,testEnd);
		
		
		MapGraph testMap = new MapGraph();
		GraphLoader.loadRoadMap("data/maps/utc.map", testMap);
		
		// A very simple test using real data
		testStart = new GeographicPoint(32.869423, -117.220917);
		testEnd = new GeographicPoint(32.869255, -117.216927);
		System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		
		
		// A slightly more complex test using real data
		testStart = new GeographicPoint(32.8674388, -117.2190213);
		testEnd = new GeographicPoint(32.8697828, -117.2244506);
		System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		*/
		
	
		
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		
        loaderGraph.loadRoadMapFromDatabase(theMap);
		System.out.println("DONE.");

		//GeographicPoint start = new GeographicPoint(45.5360917, -122.9134825);
		//GeographicPoint end = new GeographicPoint(45.536091, -122.9127192);
		
		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		List<GeographicPoint> route = theMap.dijkstra(start,end);
		List<GeographicPoint> route2 = theMap.aStarSearch(start,end);
		
		
		
		 GeographicPoint dwellingPoint = new GeographicPoint(32.8694205, -117.2205748);
		//This would test getting all monuments
		loaderGraph.siteFetch(theMap);
		GeographicPoint points = loaderGraph.closeMonument();
         
         
         List<GeographicPoint> route3= theMap.aStarSearch(dwellingPoint, points);
		
	}
		
	
	

}
