package ave.bertrand;

public class Word {

	/**
	 * écriture du mot à trouver.
	 */
	private String spelling;

	/**
	 * nom du fichier contenant le son du mot.
	 */
	private String filename;

	/**
	 * Nombre de tentative.
	 */
	private long count;
	
	/**
	 * Nombre de fois ou le mot a été trouvé.
	 */
	private long success;
	
	private long failure;

	public Word() {
		super();
		
		this.count = 0;
	}
	
	public Word(String spelling, String filename, int count, int success, int failure) {
		super();
		
		this.spelling = spelling.toLowerCase();
		this.filename = filename;
		this.count = count;
		this.setSuccess(success);
		this.setFailure(failure);
	}
	
	public String getSpelling() {
		return spelling.toLowerCase();
	}

	public void setSpelling(String spelling) {
		this.spelling = spelling.toLowerCase();
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * Permet de pointer une tentative sur un mot. 
	 * 
	 * @param b true si la tentative est bonne, false sinon
	 */
	public void addAttempt(boolean goodAttempt) {
		this.count++;
		if (goodAttempt) {
			this.setSuccess(this.getSuccess() + 1);
		} else {
			this.setFailure(this.getFailure() + 1);
		}
	}

	/**
	 * Permet de mettre à zéro les stats sur un mot.
	 */
	public void reset() {
		this.count = 0;
		this.setSuccess(0);
		this.setFailure(0);
	}

	public long getSuccess() {
		return success;
	}

	public void setSuccess(long success) {
		this.success = success;
	}

	public long getFailure() {
		return failure;
	}

	public void setFailure(long failure) {
		this.failure = failure;
	}
}
