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

package com.dmdirc.parser.interfaces;

import com.dmdirc.parser.common.AwayState;

import java.util.List;
import java.util.Map;

/**
 * Holds information about a client, and provides various methods for
 * interacting with that client.
 *
 * @since 0.6.3m2
 */
public interface ClientInfo {

    /**
     * Retrieves the nickname or display name used by this client.
     *
     * @return This client's nickname
     */
    String getNickname();

    /**
     * Retrieves the username or ident used by this client.
     *
     * @return This client's username
     */
    String getUsername();

    /**
     * Retrieves the hostname that this client is connecting from.
     *
     * @return This client's hostname
     */
    String getHostname();

    /**
     * Retrieves the full/real name of the client.
     *
     * @return This client's real name
     */
    String getRealname();

    /**
     * Retrieves the network account name of the client. (eg, Q Auth)
     *
     * @return This client's network account name. (null if not set)
     */
    String getAccountName();

    /**
     * Retrieves the number of channels that this client is known to be on.
     *
     * @return The number of channels the client is known to be on
     */
    int getChannelCount();

    /**
     * Get a list of channelClients that point to this object.
     *
     * @return int with the count of known channels
     */
    List<ChannelClientInfo> getChannelClients();

    /**
     * Get the away state of a user.
     *
     * @return AwayState of the user.
     */
    AwayState getAwayState();

    /**
     * Get the Away Reason for this user.
     *
     * @return Known away reason for user.
     */
     String getAwayReason();

    /**
     * Retrieves a {@link Map} which can be used to store arbitrary data
     * about the client.
     *
     * @return A map used for storing arbitrary data
     */
    Map<Object, Object> getMap();

    /**
     * Retrieves the parser which created this ClientInfo.
     *
     * @return This ClientInfo's parser
     */
    Parser getParser();

}
