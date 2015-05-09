/**
 * Implementation of A* Algorithm for S3 game
 * CS680 Game-AI
 * Zhichao Cao
 * zc77@drexel.edu
 */
package s3.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import s3.base.S3;
import s3.entities.S3PhysicalEntity;
import s3.util.Pair;

public class AStar{
	
	private AStarGridNode startNode;	//Start node 
	private AStarGridNode goalNode;		//Target node
	
	private HashSet<AStarGridNode> OPEN;	//The set of nodes can reach and have never been reached	 
	private HashSet<AStarGridNode> CLOSED;	//The set of nodes have been reached
	
	private HashMap<AStarGridNode, AStarGridNode> hashMap;	//Saving the sequence of nodes on the path,
															//Instead of using parent of node
 
	protected S3 sGame;	//Reference to the S3 
	protected S3PhysicalEntity sPhysicalEntity;	//Reference to the S3 physical entity
	
	
	public static int pathDistance(double start_x, double start_y, double goal_x, double goal_y, S3PhysicalEntity i_entity, S3 the_game) {
		AStar a = new AStar(start_x,start_y,goal_x,goal_y,i_entity,the_game);
		List<Pair<Double, Double>> path = a.computePath();
		if (path!=null) return path.size();
		return -1;
	}

	public AStar(double start_x, double start_y, double goal_x, double goal_y, S3PhysicalEntity i_entity, S3 the_game) {
		//The initialization of A-Star algorithm
		OPEN = new HashSet<AStarGridNode>();
		CLOSED = new HashSet<AStarGridNode>();
		startNode = new AStarGridNode(start_x, start_y);
//		startNode.setG(0);
//		double h = heuristic(start_x, start_y, goal_x, goal_y);
//		startNode.setH(h);
		goalNode = new AStarGridNode(goal_x, goal_y);
		OPEN.add(startNode);
		hashMap = new HashMap<AStarGridNode, AStarGridNode>();
		sPhysicalEntity = (S3PhysicalEntity) i_entity.clone();
		sGame = the_game;
	}

	public List<Pair<Double, Double>> computePath() {
		//Main part of A-Star algorithm
		while (OPEN.size() > 0) {
			AStarGridNode currentNode = retractLowestNode();
			double g = currentNode.getG() + 1;
			boolean process = false;
			if (goal(currentNode, goalNode) == true) {
				return toPath(currentNode);
			}
			OPEN.remove(currentNode);
			CLOSED.add(currentNode);
			int width = sGame.getMap().getWidth();
			int height = sGame.getMap().getHeight();
			for (AStarGridNode childrenNode : currentNode.getChildren(width, height, sPhysicalEntity, sGame)) {
				//Traverse available children
				if(CLOSED.contains(childrenNode)) {
					continue;
				}
				if(OPEN.contains(childrenNode) == false) {
					OPEN.add(childrenNode);
					//childrenNode.compute_heuristic_distance_to_goal(goalNode);
					childrenNode.setH(heuristic(childrenNode.getX(), childrenNode.getY(), goalNode.getX(), goalNode.getY()));
					process = true;
				} 
				else if(g < childrenNode.getG()) {
					process = true;
				}
				if(process) {
					childrenNode.setParent(currentNode);
					hashMap.put(childrenNode, currentNode);
					childrenNode.setG(g);
				}
			}
		}
		return null;
	}

	private AStarGridNode retractLowestNode() {
		//Retract the node with lowest cost from the set
//		AStarGridNode node = new AStarGridNode();
		AStarGridNode node = null;
		for (AStarGridNode n : OPEN) {
			if (node == null || n.getTotalCost() < node.getTotalCost()) {
				node = n;
			}
		}
		return node;
	}
	
	public double heuristic(double start_x, double start_y, double goal_x, double goal_y) {
		//Calculate heuristic distance to the target node
		double hd = 0.0;
		double xd = start_x - goal_x;
		double yd = start_y - goal_y;
		hd = Math.sqrt(xd * xd + yd * yd);
		return hd;
	}

	public List<Pair<Double, Double>> toPath(AStarGridNode n) {
		//Return the path which contains a list of nodes to the particular node
		if(hashMap.containsKey(n)) {
			List<Pair<Double, Double>> pathList = new ArrayList<Pair<Double, Double>>();
			pathList = toPath(hashMap.get(n));
			if(pathList == null) {
				pathList = new ArrayList<Pair<Double, Double>>();
			}
			pathList.add(new Pair<Double, Double>(n.getX(), n.getY()));
			return pathList;
		}
		return null;
	}
	
	public boolean goal(AStarGridNode n1, AStarGridNode n2) {
		//Check whether the current node is the target node
		if(n1.getX() == n2.getX() && n1.getY() == n2.getY()) {
			return true;
		}
		return false;
	}

}