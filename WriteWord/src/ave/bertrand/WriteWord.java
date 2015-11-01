package ave.bertrand;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

/**
 * Classe principale contenant le main.
 * 
 * 
 * @author bertrand
 *
 */
@SuppressWarnings("serial")
public class WriteWord extends JPanel implements ActionListener {

	private static final String SHOW_WORD_LABEL_BUTTON = "Show word";

	private static final String CHECK_WORD_LABEL_BUTTON = "Check word";

	private static final String NEXT_WORD_LABEL_BUTTON = "Next word";

	private static final String LISTEN_LABEL_BUTTON = "Listen";

	private static final String PLAY_LABEL_BUTTON = "Play";

	private static final String RECORD_LABEL_BUTTON = "Record";

	private static final String SAVE_WORD_LABEL_BUTTON = "Save word";
	
	private static final String START_LABEL_BUTTON = "Start";
	
	private static final String STOP_LABEL_BUTTON = "Stop";
	
	private static final String LISTEN_RECORDEDWORD_LABEL_BUTTON = "Listen word";

	/**
	 * Lien vers le moteur de proposition de mots.
	 */
	private WordEngine wordEngine;

	/**
	 * Lien vers l'enregistreur de mot.
	 */
	private CaptureWord recorder;
	
	/**
	 * Le mot en cours.
	 */
	private Word currentWord;

	/**
	 * Bouton pour démarrer le jeu.
	 */
	private JButton playButton;

	/**
	 * Bouton pour écouter et ré-écouter le mot.
	 */
	private JButton listenButton;

	/**
	 * Bouton pour passer au mot suivant.
	 */
	private JButton nextWordButton;

	/**
	 * Bouton pour contrôler le mot courant.
	 */
	private JButton checkWordButton;

	/**
	 * Bouton pour voir le mot.
	 */
	private JButton showWordButton;

	/**
	 * Bouton pour ajouter un mot.
	 */
	private JButton recordButton;
	
	/**
	 * Bouton pour enregistrer un mot.
	 */
	private JButton startButton;
	
	/**
	 * Bouton pour stopper l'enregistrement d'un mot.
	 */
	private JButton stopButton;

	/**
	 * Bouton pour écouter et ré-écouter le mot en mode enregistrement.
	 */
	private JButton listenRecordedWordButton;
	
	/**
	 * Bouton piur enregistrer le nouveau mot.
	 */
	private JButton saveWordButton;

	/**
	 * Zone de saisie pour l'utilisateur.
	 */
	private JTextField inputTextField;

	/**
	 * Texte pour afficher le résultat d'une tentative de l'utilisateur.
	 */
	private JLabel resultLabel;
	
	private JLabel attemptLabel;

	/**
	 * Icône pour indiquer un succès.
	 */
	private ImageIcon iconSuccess = new ImageIcon("images/middle.gif");

	/**
	 * Icône pour indiquer un échec.
	 */
	private ImageIcon iconFailure = new ImageIcon("images/middle.gif");

	/**
	 * Nombre de click sur le bouton Check Word.
	 */
	private int nbClickCheckWordButton = 0;

	private boolean isSuccessWithTips;

	private static JFrame f;

	private int nb_words = 25;
	
	private int attempt = 0;
	
	public static void main(String[] args) {
		WriteWord ssc = new WriteWord();
		ssc.open();

		f = new JFrame("Write words");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add("Center", ssc);
		f.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = 720;
		int h = 340;
		f.setLocation(screenSize.width / 2 - w / 2, screenSize.height / 2 - h
				/ 2);
		f.setSize(w, h);
		f.setVisible(true);
	}

	/**
	 * Constructeur.
	 * 
	 * Permet de construire l'aspect graphique de l'application.
	 */
	public WriteWord() {
		this.wordEngine = new WordEngine();
		this.recorder = new CaptureWord(this);

		setLayout(new BorderLayout());
		EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
		SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.LOWERED);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

		JPanel p2 = new JPanel();
		p2.setBorder(sbb);
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));

		JPanel buttonsPanel1 = new JPanel();
		buttonsPanel1.setBorder(new EmptyBorder(10, 0, 5, 0));
		JPanel buttonsPanel2 = new JPanel();
		buttonsPanel2.setBorder(new EmptyBorder(10, 0, 5, 0));
		this.playButton = addButton(PLAY_LABEL_BUTTON, buttonsPanel1, true);
		this.listenButton = addButton(LISTEN_LABEL_BUTTON, buttonsPanel1, false);
		this.checkWordButton = addButton(CHECK_WORD_LABEL_BUTTON,
				buttonsPanel1, false);
		this.nextWordButton = addButton(NEXT_WORD_LABEL_BUTTON, buttonsPanel1,
				false);
		this.showWordButton = addButton(SHOW_WORD_LABEL_BUTTON, buttonsPanel1,
				false);
		
		this.recordButton = addButton(RECORD_LABEL_BUTTON, buttonsPanel2, true);
		this.startButton = addButton(START_LABEL_BUTTON, buttonsPanel2, false);
		this.stopButton = addButton(STOP_LABEL_BUTTON, buttonsPanel2, false);
		this.listenRecordedWordButton = addButton(LISTEN_RECORDEDWORD_LABEL_BUTTON, buttonsPanel2, false);
		this.saveWordButton = addButton(SAVE_WORD_LABEL_BUTTON, buttonsPanel2,
				false);

		Font font1 = new Font("SansSerif", Font.BOLD, 40);
		this.inputTextField = new JTextField(200);
		this.inputTextField.setFont(font1);
		this.inputTextField.setHorizontalAlignment(JTextField.CENTER);
		//this.inputTextField.setVe);(JTextField.CENTER);

		this.resultLabel = new JLabel(
				"Le résultat de vos tentatives s'affichera içi.",
				this.iconSuccess, JLabel.CENTER);
		p2.add(buttonsPanel1);
		
		this.attemptLabel = new JLabel(
				"Tentatives: 0", JLabel.CENTER);
		p2.add(this.attemptLabel);
		
		p2.add(buttonsPanel2);

		p2.add(this.inputTextField);

		p2.add(this.resultLabel);

		p1.add(p2);
		add(p1);
	}

	public void open() {
	}

	/**
	 * Permet d'ajouter un bouton.
	 * 
	 * @param name
	 * @param panel
	 * @param state
	 *            actif ou pas
	 * @return
	 */
	private JButton addButton(String name, JPanel panel, boolean state) {
		JButton b = new JButton(name);
		b.addActionListener(this);
		b.setEnabled(state);
		panel.add(b);
		return b;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj.equals(this.playButton)) {
			if (this.playButton.getText().startsWith(PLAY_LABEL_BUTTON)) {
				
				String nbWords = JOptionPane.showInputDialog(
				        this, 
				        "Donne moi le nombre de mots", 
				        "Nombre de mots", 
				        JOptionPane.WARNING_MESSAGE);
				
				try {
					nb_words = Integer.parseInt(nbWords);
					
					if (nb_words <= 0) {
						nb_words = 25;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					nb_words = 25;
				}
				
				this.wordEngine.extractWordsForATour(nb_words);
				
				this.currentWord = this.wordEngine.giveANewWord();

				manageButtonsState(PLAY_LABEL_BUTTON, false);

				System.out.println(this.currentWord.getSpelling());
			}
		}
		if (obj.equals(this.recordButton)) {
			if (this.recordButton.getText().startsWith(RECORD_LABEL_BUTTON)) {
				this.currentWord = new Word();

				manageButtonsState(RECORD_LABEL_BUTTON, false);
			}
		}
		if (obj.equals(this.startButton)) {
			if (this.startButton.getText().startsWith(START_LABEL_BUTTON)) {
				if (this.inputTextField.getText().trim().length()<= 0) {
					JOptionPane.showMessageDialog(this, "Tu dois d'abord donner le mot à enregistrer !", 
						"Ajout d'un mot", JOptionPane.WARNING_MESSAGE);
				} else {
					manageButtonsState(START_LABEL_BUTTON, false);
					
					// FIXME que faire si le mot est déjà dans le dico ?
					this.currentWord.setFilename(this.inputTextField.getText().trim() + ".wav");
				
					this.recorder.start();
				}
			}
		}
		if (obj.equals(this.stopButton)) {
			if (this.stopButton.getText().startsWith(STOP_LABEL_BUTTON)) {
				this.recorder.stop();
				
				manageButtonsState(STOP_LABEL_BUTTON, false);
			}
		}
		if (obj.equals(this.listenRecordedWordButton)) {
			if (this.listenRecordedWordButton.getText().startsWith(LISTEN_RECORDEDWORD_LABEL_BUTTON)) {
				
				if (this.currentWord != null) {
					this.playASound(this.currentWord.getFilename());

					manageButtonsState(LISTEN_RECORDEDWORD_LABEL_BUTTON, false);
				}
			}
		}
		if (obj.equals(this.saveWordButton)) {
			if (this.saveWordButton.getText().startsWith(SAVE_WORD_LABEL_BUTTON)) {
				
				if (this.inputTextField.getText().trim().length() < 1) {
					// la zone est vide, c'est interdit
					JOptionPane.showMessageDialog(f, "Il faut écrire le mot qui correspond au son !");
					
				} else {
				
					this.currentWord.setSpelling(this.inputTextField.getText().trim().toLowerCase());
					
					
					this.wordEngine.saveWord(this.currentWord, true);
					
					this.inputTextField.setText("");
					manageButtonsState(SAVE_WORD_LABEL_BUTTON, false);
				}
			}
		}
		if (obj.equals(this.listenButton)) {
			if (this.listenButton.getText().startsWith(LISTEN_LABEL_BUTTON)) {
				// l'utilisateur a cliqué sur le bouton Listen, on doit jouer le
				// son du mot courant
				if (this.currentWord != null) {
					this.playASound(this.currentWord.getFilename());

					manageButtonsState(LISTEN_LABEL_BUTTON, false);
				}
			}
		}

		if (obj.equals(this.nextWordButton)) {
			if (this.nextWordButton.getText()
					.startsWith(NEXT_WORD_LABEL_BUTTON)) {
				// l'utilisateur a cliqué sur le bouton Next word, on doit lui
				// proposer un nouveau mot
				// mais d'abord on doit mémoriser son échec sur le mot courant
				this.currentWord.addAttempt(false);
				this.wordEngine.saveWord(this.currentWord, false);

				nbClickCheckWordButton = 0;

				this.isSuccessWithTips = false;

				this.currentWord = this.wordEngine.giveANewWord();

				System.out.println(this.currentWord.getSpelling());

				attempt++;
				this.attemptLabel.setText("Tentatives: " + this.attempt + "/" + this.nb_words);
				
				manageButtonsState(NEXT_WORD_LABEL_BUTTON, false);
			}
		}

		if (obj.equals(this.showWordButton)) {
			if (this.showWordButton.getText()
					.startsWith(SHOW_WORD_LABEL_BUTTON)) {
				// l'utilisateur a cliqué sur le bouton Show word, on doit lui
				// montrer le résultat.
				if (this.currentWord != null) {
					JOptionPane.showMessageDialog(f, "Le mot est: "
							+ this.currentWord.getSpelling());

					this.isSuccessWithTips = true;

					manageButtonsState(SHOW_WORD_LABEL_BUTTON, false);
				}
			}
		}
		if (obj.equals(this.checkWordButton)) {
			if (this.checkWordButton.getText().startsWith(
					CHECK_WORD_LABEL_BUTTON)) {
				// l'utilisateur a cliqué sur le bouton Check word, on doit
				// contrôler sa saisie

				nbClickCheckWordButton++;
				System.out.println("nbClickCheckWordButton= "
						+ nbClickCheckWordButton);

				String userInput = this.inputTextField.getText().trim()
						.toLowerCase();

				if (userInput.equalsIgnoreCase(this.currentWord.getSpelling())) {
					// l'utilisateur a bon mais peut être avec une aide
					if (this.isSuccessWithTips) {
						this.currentWord.addAttempt(false);
						this.resultLabel
								.setText("Vous avez bon MAIS avec de l'aide !");
					} else {
						this.currentWord.addAttempt(true);
						this.resultLabel.setText("Vous avez bon. BRAVO !");
					}
					this.wordEngine.saveWord(this.currentWord, false);
					
					attempt++;
					this.attemptLabel.setText("Tentatives: " + this.attempt + "/" + this.nb_words);

					this.currentWord = this.wordEngine.giveANewWord();

					System.out.println(this.currentWord.getSpelling());

					nbClickCheckWordButton = 0;

					this.isSuccessWithTips = false;

					manageButtonsState(CHECK_WORD_LABEL_BUTTON, true);

				} else {
					// l'utilisateur a fait une erreur
					this.currentWord.addAttempt(false);
					this.wordEngine.saveWord(this.currentWord, false);

					this.resultLabel
							.setText("Vous avez faux. il faut réesséyez !");

					manageButtonsState(CHECK_WORD_LABEL_BUTTON, false);

				}
			}
		}
	}

	/**
	 * Permet de gérer les états des boutons.
	 * 
	 * @param clickedButton
	 *            label du bouton cliqué
	 * @param success
	 *            est ce un succès ?
	 */
	private void manageButtonsState(String clickedButton, boolean success) {
		if (clickedButton.equalsIgnoreCase(PLAY_LABEL_BUTTON)) {
			this.playButton.setEnabled(false);
			this.recordButton.setEnabled(false);

			this.listenButton.setEnabled(true);
		}
		if (clickedButton.equalsIgnoreCase(RECORD_LABEL_BUTTON)) {
			this.playButton.setEnabled(false);
			this.recordButton.setEnabled(false);

			this.startButton.setEnabled(true);
			this.saveWordButton.setEnabled(false);
		}
		if (clickedButton.equalsIgnoreCase(START_LABEL_BUTTON)) {
			this.startButton.setEnabled(false);
			this.saveWordButton.setEnabled(false);
			this.stopButton.setEnabled(true);
		}
		if (clickedButton.equalsIgnoreCase(STOP_LABEL_BUTTON)) {
			this.startButton.setEnabled(true);
			this.stopButton.setEnabled(false);
			
			this.listenRecordedWordButton.setEnabled(true);
			this.saveWordButton.setEnabled(true);
		}
		if (clickedButton.equalsIgnoreCase(SAVE_WORD_LABEL_BUTTON)) {
			this.playButton.setEnabled(true);
			this.recordButton.setEnabled(true);

			this.stopButton.setEnabled(false);
			this.startButton.setEnabled(false);
			this.listenRecordedWordButton.setEnabled(false);
			this.saveWordButton.setEnabled(false);
		}
		if (clickedButton.equalsIgnoreCase(LISTEN_LABEL_BUTTON)) {
			this.checkWordButton.setEnabled(true);
		}
		if (clickedButton.equalsIgnoreCase(CHECK_WORD_LABEL_BUTTON) && success) {
			this.checkWordButton.setEnabled(false);
			this.showWordButton.setEnabled(false);
			this.nextWordButton.setEnabled(false);

			this.inputTextField.setText("");
			
			if (this.attempt == this.nb_words) {
				// on est au bout des tentatives, il faut arrêter
				this.listenButton.setEnabled(false);
				this.playButton.setEnabled(true);
				this.attemptLabel.setText("Tentatives: 0");
				
				JOptionPane.showMessageDialog(f, "Tu as fini, BRAVO !");
			}
		}
		if (clickedButton.equalsIgnoreCase(CHECK_WORD_LABEL_BUTTON) && !success) {
			this.showWordButton.setEnabled(true);
			this.nextWordButton.setEnabled(true);
		}
		if (clickedButton.equalsIgnoreCase(NEXT_WORD_LABEL_BUTTON)) {
			this.checkWordButton.setEnabled(false);
			this.showWordButton.setEnabled(false);
			this.nextWordButton.setEnabled(false);

			this.inputTextField.setText("");
		}
	}

	/**
	 * Permet de jouer un son.
	 * 
	 * @param filename
	 *            fichier contenant le son à jouer.
	 */
	private void playASound(String filename) {
		try {
			// Open an audio input stream.
			URL url = new File(filename).toURI().toURL();

			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Word getCurrentWord() {
		return this.currentWord;
	} 
}
