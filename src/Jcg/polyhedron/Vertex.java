package Jcg.polyhedron;

import geodesics.ExactAlgorithm;
import geodesics.Window;
import Jcg.geometry.*;


public class Vertex<X extends Point_>{

Halfedge<X> halfedge=null;
X point=null;
public int tag;
public int index;
public boolean extremum = false;

    public Vertex(X point) { this.point=point; }
    public Vertex() {}

    public void setEdge(Halfedge<X> halfedge) { this.halfedge=halfedge; }
    public void setPoint(X point) { this.point=point; }  
    
    public X getPoint() { return this.point; } 
    public Halfedge<X> getHalfedge() { return this.halfedge; } 
    
    public String toString(){
        return "v"+point.toString();
    }
    
	public double findBestTrack(double currentSigma)
	{
		@SuppressWarnings("unchecked")
		Halfedge<Point_3> first = (Halfedge<Point_3>) getHalfedge();
		Halfedge<Point_3> temp = first;
		Halfedge<Point_3> bestHalfhedge = null;
		Window best = null;
		Window tempWindow;
		double tempCos;
		double bestCos = -1;
		double bestX = -1;
		do {
			if (temp == temp.pair.one) { // temp is always pointing towards the vertex
				tempWindow = temp.pair.getWindow(temp.length);
				assert(tempWindow != null);
				if (tempWindow.sigma >= currentSigma) {
					temp = temp.getNext().getOpposite();
					continue;
				}
				assert((!tempWindow.tau && tempWindow.b0 < ExactAlgorithm.epsilon)
						|| (tempWindow.tau && tempWindow.b1 > temp.length - ExactAlgorithm.epsilon));
				Point_2 b0Point = new Point_2(tempWindow.b0, 0);
				Point_2 b1Point = new Point_2(tempWindow.b1, 0);
				Point_2 source = ExactAlgorithm.ofCircCoordinates(b0Point, b1Point, tempWindow.d0, tempWindow.d1, true);
				tempCos = tempWindow.tau ? 
						ExactAlgorithm.cos(b1Point, b0Point, source) 
						: ExactAlgorithm.cos(b0Point, b1Point, source);
				if (best==null || tempCos > bestCos) {
					bestCos = tempCos;
					best = tempWindow;
					bestHalfhedge = temp;
					bestX = temp.length;
				}
			} else {
				tempWindow = temp.pair.getWindow(0);
				assert(tempWindow != null);
				if (tempWindow.sigma >= currentSigma) {
					temp = temp.getNext().getOpposite();
					continue;
				}
				assert((tempWindow.tau && tempWindow.b0 < ExactAlgorithm.epsilon)
						|| (!tempWindow.tau && tempWindow.b1 > temp.length - ExactAlgorithm.epsilon));
				Point_2 b0Point = new Point_2(tempWindow.b0, 0);
				Point_2 b1Point = new Point_2(tempWindow.b1, 0);
				Point_2 source = ExactAlgorithm.ofCircCoordinates(b0Point, b1Point, tempWindow.d0, tempWindow.d1, true);
				tempCos = tempWindow.tau ? 
						ExactAlgorithm.cos(b0Point, b1Point, source) 
						: ExactAlgorithm.cos(b1Point, b0Point, source);
				if (best==null || tempCos > bestCos) {
					bestCos = tempCos;
					best = tempWindow;
					bestHalfhedge = temp;
					bestX = 0;
				}				
			}
			temp = temp.getNext().getOpposite();
		} while(temp != first);
		assert(best != null);
		return best.findTrack(best.tau ? bestX : bestHalfhedge.length - bestX, 0);
	}
}


