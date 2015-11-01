package ave.bertrand;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * Classe pour la gestion des mots.
 * 
 * @author egd
 *
 */
public class WordEngine {

	public static final String FILENAME = "words.properties";
	
	public static final String SEPARATOR = ";";
	
	/**
	 * Dictionnaire des mots.
	 */
	private Map<String, Word> words = new HashMap<String, Word>();
	
	private Map<String, Word> wordsForATour = new HashMap<String, Word>();
	
	
	private List<String> indexNoneGuessedWord = new ArrayList<String>();
	
	/**
	 * Index des mots non encore devinés.
	 */
	private List<String> indexNonePlayedWord = new ArrayList<String>();
	
	private int pos = 0;
	
	/**
	 * Constructeur.
	 */
	public WordEngine() {
		super();
		
		this.loadAllWords();
		
		System.out.println("Le dictionnaire est chargé avec " + this.words.size() + " mot(s).");
	}
	
	/**
	 * Permet de charger tous les mots du dictionnaire.
	 */
	private void loadAllWords() {
		Properties wordsProperties = new Properties();
		
		try {
			File file = new File(FILENAME);
			
			if (file != null) {
				FileInputStream fis = new FileInputStream(file);
				wordsProperties.load(fis);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		Enumeration rowProperties = wordsProperties.propertyNames();
		while (rowProperties.hasMoreElements()) {
			String propertyName = (String) rowProperties.nextElement();
						
			String[] parts = wordsProperties.getProperty(propertyName).split(SEPARATOR);
			
			int count = Integer.parseInt(parts[1]);
			int success = Integer.parseInt(parts[2]);
			int failure = Integer.parseInt(parts[3]);
			this.words.put(propertyName, new Word(propertyName, parts[0], count, success, failure));
			
			// calcul des index
			if (count == 0) {
				this.indexNonePlayedWord.add(propertyName);
			} else {
				this.indexNoneGuessedWord.add(propertyName);
			}
		}
		
		System.out.println("Taille des index: " + this.indexNonePlayedWord.size() + " " + this.indexNoneGuessedWord.size());
	}

	/**
	 * Permet d'avoir un nouveau mot. Un nouveau mot est un mot qui n'a jamais été proposé.
	 * 
	 * @return le nouveau mot
	 */
	public Word giveANewWord() {
		if (pos == this.wordsForATour.size()) {
			// on revient à zéro, on boucle quoi
			pos = 0;
		}
			
		int randomPos = randInt(0, this.wordsForATour.size() - 1);
		System.out.println("On doit prendre le mot à la position: " + randomPos);
		int i = 0;
		Word choosenWord = null;
		for (Word word : this.wordsForATour.values()) {
			
			if (i == randomPos) {
				// on est sur le bon élément
				choosenWord = word;
				System.out.println("on a choisi: " + choosenWord.getSpelling());
				break;
			}
			i++;
		}
		
		return choosenWord;
	}
	
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	/**
	 * Permet de sauvegarder les résultats du mot.
	 */
	public void saveWord(Word aWord, boolean isANewWord) {
		if (isANewWord) {
			aWord.setFilename(aWord.getSpelling() + ".wav");
			
			this.words.put(aWord.getSpelling().toLowerCase(), aWord);
		} else {
			this.words.put(aWord.getSpelling().toLowerCase(), aWord);
		}
		
		Properties wordsProperties = new Properties();
		
		for (Map.Entry<String, Word> entry : this.words.entrySet()) {
		    String key = entry.getKey();
		    Word word = entry.getValue();
		    
		    wordsProperties.put(key, word.getFilename() 
		    		+ SEPARATOR + word.getCount() 
		    		+ SEPARATOR + word.getSuccess()
		    		+ SEPARATOR + word.getFailure());
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(FILENAME);
			wordsProperties.store(fos, "# mot=nom du fichier wav contenant le son;nb tentatives");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void extractWordsForATour(int nb) {
		// on doit prendre x mots en premier dans les mots non devinés d'avant et on complète avec des nouveaux mots.
		StringBuffer buffer = new StringBuffer();
		
		System.out.println("On doit prendre " + Math.min(nb, this.indexNoneGuessedWord.size()) + " mots en échec.");
		for (int i = 1; i <= Math.min(nb, this.indexNoneGuessedWord.size()); i++) {
			int randomPos = randInt(0, Math.max(0,this.indexNoneGuessedWord.size() - 1));
			
			Word word = this.words.get(this.indexNoneGuessedWord.get(randomPos));
			
			buffer.append(word.getSpelling()).append(", ");
			this.wordsForATour.put(word.getSpelling(), word);
		}
		
		// on complète
		System.out.println("On doit compléter avec " + (nb - (Math.min(nb, this.indexNoneGuessedWord.size()) + 1)) + " mots vierges.");
		for (int i = Math.min(nb, this.indexNoneGuessedWord.size()) + 1; i <= nb; i++) {
			int randomPos = randInt(0, Math.max(0,this.indexNonePlayedWord.size() -1));
			
			Word word = this.words.get(this.indexNonePlayedWord.get(randomPos));
			
			buffer.append(word.getSpelling()).append(", ");
			this.wordsForATour.put(word.getSpelling(), word);
		}
		
		System.out.println(buffer.toString());
	}
}
