/*
 * Copyright (c) 2006-2014 DMDirc Developers
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

import com.dmdirc.parser.common.ParserError;
import com.dmdirc.parser.interfaces.ChannelClientInfo;
import com.dmdirc.parser.interfaces.ChannelInfo;
import com.dmdirc.parser.interfaces.ClientInfo;
import com.dmdirc.parser.interfaces.callbacks.ChannelNickChangeListener;
import com.dmdirc.parser.interfaces.callbacks.NickChangeListener;
import com.dmdirc.parser.irc.IRCChannelClientInfo;
import com.dmdirc.parser.irc.IRCChannelInfo;
import com.dmdirc.parser.irc.IRCClientInfo;
import com.dmdirc.parser.irc.IRCParser;
import com.dmdirc.parser.irc.ProcessingManager;

/**
 * Process a Nick change.
 */
public class ProcessNick extends IRCProcessor {

    /**
     * Create a new instance of the IRCProcessor Object.
     *
     * @param parser IRCParser That owns this IRCProcessor
     * @param manager ProcessingManager that is in charge of this IRCProcessor
     */
    public ProcessNick(final IRCParser parser, final ProcessingManager manager) {
        super(parser, manager);
    }

    /**
     * Process a Nick change.
     *
     * @param sParam Type of line to process ("NICK")
     * @param token IRCTokenised line to process
     */
    @Override
    public void process(final String sParam, final String[] token) {
        final IRCClientInfo iClient;
        IRCChannelClientInfo iChannelClient;
        final String oldNickname;

        iClient = getClientInfo(token[0]);
        if (iClient == null) {
            return;
        }
        oldNickname = parser.getStringConverter().toLowerCase(iClient.getNickname());
        // Remove the client from the known clients list
        final boolean isSameNick = parser.getStringConverter().equalsIgnoreCase(oldNickname, token[token.length - 1]);

        if (!isSameNick) {
            parser.forceRemoveClient(getClientInfo(oldNickname));
        }
        // Change the nickame
        iClient.setUserBits(token[token.length - 1], true);
        // Readd the client
        if (!isSameNick && getClientInfo(iClient.getNickname()) != null) {
            parser.callErrorInfo(new ParserError(ParserError.ERROR_FATAL + ParserError.ERROR_USER, "Nick change would overwrite existing client", parser.getLastLine()));
        } else {
            if (!isSameNick) {
                parser.addClient(iClient);
            }

            for (IRCChannelInfo iChannel : parser.getChannels()) {
                // Find the user (using the old nickname)
                iChannelClient = iChannel.getChannelClient(oldNickname);
                if (iChannelClient != null) {
                    // Rename them. This uses the old nickname (the key in the hashtable)
                    // and the channelClient object has access to the new nickname (by way
                    // of the ClientInfo object we updated above)
                    if (!isSameNick) {
                        iChannel.renameClient(oldNickname, iChannelClient);
                    }
                    callChannelNickChanged(iChannel, iChannelClient, IRCClientInfo.parseHost(token[0]));
                }
            }

            callNickChanged(iClient, IRCClientInfo.parseHost(token[0]));
        }
    }

    /**
     * Callback to all objects implementing the ChannelNickChanged Callback.
     *
     * @see com.dmdirc.parser.interfaces.callbacks.ChannelNickChangeListener
     * @param cChannel One of the channels that the user is on
     * @param cChannelClient Client changing nickname
     * @param sOldNick Nickname before change
     * @return true if a method was called, false otherwise
     */
    protected boolean callChannelNickChanged(final ChannelInfo cChannel, final ChannelClientInfo cChannelClient, final String sOldNick) {
        return getCallbackManager().getCallbackType(ChannelNickChangeListener.class).call(cChannel, cChannelClient, sOldNick);
    }

    /**
     * Callback to all objects implementing the NickChanged Callback.
     *
     * @see com.dmdirc.parser.interfaces.callbacks.NickChangeListener
     * @param cClient Client changing nickname
     * @param sOldNick Nickname before change
     * @return true if a method was called, false otherwise
     */
    protected boolean callNickChanged(final ClientInfo cClient, final String sOldNick) {
        return getCallbackManager().getCallbackType(NickChangeListener.class).call(cClient, sOldNick);
    }

    /**
     * What does this IRCProcessor handle.
     *
     * @return String[] with the names of the tokens we handle.
     */
    @Override
    public String[] handles() {
        return new String[]{"NICK"};
    }
}