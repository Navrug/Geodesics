package Jcg.geometry;

public class Point_3 implements Point_ {
  public Double x,y,z;

  public Point_3() {}
  
  public Point_3(Number x,Number y,Number z) { 
  	this.x=x.doubleValue(); 
  	this.y=y.doubleValue(); 
  	this.z=z.doubleValue();
  }

  public Point_3(Point_ p) { 
  	this.x=p.getCartesian(0).doubleValue(); 
  	this.y=p.getCartesian(1).doubleValue(); 
  	this.z=p.getCartesian(2).doubleValue();
  }
  
  public void barycenter(Point_ [] points) {
  	double x_=0., y_=0., z_=0;
  	for(int i=0;i<points.length;i++) {
  		x_ += points[i].getCartesian(0).doubleValue();
  		y_ += points[i].getCartesian(1).doubleValue();
  		z_ += points[i].getCartesian(2).doubleValue();
  	}
  	this.x = x_/points.length;
  	this.y = y_/points.length;
  	this.z = z_/points.length;
  }

  public void linearCombination(Point_[] points, Number[] coefficients) {
	  	double x_=0., y_=0., z_=0.;
	  	for(int i=0;i<points.length;i++) {
	  		x_=x_+(points[i].getCartesian(0).doubleValue()*coefficients[i].doubleValue());
	  		y_=y_+(points[i].getCartesian(1).doubleValue()*coefficients[i].doubleValue());
	  		z_=z_+(points[i].getCartesian(2).doubleValue()*coefficients[i].doubleValue());
	  	}
	  	this.x = x_;
	  	this.y = y_;
	  	this.z = z_;
	  }

  public static Point_3 linearCombination(Point_3 [] points, Number[] coefficients) {
  	double x_=0., y_=0., z_=0;
  	for(int i=0;i<points.length;i++) {
  		x_=x_+(points[i].getX().doubleValue()*coefficients[i].doubleValue());
  		y_=y_+(points[i].getY().doubleValue()*coefficients[i].doubleValue());
  		z_=z_+(points[i].getZ().doubleValue()*coefficients[i].doubleValue());
  	}
  	return new Point_3(x_,y_,z_);
  }

  public Number getX() {return x; }
  public void setX(Number x) {this.x=x.doubleValue(); }

  public Number getY() {return y; }
  public void setY(Number y) {this.y=y.doubleValue(); }
  
  public Number getZ() {return z; }
  public void setZ(Number z) {this.z=z.doubleValue(); }
    
  public void translateOf(Vector_ v) {
    this.x=x+v.getCartesian(0).doubleValue();
    this.y=y+v.getCartesian(1).doubleValue();
    this.z=z+v.getCartesian(2).doubleValue();
  }

  public void multiply (Number n) {
	    this.x*=n.doubleValue();
	    this.y*=n.doubleValue();
	    this.z*=n.doubleValue();
	  }

  public boolean equals(Object o) { 
	  Point_ p = (Point_) o;
	  return this.x.equals(p.getCartesian(0)) && this.y.equals(p.getCartesian(1)) && 
	  this.z.equals(p.getCartesian(2)); 
  }

  public int hashCode () {
	  double code = getX().doubleValue();
	  code = code * code * code;
	  code += getY().doubleValue()*getY().doubleValue();
	  code += getZ().doubleValue();
	  return (int)code;
  }
  
  public Number distanceFrom(Point_3 p) {
    double dX=p.getX().doubleValue()-x;
    double dY=p.getY().doubleValue()-y;
    double dZ=p.getZ().doubleValue()-z;
    return Math.sqrt(dX*dX+dY*dY+dZ*dZ);
  }
  
  public Number squareDistance(Point_3 p) {
    double dX=p.getX().doubleValue()-x;
    double dY=p.getY().doubleValue()-y;
    double dZ=p.getZ().doubleValue()-z;
    return dX*dX+dY*dY+dZ*dZ;
  }

  public String toString() {return "("+x+","+y+","+z+")"; }
  public int dimension() { return 3;}
  
  public Number getCartesian(int i) {
  	if(i==0) return x;
  	else if(i==1) return y;
  	return z;
  } 
  public void setCartesian(int i,Number x) {
  	if(i==0) this.x=x.doubleValue();
  	else if(i==1) this.y=y.doubleValue();
  	else this.z=x.doubleValue();
  }

  public void setOrigin() {
	  this.x=0.;
	  this.y=0.;
	  this.z=0.;
  }

  public Vector_ minus(Point_ b){
	  return new Vector_3(b.getCartesian(0).doubleValue()-x, b.getCartesian(1).doubleValue()-y, b.getCartesian(2).doubleValue()-z);
  }
  
  public int compareTo(Point_ o) {
	  Point_3 p = (Point_3) o;
	  if (this.getX().doubleValue() < p.getX().doubleValue())
		  return -1;
	  if (this.getX().doubleValue() > p.getX().doubleValue())
		  return 1;
	  if (this.getY().doubleValue() < p.getY().doubleValue())
		  return -1;
	  if (this.getY().doubleValue() > p.getY().doubleValue())
		  return 1;
	  if (this.getZ().doubleValue() < p.getZ().doubleValue())
		  return -1;
	  if (this.getZ().doubleValue() > p.getZ().doubleValue())
		  return 1;
	  return 0;
  }

  
}




