package geography;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Victor habila

public class RoadSegment {


	private GeographicPoint point1;
	private GeographicPoint point2;
	
	private List<GeographicPoint> geometryPoints;
	
	private String region;
	private String residentType;
	
	// distance in km
	private double distance;
	
	
	public RoadSegment(GeographicPoint pt1, GeographicPoint pt2, 
			List<GeographicPoint> geometry, String region,
			String residentType, double distance)
			{
			point1 = pt1;
			point2 = pt2;
			geometryPoints = new ArrayList<GeographicPoint>(geometry);
			this.region = region;
			this.residentType = residentType;
			this.distance = distance;
			}
	
	public List<GeographicPoint> getPoints(GeographicPoint start, 
			GeographicPoint end)
				{
				List<GeographicPoint> allPoints = new ArrayList<GeographicPoint>();
				if (point1.equals(start) && point2.equals(end)) {
				allPoints.add(start);
				allPoints.addAll(geometryPoints);
				allPoints.add(end);
				}
				else if (point2.equals(start) && point1.equals(end)) {
				allPoints.add(end);
				allPoints.addAll(geometryPoints);
				allPoints.add(start);
				Collections.reverse(allPoints);
				}
				else {
				throw new IllegalArgumentException("Start and end points do not "
				+ "match end points of segment");
				}
				
				return allPoints;
				}
	
	/** Two road segments are equal if they have the same start and end points
	 *  and they have the same road name.
	 */
			public boolean equals(Object o)
			{
				if (!(o instanceof RoadSegment)) {
					return false;
				}
				
				RoadSegment other = (RoadSegment)o;
				boolean ptsEqual = false;
				if (other.point1.equals(this.point1) && other.point2.equals(this.point2)) {
					ptsEqual = true;
				}
				if (other.point2.equals(this.point1) && other.point1.equals(this.point2))
				{
					ptsEqual = true;
				}
				return this.region.equals(other.region) && ptsEqual && this.distance == other.distance;
			}
			
			// get hashCode
			public int hashCode()
			{
				return point1.hashCode() + point2.hashCode();
			}
			
			// return road segment as String
			public String toString()
			{
				String toReturn = this.region + ", " +this.residentType;
				toReturn += " [" + point1;
				for (GeographicPoint p : this.geometryPoints) {
					toReturn += "; " + p;
				}
				toReturn += "; " + point2 + "]";
				
				return toReturn;
			}

			// get the length of the road segment
			public double getDistance() { return this.distance; }
			
			
			// given one end, return the other.
			public geography.GeographicPoint getOtherPoint(geography.GeographicPoint point) {
				if(point.equals(point1)) {
					return point2;
				}
				if(point.equals(point2)) {
					return point1;
				}

				System.out.println("ERROR!! : in RoadSegment::getOtherPoint Neither point matched");
				return null;
			}

			

}
