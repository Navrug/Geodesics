package Jcg.polyhedron;


import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import Jcg.geometry.*;
import Jcg.io.*;



/**
 * A vertex shared representation of a mesh.
 * For each face we store the indices of the incident vertices
 *
 * @author Luca Castelli Aleardi (INF555, 2012)
 *
 */
public class SharedVertexRepresentation {
	    
	public int sizeVertices;
	public int sizeFaces;
	public int sizeHalfedges;
	
    public int[][] faces;
    public int[] faceDegrees;
    public Point_3[] points;

    public Color[] faceColors;
    public Color[] edgesColors;
    
    //static final int max_degree=10;

    /**
     * Create a mesh representation a collection of 3D triangles
     */
    public SharedVertexRepresentation(Collection<Triangle_3> faces) {
    	this.faces = new int[faces.size()][];
    	faceDegrees = new int[faces.size()];
    	faceColors = new Color[faces.size()];
    	LinkedHashMap<Point_3,Integer> vert = new LinkedHashMap<Point_3,Integer> ();

    	int ind=0;
    	int indF = 0;
    	for (Triangle_3 f : faces) {
    		for (int i=0; i<3; i++)
    			if (!vert.containsKey(f.vertex(i)))
    				vert.put(f.vertex(i), ind++);
    		faceDegrees[indF] = 3;
    		faceColors[indF] = Color.gray;
    		this.faces[indF++] = new int[]{vert.get(f.vertex(0)), vert.get(f.vertex(1)), vert.get(f.vertex(2))};
    	}    	
    	points = vert.keySet().toArray(new Point_3[0]);
    }
    
    /**
     * Create a mesh representation from an off file (using TC library)
     */
    public SharedVertexRepresentation(String filename){
    	System.out.print("Jcg - Creating a shared vertex representation (from OFF file): ");
    	System.out.println(filename);
    	long startTime=System.nanoTime(), endTime; // for evaluating time performances
    	
    	double x, y, z;
    	IO.readTextFile(filename);
    	String line;
            
            line=IO.readLine(); // first line is empty
            line=IO.readLine();
            String[] w=IO.wordsFromString(line);
            sizeVertices=Integer.parseInt(w[0]);
            sizeFaces=Integer.parseInt(w[1]);            
            
            points=new Point_3[sizeVertices];
            faceDegrees=new int[sizeFaces];
            faces=new int[sizeFaces][];
            
            int i=0;
            Point_3 point;
            System.out.print("\tReading vertices...");
            
            while(i<sizeVertices) {
                line=IO.readLine(); w = IO.wordsFromString(line);
                //System.out.println("line "+i+" :"+line);
                x=(new Double(w[0])).doubleValue();
                y=(new Double(w[1])).doubleValue();
                z=(new Double(w[2])).doubleValue();
                
                point=new Point_3(x,y,z);
                points[i]=point;
                i++;
            }
            System.out.println("done "+sizeVertices+" vertices");
            
            //line = IO.readLine();
            System.out.print("\tReading face degrees...");
            i=0;
            while(i<sizeFaces){
            	line = IO.readLine();
            	//System.out.println("line "+i+":-"+line+"-");
            	if(line == null)  {
            		throw new Error("error: end of file reached before reading all faces");
            	}
            	String[] words=IO.wordsFromString(line);
            	//System.out.print("words length: "+words.length+" ");
            	if(words!=null && words.length>1) {
            		//System.out.println("line:-"+words[0]+"-");
            		
            		faceDegrees[i]=Integer.parseInt(words[0]); // first element encodes the degree
            		//if(faceDegrees[i]>max_degree) {
            		//	throw new Error("Error face degree");
            		//}
            		faces[i]=new int[faceDegrees[i]];
            		
            		for(int j=0; j<faceDegrees[i]; j++) {
            			faces[i][j]=Integer.parseInt(words[j+1]);
            			sizeHalfedges++;
                    }
            		i++;
                }         
            }
            System.out.println("done "+sizeFaces+" faces");
            IO.readStandardInput();
            
            endTime=System.nanoTime();
            double duration=(double)(endTime-startTime)/1000000000.;
            System.out.print("Shared Vertex Representation created");
            System.out.println(" ("+duration+" seconds)");
    }

    /**
     * Create a mesh representation from a polyhedron (half-edge data structure)
     */
	public SharedVertexRepresentation(Polyhedron_3<Point_3> polyhedron){
    	System.out.println(" ---\nCreating Mesh representation from polyhedron");
    	System.out.println("starting encoding a polyhedron...");
    	this.sizeVertices=polyhedron.sizeOfVertices();
    	this.sizeFaces=polyhedron.sizeOfFacets();            
    	this.sizeHalfedges=polyhedron.sizeOfHalfedges();
    	
    	this.points=new Point_3[sizeVertices];
    	this.faceDegrees=new int[sizeFaces];
    	this.faces=new int[sizeFaces][];
    	
    	for(int i=0;i<sizeVertices;i++) {
    		points[i]=polyhedron.vertices.get(i).getPoint();
    	}
    	
    	for(int i=0;i<sizeFaces;i++){
    		int d=polyhedron.facets.get(i).degree();
    		faceDegrees[i]=d;
    		this.faces[i]=new int[d];
    	}
    	
      	for(int i=0;i<sizeFaces;i++){
    		faces[i]=polyhedron.facets.get(i).getVertexIndices(polyhedron);
    	}
    	System.out.println("Mesh representation created \n ---");
  }
	
    /**
     * Store the representation in OFF format
     */
	public void writeOffFile(String filename) throws IOException {
    	// store vertex indices in map 
    	 BufferedWriter out = new BufferedWriter (new FileWriter(filename));
       	 out.write ("OFF\n");
       	 out.write (sizeVertices + " " + sizeFaces + " 0\n");  
    	 for (Point_3 p : points)
    		 out.write(p.getX() + " " + p.getY() + " " + p.getZ() + "\n");
    	 
    	 for (int i=0; i<sizeFaces; i++) {
    		 out.write(""+faceDegrees[i]);
    		 for (int j=0; j<faceDegrees[i]; j++)
    			 out.write (" " + faces[i][j]);
    			 out.write ("\n");
    	 }
    	 out.close();
	}
	
}
