package framework;

/**
 * Interface définissant les actions applicable à une extension,
 * mais exécuté par son proxy
 */
interface IExtensionActions {
	
	/**
	 * Charge l'extension
	 * 
	 * @return Vrai si l'extension a bien été chargé, faux sinon
	 */
	boolean load();
	
	/**
	 * Kill l'extension
	 * 
	 * @return Vrai si l'extension a bien été killé, faux sinon
	 */
	boolean kill();
	
	// 
	
	/**
	 * Récupère l'instance de l'extension
	 * 
	 * @return l'instance de l'extension
	 */
	IExtension getExtension();
	
	/**
	 * Récupère le status de l'extension
	 * 
	 * @return le status de l'extension
	 */
	String getStatus();
	
	/**
	 * Récupère la description de l'extension
	 * 
	 * @return la description de l'extension
	 */
	String getDescription();
	
	/**
	 * Indique si l'extension peut être killé
	 * 
	 * @return Vrai si l'extension peut être killé, faux sinon
	 */
	boolean isKillable();
	
	/**
	 * Permet de savoir si c'est le proxy d'une extension
	 * 
	 * @param extension		instance d'une extension
	 * @return				Vrai si l'instance donnée en paramètre est la même instance que ce proxy, faux sinon
	 */
	boolean isProxyOf(IExtension extension);
}
