### For Task A:

Work Flow:

- Step 1: Assign every node a tentative distance.

- Step 2: original node -- 0, others node -- infinity.

- Step 3: Set of Visited node, Set of Unvisited node.

- Step 4: For each node (1. Consider all unvisited neighbour. 2.Calculate. 3.If less than tentative, replace) * Step 5: After considering all neighbour, mark the current node to visited set.

- Step 6: Until the destination marked visited.

- Step 7: Set the unvisited node with the smallest tentative distance as the next current node * Step 8: Add the each node to backTrackMap ( Key is the current node, value is the nearest neighbor node.

- Step 9: Back track all node from origin to destination.

### For Task B:

Based on Task A, calculate the terrain and add to the Step2 as previous mentioned. The rest of step is same as Task A.

### For Task C:

Based on Task A as well, set each of the beginning point as 0, so start from each of these origin point, then start the Dijkstra algorithm, stop while the destination come into the visited set. Then from the destination, through backTrackMap back track to the one of the origin, this path is the nearest path we find.

### For Task D:

Based on previous Tasks. For the all origin nodes, destination nodes and way point nodes, find all permutation of one of origin nodes, and one of destination nodes the rest the way points nodes. Calculate cost of each of the path by Dijkstra algorithm, then return the shortest path. From th