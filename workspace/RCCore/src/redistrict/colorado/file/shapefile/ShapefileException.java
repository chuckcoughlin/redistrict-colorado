package redistrict.colorado.file.shapefile;

/**
  * Thrown when an error relating to the shapefile occurs.
  * The class requires a String explanation.
  */
public class ShapefileException extends Exception{ 
	private final String msg;
    public ShapefileException(String s){
        super(s);
        this.msg = "ShapefileException: "+s;;
    }
    @Override
    public String getLocalizedMessage() {
    	return msg;
    }
}




