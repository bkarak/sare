package org.sare;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Container class for IGroupAction instances
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 * @see IGroupAction
 */
public class ActionList implements Iterable {
    private int group;
    private ArrayList<IGroupAction> actionList;

    /**
     * Standard Constructor
     *
     * @param group group identifier
     */
    public ActionList(int group) {
        this.group = group;
        this.actionList = new ArrayList<IGroupAction>();
    }

    /**
     * registers an ActionGroupInterface to the group
     *
     * @param gai the IGroupAction instance
     * @see IGroupAction
     */
    public void register(IGroupAction gai) {
        this.actionList.add(gai);
    }

    /**
     * Returns an iterator with the registered interfaces
     *
     * @return the iterator
     */
    public Iterator<IGroupAction> iterator() {
        return this.actionList.listIterator();
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
        for (IGroupAction IGroupAction : this.actionList) {
            IGroupAction.start();
        }
    }

    /**
     * Execute end() for all registered interfaces
     */
    public void end() {
        for (IGroupAction IGroupAction : this.actionList) {
            IGroupAction.end();
        }
    }

    /**
     * Execute find() for all registered interfaces
     */
    public void match(int count, String full, String group) {
        for (IGroupAction IGroupAction : this.actionList) {
            IGroupAction.match(count, full, group);
        }
    }
}
