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
	public double sigma;
	public double b0;
	public double b1;
	public double d0;
	public double d1;
	public boolean tau; // Determines whether the source is on the side of the first halfedge or the second
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
		double p, odp, wdp;

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
		
		void pSet(double p, double odp, double wdp)
		{
			this.p = p;
			this.odp = odp;
			this.wdp = wdp;
		}
	}

	/**
	 * Checks if two windows overlap
	 * @param the other window
	 * @return true iff there is an overlap
	 */
	private boolean overlapExists(Window w)
	{
		return (!(b1 <= w.b0 - ExactAlgorithm.epsilon || w.b1 <= b0 - ExactAlgorithm.epsilon));
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
			data.wD0 = w.d0 + w.sigma;
			data.oD0 = Math.sqrt(o_sy*o_sy + (o_sx - w.b0)*(o_sx - w.b0)) + sigma;
			data.delta0 = w.b0;
		} else {
			data.oD0 = d0 + sigma;
			data.wD0 = Math.sqrt(w_sy*w_sy + (w_sx - b0)*(w_sx - b0)) + w.sigma;		
			data.delta0 = b0;	
		}
		if (w.b1 < b1 + ExactAlgorithm.epsilon) {
			data.wD1 = w.d1 + w.sigma;
			data.oD1 = Math.sqrt(o_sy*o_sy + (o_sx - w.b1)*(o_sx - w.b1)) + sigma;
			data.delta1 = w.b1;
		} else {
			data.oD1 = d1 + sigma;
			data.wD1 = Math.sqrt(w_sy*w_sy + (w_sx - b1)*(w_sx - b1)) + w.sigma;		
			data.delta1 = b1;	
		}
		assert(data.delta0 < data.delta1 + ExactAlgorithm.epsilon);
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
	private void overlapBoundary(Window w, OverlapData data)
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
		assert(Math.abs(A) > ExactAlgorithm.epsilon*ExactAlgorithm.epsilon);
		double B = gamma*alpha + 2*w_sx*squareBeta;
		double discriminant = Math.abs(B*B-4*A*(gamma*gamma/4 - squareS1*squareBeta));
		assert(discriminant >= 0);
		double p1 = (-B - Math.sqrt(discriminant))/(2*A);
		double p2 = (-B + Math.sqrt(discriminant))/(2*A);
		double odp1 = Math.sqrt(data.o_s.y*data.o_s.y + (data.o_s.x - p1)*(data.o_s.x - p1));
		double odp2 = Math.sqrt(data.o_s.y*data.o_s.y + (data.o_s.x - p2)*(data.o_s.x - p2));
		double wdp1 = Math.sqrt(data.w_s.y*data.w_s.y + (data.w_s.x - p1)*(data.w_s.x - p1));
		double wdp2 = Math.sqrt(data.w_s.y*data.w_s.y + (data.w_s.x - p2)*(data.w_s.x - p2));
		double eps1 = Math.abs(sigma + odp1 - w.sigma - wdp1);
		double eps2 = Math.abs(sigma + odp2 - w.sigma - wdp2);
		if ((data.delta0 > p1 - ExactAlgorithm.epsilon)
				|| (data.delta1 < p1 + ExactAlgorithm.epsilon)) {
			data.pSet(p2, odp2, wdp2);
			assert(eps2 < ExactAlgorithm.epsilon);
		} else if ((data.delta0 > p2 - ExactAlgorithm.epsilon)
				|| (data.delta1 < p2 + ExactAlgorithm.epsilon)) {
			data.pSet(p1, odp1, wdp1);
			assert(eps1 < ExactAlgorithm.epsilon);
		} else if (eps1 < eps2) {
			data.pSet(p1, odp1, wdp1);
			assert(eps1 < ExactAlgorithm.epsilon);
		} else {
			data.pSet(p2, odp2, wdp2);
			assert(eps2 < ExactAlgorithm.epsilon);
		}
		assert(data.delta0 < data.p - ExactAlgorithm.epsilon);
		assert(data.delta1 > data.p + ExactAlgorithm.epsilon);
		return;
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
					w.d0 = data.wD1 - w.sigma;
					return 0;
				}
			else {
				if (!(Math.abs(data.delta1 - w.b1) < ExactAlgorithm.epsilon)) { // Double cut
					Window right = new Window(w.pair, w.sigma, b1, w.b1, data.wD1 - w.sigma, w.d1, w.tau, w.converted);
					right.unConvert();
					pair.addWindowLater(right);
				}
				w.b1 = b0;
				w.d1 = data.wD0 - w.sigma;
				return 0;
			}

		else if (fullCutResult == -1) //argument better on full interval		
			if(Math.abs(data.delta0 - b0) < ExactAlgorithm.epsilon)
				if(Math.abs(data.delta1 - b1) < ExactAlgorithm.epsilon)
					return -1; //delete the object
				else {
					b0 = data.delta1;
					d0 = data.oD1 - sigma;
					return 0;
				}
			else {
				if (!(Math.abs(data.delta1 - b1) < ExactAlgorithm.epsilon)) {
					Window right = new Window(pair, sigma, w.b1, b1, data.oD1 - sigma, d1, tau, converted);
					right.unConvert();
					pair.addWindowLater(right);
				}
				b1 = w.b0;
				d1 = data.oD0 - sigma;
				return 0;
			}
		else {
			overlapBoundary(w, data);
			assert(Math.abs(sigma + data.odp - w.sigma - data.wdp) < ExactAlgorithm.epsilon);
			if (data.wD0 < data.oD0/* + ExactAlgorithm.epsilon*/) { //Argument better on the left of p
				assert(data.oD1 < data.wD1 + ExactAlgorithm.epsilon);
				if (b0 < data.delta0 - ExactAlgorithm.epsilon) { //Object goes further than argument on the left, double cut
					Window left = new Window(pair, sigma, b0, w.b0, d0, data.oD0 - sigma, tau, converted);
					left.unConvert();			
					pair.addWindowLater(left);
				}
				if (w.b1 > data.delta1 + ExactAlgorithm.epsilon) {//Argument goes further than object on the left, double cut
					Window right = new Window(w.pair, w.sigma, b1, w.b1, data.wD1 - w.sigma, w.d1, w.tau, w.converted);
					right.unConvert();
					pair.addWindowLater(right);
				}
				b0 = data.p;
				d0 = data.odp;
				w.b1 = data.p;
				w.d1 = data.wdp;
				return 0;
			} else { assert(data.wD1 < data.oD1/* + ExactAlgorithm.epsilon*/); //Argument better on the right of p
			if (w.b0 < data.delta0 - ExactAlgorithm.epsilon) {
				Window left = new Window(w.pair, w.sigma, w.b0, data.delta0, w.d0, data.wD0 - w.sigma, w.tau, w.converted);
				left.unConvert();			
				pair.addWindowLater(left);		
			}
			if (b1 > data.delta1 + ExactAlgorithm.epsilon) {
				Window right = new Window(pair, sigma, data.delta1, b1, data.oD1 - sigma, d1, tau, converted);
				right.unConvert();
				pair.addWindowLater(right);
			}
			b1 = data.p;
			d1 = data.odp;
			w.b0 = data.p;
			w.d0 = data.wdp;
			return 0;
			}
		}
	}


	public double distance()
	{
		return (new Double(Math.min(d0, d1) + sigma));
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

		if (ExactAlgorithm.equalPoints(s, v0)) {
			h.face.setPath(h, x, h.prev, h.prev.length);
			if (sigma == 0)
				return distance + (double) g.distanceFrom(s);
			return distance + (double) g.distanceFrom(s) + h.getOpposite().getVertex().findBestTrack(sigma);
		} else if (ExactAlgorithm.equalPoints(s, v1)) {
			h.face.setPath(h, x, h.next, 0);
			if (sigma == 0)
				return distance + (double) g.distanceFrom(s);
			return distance + (double) g.distanceFrom(s) + h.getVertex().findBestTrack(sigma);
		} else if (ExactAlgorithm.equalPoints(s, v2)) {
			h.face.setPath(h, x, null, -1);
			if (sigma == 0)
				return distance + (double) g.distanceFrom(s);
			return distance + (double) g.distanceFrom(s) + h.getNext().getVertex().findBestTrack(sigma);
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
			assert(nextWindow != null);
//			System.out.println("Current distance in edge " + (tau ? pair.one.index : pair.two.index) + " is " + distance);
			return nextWindow.findTrack(nextWindow.tau ? nextX : h.prev.length - nextX, 
					distance + (double) g.distanceFrom(intersection));
		} else {
			intersection = GeometricOperations_2.intersect(new Segment_2(v1, v2), new Segment_2(g, s));
			nextX = (double) v1.distanceFrom(intersection);
			h.face.setPath(h, x, h.next, nextX);
			nextX = h.next == h.next.pair.one ? nextX : h.next.length - nextX;
			nextWindow = h.next.pair.getWindow(nextX);	
			assert(nextWindow != null);
//			System.out.println("Current distance in edge " + (tau ? pair.one.index : pair.two.index) + " is " + distance);
			return nextWindow.findTrack(nextWindow.tau ? nextX : h.next.length - nextX, 
					distance + (double) g.distanceFrom(intersection));
		}
	}

	public boolean contains(double x) { // x is given in "one" coordinates

		return (tau && b0 <= x + ExactAlgorithm.epsilon && x <= b1 + ExactAlgorithm.epsilon) 
				|| (!tau && (pair.one.length - b1) <= x + ExactAlgorithm.epsilon  && x <=(pair.one.length - b0)+ ExactAlgorithm.epsilon);
	}

}