/*
# Copyright (c) 2012, Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright

    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * The names of its contributors may not be used to endorse or promote products
      derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL Vassilios Karakoidas BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
     * registers an IGroupAction to the group
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
