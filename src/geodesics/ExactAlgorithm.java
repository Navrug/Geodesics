package geodesics;

import java.util.PriorityQueue;

import Jcg.geometry.GeometricOperations_2;
import Jcg.geometry.Point_2;
import Jcg.geometry.Point_3;
import Jcg.geometry.Segment_2;
import Jcg.polyhedron.Face;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;

public class ExactAlgorithm
{
	PriorityQueue<Window> queue = new PriorityQueue<Window>();
	Vertex<Point_3> first, second;
	static double epsilon = 10e-8;
	
	
	public void setFirstPoint(Vertex<Point_3> first){
		this.first = first; 
	}
	
	public void setSecondPoint(Vertex<Point_3> second){
		if (this.second != null)
			this.second.extremum = false;
		this.second = second; 
	}

	/**
	 * Tries to add a new window, if successful propagates it.
	 * @return whether the window was added to the edge
	 */
	boolean addWindow(Window w, Halfedge<Point_3> h)
	{
		boolean result = h.pair.addWindow(w);
		if (result) {
			w.display();
			queue.add(w);
		}
		return result;
	}

	/**
	 *  !!! Beware of rounding errors in the edge borders, may need to handle them more properly
	 *  There are three main cases depending on the orientation of the extreme rays: fully in first edge,
	 *  in both edges or in the second edge.
	 */
	void propagate(Window v)
	{
		Halfedge<Point_3> e = v.tau ? v.pair.two : v.pair.one; //We propagate to the opposite direction of the source
		Point_2 b0Point = new Point_2(v.b0, 0);
		Point_2 b1Point = new Point_2(v.b1,0);
		Point_2 source = ofCircCoordinates(b0Point, b1Point, v.d0, v.d1, true);
		//System.out.println("source: "+ source.x + " " + source.y);
		Point_2 v0 = new Point_2(0,0);
		Point_2 v2 = new Point_2(e.length, 0);
		Point_2 v1 = ofCircCoordinates(v0, v2, e.next.length, e.prev.length, false);
		//
		Point_2 c1, c2;
		Window w;
		int orientation1 = GeometricOperations_2.orientation(source, b0Point, v1);
		int orientation2 = GeometricOperations_2.orientation(source, b1Point, v1);
		if(orientation1 == 0 || orientation2 == 0) // Case of perfectly aligned opposite, should not happen with doubles, does it?
			throw new RuntimeException();
		else if (orientation1 == 1) {
			if (orientation2 == 1) { //Propagates fully in the first edge
				c1 = GeometricOperations_2.intersect(new Segment_2(source, b0Point), new Segment_2(v0, v1));
				c2 = GeometricOperations_2.intersect(new Segment_2(source, b1Point), new Segment_2(v0, v1));
				w = new Window(e.next, v.sigma,
						(double) c1.distanceFrom(v0), (double) c2.distanceFrom(v0),
						(double) c1.distanceFrom(source), (double) c2.distanceFrom(source));
				addWindow(w, e.next);

			} else { //Propagates in both edges
				c1 = GeometricOperations_2.intersect(new Segment_2(source, b0Point), new Segment_2(v0, v1));
				c2 = GeometricOperations_2.intersect(new Segment_2(source, b1Point), new Segment_2(v1, v2));
				w = new Window(e.next, v.sigma,
						(double) c1.distanceFrom(v0), e.next.length,
						(double) c1.distanceFrom(source), (double) v1.distanceFrom(source));
				addWindow(w, e.next);
				w = new Window(e.prev, v.sigma,
						(double) 0, (double) c2.distanceFrom(v1),
						(double) v1.distanceFrom(source), (double) c2.distanceFrom(source));
				addWindow(w, e.prev);
			}
		}
		else
			if(orientation2 == 1) //Impossible case
				throw new RuntimeException();
			else {// Propagate fully in the second edge
				c1 = GeometricOperations_2.intersect(new Segment_2(source, b0Point), new Segment_2(v1, v2));
				c2 = GeometricOperations_2.intersect(new Segment_2(source, b1Point), new Segment_2(v1, v2));
				w = new Window(e.prev, v.sigma,
						(double) c1.distanceFrom(v1), (double) c2.distanceFrom(v1),
						(double) c1.distanceFrom(source), (double) c2.distanceFrom(source));
				addWindow(w, e.prev);
			}
	}

	/**
	 * Function used to convert a point memorized in circular coordinates
	 * into Cartesian coordinates with signe(y) = sign.
	 * @return a new point with such coordinates.
	 */
	static Point_2 ofCircCoordinates(Point_2 a, Point_2 b, double d0, double d1, boolean sign)
	{
		assert((double) a.getY() == 0 && (double) b.getY() == 0);
		assert((a.x - b.x < d0 + d1)&&(-a.x + b.x < d0 + d1));
		double b0 = (double) a.getX();
		double b1 = (double) b.getX();
		Point_2 result = new Point_2();
//		(b1-sx)2 + sy^2 = d1^2
//		(b0-sx)2 + sy^2 = d0^2
//		hence
//		d1^2 - (b1-sx)2 = d0^2 - (b0-sx)2
//		d1^2 - b1^2 +2*b1*sx = d0^2 - b0^2 +2*b0*sx
//		so
		double sx = ((d0*d0 - d1*d1 - b0*b0 + b1*b1)/(2*(b1 - b0)));
//		System.out.println(sx);
		if (sign)
			result.setY(Math.sqrt(Math.abs(d1*d1-(b1 - sx)*(b1 - sx))));
		else
			result.setY(-Math.sqrt(Math.abs(d1*d1-(b1 - sx)*(b1 - sx))));
		result.setX(sx);
		//System.out.println("b0 = " + b0 + "; b1 = " + b1 + "; d0 = " + d0 + "; d1 = " + d1 + " => x = "+sx+"; y = "+result.y);
		return result;
	}
	
	/**
	 * Function used to add the first three windows to the queue.
	 */
	private void initializeQueue(Point_2 source, Face<Point_3> face)
	{
		 
	}
	
	
	/**
	 * Function used to add the first windows to the queue.
	 */
	private void initializeQueue(Vertex<Point_3> v)
	{
		//Adding the opposite edges
		Halfedge<Point_3> first = v.getHalfedge().getOpposite().getNext();
		Halfedge<Point_3> temp = first;
		do {
			Window w = new Window(temp, 0, 0, temp.length, temp.prev.length, temp.next.length);
			w.first = true;
			temp.pair.addWindow(w);
			queue.add(w);
			temp = temp.getNext().getOpposite().getNext();
		} while(temp != first);		
		
		//Adding the adjacent edges
		first = v.getHalfedge();
		temp = first;
		do {
			Window w = new Window(temp, 0, 0, temp.length, temp.length, 0);
			w.first = true;
			temp.pair.addWindow(w);
			temp = temp.getNext().getOpposite();
		} while(temp != first);
	}
	
	
	/**
	 * Main function for the exact algorithm. Directly modify the window information in the edges.
	 */
	public void compute()
	{
		if(first == null || second == null) {
			System.out.println("Please define points first");
			return;
		}
		initializeQueue(first);
		Window current;
		while (!queue.isEmpty()) {
			System.out.println("----------------"); 
			current = queue.poll();
			if (!current.valid && Math.abs(current.b1 - current.b0) < epsilon)
				continue;
			propagate(current);

		}
		
	}
	
	public void computeInit()
	{
		if(first == null || second == null) {
			System.out.println("Please define points first");
			return;
		}
		initializeQueue(first);
	}
	
	public void computeOne()
	{
		System.out.println("----------------"); 
		Window current = queue.poll();
		current.display();
		System.out.println("Distance " + current.distance());
//		current.pair.one.getFace().color();
		if (!current.valid && Math.abs(current.b1 - current.b0) < epsilon)
			return;
		propagate(current);
	}
	
	
	public void backtrack()
	{
		Halfedge<Point_3> first = second.getHalfedge();
		Halfedge<Point_3> temp = first;
		Halfedge<Point_3> bestHalfhedge = null;
		Window best = null;
		Window tempWindow;
		double tempDistance;
		double bestDistance = Double.MAX_VALUE;
		double bestX = -1;
		do {
			if (temp == temp.pair.one) {
				tempWindow = temp.pair.getWindow(temp.length);
				tempDistance = tempWindow.tau ? tempWindow.d1 : tempWindow.d0;
				if (best==null || bestDistance > tempDistance) {
					bestDistance = tempDistance;
					best = tempWindow;
					bestHalfhedge = temp;
					bestX = temp.length;
				}
			} else {
				tempWindow = temp.pair.getWindow(0);
				tempDistance = tempWindow.tau ? tempWindow.d0 : tempWindow.d1;
				if (best==null || bestDistance > tempDistance) {
					bestDistance = tempDistance;
					best = tempWindow;
					bestHalfhedge = temp;
					bestX = 0;
				}				
			}
			temp = temp.getNext().getOpposite();
		} while(temp != first);
		double distance;
//		if ((bestHalfhedge.getVertex() == second && !best.tau)
//				|| (bestHalfhedge.getVertex() != second && best.tau))
//				distance = best.findTrack(0, 0);
//			else
//				distance = best.findTrack(bestHalfhedge.length, 0);
		if (best.tau)
				distance = best.findTrack(bestX, 0);
			else
				distance = best.findTrack(bestHalfhedge.length - bestX, 0);
		System.out.println("The geodesic measures "+ distance ) ;
	}
	



	/*static public void main(String[] args){
		Point_2 a = new Point_2(1,0);
		Point_2 b = new Point_2(8,0);
		Point_2 s = ofCircCoordinates(a, b, 5, Math.sqrt(32));
		System.out.println(s.getX() + " " + s.getY());
 
		Vertex<Point_3> o = new Vertex<Point_3>(new Point_3(3, 4, 0));
		Vertex<Point_3> v0 = new Vertex<Point_3>(new Point_3(0, 0, 0));
		Vertex<Point_3> v1 = new Vertex<Point_3>(new Point_3(4, -4, 0));
		Vertex<Point_3> v2 = new Vertex<Point_3>(new Point_3(7, 0, 0));
		Halfedge<Point_3> h0 = new Halfedge<Point_3>();
		h0.vertex = v0;
		Halfedge<Point_3> h1 = new Halfedge<Point_3>();
		h1.vertex = o;
		Halfedge<Point_3> h2 = new Halfedge<Point_3>();
		h2.vertex = v2;
		Halfedge<Point_3> h3 = new Halfedge<Point_3>();
		h3.vertex = v0;
		Halfedge<Point_3> h4 = new Halfedge<Point_3>();
		h4.vertex = v1;
		Halfedge<Point_3> h5 = new Halfedge<Point_3>();
		h5.vertex = v2;
		h0.prev = h1;
		h1.next = h0;
		h1.prev = h2;
		h2.next = h1;
		h2.prev = h0;
		h0.next = h2;
		h3.prev = h5;
		h5.next = h3;
		h5.prev = h4;
		h4.next = h5;
		h4.prev = h3;
		h3.next = h4;
		h2.opposite = h3;
		h3.opposite = h2;
		Halfedge<Point_3> h6 = new Halfedge<Point_3>();
		h6.vertex = o;
		h6.opposite = h0;
		h0.initialize();
		h1.initialize();
		h2.initialize();
		h3.initialize();
		h4.initialize();
		h5.initialize();
		h6.length = (double) ((Point_3) h6.vertex.getPoint()).distanceFrom((Point_3) h6.opposite.vertex.getPoint());
		
		h4.index = 4;
		h5.index = 5;
		
		Window w = new Window(h2, 0, 1, 5, 4, Math.sqrt(32));
		ExactAlgorithm algo = new ExactAlgorithm();
		algo.queue.add(w);
		algo.propagate(w);
		for(Window v : h4.windows)
			v.display();
		for(Window v : h5.windows)
			v.display();
		Window w2 = new Window(h2, 0, 3.5, 6, Math.sqrt(32), 4);
		algo.propagate(w2);
		for(Window v : h4.windows)
			v.display();
		for(Window v : h5.windows)
			v.display();
	}*/
}


