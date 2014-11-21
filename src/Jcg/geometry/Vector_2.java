package Jcg.geometry;

public class Vector_2 implements Vector_{
  public Double x,y;

  public Vector_2() {}
  
  public Vector_2(Number x,Number y) { 
  	this.x=x.doubleValue(); 
  	this.y=y.doubleValue();
  }

  public Vector_2(Point_2 a, Point_2 b) { 
	  	this.x=b.getX().doubleValue()-a.getX().doubleValue(); 
	  	this.y=b.getY().doubleValue()-a.getY().doubleValue(); 
	  }

  public Number getX() {return x; }
  public Number getY() {return y; }
  
  public void setX(Number x) {this.x=x.doubleValue(); }
  public void setY(Number y) {this.y=y.doubleValue(); }
  

  public boolean equals(Vector_ v) { 
  	return(this.x.equals(v.getCartesian(0).doubleValue()) 
  		&& this.y.equals(v.getCartesian(1).doubleValue())); 
  }

  public String toString() {return "["+x+","+y+"]"; }
  public int dimension() { return 2;}
  
  public Number getCartesian(int i) {
  	if(i==0) return x.doubleValue();
  	return y.doubleValue();
  } 
  
  public void setCartesian(int i, Number x) {
  	if(i==0) this.x=x.doubleValue();
  	else this.y=x.doubleValue();
  }
    
  public Vector_2 sum(Vector_ v) {
  	return new Vector_2(this.x+v.getCartesian(0).doubleValue(),
  						this.y+v.getCartesian(1).doubleValue());  	
  }
  
  public Vector_2 difference(Vector_ v) {
  	return new Vector_2(v.getCartesian(0).doubleValue()-x,
  						v.getCartesian(1).doubleValue()-y);  	
  }
  
  public Vector_2 opposite() {
  	return new Vector_2(-x,-y);  	
  }
  
  public Number innerProduct(Vector_ v) {
  	return this.x*v.getCartesian(0).doubleValue()+
  		   this.y*v.getCartesian(1).doubleValue();  	
  }

  public Vector_2 divisionByScalar(Number s) {
  	return new Vector_2(x/s.doubleValue(),y/s.doubleValue());  	
  }
  
  public Vector_2 multiplyByScalar(Number s) {
  	return new Vector_2(x*s.doubleValue(),y*s.doubleValue());  	
  }
  
  public Number squaredLength() {
  	return innerProduct(this);  	
  }
  
  public Vector_2 perpendicular(Orientation o) {
  	if(o.isCounterclockwise()==true)
  		return new Vector_2(-y,x);  	
  	else
  		return new Vector_2(y,-x);
  }

  
}




