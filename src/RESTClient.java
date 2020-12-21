import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RESTClient {
	
	Client client;
	HashMap<String, String> source_lang_codes;
	HashMap<String, String> target_lang_codes;
	private String auth_key;

	public RESTClient(String auth_key) throws ServiceAuthenticationException {
		// Dependencies: http://openbook.rheinwerk-verlag.de/java8/15_002.html
		this.client = ClientBuilder.newClient();
		
		initialize_language_codes();
		this.auth_key = auth_key;
		authenticate();
	}
	
	private void authenticate() throws  ServiceAuthenticationException {
		try {
			String auth_request = "https://api.deepl.com/v2/usage?auth_key=" + auth_key;
			WebTarget auth_target = client.target(auth_request);
			Invocation.Builder builder = auth_target.request(MediaType.APPLICATION_JSON);
			Response response = builder.get(Response.class);
			String result = response.readEntity(new GenericType<String>(){});
			result = result.substring(1, 2);
		} catch (Exception e) {
			throw new ServiceAuthenticationException();
		}
	}
	
	private void initialize_language_codes() {
		source_lang_codes = new HashMap<String, String>();
		source_lang_codes.put("German", "DE");
		source_lang_codes.put("English", "EN");
		source_lang_codes.put("French", "FR");
		source_lang_codes.put("Italian", "IT");
		source_lang_codes.put("Japanese", "JA");
		source_lang_codes.put("Spanish", "ES");
		source_lang_codes.put("Dutch", "NL");
		source_lang_codes.put("Polish", "PL");
		source_lang_codes.put("Portuguese", "PT");
		source_lang_codes.put("Russian", "RU");
		source_lang_codes.put("Chinese", "ZH");
		
		target_lang_codes = new HashMap<String, String>();
		target_lang_codes.put("German", "DE");
		target_lang_codes.put("English", "EN-US");
		target_lang_codes.put("French", "FR");
		target_lang_codes.put("Italian", "IT");
		target_lang_codes.put("Japanese", "JA");
		target_lang_codes.put("Spanish", "ES");
		target_lang_codes.put("Dutch", "NL");
		target_lang_codes.put("Polish", "PL");
		target_lang_codes.put("Portuguese", "PT-PT");
		target_lang_codes.put("Russian", "RU");
		target_lang_codes.put("Chinese", "ZH");
	}

	
	public Translation translate(Translation input) {
		
		String source = input.getSource();
		String sourceLang = source_lang_codes.get(input.getSourceLang());
		String targetLang = target_lang_codes.get(input.getTargetLang());
		
		try {
			source = URLEncoder.encode(source, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String request = "https://api.deepl.com/v2/translate?auth_key=" + auth_key + "&source_lang=" + sourceLang
				+ "&target_lang=" + targetLang + "&text=" + source;

		WebTarget webTarget = client.target(request);
			
		// Additional Dependency: javax.activation
		// https://jar-download.com/artifacts/com.sun.activation/javax.activation/1.2.0/source-code
		Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = builder.get(Response.class);
		String translation = response.readEntity(new GenericType<String>(){});
		
		translation = translation.substring(translation.indexOf("\"text\":")+8, translation.length()-4);
	    
	    input.setTarget(translation);
	    return input;
	}

}
