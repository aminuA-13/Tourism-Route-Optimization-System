package TailTour;

import java.util.ArrayList;
import java.util.List;

import geography.GeographicPoint;




public class MapNode implements Comparable<MapNode> {
	
	private GeographicPoint location;
	private List<MapEdge> roadList;
	private List<MapNode> neighbors;
	private Double distance;
	private Double heuristicCost;
	private boolean astarFlag;
	
	/** Create an empty MapNode 
	 * */
	public MapNode() {
		this.location = null;
		this.roadList = new ArrayList<MapEdge>();
		this.neighbors = new ArrayList<MapNode>();
		this.distance = null;
		this.heuristicCost = null;
		this.astarFlag = false;
	}
	
	/** Create a MapNode with a given location
	 * and add empty list of all roads connected and neighbors
	 */
	public MapNode(GeographicPoint loc) {
		this.location = loc;
		this.roadList = new ArrayList<MapEdge>();
		this.neighbors = new ArrayList<MapNode>();
		this.distance = null;
		this.heuristicCost = null;
		this.astarFlag = false;		
	}

	
	
	/**
	 * @return the location
	 */
	public GeographicPoint getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(GeographicPoint location) {
		this.location = location;
	}

	/**
	 * @return the roadList
	 */
	public List<MapEdge> getRoadList() {
		return roadList;
	}

	/**
	 * @param roadList the roadList to set
	 */
	public void setRoadList(List<MapEdge> roadList) {
		this.roadList = roadList;
	}

	/**
	 * @return the neighbors
	 */
	public List<MapNode> getNeighbors() {
		return neighbors;
	}

	/**
	 * @param neighbors the neighbors to set
	 */
	public void setNeighbors(List<MapNode> neighbors) {
		this.neighbors = neighbors;
	}

	/**
	 * @return the distance
	 */
	public Double getDistance() {
		return distance;
	}
	
	/** Setter for a distance of the MapNode 
	 * @param Double distanceVal 
	 */
	public void setHCost(MapNode other) {
		this.heuristicCost = this.location.distance(other.getLocation());		
	}
	
	public void setAstar() {
		this.astarFlag = true;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	public Double getHCost() {
		return this.heuristicCost;
	}

	/**
	 * @return the heuristicCost
	 */
	public Double getHeuristicCost() {
		return heuristicCost;
	}

	/**
	 * @param heuristicCost the heuristicCost to set
	 */
	public void setHeuristicCost(Double heuristicCost) {
		this.heuristicCost = heuristicCost;
	}

	/**
	 * @return the astarFlag
	 */
	public boolean isAstarFlag() {
		return astarFlag;
	}

	/**
	 * @param astarFlag the astarFlag to set
	 */
	public void setAstarFlag(boolean astarFlag) {
		this.astarFlag = astarFlag;
	}
	
	/** Print MapNode Attributes
	 * @return s - information of MapNode	  
	 */
	public String toString() {
		String s = "location of MapNode: " + this.location;
		s += "\nRoad List: \n";
		for (MapEdge e: this.roadList) {
			s += e;
		}
		
		return s;
	}
	

	@Override
	public int compareTo(MapNode other) {
		if (this.astarFlag) {
			Double this_cost = this.getHCost() + this.getDistance();
			Double other_cost = other.getHCost() + other.getDistance();
			
			return this_cost.compareTo(other_cost);			
		}
		
		return this.getDistance().compareTo(other.getDistance());
	}
	
	
	
	

}
