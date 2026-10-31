/*
 * Copyright (c) 2013, 2026 Vassilios Karakoidas.
 * Licensed under the BSD 3-Clause License; see LICENSE.
 */
package org.sare;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The actions assigned to one group, in the order they were assigned.
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 * @see IGroupAction
 */
final class ActionList implements Iterable<IGroupAction> {
    private final int group;
    private final List<IGroupAction> actions = new ArrayList<>();

    ActionList(int group) {
        this.group = group;
    }

    void register(IGroupAction action) {
        actions.add(action);
    }

    int group() {
        return group;
    }

    @Override
    public Iterator<IGroupAction> iterator() {
        return actions.iterator();
    }

    /**
     * Calls {@link IGroupAction#match} on every action of this group.
     */
    void match(String full, String text) {
        for (IGroupAction action : actions) {
            action.match(group, full, text);
        }
    }
}
