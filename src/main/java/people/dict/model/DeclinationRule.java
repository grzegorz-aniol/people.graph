package people.dict.model;

public class DeclinationRule {
	private String suffix;
	private boolean man;
	private String D;
	private String C;
	private String B;
	private String N;
	private String Ms;
	private String W;

	public DeclinationRule() {
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public boolean isMan() {
		return man;
	}

	public void setMan(boolean man) {
		this.man = man;
	}

	public String getD() {
		return D;
	}

	public void setD(String d) {
		D = d;
	}

	public String getC() {
		return C;
	}

	public void setC(String c) {
		C = c;
	}

	public String getB() {
		return B;
	}

	public void setB(String b) {
		B = b;
	}

	public String getN() {
		return N;
	}

	public void setN(String n) {
		N = n;
	}

	public String getMs() {
		return Ms;
	}

	public void setMs(String m) {
		Ms = m;
	}

	public String getW() {
		return W;
	}

	public void setW(String w) {
		W = w;
	}
}