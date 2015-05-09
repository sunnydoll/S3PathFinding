/**
 * Implementation of A* Algorithm for S3 game
 * CS680 Game-AI
 * Zhichao Cao
 * zc77@drexel.edu
 */
package s3.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import s3.base.S3;
import s3.entities.S3PhysicalEntity;
import s3.util.Pair;

public class AStar {
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
		sPhysicalEntity = (S3PhysicalEntity) (i_entity.clone());
		sGame = the_game;
	}

	public List<Pair<Double, Double>> computePath() {
		//Return the path contains a list of nodes to the target node
		//Main part of A-Star algorithm
		while (OPEN.size() > 0) {
			AStarGridNode chosenNode = retractLowestNode();
			if (chosenNode.equals(goalNode)) {
				return toPath(chosenNode);
			}
			OPEN.remove(chosenNode);
			CLOSED.add(chosenNode);
			for (AStarGridNode neighborNode : chosenNode.getChildren(sGame.getMap().getWidth(), sGame.getMap().getHeight())) {
				if (CLOSED.contains(neighborNode))
					continue;
				double g = chosenNode.getG() + 1;
				boolean process = false;
				if (OPEN.contains(neighborNode) == false) {
					OPEN.add(neighborNode);
					neighborNode.getDistance();
					process = true;
				} 
				else if (g < neighborNode.getG())
					process = true;
				if (process == true) {
					hashMap.put(neighborNode, chosenNode);
					neighborNode.setG(g);
				}
			}
		}
		return null;
	}
	
	private List<Pair<Double, Double>> toPath(AStarGridNode curNode) {
		//Return the path which contains a list of nodes to the particular node
		if (hashMap.containsKey(curNode)) {
			List<Pair<Double, Double>> list = new ArrayList<Pair<Double, Double>>();
			list = toPath(hashMap.get(curNode));
			if (list != null)
				list.add(new Pair<Double, Double>(curNode.getX(), curNode.getY()));
			else {
				list = new ArrayList<Pair<Double, Double>>();
				list.add(new Pair<Double, Double>(curNode.getX(), curNode.getY()));
			}
			return list;
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
	
	public boolean goal(AStarGridNode n1, AStarGridNode n2) {
		//Check whether the current node is the target node
		if(n1.getX() == n2.getX() && n1.getY() == n2.getY()) {
			return true;
		}
		return false;
	}	
	
	public double heuristic(double start_x, double start_y, double goal_x, double goal_y) {
		//Calculate heuristic distance to the target node
		double hd = 0.0;
		double xd = start_x - goal_x;
		double yd = start_y - goal_y;
		hd = Math.sqrt(xd * xd + yd * yd);
		return hd;
	}

	protected class AStarGridNode {
		//Class for the grid nodes on the map
		private double x;
		private double y;
		private double g = 9999;
		private double h = 9999;

		private AStarGridNode parent;  //Parent node in the path
		
		public AStarGridNode() {
			x = 0.0;
			y = 0.0;
			parent = null;
		}

		public AStarGridNode(double qx, double qy) {
			x = qx;
			y = qy;
			parent = null;
		}

		public void getDistance() {
			//Get the result of calculation of distance between two nodes
			h = calDistance(this, goalNode);
		}

		public List<AStarGridNode> getChildren(int width, int height) {
			//Check all children of the node whether they can be reached or not
			//If can, add them to the list and return, otherwise just ignore them
			List<AStarGridNode> childrenList = new ArrayList<AStarGridNode>();
			for (int i = -1; i <= 1; i++)
				for (int j = -1; j <= 1; j++) {
					//Traverse all available children
					if (!((i == 0 && j == 0) || (x + i) < 0 || (y + j) < 0 || (x + i) >= width || (y + j) >= height)) {
						sPhysicalEntity.setX((int) x + i);
						sPhysicalEntity.setY((int) y + j);
						if (sGame.anyLevelCollision(sPhysicalEntity)==null) {
							//Check whether children can be reached
							AStarGridNode newNode = new AStarGridNode(x + i, y + j);
							childrenList.add(newNode);
                        }
					}
				}

			return childrenList;
		}

        public int hashCode() {
            return (int)(x + (y * 128));
        }
        
        public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getG() {
			return g;
		}

		public double getH() {
			return h;
		}

		public AStarGridNode getParent() {
			return parent;
		}

		public void setX(double x) {
			this.x = x;
		}

		public void setY(double y) {
			this.y = y;
		}

		public void setG(double g) {
			this.g = g;
		}

		public void setH(double h) {
			this.h = h;
		}

		public void setParent(AStarGridNode parent) {
			this.parent = parent;
		}

		public double getTotalCost() {
			//Get total cost of of node to the target node
			return (h + g);
		}

		private double calDistance(AStarGridNode n1, AStarGridNode n2) {
			//Compute the straight distance
			return Math.sqrt((n1.getX() - n2.getX()) * (n1.getX() - n2.getX()) + (n1.getY() - n2.getY()) * (n1.getY() - n2.getY()));
		}

		public boolean equals(Object target) {
			//Check whether two nodes are the same
			AStarGridNode n = (AStarGridNode) target;
			if (this.x == n.getX() && this.y == n.getY())
				return true;
			return false;
		}
	}
}

