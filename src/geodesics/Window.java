package geodesics;

import Jcg.geometry.GeometricOperations_2;
import Jcg.geometry.Point_2;
import Jcg.geometry.Point_3;
import Jcg.geometry.Segment_2;
import Jcg.polyhedron.Halfedge;
import Jcg.polyhedron.HalfedgePair;

public class Window implements Comparable<Window>
{
	HalfedgePair pair;
	double sigma;
	double b0;
	double b1;
	double d0;
	double d1;
	boolean tau; // Determines whether the source is on the side of the first halfedge or the second
	boolean first = false;
	public boolean valid = true; //Temporary solution that avoid removing windows from the queue
	private boolean converted = false;

	public Window(HalfedgePair pair, double sigma, double b0, double b1, double d0, double d1, boolean tau, boolean converted)
	{
		this.pair = pair;
		this.sigma = sigma;
		this.b0 = b0;
		this.b1 = b1;
		this.d0 = d0;
		this.d1 = d1;
		this.tau = tau;
		this.converted = converted;
		if(tau)
			pair.one.getFace().color();
		else
			pair.two.getFace().color();

	}

	public Window(Halfedge<Point_3> h, double sigma, double b0, double b1, double d0, double d1)
	{
		assert(d0 < b1 - b0 + d1 + ExactAlgorithm.epsilon);
		assert(d1 < b1 - b0 + d0 + ExactAlgorithm.epsilon);
		this.pair = h.pair;
		this.sigma = sigma;
		this.b0 = b0;
		this.b1 = b1;
		this.d0 = d0;
		this.d1 = d1;
		tau = h.pair.one == h;
		if(tau)
			pair.one.getFace().color();
		else
			pair.two.getFace().color();

	}

	public void display()
	{
		System.out.println("Window of edge "+pair.one.index+" : { sigma="+sigma+", b0="+b0+", b1="+b1+", d0="+d0+", d1="+d1+" , tau="+tau+" }");
	}

	public void convert()
	{
		assert(!converted);
		converted = true;
		if (tau) return;
		double temp = b0;
		b0 = pair.one.length - b1;
		b1 = pair.one.length - temp;
		temp = d0;
		d0 = d1;
		d1 = temp;
	}

	public void unConvert()
	{
		assert(converted);
		converted = false;
		if (tau) return;
		double temp = b0;
		b0 = pair.one.length - b1;
		b1 = pair.one.length - temp;
		temp = d0;
		d0 = d1;
		d1 = temp;
	}

	public Halfedge<Point_3> getHalfedge()
	{
		return tau ? pair.one : pair.two;
	}


	private class OverlapData {
		Point_2 o_b0Point;
		Point_2 o_b1Point;
		Point_2 o_s;
		Point_2 w_b0Point;
		Point_2 w_b1Point;
		Point_2 w_s;
		double delta0, delta1; // Abscissa of the window intersection bounds
		double wD0, wD1, oD0, oD1; // Distances to the source of the extremal points of window intersection

		OverlapData(Window w) 
		{
			assert(w.pair == pair);
			o_b0Point = new Point_2(b0,0);
			o_b1Point = new Point_2(b1,0);
			o_s = ExactAlgorithm.ofCircCoordinates(o_b0Point, o_b1Point, d0, d1, true);
			w_b0Point = new Point_2(w.b0,0);
			w_b1Point = new Point_2(w.b1,0);
			w_s = ExactAlgorithm.ofCircCoordinates(w_b0Point, w_b1Point, w.d0, w.d1, true);
		}
	}

	/**
	 * Checks if two windows overlap
	 * @param the other window
	 * @return true iff there is an overlap
	 */
	private boolean overlapExists(Window w)
	{
		return (!(b1 <= w.b0 + ExactAlgorithm.epsilon || w.b1 <= b0 + ExactAlgorithm.epsilon));
	}

	/**
	 * Computes the window intersection bounds and their distance to the sources,
	 * checks if one of the windows is better than the other on the full interval
	 * @param w the other window
	 * @return	1 if the object is better, -1 if the argument is, 0 otherwise
	 */
	private int fullCut(Window w, OverlapData data)
	{
		//Computing the bounds
		double o_sx = (double) data.o_s.getX();
		double w_sx = (double) data.w_s.getX();
		double o_sy = (double) data.o_s.getY();
		double w_sy = (double) data.w_s.getY();
		if (b0 < w.b0 + ExactAlgorithm.epsilon) {
			data.wD0 = w.d0;
			data.oD0 = Math.sqrt(o_sy*o_sy + (o_sx - w.b0)*(o_sx - w.b0));
			data.delta0 = w.b0;
		} else {
			data.oD0 = d0;
			data.wD0 = Math.sqrt(w_sy*w_sy + (w_sx - b0)*(w_sx - b0));		
			data.delta0 = b0;	
		}
		if (w.b1 < b1 + ExactAlgorithm.epsilon) {
			data.wD1 = w.d1;
			data.oD1 = Math.sqrt(o_sy*o_sy + (o_sx - w.b1)*(o_sx - w.b1));
			data.delta1 = w.b1;
		} else {
			data.oD1 = d1;
			data.wD1 = Math.sqrt(w_sy*w_sy + (w_sx - b1)*(w_sx - b1));		
			data.delta1 = b1;	
		}
		assert(data.delta0 < data.delta1 + ExactAlgorithm.epsilon);


		//		//Computing the distance to the source of the window intersection bounds
		//		double oMin, oMax, wMin, wMax;
		//		//Checking whether the source is on the left, between or on the right of the bounds
		//		if (o_sx < data.delta0 + ExactAlgorithm.epsilon)
		//			oMin = data.oD0;
		//		else if (data.delta1 < o_sx + ExactAlgorithm.epsilon)
		//			oMin = data.oD1;
		//		else
		//			oMin = o_sy;
		//		oMax = Math.max(data.oD0, data.oD1);
		//		if (w_sx < data.delta0 + ExactAlgorithm.epsilon)
		//			wMin = data.wD0;
		//		else if (data.delta1 < w_sx + ExactAlgorithm.epsilon)
		//			wMin = data.wD1;
		//		else
		//			wMin = w_sy;
		//		wMax = Math.max(data.wD0, data.wD1);
		//
		//		//Checking whether there is a full cut
		//		if(oMax <= wMin + ExactAlgorithm.epsilon)
		//			return 1;
		//		else if (wMax <= oMin + ExactAlgorithm.epsilon)
		//			return -1;
		//		else
		//			return 0;

		if (data.oD0 < data.wD0 + ExactAlgorithm.epsilon && data.oD1 < data.wD1 + ExactAlgorithm.epsilon)
			return 1;
		if (data.wD0 < data.oD0 + ExactAlgorithm.epsilon && data.wD1 < data.oD1 + ExactAlgorithm.epsilon)
			return -1;
		return 0;
	}

	/**
	 * Returns the boundary between the intervals over which the overlapping windows are better
	 * Consists in solving the quadratic of the paper
	 * @param w a window that partially overlaps with the object
	 * @return the boundary
	 */
	private double overlapBoundary(Window w, OverlapData data)
	{
		//Overlap, compute new bound
		double o_sx = (double) data.o_s.getX();
		double w_sx = (double) data.w_s.getX();
		double o_sy = (double) data.o_s.getY();
		double w_sy = (double) data.w_s.getY();
		double alpha = w_sx - o_sx;
		double beta = w.sigma - sigma;
		double squareS0 = o_sx*o_sx + o_sy*o_sy;
		double squareS1 = w_sx*w_sx + w_sy*w_sy;
		double squareBeta = beta*beta;
		double gamma = squareS0 - squareS1 - squareBeta;
		double A = alpha*alpha - squareBeta;
		double B = gamma*alpha + 2*w_sx*squareBeta;
		assert(Math.abs(B*B-4*A*(gamma*gamma/4 - squareS1*squareBeta)) < ExactAlgorithm.epsilon);
		double p = (-B)/(2*A);
		assert(data.delta0 < p + ExactAlgorithm.epsilon);
		assert(data.delta1 > p - ExactAlgorithm.epsilon);
		return p;
	}


	public int overlap(Window w)
	{
		assert(w.pair == w.pair);
		//Computing the useful points
		OverlapData data = new OverlapData(w);
		if(!overlapExists(w))
			return 0;
		int fullCutResult = fullCut(w, data);
		if(fullCutResult == 1) //object better on full interval
			if(Math.abs(data.delta0 - w.b0) < ExactAlgorithm.epsilon)
				if(Math.abs(data.delta1 - w.b1) < ExactAlgorithm.epsilon)
					return 1; //delete the argument
				else {
					w.b0 = data.delta1;
					w.d0 = data.wD1;
					return 0;
				}
			else {
				if (!(Math.abs(data.delta1 - w.b1) < ExactAlgorithm.epsilon)) { // Double cut
					Window right = new Window(w.pair, w.sigma, b1, w.b1, data.wD1, w.d1, w.tau, w.converted);
					right.unConvert();
					pair.addWindowLater(right);
				}
				w.b1 = b0;
				w.d1 = data.wD0;
				return 0;
			}
		else if (fullCutResult == -1) //argument better on full interval		
			if(Math.abs(data.delta0 - b0) < ExactAlgorithm.epsilon)
				if(Math.abs(data.delta1 - b1) < ExactAlgorithm.epsilon)
					return -1; //delete the object
				else {
					b0 = data.delta1;
					d0 = data.oD1;
					return 0;
				}
			else {
				if (!(Math.abs(data.delta1 - b1) < ExactAlgorithm.epsilon)) {
					Window right = new Window(pair, sigma, w.b1, b1, data.oD1, d1, tau, converted);
					right.unConvert();
					pair.addWindowLater(right);
				}
				b1 = w.b0;
				d1 = data.oD0;
				return 0;
			}
		else {
			double p = overlapBoundary(w, data);
			double oDp = Math.sqrt(data.o_s.y*data.o_s.y + (data.o_s.x - p)*(data.o_s.x - p));
			double wDp = Math.sqrt(data.w_s.y*data.w_s.y + (data.w_s.x - p)*(data.w_s.x - p));
			assert(Math.abs(oDp - wDp) < ExactAlgorithm.epsilon);

			if (data.wD0 < data.oD0 + ExactAlgorithm.epsilon) { //Argument better on the left of p
				assert(data.oD1 < data.wD1 + ExactAlgorithm.epsilon);
				if (b0 < data.delta0 - ExactAlgorithm.epsilon) { //Object goes further than argument on the left, double cut
					Window left = new Window(pair, sigma, b0, w.b0, d0, data.oD0, tau, converted);
					left.unConvert();			
					pair.addWindowLater(left);
				}
				if (w.b1 > data.delta1 + ExactAlgorithm.epsilon) {
					Window right = new Window(w.pair, w.sigma, b1, w.b1, data.wD1, w.d1, w.tau, w.converted);
					right.unConvert();
					pair.addWindowLater(right);
				}
				b0 = p;
				d0 = oDp;
				w.b1 = p;
				w.d1 = wDp;
				return 0;
			} else { assert(data.wD1 < data.oD1 + ExactAlgorithm.epsilon); //Argument better on the right of p
			if (w.b0 < data.delta0 - ExactAlgorithm.epsilon) {
				Window left = new Window(w.pair, w.sigma, w.b0, data.delta0, w.d0, data.wD0, w.tau, w.converted);
				left.unConvert();			
				pair.addWindowLater(left);		
			}
			if (b1 > data.delta1 + ExactAlgorithm.epsilon) {
				Window right = new Window(pair, sigma, data.delta1, b1, data.oD1, d1, tau, converted);
				right.unConvert();
				pair.addWindowLater(right);
			}
			b1 = p;
			d1 = oDp;
			w.b0 = p;
			w.d0 = wDp;
			return 0;
			}

			//			if (Math.abs(data.delta0 - b0) < ExactAlgorithm.epsilon) {
			//				if(Math.abs(data.delta1-w.b1) < ExactAlgorithm.epsilon) {
			//					b0 = p;
			//					d0 = oDp;
			//					w.b1 = p;
			//					w.d1 = wDp;
			//				}
			//				else {
			//	
			//				}
			//			} else if (Math.abs(data.delta0 - w.b0) < ExactAlgorithm.epsilon){
			//				//assert(Math.abs(data.delta1 - b1) < ExactAlgorithm.epsilon); wrong assumption
			//				w.b0 = p;
			//				w.d0 = wDp;
			//				b1 = p;
			//				d1 = oDp;
			//			} else
			//				return 0;
		}
	}


	/**
	 * Function used to compute the new window bounds when 2 windows overlap.
	 * Must ensure that the resulting windows are larger than 0.
	 * @return 0 if there was no overlap, 1 if there was some, 2 if the object is deleted, 3 if the argument is

	public int overlapOld(Window w)
	{
		//No overlap
		if(b1 <= w.b0 || w.b1 <= b0)
			return 0;
		//Overlap, compute new bound
		assert(w.h == h);
		Point_2 b0Point = new Point_2(b0,0);
		Point_2 b1Point = new Point_2(b1,0);
		Point_2 s0 = ExactAlgorithm.ofCircCoordinates(b0Point, b1Point, d0, d1);
		Point_2 b2Point = new Point_2(w.b0,0);
		Point_2 b3Point = new Point_2(w.b1,0);
		Point_2 s1 = ExactAlgorithm.ofCircCoordinates(b2Point, b3Point, w.d0, w.d1);
		double s0x = (double) s0.getX();
		double s1x = (double) s1.getX();
		double s0y = (double) s0.getY();
		double s1y = (double) s1.getY();
		double alpha = s1x - s0x;
		double beta = w.sigma - sigma;
		double squareS0 = s0x*s0x + s0y*s0y;
		double squareS1 = s1x*s1x + s1y*s1y;
		double squareBeta = beta*beta;
		double gamma = squareS0 - squareS1 - squareBeta;
		double A = alpha*alpha - squareBeta;
		double B = gamma*alpha + 2*s1x*squareBeta;
		//		double C = gamma*gamma/4 - squareS1*squareBeta;
		//		double delta = B*B - 4*A*C;
		double p = (-B)/(2*A);
		//Assigning computed bound
		if ((p<=b0 || p>=b1) {
			return 2;
		} else if (p<=w.b0 || p>=w.b1) {
			return 3;
		} else if (b1 > w.b0) {
			b1 = p;
			w.b0 = p;
		} else if (b0 < w.b1) {
			b0 = p;
			w.b1 = p;
		}
		else
			throw new RuntimeException();
		return 1;
	}
	 */
	public double distance()
	{
		return (new Double(Math.min(d0, d1) + sigma));
	}

	@Override
	public int hashCode()
	{
		return (new Double(distance())).hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Window))
			return false;
		Window w = (Window) o;
		return new Double(distance()).equals(w.distance());
	}

	@Override
	public int compareTo(Window w)
	{
		return new Double(distance()).compareTo(w.distance());
	}


	public double findTrack(double x, double distance) // x is given in coordinates of the window
	{
		assert(x >= b0 - ExactAlgorithm.epsilon && x <= b1 + ExactAlgorithm.epsilon); 
		Halfedge<Point_3> h = getHalfedge();
		h.getFace().underPath = true;
		Point_2 b0Point = new Point_2(b0,0);
		Point_2 b1Point = new Point_2(b1,0);
		Point_2 v0 = new Point_2(0,0);
		Point_2 v1 = new Point_2(h.length,0);
		Point_2 s = ExactAlgorithm.ofCircCoordinates(b0Point, b1Point, d0, d1, true);
		Point_2 v2 = ExactAlgorithm.ofCircCoordinates(v0, v1, h.prev.length, h.next.length, true);
		Point_2 g = new Point_2(x, 0);
		if (first) {
			h.face.setPath(h, x, null, -1);
			return distance + (double) g.distanceFrom(s);
		}
		Window nextWindow;
		Point_2 intersection;
		double nextX;
		if (GeometricOperations_2.orientation(g, v2, s)==1) {
			intersection = GeometricOperations_2.intersect(new Segment_2(v0, v2), new Segment_2(g, s));
			nextX = (double) v0.distanceFrom(intersection);
			h.face.setPath(h, x, h.prev, h.prev.length - nextX);
			assert(nextX <= h.prev.length);
			nextX = h.prev == h.prev.pair.one ? h.prev.length - nextX : nextX;
			nextWindow = h.prev.pair.getWindow(nextX);
			System.out.println("Current distance in edge " + (tau ? pair.one.index : pair.two.index) + " is " + distance);
			return nextWindow.findTrack(nextWindow.tau ? nextX : h.prev.length - nextX, 
					distance + (double) g.distanceFrom(intersection));
		} else {
			intersection = GeometricOperations_2.intersect(new Segment_2(v1, v2), new Segment_2(g, s));
			nextX = (double) v1.distanceFrom(intersection);
			h.face.setPath(h, x, h.next, nextX);
			assert(nextX <= h.next.length);
			nextX = h.next == h.next.pair.one ? nextX : h.next.length - nextX;
			nextWindow = h.next.pair.getWindow(nextX);	
			System.out.println("Current distance in edge " + (tau ? pair.one.index : pair.two.index) + " is " + distance);
			return nextWindow.findTrack(nextWindow.tau ? nextX : h.next.length - nextX, 
					distance + (double) g.distanceFrom(intersection));
		}
	}

	public boolean contains(double x) { // x is given in "one" coordinates
		
		return (tau && b0 <= x + ExactAlgorithm.epsilon && x <= b1 + ExactAlgorithm.epsilon) 
				|| (!tau && (pair.one.length - b1) <= x + ExactAlgorithm.epsilon  && x <=(pair.one.length - b0)+ ExactAlgorithm.epsilon);
	}

}