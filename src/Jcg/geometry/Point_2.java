package Jcg.geometry;

public class Point_2 implements Point_{
  public Double x,y;

  public Point_2() {}
  
  public Point_2(Number x,Number y) { 
  	this.x=x.doubleValue(); 
  	this.y=y.doubleValue();
  }

  public Point_2(Point_ p) { 
  	this.x=p.getCartesian(0).doubleValue(); 
  	this.y=p.getCartesian(1).doubleValue(); 
  }
  
  public void barycenter(Point_[] points) {
  	double x_=0., y_=0.;
  	for(int i=0;i<points.length;i++) {
  		x_=x_+points[i].getCartesian(0).doubleValue();
  		y_=y_+points[i].getCartesian(1).doubleValue();
  	}
  	this.x = x_/points.length;
  	this.y = y_/points.length;
  }
  
  public void linearCombination(Point_[] points, Number[] coefficients) {
	  	double x_=0., y_=0.;
	  	for(int i=0;i<points.length;i++) {
	  		x_=x_+(points[i].getCartesian(0).doubleValue()*coefficients[i].doubleValue());
	  		y_=y_+(points[i].getCartesian(1).doubleValue()*coefficients[i].doubleValue());
	  	}
	  	this.x = x_;
	  	this.y = y_;
	  }

  public static Point_2 linearCombination(Point_2 [] points, Number[] coefficients) {
	  	Double x_=0., y_=0.;
	  	for(int i=0;i<points.length;i++) {
	  		x_=x_+(points[i].getX().doubleValue()*coefficients[i].doubleValue());
	  		y_=y_+(points[i].getY().doubleValue()*coefficients[i].doubleValue());
	  	}
	  	return new Point_2(x_,y_);
	  }

  public static Point_2 midPoint (Point_2 p, Point_2 q) {
	  Point_2 [] seg = new Point_2 [2];
	  seg[0] = p;
	  seg[1] = q;
	  Number [] coef = new Number [2];
	  coef[0] = coef[1] = 0.5;
	  return linearCombination (seg, coef);
	  }

  public Number getX() {return x; }
  public Number getY() {return y; }
  
  public void setX(Double x) {this.x=x.doubleValue(); }
  public void setY(Double y) {this.y=y.doubleValue(); }
    
  public void translateOf(Vector_ v) {
    this.x=x+v.getCartesian(0).doubleValue();
    this.y=y+v.getCartesian(1).doubleValue();
  }

  public boolean equals(Object o) {
	  if (o instanceof Point_) {
		  Point_ p = (Point_) o;
		  return this.x.equals(p.getCartesian(0)) && this.y.equals(p.getCartesian(1)); 
	  }
	throw new RuntimeException ("Comparing Point_2 with object of type " + o.getClass());  	
  }

  public int hashCode () {
	 return (int)(this.x*this.x + this.y);
  }

  public Number distanceFrom(Point_2 p) {
    double dX=p.getX().doubleValue()-x;
    double dY=p.getY().doubleValue()-y;
    return Math.sqrt(dX*dX+dY*dY);
  }
  
  public Number squareDistance(Point_2 p) {
    double dX=p.getX().doubleValue()-x;
    double dY=p.getY().doubleValue()-y;
    return dX*dX+dY*dY;
  }

  public String toString() {return "("+x+","+y+")"; }
  public int dimension() { return 2;}
  
  public Number getCartesian(int i) {
  	if(i==0) return x;
  	return y;
  } 
  public void setCartesian(int i, Number x) {
  	if(i==0) this.x=x.doubleValue();
  	else this.y=x.doubleValue();
  }

  public void setOrigin() {
	  	this.x=0.;
	  	this.y=0.;
	  }
    
  public Vector_ minus(Point_ b){
  	return new Vector_2(b.getCartesian(0).doubleValue()-x, 
  						b.getCartesian(1).doubleValue()-y);
  }
  
  public Point_2 sum(Vector_ v) {
	  	return new Point_2(this.x+v.getCartesian(0).doubleValue(),
	  						this.y+v.getCartesian(1).doubleValue());  	
}

  public int compareTo(Point_ o) {
	  Point_2 p = (Point_2) o;
	  if(this.x<p.getX().doubleValue())
		  return -1;
	  if(this.x>p.getX().doubleValue())
		  return 1;
	  if(this.y<p.getY().doubleValue())
		  return -1;
	  if(this.y>p.getY().doubleValue())
		  return 1;
	  return 0;
  }

}




