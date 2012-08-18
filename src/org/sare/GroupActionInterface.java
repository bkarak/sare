package org.sare;

/**
 * The action interface. Each action must implement this.
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 */
public interface GroupActionInterface {
    /**
     * Executed once, just before the match initiates
     */
    public void start();

    /**
     * Executed once, just after the match ends
     */
    public void end();

    /**
     * This method is called each time a match occurs for each registered group
     *
     * @param count match count (incremental)
     * @param full the full match (group(0)) 
     * @param group group match (group(count))
     */
    public void match(int count, String full, String group);
}
