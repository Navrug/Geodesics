package Jcg.polyhedron;

import geodesics.Window;

import java.util.ArrayList;
import java.util.HashSet;

import Jcg.geometry.Point_3;

/**
 * Class for representing a pair of half edges
 */
public class HalfedgePair {
	int first, second;
	public Halfedge<Point_3> one;
	public Halfedge<Point_3> two;
	public HashSet<Window> windows = new HashSet<Window>();
	private ArrayList<Window> toAdd = new ArrayList<>();
	public int color;

	public HalfedgePair(int first, int second) {
		this.first=first;
		this.second=second;
	}

	@Override
	public boolean equals(Object o) {
		HalfedgePair e=(HalfedgePair)o;
		if(first==e.first && second==e.second)
			return true;
		if(first==e.second && second==e.first)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return first*second;
	}
	

	public void addWindowLater(Window w)
	{
		toAdd.add(w);
	}


	public boolean addWindow(Window w)
	{
		int result;
		ArrayList<Window> toRemove = new ArrayList<Window>();
		w.convert();
		for(Window v : windows) {
			v.convert();
			result = w.overlap(v);
			v.unConvert();
			if (result == 1) {  //The object is marked invalid and is deleted from the queue when popped
				toRemove.add(v);
				v.valid = false;
			}
			if (result == -1)
				return false;
		}
		w.unConvert();
		for (Window v : toRemove)
			windows.remove(v);
		//Retrieving the windows we should have had at this level, freeing the list for deeper additions
		if (!toAdd.isEmpty()) {
			System.out.println("#############################################");
			System.out.println("# There are    "+toAdd.size()+"   doubles #");
			System.out.println("#############################################");
			ArrayList<Window> toAddNow = toAdd;
			toAdd = new ArrayList<Window>();
			for (Window v : toAddNow)
				addWindow(v);
		}
		//Adding the argument, it could not overlap with any of the windows from the adding loop
		windows.add(w);
		return true;
	}
	
	/**
	 * 
	 * @param x is given in "one" coordinates
	 * @return
	 */
	public Window getWindow(double x)
	{
		for (Window w : windows) {
			if (w.contains(x))
				return w;
		}
		throw new RuntimeException();
	}
}
