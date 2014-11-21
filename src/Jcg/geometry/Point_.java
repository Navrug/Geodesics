package Jcg.geometry;

public interface Point_ extends Comparable<Point_>{

	  public Number getCartesian(int i);  
	  public void setCartesian(int i, Number x);
	  public void setOrigin();
	    
	  public void translateOf(Vector_ p);
	  public Vector_ minus(Point_ p);
	  public void barycenter(Point_ [] points);
	  public void linearCombination(Point_[] points, Number[] coefficients);
	  
	  public int dimension();
	  public String toString();
	  
	}

	class Orientation {
		
		static int CLOCKWISE=1;
		static int COUNTERCLOCKWISE=-1;
		static int COLLINEAR=0;
		
		private int or;
		
		public Orientation(int or) {
			this.or=or;
		}
		
		public boolean isClockwise() {
			return this.or == CLOCKWISE;
		}

		public boolean isCounterclockwise() {
			return this.or == COUNTERCLOCKWISE;
		}

		public boolean isCollinear() {
			return this.or == COLLINEAR;
		}
	}


