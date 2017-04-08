package org.adorsys.jjwk.keystore;

/**
 * Represents a password source.
 *
 * The password returned to the caller is held in the object. The caller must call
 * cleanup to reset all password arrays contained in the password source object.
 * 
 * Generally the password source shall not initialize those password arrays 
 * unless the getPassword method is called. Then the caller of getPassword must
 * call initialized in the finally block to make sure the password array is 
 * reset.
 * 
 * @author fpo
 *
 */
public interface PasswordSource {
	
	/**
	 * Returns the password for the corresponding entry.
	 * 
	 * @param entryName
	 * @return
	 */
	public char[] getPassword(String entryName);

	/**
	 * Cleans up password arrays contained in this source.
	 */
	public void cleanup();
}
