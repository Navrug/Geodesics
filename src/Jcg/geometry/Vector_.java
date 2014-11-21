package Jcg.geometry;

public interface Vector_ {
	
	  public Number getCartesian(int i);  
	  public void setCartesian(int i, Number x);
	      
	  public boolean equals(Vector_ v);
	  
	  public Vector_ sum(Vector_ v);
	  public Vector_ difference(Vector_ v);
	  public Vector_ opposite();

	  public Number innerProduct(Vector_ v);

	  public Vector_ divisionByScalar(Number s);
	  public Vector_ multiplyByScalar(Number s);
	  
	  public Number squaredLength();
	   
	  public int dimension();
	  public String toString();
}
