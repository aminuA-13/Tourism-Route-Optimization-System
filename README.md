# Tourist Tour Project (Tailored Tourist Tours)

A Java application that helps a tourist plan a trip around tourist sites in France. The tourist gives constraints (budget, travel dates, type of site), and the system models sites as a **weighted directed graph** and uses graph-search algorithms to find routes between them, along with estimated transport cost and travel time.

> **Status:** early-stage / work in progress. The graph model, pathfinding algorithms, database loading and cost estimation are in place. The user interface and the full day-by-day itinerary planner are still to be built (see [Roadmap](#roadmap)).

---

## Features

- **Graph model of sites and routes**: `MapGraph`, `MapNode`, `MapEdge` and `GeographicPoint` represent locations (latitude/longitude) and the connections between them.
- **Pathfinding algorithms** in `MapGraph`:
  - Breadth-First Search (`bfs`)
  - Dijkstra's algorithm (`dijkstra`)
  - A\* search (`aStarSearch`), using straight-line distance as the heuristic
- **Distance calculation** with the Haversine formula (`GoogleMapApis.calculateDistance`).
- **Transport cost and time estimation** for bus and train based on distance (`estimateTransportationCostAndTime`).
- **MySQL integration** (JDBC): site data and road/connection data are loaded from CSV/text files into MySQL, and the graph is then built from the database.
- **Tourist constraints** (`Constraint.constraint`): budget, start date, end date and preferred site type.

## Project Structure

```
Tourist-Tour-Project/
├── src/
│   ├── module-info.java            # Java module definition
│   ├── Dataset.csv                 # City dataset (id, City, longitude, latitude)
│   ├── Dataset2.csv                # Tourist sites (id, Names, Latitude, Longitude, Address)
│   ├── DATASETEST.txt              # Test edge data: lat1 lon1 lat2 lon2 "region" type
│   ├── Constraint/
│   │   ├── constraint.java         # Tourist constraints (budget, dates, site type)
│   │   └── Sites.java              # Enum of site types
│   ├── TailTour/
│   │   ├── MapGraph.java           # Graph + BFS / Dijkstra / A* + cost estimation (main entry point)
│   │   ├── MapNode.java            # Graph vertex (location, edges, distance, heuristic)
│   │   ├── MapEdge.java            # Directed edge (start, end, region, type, distance)
│   │   ├── SiteFactory.java        # Imports Dataset2.csv into MySQL (table sitedata)
│   │   └── GoogleMapApis.java      # Haversine distance, cost/time estimates, text-file import
│   └── geography/
│       ├── GeographicPoint.java    # Latitude/longitude point with distance calculation
│       ├── RoadSegment.java        # Road segment helper
│       ├── GraphDBMSLoader.java    # Loads graph data from the database
│       └── loaderGraph.java        # Builds the MapGraph from MySQL, fetches monuments
├── jarFiles/
│   ├── commons-csv-1.10.0.jar      # CSV parsing
│   └── mysql-connector-java-8.0.28.jar  # MySQL JDBC driver
└── bin/                            # Compiled classes
```

## How It Works

1. **Load data**: `SiteFactory` reads `Dataset2.csv` (site name, coordinates, address) with Apache Commons CSV and inserts it into the MySQL table `sitedata`. `GoogleMapApis.fetchDataFromTextFile()` loads connection data (start point, end point, region, type) into the table `dataset`.
2. **Build the graph**: `loaderGraph.loadRoadMapFromDatabase()` reads the `dataset` table and adds vertices and directed edges to a `MapGraph`.
3. **Find routes**: `dijkstra()` and `aStarSearch()` return the shortest path between two `GeographicPoint`s. `siteFetch()` and `closeMonument()` select monuments to route to from a starting point.
4. **Estimate cost and time**: for a route's total distance, the program prints estimated bus and train cost (€) and travel time.

### Transport assumptions (in `MapGraph`)

| Mode  | Cost per km | Average speed |
|-------|-------------|---------------|
| Bus   | 4.5         | 60 km/h       |
| Train | 2.5         | 120 km/h      |

These are rough assumptions and can be changed in `MapGraph.java`.

## Tech Stack

- Java (module-based project, built in Eclipse)
- MySQL 8 and JDBC
- Apache Commons CSV 1.10.0

## Getting Started

### Prerequisites

- JDK 11 or newer
- MySQL Server running locally
- Eclipse IDE (recommended, since the project includes Eclipse `.project` and `.classpath` files)

### 1. Set up the database

Create the database and the two tables the code expects:

```sql
CREATE DATABASE tailouredtourdb;
USE tailouredtourdb;

CREATE TABLE sitedata (
  id        INT PRIMARY KEY,
  Names     VARCHAR(255),
  latitude  DOUBLE,
  longitude DOUBLE,
  address   VARCHAR(255)
);

CREATE TABLE dataset (
  lat1 DOUBLE, lon1 DOUBLE,
  lat2 DOUBLE, lon2 DOUBLE,
  region VARCHAR(255),
  residentType VARCHAR(100)
);
```

> The schema above is inferred from the queries in the code. Adjust column types to suit your data.

### 2. Configure the project

The database connection and file paths are currently **hard-coded**. Before running, update:

- `JDBC_URL`, `DB_USERNAME`, `DB_PASSWORD` in `SiteFactory.java`, `GoogleMapApis.java`, `GraphDBMSLoader.java` and `loaderGraph.java` (default: `jdbc:mysql://localhost:3306/tailouredtourdb`, user `root`, empty password).
- `CSV_FILE_PATH` in `SiteFactory.java` and the file path in `GoogleMapApis.fetchDataFromTextFile()` so they point to `Dataset2.csv` and `DATASETEST.txt` on your machine.

### 3. Import the project and run

1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/Tourist-Tour-Project.git
   ```
2. In Eclipse: **File → Import → Existing Projects into Workspace**, then select the project folder.
3. Make sure `jarFiles/commons-csv-1.10.0.jar` and `jarFiles/mysql-connector-java-8.0.28.jar` are on the build path.
4. Run the data import once:
   - `TailTour.SiteFactory` (imports sites)
   - `TailTour.GoogleMapApis` (imports connection data)
5. Run `TailTour.MapGraph` to build the graph and test Dijkstra and A\* routing.

## Data

| File | Content |
|------|---------|
| `Dataset.csv` | ~750 French/nearby cities with coordinates |
| `Dataset2.csv` | ~55 tourist sites (walks, circuits, promenades) with name, coordinates and address |
| `DATASETEST.txt` | ~1,000 directed edges used to test the graph and search algorithms |

## Design Notes

- The graph is a **weighted directed graph**; the weight represents distance, from which transport cost and time are derived.
- Sites are modelled by type (e.g. monument, museum, castle, coastal area, festivity, landmark) so that tourists can filter by what they want to see.
- A tourist can visit more than one site per day, but the same site should not be visited twice in a trip.

## Roadmap

- [ ] User interface (Swing or web) for entering budget, dates, origin and site types
- [ ] Day-by-day itinerary generation that respects budget and time and never repeats a site
- [ ] Apply the `constraint` object (budget, dates, site type) inside the route search
- [ ] Complete the `Site` hierarchy (Monument, Museum, Castle, Coastal Area, Festivity, Landmark)
- [ ] Real Google Maps API integration for road distances (currently Haversine straight-line distance)
- [ ] Move database credentials and file paths into a configuration file
- [ ] Replace the sample test data with France-wide site data
- [ ] Unit tests for the search algorithms


## Contributing

Contributions, issues and feature requests are welcome. Fork the repository, create a feature branch, and open a pull request.

## License

No license has been specified yet. Add a `LICENSE` file (for example MIT) before sharing publicly.
