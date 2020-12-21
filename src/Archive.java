import java.util.ArrayList;

public class Archive {

	private ArrayList<Translation> archive;
	private RESTClient client;
	
	public Archive(RESTClient client) {
		this.client = client;
		archive = new ArrayList<Translation>();
	}
	
	public ArrayList<Translation> getArchive() {
		return archive;
	}
	
	public Translation lookup(Translation reference) {
		
		for (Translation tr: archive) {
			if (tr.getSourceLang().equals(reference.getSourceLang())
					&& tr.getTargetLang().equals(reference.getTargetLang())
					&& tr.getSource().equals(reference.getSource())) {
				return tr;
			}
		}
		
		return null;
	}
	
	public Translation lookup_and_translate(Translation input) {
		
		Translation output;
		
		if (lookup(input) != null) {
			output = lookup(input);
		}
		
		else {
			output = client.translate(input);
			archive.add(output);
		}
		
		return output;		
	}

}
