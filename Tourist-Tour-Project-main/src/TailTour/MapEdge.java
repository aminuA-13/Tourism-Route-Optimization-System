package TailTour;

import geography.GeographicPoint;

public class MapEdge {
	
	private GeographicPoint start;
	private GeographicPoint end;
	private String region;
	private String residentType;
	private double distance;
	
	public MapEdge()
	{
		
	}
	
	/**
	 * @param start
	 * @param end
	 * @param region
	 * @param residentType
	 * @param distance
	 */
	public MapEdge(GeographicPoint start, GeographicPoint end, String region,
			String residentType,double distance) {
		this.start = start;
		this.end = end;
		this.region = region;
		this.residentType = residentType;
		this.distance = distance;
	}
	/** Getter for start point 
	 * @return start
	 * */
	public GeographicPoint getStart() {
		return this.start;
	}
	
	/** Getter for end point 
	 * @return endf
	 * */
	public GeographicPoint getEnd() {
		return this.end;
	}
	
	/** Getter for road name 
	 * @return name of a road
	 * */
	public String getRegion() {
		return this.region;
	}
	
	/** Getter for road type 
	 * @return type of a road
	 * */
	public String getResidentType() {
		return this.residentType;
	}
	
	/** Getter for distance b/w stand and end point
	 * @return distance 
	 *  */
	public double getDistance() {
		return this.distance;
	}
	
	/** Print MapEdge Attributes
	 * @return s - information of MapEdge	  
	 */
	public String toString() {
		String s = "MapEdge Attributes: \n" ;
		s += "\tstartPoint: " + getStart() + ",\n";
		s += "\tendPoint: " + getEnd() + ",\n";
		s += "\tStreetName: " + getRegion() + ",\n";
		s += "\tStreetType: " + getResidentType() + ",\n";
		s += "\tlength: " + getDistance();
		s += "\n";
		
		return s;
	}
	

}
