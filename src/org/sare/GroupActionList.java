package org.sare;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Container class for GroupActionInterface instances
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 * @see GroupActionInterface
 */
public class GroupActionList extends ArrayList<GroupActionInterface>{
    private int group;

    /**
     * Standard Constructor
     *
     * @param group group identifier
     */
    public GroupActionList(int group) {
        this.group = group;
    }

    /**
     * registers an ActionGroupInterface to the group
     *
     * @param gai the GroupActionInterface instance
     * @see GroupActionInterface
     */
    public void register(GroupActionInterface gai) {
        add(gai);
    }

    /**
     * Returns an iterator with the registered interfaces
     *
     * @return the iterator
     */
    public Iterator<GroupActionInterface> iterator() {
        return iterator();
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
        for(int i = 0;i < c;i++) {
            get(i).start();
        }
    }

    /**
     * Execute end() for all registered interfaces
     */
    public void end() {
        int c = size();
        for(int i = 0;i < c;i++) {
            get(i).end();
        }
    }

    /**
     * Execute find() for all registered interfaces
     */
    public void match(int count, String full, String group) {
        int c = size();
        for(int i = 0;i < c;i++) {
            get(i).match(count,full,group);
        }
    }
}
