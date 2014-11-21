package Jcg.polyhedron;

import Jcg.geometry.*;


public class Face<X extends Point_>{

Halfedge<X> halfedge=null;
public int tag;
private boolean colored = false;

    public Face() {}
        
    public void color()
    {
    	colored = true;
    }
    
    public boolean isColored()
    {
    	return colored;
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

}
