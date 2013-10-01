package org.sare;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Container class for IGroupAction instances
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 * @see IGroupAction
 */
public class ActionList extends ArrayList<IGroupAction>{
    private int group;

    /**
     * Standard Constructor
     *
     * @param group group identifier
     */
    public ActionList(int group) {
        this.group = group;
    }

    /**
     * registers an ActionGroupInterface to the group
     *
     * @param gai the IGroupAction instance
     * @see IGroupAction
     */
    public void register(IGroupAction gai) {
        add(gai);
    }

    /**
     * Returns an iterator with the registered interfaces
     *
     * @return the iterator
     */
    @Override
    public Iterator<IGroupAction> iterator() {
        return this.listIterator();
    }

    /**
     * Return the group number
     *
     * @return group number
     */
    public int getGroup() {
        return group;
    }

    /**
     * Execute start() for all registered interfaces
     */
    public void start() {
        int c = size();
        for (IGroupAction IGroupAction : this) {
            IGroupAction.start();
        }
    }

    /**
     * Execute end() for all registered interfaces
     */
    public void end() {
        for (IGroupAction IGroupAction : this) {
            IGroupAction.end();
        }
    }

    /**
     * Execute find() for all registered interfaces
     */
    public void match(int count, String full, String group) {
        for (IGroupAction IGroupAction : this) {
            IGroupAction.match(count, full, group);
        }
    }
}
