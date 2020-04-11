package redistrict.colorado.core;

/**
 * This is a holder for attributes of a Gate that are stored
 * in the database.
 */
public class GateProperty {
	private final GateType type;
	private double weight;
	private double fairValue;
	private double unfairValue;

	public GateProperty(GateType gt,double wt,double fv, double ufv) {
		this.type = gt;
		this.weight = wt;
		this.fairValue = fv;
		this.unfairValue = ufv;
	}
	
	public GateType getType() { return this.type; }
	public double getFairValue() { return this.fairValue; }
	public double getUnfairValue() { return this.unfairValue; }
	public double getWeight() { return this.weight; }
	public void setFairValue(double val) { this.fairValue = val; }
	public void setUnfairValue(double val) { this.unfairValue = val; }
	public void setWeight(double val) { this.weight = val; }
}
