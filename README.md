SARE - Simple API for Regular Expressions
=========================================

SARE stands for Simple API for Regular Expressions. Its an extension API that provides event based regular expression parsing. SARE's API is very simple. The developer need only to implement an interface, action event handler and attach it to the specific group of SARE parser. For example, the following listing shows have SARE can be used to parse each 4 digits from an IP address.

<pre>
public class ExampleSARE {
    public static void main(String[] args) {
        String regex = "([0-9]+)\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)";
        String data  = "192.255.255.150";

        RegexParser sare = new RegexParser(regex);
        ConsoleOut co = new ConsoleOut();

        sare.assignAction(0, co);
        sare.assignAction(1, co);
        sare.assignAction(2, co);
        sare.assignAction(3, co);
        sare.assignAction(4, co);

        sare.process(data);
    }
}

class ConsoleOut implements IGroupAction {
    public void start() {
        System.out.println("Starting");
    }

    public void end() {
        System.out.println("End");
    }

    public void match(int count, String full, String group) {
        System.out.println("[" + count + "] - " + group);
    }
}
</pre>

As you can see, SARE follows a variant of the visitor pattern. For each group defined in a regex, you can assign an object that implements the <code>IGroupAction</code> interface. When the method <code>process()</code> is invoked, the scan process begins and when a match is found, the specified IGroupAction is executed, through the <code>match</code> method.

SARE at the time being uses the standard JSDK regular expression library (<code>java.util.regex.*</code>).

This pattern provides an easy way to write less and more reusable code, when text processing is required. Current Version is 2.0. So feel free to download, experiment with the source and send me any comments.

License
=======
Copyright (c) 2012, Vassilios Karakoidas (vassilios.karakoidas@gmail.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

+ Redistributions of source code must retain the above copyright
+ Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
+ The names of its contributors may not be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Vassilios Karakoidas BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.



