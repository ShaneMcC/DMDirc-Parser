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

import com.dmdirc.parser.events.MOTDEndEvent;
import com.dmdirc.parser.events.MOTDLineEvent;
import com.dmdirc.parser.events.MOTDStartEvent;
import com.dmdirc.parser.irc.IRCParser;

import java.time.LocalDateTime;

import javax.inject.Inject;

/**
 * Process a MOTD Related Line.
 */
public class ProcessMOTD extends IRCProcessor {

    /**
     * Create a new instance of the IRCProcessor Object.
     *
     * @param parser IRCParser That owns this IRCProcessor
     */
    @Inject
    public ProcessMOTD(final IRCParser parser) {
        super(parser, "372", "375", "376", "422");
    }

    /**
     * Process a MOTD Related Line.
     *
     * @param sParam Type of line to process ("375", "372", "376", "422")
     * @param token IRCTokenised line to process
     */
    @Override
    public void process(final LocalDateTime time, final String sParam, final String... token) {
        switch (sParam) {
            case "375":
                callMOTDStart(time, token[token.length - 1]);
                break;
            case "372":
                callMOTDLine(time, token[token.length - 1]);
                break;
            default:
                callMOTDEnd(time, "422".equals(sParam), token[token.length - 1]);
                break;
        }
    }

    /**
     * Callback to all objects implementing the MOTDEnd Callback.
     *
     * @param noMOTD Was this an MOTDEnd or NoMOTD
     * @param data The contents of the line (incase of language changes or so)
     */
    protected void callMOTDEnd(final LocalDateTime time, final boolean noMOTD, final String data) {
        getCallbackManager().publish(new MOTDEndEvent(parser, time, noMOTD, data));
    }

    /**
     * Callback to all objects implementing the MOTDLine Callback.
     *
     * @param data Incomming Line.
     */
    protected void callMOTDLine(final LocalDateTime time, final String data) {
        getCallbackManager().publish(new MOTDLineEvent(parser, time, data));
    }

    /**
     * Callback to all objects implementing the MOTDStart Callback.
     *
     * @param data Incomming Line.
     */
    protected void callMOTDStart(final LocalDateTime time, final String data) {
        getCallbackManager().publish(new MOTDStartEvent(parser, time, data));
    }

}
