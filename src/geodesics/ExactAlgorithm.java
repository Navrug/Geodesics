package geodesics;

import java.util.PriorityQueue;

import Jcg.geometry.GeometricOperations_2;
import Jcg.geometry.Point_2;
import Jcg.geometry.Point_3;
import Jcg.geometry.Segment_2;
import Jcg.geometry.Vector_2;
import Jcg.polyhedron.Face;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.Polyhedron_3;
import Jcg.polyhedron.Vertex;

public class ExactAlgorithm
{
	private PriorityQueue<Window> queue = new PriorityQueue<Window>();
	public Vertex<Point_3> first;
	public Vertex<Point_3> second;
	public static double epsilon = 10e-8;


	public void setFirstPoint(Vertex<Point_3> first){
		if (this.first != null)
			this.first.extremum = false;
		this.first = first; 
		first.extremum = true;
		System.out.println("Set vertex " + first.index + " as first point.");
	}

	public int getFirstId()
	{
		return first.index;
	}

	public void setSecondPoint(Vertex<Point_3> second){
		if (this.second != null)
			this.second.extremum = false;
		this.second = second; 
		second.extremum = true;
		System.out.println("Set vertex " + second.index + " as second point.");
	}

	/**
	 * Tries to add a new window, if successful propagates it.
	 * @return whether the window was added to the edge
	 */
	boolean addWindow(Window w, Halfedge<Point_3> h)
	{
		boolean result = h.pair.addWindow(w);
		if (result) {
//			w.display();
			queue.add(w);
		}
		return result;
	}

	public static boolean equalPoints(Point_2 a, Point_2 b)
	{
		return (double) a.distanceFrom(b) < ExactAlgorithm.epsilon;
	}

	/**
	 *  !!! Beware of rounding errors in the edge borders, may need to handle them more properly
	 *  There are three main cases depending on the orientation of the extreme rays: fully in first edge,
	 *  in both edges or in the second edge.
	 */
	void propagate(Window v)
	{
		Halfedge<Point_3> e = v.tau ? v.pair.two : v.pair.one; //We propagate to the opposite direction of the source
		assert(v.b0 < v.b1 - ExactAlgorithm.epsilon);
		Point_2 b0Point = new Point_2(v.b0, 0);
		Point_2 b1Point = new Point_2(v.b1,0);
		Point_2 source = ofCircCoordinates(b0Point, b1Point, v.d0, v.d1, true);
		Point_2 v0 = new Point_2(0,0);
		Point_2 v2 = new Point_2(e.length, 0);
		Point_2 v1 = ofCircCoordinates(v0, v2, e.next.length, e.prev.length, false);
		int orientation1 = GeometricOperations_2.orientation(source, b0Point, v1);
		int orientation2 = GeometricOperations_2.orientation(source, b1Point, v1);
		assert(!(orientation1 == 0 && orientation2 == 0)); // this implies that b0==b1, impossible
		if (equalPoints(v0, source) || equalPoints(v2, source)) {
			//assert(equalPoints(b0Point, source) || equalPoints(b1Point, source));
			doublePropagation(v0, v1, v2, b0Point, b1Point, source, e, v);
			return;
		}
		if (source.y < ExactAlgorithm.epsilon) //Not sure how this can happen, but it does
			return;
		else if (orientation1 == 1) {
			if (orientation2 >= 0) { //Propagates fully in the first edge
				boolean saddle = (/*e.getOpposite().getVertex().isSaddle()
						&&*/ Math.abs(v.b1 - e.length) < ExactAlgorithm.epsilon);
				leftPropagation(v0, v1, v2, b0Point, b1Point, source, e, v, saddle);

			} else { //Propagates in both edges
				doublePropagation(v0, v1, v2, b0Point, b1Point, source, e, v);
			}
		}
		else { //Propagates in the second edge
			assert(orientation2 != 1);
			boolean saddle =  (/*e.getVertex().isSaddle() &&*/ v.b0 < ExactAlgorithm.epsilon);
			rightPropagation(v0, v1, v2, b0Point, b1Point, source, e, v, saddle);
		}
	}

	private void leftPropagation(Point_2 v0, Point_2 v1, Point_2 v2,
			Point_2 b0Point, Point_2 b1Point, Point_2 source,
			Halfedge<Point_3> e, Window v, boolean saddle)
	{
		Point_2 c1 = GeometricOperations_2.intersect(new Segment_2(source, b0Point), new Segment_2(v0, v1));
		Point_2 c2 = GeometricOperations_2.intersect(new Segment_2(source, b1Point), new Segment_2(v0, v1));
		Window w = new Window(e.next, v.sigma,
				(double) c1.distanceFrom(v0), (double) c2.distanceFrom(v0),
				(double) c1.distanceFrom(source), (double) c2.distanceFrom(source));
		addWindow(w, e.next);
		if (!saddle)
			return;
		w = new Window(e.next, v.sigma + v.d1,
				(double) c2.distanceFrom(v0), e.next.length,
				(double) c2.distanceFrom(v2), e.prev.length);
		addWindow(w, e.next);
		w = new Window(e.prev, v.sigma + v.d1,
				0, e.prev.length,
				e.prev.length, 0);
		addWindow(w, e.prev);
	}

	private void doublePropagation(Point_2 v0, Point_2 v1, Point_2 v2,
			Point_2 b0Point, Point_2 b1Point, Point_2 source,
			Halfedge<Point_3> e, Window v)
	{
		Point_2 c1;
		Point_2 c2;
		if (equalPoints(v0, source))
			c1 = v0;
		else
			c1 = GeometricOperations_2.intersect(new Segment_2(source, b0Point), new Segment_2(v0, v1));
		if (equalPoints(v2, source))
			c2 = v2;
		else
			c2 = GeometricOperations_2.intersect(new Segment_2(source, b1Point), new Segment_2(v1, v2));
		Window w = new Window(e.next, v.sigma,
				(double) c1.distanceFrom(v0), e.next.length,
				(double) c1.distanceFrom(source), (double) v1.distanceFrom(source));
		addWindow(w, e.next);
		w = new Window(e.prev, v.sigma,
				(double) 0, (double) c2.distanceFrom(v1),
				(double) v1.distanceFrom(source), (double) c2.distanceFrom(source));
		addWindow(w, e.prev);
	}

	private void rightPropagation(Point_2 v0, Point_2 v1, Point_2 v2,
			Point_2 b0Point, Point_2 b1Point, Point_2 source,
			Halfedge<Point_3> e, Window v, boolean saddle)
	{
		Point_2 c1 = GeometricOperations_2.intersect(new Segment_2(source, b0Point), new Segment_2(v1, v2));
		Point_2 c2 = GeometricOperations_2.intersect(new Segment_2(source, b1Point), new Segment_2(v1, v2));
		Window w = new Window(e.prev, v.sigma,
				(double) c1.distanceFrom(v1), (double) c2.distanceFrom(v1),
				(double) c1.distanceFrom(source), (double) c2.distanceFrom(source));
		addWindow(w, e.prev);
		if (!saddle)
			return;
		w = new Window(e.next, v.sigma + v.d0,
				0, e.next.length,
				0, e.next.length);
		addWindow(w, e.next);
		w = new Window(e.prev, v.sigma + v.d0,
				0, (double) c1.distanceFrom(v1),
				e.next.length, (double) c1.distanceFrom(v0));
		addWindow(w, e.prev);
	}

	/**
	 * Function used to convert a point memorized in circular coordinates
	 * into Cartesian coordinates with sign(y) = sign.
	 * @return a new point with such coordinates.
	 */
	public static Point_2 ofCircCoordinates(Point_2 a, Point_2 b, double d0, double d1, boolean sign)
	{
		assert((double) a.getY() == 0 && (double) b.getY() == 0);
		assert((a.x - b.x < d0 + d1 + ExactAlgorithm.epsilon)&&(-a.x + b.x < d0 + d1 + ExactAlgorithm.epsilon));
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
		if (sign)
			result.setY(Math.sqrt(Math.abs(d1*d1-(b1 - sx)*(b1 - sx))));
		else
			result.setY(-Math.sqrt(Math.abs(d1*d1-(b1 - sx)*(b1 - sx))));
		result.setX(sx);
		return result;
	}

	
	/**
	 * Function used to add the first windows to the queue.
	 */
	private void initializeQueue(Vertex<Point_3> v)
	{
		//Adding the adjacent edges
		Halfedge<Point_3> first = v.getHalfedge();
		Halfedge<Point_3> temp = first;
		do {
			Window w = new Window(temp, 0, 0, temp.length, temp.length, 0);
			w.first = true;
			temp.pair.addWindow(w);
			queue.add(w);
			temp = temp.getNext().getOpposite();
		} while(temp != first);
	}


	/**
	 * Main function for the exact algorithm. Directly modify the window information in the edges.
	 */
	public void compute()
	{
		computeInit();
		Window current;
		while (!queue.isEmpty()) {
			current = queue.poll();
			if (!current.valid && Math.abs(current.b1 - current.b0) < epsilon)
				continue;
			propagate(current);
		}
	}

	public void computeInit()
	{
		if(first == null) {
			System.out.println("Please define source point first");
			return;
		}
		initializeQueue(first);
	}

	public boolean computeOne()
	{ 
		if (queue.isEmpty()) {
			System.out.println("Empty queue!");
			return false;
		}
		Window current = queue.poll();
		System.out.println("Distance " + current.distance());
		if (!current.valid && Math.abs(current.b1 - current.b0) < epsilon)
			return true;
		propagate(current);
		return true;
	}

	/**
	 * Computes the cosinus of the angle formed by the two line going from
	 * the origin to theses points.
	 * @return
	 */
	public static double cos(Point_2 origin, Point_2 a, Point_2 b)
	{
		Vector_2 u = new Vector_2(origin, a);
		Vector_2 v = new Vector_2(origin, b);
		return (double) u.innerProduct(v)
				/Math.sqrt((double) u.squaredLength() * (double) v.squaredLength());
	}


	public void backtrack()
	{
		double distance = second.findBestTrack(Double.MAX_VALUE);
		System.out.println("The geodesic measures "+ distance ) ;
	}

}


