package Jcg.geometry;

/*
 * Class for representing a point in d-dimension space 
 */
public class Point_d implements Point_{
	  public Double[] coordinates;

	  public Point_d(int d) {
		  this.coordinates=new Double[d];
	  }
	  
	  public Point_d(double[] coord) { 
		  this.coordinates=new Double[coord.length];
		  for(int i=0;i<coord.length;i++)
			  this.coordinates[i]=coord[i];
	  }

	  public Point_d(Point_ p) { 
		  this.coordinates=new Double[p.dimension()];
		  for(int i=0;i<p.dimension();i++)
			  this.coordinates[i]=p.getCartesian(i).doubleValue();
	  }
	  
	  public void barycenter(Point_[] points) {
	  	double[] x=new double[dimension()];
	  	for(int i=0;i<points.length;i++) {
	  		for(int j=0;j<dimension();j++)
	  			x[j]=x[j]+points[i].getCartesian(j).doubleValue();
	  	}
	  	for(int j=0;j<dimension();j++)
	  		this.coordinates[j]=x[j]/points.length;
	  }

	  public void linearCombination(Point_[] points, Number[] coefficients) {
		  throw new Error("to be completed");
	  }
	    
	  public void translateOf(Vector_ v) {
		  if(v.dimension()!=this.dimension())
			  throw new Error("Point_d error: wrong dimension");
		  for(int i=0;i<dimension();i++)
			  this.coordinates[i]=this.coordinates[i]+v.getCartesian(i).doubleValue();
	  }

	  public boolean equals(Object o) {
		  Point_ p = (Point_) o;
		  for(int i=0;i<dimension();i++) {
			  if(this.coordinates[i].equals(p.getCartesian(i))== false)
				  return false;
		  }
		  return true;
	  }
	  
	  public String toString() {
		  String result="(";
		  for(int i=0;i<dimension()-1;i++)
			  result=result+this.getCartesian(i)+",";
		  return result+this.getCartesian(dimension()-1)+")";
	  }
	  
	  public int dimension() { return this.coordinates.length;}
	  
	  public Number getCartesian(int i) {
		  return this.coordinates[i];
	  }
	  public void setCartesian(int i, Number x) {
		  this.coordinates[i]=x.doubleValue();
	  }

	  public void setOrigin() {
		  for(int i=0;i<this.coordinates.length;i++)
			  this.coordinates[i]=0.;
	  }
	    
	  public Vector_ minus(Point_ b){
		  throw new Error("a' completer");
	  }

	    public double squareDistance(Point_ p) {
	    	double d = 0.;
	    	
	    	int dim = Math.min (this.dimension(), p.dimension());
	    	for (int i=0; i<dim; i++)
	    	    d += (this.getCartesian(i).doubleValue()-p.getCartesian(i).doubleValue()) * 
	    	    (this.getCartesian(i).doubleValue()-p.getCartesian(i).doubleValue());
	    	return d;
	    }

	  public int compareTo(Point_ o) {
		  throw new Error("a' completer");
	  }
	  
}
