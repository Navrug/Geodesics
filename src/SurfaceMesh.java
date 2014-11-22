import Jcg.geometry.*;
import Jcg.polyhedron.*;


/**
 * Class for rendering a surface triangle mesh (using Processing)
 * 
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class SurfaceMesh {

	double scaleFactor=60; // scaling factor: useful for 3d rendering
	MeshViewer view; // Processing 3d frame (where meshes are rendered)
	public Polyhedron_3<Point_3> polyhedron3D; // triangle mesh

	/**
	 * Create a surface mesh from an OFF file
	 */	
	public SurfaceMesh(MeshViewer view, String filename) {
		this.view=view;

		// shared vertex representation of the mesh
		SharedVertexRepresentation sharedVertex=new SharedVertexRepresentation(filename);
		LoadMesh<Point_3> load3D=new LoadMesh<Point_3>();

		polyhedron3D=load3D.createTriangleMesh(sharedVertex.points,sharedVertex.faceDegrees,
				sharedVertex.faces,sharedVertex.sizeHalfedges);

		//System.out.println(polyhedron3D.verticesToString());   	
		//System.out.println(polyhedron3D.facesToString());
		polyhedron3D.isValid(false);

		this.scaleFactor=this.computeScaleFactor();
	}

	/**
	 * Draw a segment between two points
	 */	
	public void drawSegment(Point_3 p, Point_3 q) {
		float s=(float)this.scaleFactor;
		float x1=(float)p.getX().doubleValue()*s;
		float y1=(float)p.getY().doubleValue()*s;
		float z1=(float)p.getZ().doubleValue()*s;
		float x2=(float)q.getX().doubleValue()*s;
		float y2=(float)q.getY().doubleValue()*s;
		float z2=(float)q.getZ().doubleValue()*s;
		this.view.line(	x1, y1, z1, x2, y2, z2 );		
	}

	/**
	 * Draw a vertex (as a small sphere)
	 */	
	public void drawVertex(Vertex<Point_3> v) {
		float s=(float)this.scaleFactor;
		float x1=(float)v.getPoint().getX().doubleValue()*s;
		float y1=(float)v.getPoint().getY().doubleValue()*s;
		float z1=(float)v.getPoint().getZ().doubleValue()*s;

		if (v.extremum)
			view.fill(0f,250f,0f);
		else
			view.fill(0f,0f,250f);

		view.translate(x1, y1, z1);
		view.sphere(s/25f);
		view.translate(-x1, -y1, -z1);
	}


	/**
	 * Draw a triangle
	 */	
	public void drawTriangle(Point_3 p, Point_3 q, Point_3 r) {
		float s=(float)this.scaleFactor;
		view.vertex( (float)(p.getX().doubleValue()*s), (float)(p.getY().doubleValue()*s), (float)(p.getZ().doubleValue()*s));
		view.vertex( (float)(q.getX().doubleValue()*s), (float)(q.getY().doubleValue()*s), (float)(q.getZ().doubleValue()*s));
		view.vertex( (float)(r.getX().doubleValue()*s), (float)(r.getY().doubleValue()*s), (float)(r.getZ().doubleValue()*s));
	}



	/**
	 * Draw a (triangle or polygonal) face
	 */	
	public void drawFace(Face<Point_3> f) {
		Halfedge<Point_3> h=f.getEdge();
		Halfedge<Point_3> pEdge=h.getNext();

		Point_3 u=h.getOpposite().getVertex().getPoint();
		view.noStroke();
		if (f.underPath)
			view.fill(0,0,200,255);
		else if(f.color >= 1)
			view.fill(0,Math.max(50, (13-f.color)*20),0,255);
		else
			view.fill(200,0,0,255); // color of the triangle

		while(pEdge.getVertex()!=h.getOpposite().getVertex()) {
			Point_3 v=pEdge.getOpposite().getVertex().getPoint();
			Point_3 w=pEdge.getVertex().getPoint();
			this.drawTriangle(u, v, w); // draw a triangle face

			pEdge=pEdge.getNext();
		}
	}

	public void drawPath(Face<Point_3> f) {
		if (f.path != null) {
			Point_3[] tab = new Point_3[2];
			tab[1] = f.path.first.getVertex().getPoint();
			tab[0] = f.path.first.getOpposite().getVertex().getPoint();
			Number[] coeff = new Number[2];
			coeff[0] = 1 - f.path.rFirst;
			coeff[1] = f.path.rFirst;
			Point_3 a = Point_3.linearCombination(tab, coeff);
			Point_3 b;
			if (f.path.second == null) {
				b = f.path.first.getNext().getVertex().getPoint();
			} else {
				tab[1] = f.path.second.getVertex().getPoint();
				tab[0] = f.path.second.getOpposite().getVertex().getPoint();
				coeff[0] = 1 - f.path.rSecond;
				coeff[1] = f.path.rSecond;
				b = Point_3.linearCombination(tab, coeff);
			}
			view.stroke(255,0,255,255);
			drawSegment(a, b);
		}
	}




	/**
	 * Draw the entire mesh
	 */
	public void draw(int type) {
		//this.drawAxis();

		// draw all faces
		view.beginShape(view.TRIANGLES);
		for(Face<Point_3> f: this.polyhedron3D.facets) {
			this.drawFace(f);
		}
		view.endShape();

		view.strokeWeight(4); // line width (for edges)
		for(Face<Point_3> f: this.polyhedron3D.facets) {
			this.drawPath(f);
		}

		if(type==1) return; // no rendering of edges

		// draw all edges
		view.strokeWeight(2); // line width (for edges)
		view.stroke(20);
		for(Halfedge<Point_3> e: this.polyhedron3D.halfedges) {
			Point_3 p=e.vertex.getPoint();
			Point_3 q=e.opposite.vertex.getPoint();
			if (e.pair.color == -1)
				view.stroke(0,255,255,255);
			else
				view.stroke(0,0,0,255);
			this.drawSegment(p, q); // draw edge (p,q)
		}
		//view.strokeWeight(1);

		if(type==0) return; // no rendering for vertices

		view.noStroke();
		for(Vertex<Point_3> v: this.polyhedron3D.vertices) {
			this.drawVertex(v);
		}
		view.strokeWeight(1);
	}

	/**
	 * Draw the X, Y and Z axis
	 */
	public void drawAxis() {
		double s=1;
		Point_3 p000=new Point_3(0., 0., 0.);
		Point_3 p100=new Point_3(s, 0., 0.);
		Point_3 p010=new Point_3(0.,s, 0.);
		Point_3 p011=new Point_3(0., 0., s);

		drawSegment(p000, p100);
		drawSegment(p000, p010);
		drawSegment(p000, p011);
	}


	/**
	 * Return the value after truncation
	 */
	public static double round(double x, int precision) {
		return ((int)(x*precision)/(double)precision);
	}

	/**
	 * Compute the scale factor (depending on the max distance of the point set)
	 */
	public double computeScaleFactor() {
		if(this.polyhedron3D==null || this.polyhedron3D.vertices.size()<1)
			return 1;
		double maxDistance=0.;
		Point_3 origin=new Point_3(0., 0., 0.);
		for(Vertex<Point_3> v: this.polyhedron3D.vertices) {
			double distance=Math.sqrt(v.getPoint().squareDistance(origin).doubleValue());
			maxDistance=Math.max(maxDistance, distance);
		}
		return Math.sqrt(3)/maxDistance*150;
	}

	/**
	 * Update the scale factor
	 */
	public void updateScaleFactor() {
		this.scaleFactor=this.computeScaleFactor();
	}


	public void checkEdges()
	{
		for (Halfedge<Point_3> h : polyhedron3D.halfedges) {
			if (!h.pair.test(1000)) {
				h.pair.color = -1;
				h.pair.display();
			}
		}
	}
	
	public void cleanPath()
	{
		for (Face<Point_3> f : polyhedron3D.facets) {
			f.underPath = false;
			f.path = null;
		}
	}


}
