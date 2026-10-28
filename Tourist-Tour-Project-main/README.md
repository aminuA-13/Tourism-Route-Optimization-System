#Tourist Tour Project

Site (Interface class)
Attributes (Name, period, location (longitude and latitude), type, country, city(Paris,
Lyon, Nice etc))
Monuments (regular class implementing site interface)
Museum (regular class implementing site interface)
Festivities (regular class implementing site interface)
Coastal Area (regular class implementing site interface)
Castle (regular class implementing site interface)
Landmark (regular class implementing site interface)
Monuments Museum Coastal Area Castle
Eiffel Tower Louvre French Riviera Carcassonne
Palace od Versailles Cote d’Azur
Mont Saint-Michel
Notre-Dame Cathedral
Chateau de Chambord
Pont du Gard
Saint-Chapelle
Palace of the Popes
Define Constraint class.
Attributes (time, budget, site type (monument, museum, castle etc.), origin (city
where you will stay (This would be the initial starting node)))
Define various setters, getters, constructors and other methods respectively.
Define Graph class.
This would hold the sites and their relationships. The relationship between the site can
be represented using adjacency list or adjacency matrix.
Define various setters, getters, constructors and other methods respectively.In the same Graph class, define additional methods such as
getSize(), addSite(), addRelationship() and findShortestPath().
Define site factory class (SiteFactory).
This would read our dataset and parse the needed data into our relational database
(MYSQL)
In the same class also define a static class FetchFactory to fetch data from our
relational database depending on the site type provided by the tourist in the constraint.
NOTE
We are using a Weighted Directed Graph with weight = Cost or Price of
transportation.
Define a class GoogleMapsDataExtractor()
To configure google map API
Define a method called CalculateDistances() in the GoogleMapsDataExtractor
class to calculate the distances from the source to destination using Longitude and
latitude coordinates.
Define site relationship class (SiteRelationship).
This would take in parameters like (Sourcesite / source, destinationSite, maybe with
relationship type(adjacent, connected, related)) (it would work like adding edges and
weight)
Define various setters, getters, constructors and other methods respectively.
Algorithm to use.
A. Dijkstra’s algorithm / A* algorithm to fine the shortest path between sites based on
distances and cost constraints etc.
B. DFS / BFS to traverse the graph and explore our sites
EtcUser Interface design
We need to design a user interface where tourist can input their various constraints
and preferences. We can use Swing or a web base user interface.
NOTE
Assuming a tourist can visit more than one site in a day. That means the system should
compute and display for each day the shortest recommendation of sites observing the
provides constraints without allowing a user to visit the same site twice.
This is not everything of course its way more bigger than this. But we can start from
here and keep building up on it if time permits
