package baiducontest.math.function;

public final class Mult implements DoubleFunction {

	  private double multiplicator;

	  Mult(double multiplicator) {
	    this.multiplicator = multiplicator;
	  }

	  /** Returns the result of the function evaluation. */
	  @Override
	  public double apply(double a) {
	    return a * multiplicator;
	  }

	  /** <tt>a / constant</tt>. */
	  public static Mult div(double constant) {
	    return mult(1 / constant);
	  }

	  /** <tt>a * constant</tt>. */
	  public static Mult mult(double constant) {
	    return new Mult(constant);
	  }

	  public double getMultiplicator() {
	    return multiplicator;
	  }

	  public void setMultiplicator(double multiplicator) {
	    this.multiplicator = multiplicator;
	  }
	}

