/*
 * Copyright (c) 2006-2017 DMDirc Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dmdirc.parser.irc.processors;

import com.dmdirc.parser.common.CallbackManager;
import com.dmdirc.parser.common.ParserError;
import com.dmdirc.parser.common.QueuePriority;
import com.dmdirc.parser.irc.IRCChannelInfo;
import com.dmdirc.parser.irc.IRCClientInfo;
import com.dmdirc.parser.irc.IRCParser;

import java.time.LocalDateTime;

/**
 * IRCProcessor.
 * Superclass for all IRCProcessor types.
 */
public abstract class IRCProcessor {

    /** Reference to the IRCParser that owns this IRCProcessor. */
    protected final IRCParser parser;

    /** The names of the tokens this processor handles. */
    private final String[] handledTokens;

    // Some functions from the main parser are useful, and having to use parser.functionName
    // is annoying, so we also implement them here (calling them again using parser)
    /**
     * Create a new instance of the IRCProcessor Object.
     *  @param parser IRCParser That owns this IRCProcessor
     * @param handledTokens Tokens that this processor handles
     */
    protected IRCProcessor(final IRCParser parser, final String... handledTokens) {
        this.parser = parser;
        this.handledTokens = handledTokens;
    }

    /**
     * Callback to all objects implementing the IErrorInfo Interface.
     *
     * @param errorInfo ParserError object representing the error.
     */
    protected final void callErrorInfo(final ParserError errorInfo) {
        parser.callErrorInfo(errorInfo);
    }

    /**
     * Callback to all objects implementing the DebugInfo Callback.
     *
     * @param level Debugging Level (DEBUG_INFO, ndSocket etc)
     * @param data Debugging Information
     * @param args Formatting String Options
     */
    protected final void callDebugInfo(final int level, final String data, final Object... args) {
        parser.callDebugInfo(level, data, args);
    }

    /**
     * Callback to all objects implementing the DebugInfo Callback.
     *
     * @param level Debugging Level (DEBUG_INFO, ndSocket etc)
     * @param data Debugging Information
     */
    protected final void callDebugInfo(final int level, final String data) {
        parser.callDebugInfo(level, data);
    }

    /**
     * Check if a channel name is valid .
     *
     * @param sChannelName Channel name to test
     * @return true if name is valid on the current connection, false otherwise. (Always false before noMOTD/MOTDEnd)
     */
    protected final boolean isValidChannelName(final String sChannelName) {
        return parser.isValidChannelName(sChannelName);
    }

    /**
     * Get the ClientInfo object for a person.
     *
     * @param sWho Who can be any valid identifier for a client as long as it contains a nickname (?:)nick(?!ident)(?@host)
     * @return ClientInfo Object for the client, or null
     */
    protected final IRCClientInfo getClientInfo(final String sWho) {
        return parser.isKnownClient(sWho) ? parser.getClient(sWho) : null;
    }

    /**
     * Get the ChannelInfo object for a channel.
     *
     * @param name This is the name of the channel.
     * @return ChannelInfo Object for the channel, or null
     */
    protected final IRCChannelInfo getChannel(final String name) {
        return parser.getChannel(name);
    }

    protected CallbackManager getCallbackManager() {
        return parser.getCallbackManager();
    }

    /**
     * Send a line to the server and add proper line ending.
     *
     * @param line Line to send (\r\n termination is added automatically)
     * @param priority Priority of this line.
     */
    protected final void sendString(final String line, final QueuePriority priority) {
        parser.sendString(line, priority);
    }

    /**
     * Process a Line.
     *
     * @param date Date of this line
     * @param sParam Type of line to process ("005", "PRIVMSG" etc)
     * @param token IRCTokenised line to process
     */
    public abstract void process(final LocalDateTime date, final String sParam, final String... token);

    /**
     * What does this IRCProcessor handle.
     *
     * @return String[] with the names of the tokens we handle.
     */
    public String[] handles() {
        return handledTokens;
    }

    /**
     * Get the name for this Processor.
     * @return the name of this processor
     */
    public final String getName() {
        final Package thisPackage = getClass().getPackage();
        int packageLength = 0;
        if (thisPackage != null) {
            packageLength = thisPackage.getName().length() + 1;
        }
        return getClass().getName().substring(packageLength);
    }

    /**
     * Get the name for this Processor.
     * @return the name of this processor
     */
    @Override
    public final String toString() {
        return getName();
    }
}
