/**
 *   (c) 2019  Charles Coughlin. All rights reserved.
 *   GNU GENERAL PUBLIC LICENSE.
 */
package redistrict.colorado.pref;

import java.util.prefs.Preferences;

/**
 *  This class provides for reading and writing preferences. In general,
 *  preferences define core configuration not appropriate for the database.
 */
public class PreferenceHandler  {
	private final static String CLSS = "PreferenceHandler";
	private final Preferences prefs;

	
	/**
	 * Constructor:
	 */
	public PreferenceHandler()  {
		this.prefs = Preferences.userRoot().node(PreferenceKeys.PREFERENCES_NAME);

	}

	/**
	 * @parm key
	 * @return the value of a Java preference used by the framework.
	 *         Execute this locally.
	 */
	public String getPreference(String key) {
		return prefs.get(key,"");
	}
	
	/**
	 * Set the value of a Java preference used by the test framework.
	 * @param the value of a Java preference used by the framework.
	 */
	public void setPreference(String key,String value) {
		prefs.put(key,value);
	}
	
}
