import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	JLabel L_target;
	Archive archive;
	static String target_directory;
	String[] languages = {"", "German", "English", "French", "Spanish", "Portuguese", "Italian", "Dutch", "Polish", "Russian", "Japanese", "Chinese"};
	
	public static void main(String[] args) {
		GUI window = new GUI();
		window.setVisible(true);
		
		// Create Translate Archiver Directory
		String user = System.getProperty("user.name");		
		target_directory = "C:\\Users\\" + user + "\\OneDrive\\Documents\\Translate Archiver";
		File directory = new File(target_directory);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
	    /*try {
			Files.createSymbolicLink(Paths.get("Export Files"), directory.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public GUI() {
		
		File settings = new File("Settings.txt");
		RESTClient client = null;
		
		try {
			Scanner read_settings = new Scanner(settings);
			String auth_key = read_settings.nextLine();
			read_settings.close();
			auth_key = auth_key.substring(auth_key.indexOf("%%%")+3, auth_key.length()-3);
			client = new RESTClient(auth_key);
		} catch (FileNotFoundException e) {
			try {			
				settings.createNewFile();
				FileWriter wr = new FileWriter("Settings.txt");
				wr.write("DeepL-Auth-Key: %%%[authentication_key]%%%");
				wr.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "Please adjust your Settings File (Settings.txt) by inserting your valid DeepL authentication key.\n"
					+ "For more information visit www.deepl.com.");
			System.exit(0);
			
		} catch (ServiceAuthenticationException e) {
			JOptionPane.showMessageDialog(null, "Please adjust your Settings File (Settings.txt) by inserting your valid DeepL authentication key.\n"
					+ "For more information visit www.deepl.com.");
			System.exit(0);
		}
		
		this.archive = new Archive(client);
		
		setSize(800, 550);
		setTitle("Translate Archiver");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 1));
		
		// Source Language		
		JLabel L_sourceLang = new JLabel("Source Language");
		panel.add(L_sourceLang, BorderLayout.CENTER);
		
		JComboBox<String> C_sourceLang = new JComboBox<String>(languages);
		C_sourceLang.setSelectedIndex(0);
		panel.add(C_sourceLang, BorderLayout.CENTER);
		
		// Target Language
		JLabel L_targetLang = new JLabel("Target Language");
		panel.add(L_targetLang, BorderLayout.CENTER);
		
		JComboBox<String> C_targetLang = new JComboBox<String>(languages);
		C_targetLang.setSelectedIndex(0);
		panel.add(C_targetLang, BorderLayout.CENTER);
		
		// Source Text
		JLabel L_source = new JLabel("Source");
		panel.add(L_source, BorderLayout.CENTER);
		
		JTextField T_source = new JTextField();
		panel.add(T_source, BorderLayout.CENTER);

		// Translation
		JLabel L_translation = new JLabel("Translation: ");
		panel.add(L_translation, BorderLayout.CENTER);
		
		this.L_target = new JLabel();
		panel.add(L_target, BorderLayout.CENTER);
		
		JButton translate = new JButton("Translate (using DeepL API)");
		translate.addActionListener(x -> update_translation(C_sourceLang.getSelectedItem().toString(), C_targetLang.getSelectedItem().toString(), T_source.getText()));
		panel.add(translate);
		
		JButton export = new JButton("Export Archive");
		export.addActionListener(x -> export_archive());
		panel.add(export);
		
		add(panel);
	}
	
	private void update_translation(String sourceLang, String targetLang, String source) {
		Translation t = new Translation(sourceLang, targetLang, source, null);
		String target = archive.lookup_and_translate(t).getTarget();

		L_target.setText(target);
	}
	
	private void export_archive() {
		String file = "Archive_Export_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".txt";
		String filename = target_directory + "\\" + file;
		try {
			FileWriter fw = new FileWriter(filename);
			HashMap<String[], ArrayList<Translation>> archive_per_lang = new HashMap<String[], ArrayList<Translation>>();
			for (Translation tr: archive.getArchive()) {
				String[] key_new = new String[2];
				key_new[0] = tr.getSourceLang();
				key_new[1] = tr.getTargetLang();
				
				ArrayList<Translation> updated_translations = new ArrayList<Translation>();
				for (String[] keys: archive_per_lang.keySet()) {
					if (key_new[0].equals(keys[0]) && key_new [1].equals(keys[1])) {
						updated_translations = archive_per_lang.get(keys);
						key_new = keys;
						break;
					}
				}

				updated_translations.add(tr);
				archive_per_lang.put(key_new, updated_translations);
			}
			
			for (String[] keys: archive_per_lang.keySet()) {
				fw.write(keys[0] + " => " + keys[1] + ":\n");
				for (Translation tr: archive_per_lang.get(keys)) {
					fw.write("\t" + tr.getSource());
					for (int i = tr.getSource().length(); i < 20; i++) {
						fw.write(" ");
					}
					fw.write("\t => \t " + tr.getTarget() + "\n");
				}
				fw.write("\n");
			}
			fw.close();
			
			Object[] options = { "Open location", "Undo", "Return"};
			int result = JOptionPane.showOptionDialog(null, "Translations have been exported to file: \n "+file+"\n \n", "Export Status",
			        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
			if (result == JOptionPane.YES_OPTION) {
				Runtime.getRuntime().exec("explorer.exe /select," + filename);
			}
			if (result == JOptionPane.NO_OPTION) {
				new File(filename).delete();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	

}
