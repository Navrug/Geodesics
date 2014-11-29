package Jcg.polyhedron;

import Jcg.geometry.*;


public class Face<X extends Point_>{

	Halfedge<X> halfedge=null;
	public int tag;
	public int color = 0;
	public boolean underPath = false;
	public Path path = null;

	public Face() {}

	public void color()
	{
		color++;
	}

	public int degree() {
		Halfedge<X> e,p;
		if(this.halfedge==null) return 0;

		e=halfedge; p=halfedge.next;
		int cont=1;
		while(p!=e) {
			cont++;
			p=p.next;
		}
		return cont;
	}

	/**
	 * true iff the face is a triangle
	 */	
	public boolean isTriangle() {
		Halfedge<X> h=this.getEdge();
		if(h.getNext().getNext().getNext()!=h)
			return false;
		return true;
	}

	/**
	 * true iff the face is a quad
	 */	
	public boolean isQuad() {
		if(this.degree()==4)
			return true;
		return false;
	}


	public int[] getVertexIndices(Polyhedron_3<X> polyhedron) {
		int d=this.degree();
		int[] result=new int[d];
		Vertex<X> v;

		Halfedge<X> e,p;
		if(this.halfedge==null) return null;

		e=halfedge; p=halfedge.next;
		v=e.getVertex();
		result[0]=polyhedron.vertices.indexOf(v);
		int cont=1;
		while(p!=e) {
			v=p.getVertex();
			result[cont]=polyhedron.vertices.indexOf(v);
			cont++;
			p=p.next;
		}
		return result;
	}


	public void setEdge(Halfedge<X> halfedge) { this.halfedge=halfedge; }
	public Halfedge<X> getEdge() { return this.halfedge; }

	public String toString(){
		String result="";
		Halfedge<X> e,p;
		if(this.halfedge==null) {
			System.out.println("face.toString() error: null incident halfedge");
			return null;
		}

		e=halfedge;
		p=halfedge.next;
		result=result+e.getVertex().toString();
		while(p!=e) {
			result=result+"\t"+p.getVertex().toString();
			p=p.next;
		}
		return result;
	}

	public class Path {
		public Halfedge<Point_3> first;
		public Halfedge<Point_3> second;
		public double rFirst; //must be between 0 and 1
		public double rSecond;
		
		public Path(Halfedge<Point_3> h1, double x1, Halfedge<Point_3> h2,
				double x2) {
			first = h1;
			second = h2;
			rFirst = x1/h1.length;
			rSecond = h2==null ? -1 : x2/h2.length;			
		}
	}

	public void setPath(Halfedge<Point_3> h1, double x1, Halfedge<Point_3> h2,
				double x2) {
		path = new Path(h1, x1, h2, x2);		
	}
	

}
