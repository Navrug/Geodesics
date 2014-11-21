package geodesics;
import java.util.HashSet;

import Jcg.geometry.Point_3;
import Jcg.polyhedron.Halfedge;


public class GHalfedge extends Halfedge<Point_3>
{
	double length;
	HashSet<Window> windows;
	
	public GHalfedge(Halfedge<Point_3> h) 
	{
		face = h.face;
		prev = h.prev;
		next = h.next;
		
		
	}
	


}
