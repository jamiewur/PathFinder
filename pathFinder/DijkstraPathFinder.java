package pathFinder;

import map.Coordinate;
import map.PathMap;

import java.util.*;


/* Dijkstra Algorithm
 * Step 1: Assign every node a tentative distance.
 * Step 2: original node -- 0, others node -- infinity.
 * Step 3: Set of Visited node, Set of Unvisited node.
 * Step 4: For each node (1. Consider all unvisited neighbour. 2.Calculate. 3.If less than tentative, replace)
 * Step 5: After considering all neighbour, mark the current node to visited set.
 * Step 6: Until the destination marked visited.
 * Step 7: Set the unvisited node with the smallest tentative distance as the next current node
 */
public class DijkstraPathFinder implements PathFinder
{
    // TODO: You might need to implement some attributes
    // An instance of PathMap
    public final PathMap pathMap ;
    // number of rows
    public int sizeR;
    // number of columns
    public int sizeC;
    // Array of map
    public Coordinate[][] cells;
    // Array of distance
    public int[][] cellDistanceArray;
    // List of origin cell
    public List<Coordinate> origins;
    // List of destination cell
    public List<Coordinate> destinations;
    // Visited node array list
    public ArrayList<Coordinate> visitedList;
    // Unvisited node array list
    public ArrayList<Coordinate> unvisitedList;
    // Impassable node array lis
    public ArrayList<Coordinate> impassableList;
    // Way points
    public List<Coordinate> wayPoints;
    // Way points arraylist
    public ArrayList<ArrayList<Coordinate>> wayPointPathList;
    // The track back map
    public Map<Coordinate, Coordinate> trackBackMap;


    public DijkstraPathFinder(PathMap map) {
        // TODO :Implement
        this.pathMap = map;
        this.sizeR = map.sizeR;
        this.sizeC = map.sizeC;
        this.cells = map.cells;
        this.cellDistanceArray = new int[sizeR][sizeC];
        this.origins = map.originCells;
        this.destinations = map.destCells;
        this.wayPoints = map.waypointCells;
        this.wayPointPathList = new ArrayList<>();
        this.trackBackMap = new HashMap<>();

    } // end of DijkstraPathFinder()



    @Override
    public List<Coordinate> findPath() {
        // You can replace this with your favourite list, but note it must be a
        List<Coordinate> path = new ArrayList<Coordinate>();
        // TODO: Implement

        // Return the shortest path without way points
        if (wayPoints.isEmpty()) {
            path = pathFinder(origins,destinations);
            return path;
        }

        // Return the shortest path with way points
        else {
            path = wayPointsPath(origins,destinations,wayPoints);
            return path;
        }
    } // end of findPath()



    // Path finder return the nearest node, from the origins to destinations
    public ArrayList<Coordinate> pathFinder(List<Coordinate> origin,List<Coordinate> destination){

        // Step 0: Initial. Map<currentNode,preNode>
        initialCost();
        initialDistance(origin);

        // Step 1: Initial each node cost, and node distance
        unvisitedList = new ArrayList<>();
        visitedList = new ArrayList<>();
        impassableList = new ArrayList<>();
        for(int r=0;r<sizeR;r++)
            for(int c=0;c<sizeC;c++){
                if(cells[r][c].getImpassable()){
                    impassableList.add(cells[r][c]);
                    continue;
                }
                unvisitedList.add(cells[r][c]);
                trackBackMap.put(cells[r][c],null);
            }


        // Step 2: Select a minimum shorted-distance node from unvisited set
        while(unvisitedList!=null&&!containNode(destination,visitedList)){
        Coordinate shortestNode = getShortestNode(unvisitedList);
        // Step 3: Add the node to the visited list
        visitedList.add(shortestNode);
        unvisitedList.remove(shortestNode);

        // Step 4: Update the neighbour distance of the node
        for(Coordinate neighbour: findNeighbours(shortestNode)) {
            if (!visitedList.contains(neighbour)) {
                int neighbourDistance = cells[neighbour.getRow()][neighbour.getColumn()].getTerrainCost() + cellDistanceArray[shortestNode.getRow()][shortestNode.getColumn()];
                if (neighbourDistance < cellDistanceArray[neighbour.getRow()][neighbour.getColumn()]){
                    cellDistanceArray[neighbour.getRow()][neighbour.getColumn()] = neighbourDistance;
                    trackBackMap.put(neighbour,shortestNode);
                    System.out.println("Current: "+ "("+ shortestNode.getRow()+","+shortestNode.getColumn()+")" + " nearest neibour is: "+ "("+ neighbour.getRow()+","+neighbour.getColumn()+")"+cellDistanceArray[neighbour.getRow()][neighbour.getColumn()]);
                }

            }
        }
        }
        return reversePath(trackBackMap,origin,destination);
    }


    // return the distance of path
    public int returnDistance(Coordinate origin,Coordinate dest) {
        // Step 0: Initial. Map<currentNode,preNode>
        initialCost();
        for (int r = 0; r < sizeR; r++)
            for (int c = 0; c < sizeC; c++) {
                if (cells[r][c].hashCode() != origin.hashCode())
                    cellDistanceArray[r][c] = Integer.MAX_VALUE;
                else cellDistanceArray[r][c] = 0;
            }
        // Step 1: Initial each node cost, and node distance
        unvisitedList = new ArrayList<>();
        visitedList = new ArrayList<>();
        impassableList = new ArrayList<>();
        for (int r = 0; r < sizeR; r++)
            for (int c = 0; c < sizeC; c++) {
                if (cells[r][c].getImpassable()) {
                    impassableList.add(cells[r][c]);
                    continue;
                }
                unvisitedList.add(cells[r][c]);

            }

            // Step 2: Select a minimum shorted-distance node from unvisited set
        while (unvisitedList != null && !visitedList.contains(dest)) {
            Coordinate shortestNode = getShortestNode(unvisitedList);
            // Step 3: Add the node to the visited list
            visitedList.add(shortestNode);
            unvisitedList.remove(shortestNode);

            // Step 4: Update the neighbour distance of the node
            for (Coordinate neighbour : findNeighbours(shortestNode)) {
                if (!visitedList.contains(neighbour)) {
                    int neighbourDistance = cells[neighbour.getRow()][neighbour.getColumn()].getTerrainCost() + cellDistanceArray[shortestNode.getRow()][shortestNode.getColumn()];
                    if (neighbourDistance < cellDistanceArray[neighbour.getRow()][neighbour.getColumn()]) {
                        cellDistanceArray[neighbour.getRow()][neighbour.getColumn()] = neighbourDistance;
                    }

                }
            }
        }

        return cellDistanceArray[dest.getRow()][dest.getColumn()];
    }


    // Return the shortest path between two point
    public ArrayList<Coordinate> returnShortestPath(Coordinate origin,Coordinate dest) {
        initialCost();
        for (int r = 0; r < sizeR; r++)
            for (int c = 0; c < sizeC; c++) {
                if (cells[r][c].hashCode() != origin.hashCode())
                    cellDistanceArray[r][c] = Integer.MAX_VALUE;
                else cellDistanceArray[r][c] = 0;
            }

        unvisitedList = new ArrayList<>();
        visitedList = new ArrayList<>();
        impassableList = new ArrayList<>();
        for (int r = 0; r < sizeR; r++)
            for (int c = 0; c < sizeC; c++) {
                if (cells[r][c].getImpassable()) {
                    impassableList.add(cells[r][c]);
                    continue;
                }
                unvisitedList.add(cells[r][c]);
            }

        while (unvisitedList != null && !visitedList.contains(dest)) {
            Coordinate shortestNode = getShortestNode(unvisitedList);
            // Step 3: Add the node to the visited list
            visitedList.add(shortestNode);
            unvisitedList.remove(shortestNode);

            // Step 4: Update the neighbour distance of the node
            for (Coordinate neighbour : findNeighbours(shortestNode)) {
                if (!visitedList.contains(neighbour)) {
                    int neighbourDistance = cells[neighbour.getRow()][neighbour.getColumn()].getTerrainCost() + cellDistanceArray[shortestNode.getRow()][shortestNode.getColumn()];
                    if (neighbourDistance < cellDistanceArray[neighbour.getRow()][neighbour.getColumn()]) {
                        cellDistanceArray[neighbour.getRow()][neighbour.getColumn()] = neighbourDistance;
                        trackBackMap.put(neighbour, shortestNode);

                    }
                }
            }
        }
        return reverseNodePath(origin,dest);
    }

    // Return the shortest path with way points
    public ArrayList<Coordinate> wayPointsPath(List<Coordinate> origin,List<Coordinate> destination, List<Coordinate> wayPoints){

        for(Coordinate orginNode:origin){
            for(Coordinate destinationNode:destination){
                ArrayList<Coordinate> allpoints = new ArrayList<>();
                allpoints.add(orginNode);
                allpoints.add(destinationNode);
                for(Coordinate wayPointsNode:wayPoints){
                    allpoints.add(wayPointsNode);
                }
                permutePath(allpoints,0);
            }
        }
        ArrayList<ArrayList<Coordinate>> allValidPathList = returnAllValidPaths(wayPointPathList,origin,destination);
        ArrayList<Coordinate> shortestPath = new ArrayList<>();
        //Calculate cost of each path
        int n = 0;
        int distance = Integer.MAX_VALUE;
        for(ArrayList<Coordinate> path: allValidPathList){
            int currentDistance = 0;
            for(int i = 0; i< path.size()-1;i++){
                currentDistance += returnDistance(path.get(i),path.get(i+1));
            }
            if(currentDistance<distance){
                distance=currentDistance;
                shortestPath=path;
            }
        }
        for(Coordinate shrot:shortestPath){
            System.out.println(shrot.getRow()+ " "+shrot.getColumn());
        }
        ArrayList<Coordinate> way = new ArrayList<>();
        ArrayList<Coordinate> currentWay = new ArrayList<>();
        way.add(shortestPath.get(0));
        // return the shortestPath between chosen points
        for(int i = 0; i < shortestPath.size()-1; i++){
            currentWay=returnShortestPath(shortestPath.get(i),shortestPath.get(i+1));
            for(Coordinate node:currentWay){
                way.add(node);
            }
        }
        return way;
    }

    // permute all point and return a list of all permutation, From start point to destination
    public void permutePath(ArrayList<Coordinate> allPoint, int pointer){

        // The total number of point
        // The total number of point
        for(int i = pointer; i <allPoint.size();i++){
            Collections.swap(allPoint,i,pointer);
            permutePath(allPoint,pointer+1);
            Collections.swap(allPoint,pointer,i);
        }
        if(pointer == allPoint.size()-1){
            ArrayList<Coordinate> currentPath = new ArrayList<>();
            for(Coordinate point: allPoint) {
                currentPath.add(point);
            }
            wayPointPathList.add(currentPath);

        }
    }


    // Generate an arraylist contain all point
    public ArrayList<Coordinate> returnAllCoordinates(List<Coordinate> origin, List<Coordinate> destination, List<Coordinate> wayPoints){
        // An arrayList hold all point
        ArrayList<Coordinate> allPoint = new ArrayList<>();
        for (Coordinate coordinate: origin){
            allPoint.add(coordinate);
        }
        for (Coordinate coordinate: destination){
            allPoint.add(coordinate);
        }
        for (Coordinate coordinate: wayPoints){
            allPoint.add(coordinate);
        }

        return allPoint;
    }

    // A arraylist filter, return all path start from origin and end with destination
    public ArrayList<ArrayList<Coordinate>> returnAllValidPaths(ArrayList<ArrayList<Coordinate>> wayPointPathList, List<Coordinate> origin, List<Coordinate> destination){

        //The all valid points arraylist
        ArrayList<ArrayList<Coordinate>> allValidPathsList = new ArrayList<>();
        int totalNumberOfPaths = wayPointPathList.size();
        int numberOfNode = wayPoints.size()+2;

        for(int i = 0; i<totalNumberOfPaths; i++){
            if(origin.contains(wayPointPathList.get(i).get(0)) && destination.contains(wayPointPathList.get(i).get(numberOfNode-1)))
                allValidPathsList.add(wayPointPathList.get(i));
        }
        return allValidPathsList;
    }



    // Return a path from a node
    public ArrayList<Coordinate> reversePath(Map<Coordinate,Coordinate> nodeMap,List<Coordinate> origin,List<Coordinate> destination){
        ArrayList<Coordinate> path = new ArrayList<>();
        for(Coordinate destNode: destination) {

            Coordinate currentNode = destNode;
            Coordinate preNode = nodeMap.get(currentNode);
            if (preNode == null) {
                continue;
            }
            path.add(destNode);


            while (!ifcontain(origin,preNode)) {
                    path.add(preNode);
                    currentNode = preNode;
                    preNode = nodeMap.get(currentNode);
            }
            for(Coordinate oriNode:origin){
                if(preNode.hashCode()==oriNode.hashCode())
                    path.add(preNode);
            }
        }
        Collections.reverse(path);
        return path;

    }

    // Return a path from two nodes
    public ArrayList<Coordinate> reverseNodePath(Coordinate origin, Coordinate dest){
        ArrayList<Coordinate> path = new ArrayList<>();
        Coordinate currentNode = dest;
        Coordinate preNode = trackBackMap.get(dest);
        path.add(dest);
        while (origin.hashCode() != preNode.hashCode()){
            path.add(preNode);
            currentNode = preNode;
            preNode = trackBackMap.get(currentNode);
        }
        Collections.reverse(path);
        return path;
    }

    // Return whether the hashcode of an element is existed in a list
    public boolean ifcontain(List<Coordinate> nodes, Coordinate preNode){
        for(Coordinate node: nodes){
            if(preNode.hashCode()==node.hashCode())
                return true;
        }
        return false;
    }

    // Return a shortest-distance node
    public Coordinate getShortestNode(List<Coordinate> list){

        int Shortestdistance = Integer.MAX_VALUE;
        Coordinate shortestNode = null;
        for(Coordinate nodes: list){
            if(cellDistanceArray[nodes.getRow()][nodes.getColumn()]<Shortestdistance){
                Shortestdistance = cellDistanceArray[nodes.getRow()][nodes.getColumn()];
                shortestNode = nodes;
            }
        }
        return shortestNode;
    }

    // If a list contain the points in another list
    public boolean containNode(List<Coordinate> desNodes,List<Coordinate> list){
        for(Coordinate desnode: desNodes){
            if(list.contains(desnode))
                return true;
        }
        return false;
    }




    // Find the neighbours of a node
    public ArrayList<Coordinate> findNeighbours(Coordinate node) {
        ArrayList<Coordinate> neighbours = new ArrayList<>();

        // above
        if(pathMap.isIn((node.getRow()+1),node.getColumn())) {
            if(!cells[node.getRow()+1][node.getColumn()].getImpassable())
            neighbours.add(cells[node.getRow()+1][node.getColumn()]);
        }
        //bottom
        if(pathMap.isIn((node.getRow()-1),node.getColumn())) {
            if (!cells[node.getRow() - 1][node.getColumn()].getImpassable())
                neighbours.add(cells[node.getRow() - 1][node.getColumn()]);
        }
        //left
        if(pathMap.isIn((node.getRow()),node.getColumn()-1)) {
            if (!cells[node.getRow()][node.getColumn()-1].getImpassable())
                neighbours.add(cells[node.getRow()][node.getColumn()-1]);
        }
        //right
        if(pathMap.isIn((node.getRow()),node.getColumn()+1)) {
            if (!cells[node.getRow()][node.getColumn()+1].getImpassable())
                neighbours.add(cells[node.getRow()][node.getColumn()+1]);
        }
        return neighbours;
    }


    // Initial all node with value of terrain cost
    public void initialCost(){
        // init all node cost
        for(int r=0; r<sizeR; r++)
            for (int c=0; c<sizeC; c++){
                if(cells[r][c].getImpassable()){
                    cells[r][c].setTerrainCost(Integer.MAX_VALUE);
                    continue;
                }
            }
    }

    //Initial all node with the distance from origin, and zero for origin
    public void initialDistance(List<Coordinate> origins) {
        for (int r = 0; r < sizeR; r++)
            for (int c = 0; c < sizeC; c++) {
                if (!origins.contains(cells[r][c]))
                    cellDistanceArray[r][c] = Integer.MAX_VALUE;
                else cellDistanceArray[r][c] = 0;
            }
    }




    @Override
    public int coordinatesExplored() {
        // TODO: Implement (optional)

        // placeholder
        return 0;
    } // end of cellsExplored()



} // end of class DijsktraPathFinder
