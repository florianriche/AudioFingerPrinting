package audiofinger;

/**
 * 
 * @author Paco
 *
 */
public class Complex {
	
    private final double re;   // the real part
    private final double im;   // the imaginary part

    /**
     * Create a new object with the given real and imaginary parts
     * @param real
     * @param imag
     */
    public Complex(double real, double imag) {
        re = real;
        im = imag;
    }

    /**
     * Return a string representation of the invoking Complex object
     */
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    /**
     * return abs/modulus/magnitude
     * @return
     */
    public double abs(){ 
    	return Math.hypot(re, im); 
    } 
    
    /**
     * return angle/phase/argument
     * @return
     */
    public double phase(){ 
    	return Math.atan2(im, re); 
    }  

    /**
     * Return a new Complex object whose value is (this + b)
     * @param b
     * @return
     */
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return new Complex(real, imag);
    }

    /**
     * Return a new Complex object whose value is (this - b)
     * @param b
     * @return
     */
    public Complex minus(Complex b) {
        Complex a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return new Complex(real, imag);
    }

    /**
     * Return a new Complex object whose value is (this * b)
     * @param b
     * @return
     */
    public Complex times(Complex b) {
        Complex a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    /**
     * Scalar multiplication
     * @param alpha
     * @return : new object whose value is (this * alpha)
     */
    public Complex times(double alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    /**
     * Return a new Complex object whose value is the conjugate of this
     * @return
     */
    public Complex conjugate() {  return new Complex(re, -im); }

    /**
     * Return a new Complex object whose value is the reciprocal of this
     * @return
     */
    public Complex reciprocal() {
        double scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    /**
     * Return the real or imaginary part
     * @return
     */
    public double re() { return re; }
    public double im() { return im; }

    /**
     * Return a / b
     * @param b
     * @return
     */
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    /**
     * Return a new Complex object whose value is the complex exponential of this
     * @return
     */
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    /**
     * Return a new Complex object whose value is the complex sine of this
     * @return
     */
    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

    /**
     * Return a new Complex object whose value is the complex cosine of this
     * @return
     */
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

    /**
     * Return a new Complex object whose value is the complex tangent of this
     * @return
     */
    public Complex tan() {
        return sin().divides(cos());
    }

}//end of class
