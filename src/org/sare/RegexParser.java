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

import org.sare.errors.DefaultError;
import org.sare.errors.SAREError;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import java.util.HashMap;
import java.util.Collection;
import java.io.InputStream;
import java.io.BufferedReader;

/**
 * The RegexParser (Simple API for Regular Expressions) class (java.util.regex version)
 *
 * @author Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
 * @see java.util.regex
 */
public class RegexParser {
    private Pattern pattern;
    private HashMap<Integer,ActionList> action;
    private SAREError error = new DefaultError();

    /**
     * Standard Constructor
     *
     * @param regex the input regular expressions
     */
    public RegexParser(String regex) {
        try {
            pattern = Pattern.compile(regex);
            action = new HashMap<Integer, ActionList>();
        } catch (PatternSyntaxException pse) {
            error.error(regex,pse);
        }
    }

    /**
     * Standard Constructor
     *
     * @param pattern the compiled Regular Expression
     * @see java.util.regex.Pattern
     */
    public RegexParser(Pattern pattern) {
        this.pattern = pattern;
        action = new HashMap<Integer,ActionList>();
    }

    /**
     * Registers an action for a specific group
     *
     * @param act the target group
     * @param gai the instance of the class that implements the interface IGroupAction
     * @see IGroupAction
     */
    public void assignAction(int act, IGroupAction gai) {
        ActionList gal = action.get(new Integer(act));
        if(gal == null) {
            gal = new ActionList(act);
            action.put(act, gal);
        }
        gal.register(gai);
    }

    /**
     * Sets the generic error handler for this parser
     *
     * @param error instance of the error handler
     */
    public void setError(SAREError error) {
        this.error = error;
    }

    /**
     * Process a Character Sequence as input data
     *
     * @param cs the charsequence object
     * @return number of matches
     */
    public int process(CharSequence cs) {
        start();
        int result = find(cs);
        end();

        return result;
    }

    /**
     * Process an InputStream as input data
     *
     * @param is the input stream
     * @return number of matches
     */
    public int process(InputStream is) {
        int result = 0;
        start();

        try {
            int avail = is.available();
            byte[] arr = new byte[avail];
            is.read(arr, 0, avail);
            result = find(new String(arr));
        } catch (Exception e) {
            error.error(pattern.pattern(),e);
            return 0;
        }

        end();

        return result;
    }

    /**
     * Process a BufferedReader as input data
     *
     * @param br the buffered reader
     * @return number of matches
     */
    public int process(BufferedReader br) {
        int result = 0;
        start();

        try {
            String s = "";
            while(s != null) {
                s = br.readLine();
                result += find(s);
            }
        } catch (Exception e) {
            error.error(pattern.pattern(), e);
            return 0;
        }

        end();

        return result;
    }

    /**
     * executes start() method for all registered actions
     *
     * @see IGroupAction
     */
    private void start() {
        Collection<ActionList> col = action.values();
        for (ActionList aCol : col) {
            aCol.start();
        }
    }

    /**
     * executes end() method for all registered actions
     *
     * @see IGroupAction
     */
    private void end() {
        Collection<ActionList> col = action.values();
        for (ActionList aCol : col) {
            aCol.end();
        }
    }

    /**
     * executes find(CharSequence) method for all registered actions
     *
     * @param cs the CharSequence instance
     * @return number of matches
     */
    private int find(CharSequence cs) {
        Matcher matcher = pattern.matcher(cs);
        int matchCount = 0;

        while(matcher.find()) {
            matchCount++;
            int groupCount = matcher.groupCount();
            for(int i = 0;i <= groupCount;i++) {
                ActionList gal = action.get(new Integer(i));
                if(gal == null) { continue; }
                gal.match(i,matcher.group(0),matcher.group(i));
            }
        }

        return matchCount;
    }
}
