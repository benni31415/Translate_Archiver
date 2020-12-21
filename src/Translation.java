
public class Translation {

	String sourceLang;
	String targetLang;
	String source;
	String target;
	
	public Translation(String sL, String tL, String s, String t) {
		sourceLang = sL;
		targetLang = tL;
		source = s;
		target = t;
	}

	@SuppressWarnings("unused")
	public String getSourceLang() {
		return sourceLang;
	}

	@SuppressWarnings("unused")
	public String getTargetLang() {
		return targetLang;
	}

	@SuppressWarnings("unused")
	public String getSource() {
		return source;
	}

	@SuppressWarnings("unused")
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}

}
