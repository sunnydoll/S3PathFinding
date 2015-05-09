/**
 * Class of Node of map for implementation of A* Algorithm for S3 game
 * CS680 Game-AI
 * Zhichao Cao
 * zc77@drexel.edu
 */
package s3.ai;

import java.util.ArrayList;
import java.util.List;

import s3.base.S3;
import s3.entities.S3PhysicalEntity;

public class AStarGridNode {
	
	private double x;
	private double y;
	private double g = 9999;
	private double h = 9999;

	private AStarGridNode parent;
	
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

	public double getG() {
		return g;
	}

	public double getH() {
		return h;
	}

	public void setG(double g) {
		this.g = g;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
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

	public void setParent(AStarGridNode parent) {
		this.parent = parent;
	}

    public int hashCode() {
        return (int)(x + (y * 128));
    }
    
	public double getTotalCost() {
		//Get total cost of of node to the target node
		return h + g;
	}

	public List<AStarGridNode> getChildren(int width, int height, S3PhysicalEntity sPhysicalEntity, S3 sGame) {
		//Check children of the node whether they can be reached or not
		//If can, add them to the list and return
		List<AStarGridNode> childrenList = new ArrayList<AStarGridNode>();		
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				//Traverse all available children
				if (!((i == 0 && j == 0) || (x + i) < 0 || (y + j) < 0 || (x + i) >= width || (y + j) >= height)) {
					sPhysicalEntity.setX((int) x + i);
					sPhysicalEntity.setY((int) y + j);
					if (sGame.anyLevelCollision(sPhysicalEntity) == null) {
						//Check whether children can be reached
						AStarGridNode newNode = new AStarGridNode(x + i, y + j);
						childrenList.add(newNode);
                    }
				}
			}
		return childrenList;
	}

	public boolean equals(Object target) {
		//Check whether the implicit node is explicit target node
		AStarGridNode n = (AStarGridNode) target;
		if (this.x == n.x && this.y == n.y)
			return true;
		return false;
	}

}
