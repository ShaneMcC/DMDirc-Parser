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

package com.dmdirc.parser.irc;

import com.dmdirc.parser.common.ParserError;
import com.dmdirc.parser.interfaces.callbacks.NumericListener;
import com.dmdirc.parser.irc.processors.IRCProcessor;
import com.dmdirc.parser.irc.processors.Process001;
import com.dmdirc.parser.irc.processors.Process004005;
import com.dmdirc.parser.irc.processors.Process464;
import com.dmdirc.parser.irc.processors.ProcessAccount;
import com.dmdirc.parser.irc.processors.ProcessAway;
import com.dmdirc.parser.irc.processors.ProcessCap;
import com.dmdirc.parser.irc.processors.ProcessInvite;
import com.dmdirc.parser.irc.processors.ProcessJoin;
import com.dmdirc.parser.irc.processors.ProcessKick;
import com.dmdirc.parser.irc.processors.ProcessList;
import com.dmdirc.parser.irc.processors.ProcessListModes;
import com.dmdirc.parser.irc.processors.ProcessMOTD;
import com.dmdirc.parser.irc.processors.ProcessMessage;
import com.dmdirc.parser.irc.processors.ProcessMode;
import com.dmdirc.parser.irc.processors.ProcessNames;
import com.dmdirc.parser.irc.processors.ProcessNick;
import com.dmdirc.parser.irc.processors.ProcessNickInUse;
import com.dmdirc.parser.irc.processors.ProcessNoticeAuth;
import com.dmdirc.parser.irc.processors.ProcessPart;
import com.dmdirc.parser.irc.processors.ProcessQuit;
import com.dmdirc.parser.irc.processors.ProcessTopic;
import com.dmdirc.parser.irc.processors.ProcessWallops;
import com.dmdirc.parser.irc.processors.ProcessWho;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * IRC Parser Processing Manager.
 * Manages adding/removing/calling processing stuff.
 */
public class ProcessingManager {

    /** Reference to the parser object that owns this ProcessingManager. */
    private final IRCParser parser;
    /** Hashtable used to store the different types of IRCProcessor known. */
    private final Map<String, IRCProcessor> processHash = new HashMap<>();

    /**
     * Constructor to create a ProcessingManager.
     *
     * @param parser IRCParser that owns this Processing Manager
     * @param prefixModeManager The manager to use to access prefix modes.
     * @param userModeManager Mode manager to use for user modes.
     * @param chanModeManager Mode manager to use for channel modes.
     */
    public ProcessingManager(final IRCParser parser, final PrefixModeManager prefixModeManager,
            final ModeManager userModeManager, final ModeManager chanModeManager) {
        this.parser = parser;
        //------------------------------------------------
        // Add processors
        //------------------------------------------------
        // NOTICE AUTH
        addProcessor(new ProcessNoticeAuth(parser, this));
        // 001
        addProcessor(new Process001(parser, this));
        // 004
        // 005
        addProcessor(new Process004005(parser, this));
        // 464
        addProcessor(new Process464(parser, this));
        // 301
        // 305
        // 306
        addProcessor(new ProcessAway(parser, this));
        // 352
        addProcessor(new ProcessWho(parser, this));
        // INVITE
        addProcessor(new ProcessInvite(parser, this));
        // JOIN
        addProcessor(new ProcessJoin(parser, prefixModeManager, userModeManager, chanModeManager,
                this));
        // KICK
        addProcessor(new ProcessKick(parser, this));
        // PRIVMSG
        // NOTICE
        addProcessor(new ProcessMessage(parser, prefixModeManager, this));
        // MODE
        // 324
        addProcessor(new ProcessMode(parser, prefixModeManager, userModeManager, chanModeManager,
                this));
        // 372
        // 375
        // 376
        // 422
        addProcessor(new ProcessMOTD(parser, this));
        // 353
        // 366
        addProcessor(new ProcessNames(parser, prefixModeManager, userModeManager, this));
        // 433
        addProcessor(new ProcessNickInUse(parser, this));
        // NICK
        addProcessor(new ProcessNick(parser, this));
        // PART
        addProcessor(new ProcessPart(parser, this));
        // QUIT
        addProcessor(new ProcessQuit(parser, this));
        // TOPIC
        // 332
        // 333
        addProcessor(new ProcessTopic(parser, this));
        // 344
        // 345
        // 346
        // 347
        // 348
        // 349
        // 367
        // 368
        addProcessor(new ProcessListModes(parser, this));
        // WALLOPS
        addProcessor(new ProcessWallops(parser, this));
        // 321
        // 322
        // 323
        addProcessor(new ProcessList(parser, this));
        // CAP
        addProcessor(new ProcessCap(parser, this));
        // ACCOUNT
        addProcessor(new ProcessAccount(parser, this));
    }

    /**
     * Debugging Data to the console.
     */
    private void doDebug(final String line, final Object... args) {
        parser.callDebugInfo(IRCParser.DEBUG_PROCESSOR, line, args);
    }

    /**
     * Add new Process type.
     *
     * @param processor IRCProcessor subclass for the processor.
     */
    public void addProcessor(final IRCProcessor processor) {
        // handles() returns a String array of all the tokens
        // that this processor will parse.
        addProcessor(processor.handles(), processor);
    }

    /**
     * Add a processor to tokens not-specified in the handles() reply.
     *
     * @param processor IRCProcessor subclass for the processor.
     * @param handles String Array of tokens to add this processor as a hadler for
     */
    public void addProcessor(final String[] handles, final IRCProcessor processor) {
        doDebug("Adding processor: " + processor.getName());

        for (String handle : handles) {
            if (processHash.containsKey(handle.toLowerCase())) {
                // New Processors take priority over old ones
                processHash.remove(handle.toLowerCase());
            }
            doDebug("\t Added handler for: " + handle);
            processHash.put(handle.toLowerCase(), processor);
        }
    }

    /**
     * Remove a Process type.
     *
     * @param processor IRCProcessor subclass for the processor.
     */
    public void delProcessor(final IRCProcessor processor) {
        IRCProcessor testProcessor;
        doDebug("Deleting processor: " + processor.getName());
        for (String elementName : processHash.keySet()) {
            doDebug("\t Checking handler for: " + elementName);
            testProcessor = processHash.get(elementName);
            if (testProcessor.getName().equalsIgnoreCase(processor.getName())) {
                doDebug("\t Removed handler for: " + elementName);
                processHash.remove(elementName);
            }
        }
    }

    /**
     * Get the processor used for a specified token.
     *
     * @param sParam Type of line to process ("005", "PRIVMSG" etc)
     * @return IRCProcessor for the given param.
     * @throws ProcessorNotFoundException if no processer exists for the param
     */
    public IRCProcessor getProcessor(final String sParam) throws ProcessorNotFoundException {
        if (processHash.containsKey(sParam.toLowerCase())) {
            return processHash.get(sParam.toLowerCase());
        } else {
            throw new ProcessorNotFoundException("No processors will handle " + sParam);
        }
    }

    /**
     * Process a Line.
     *
     * @param sParam Type of line to process ("005", "PRIVMSG" etc)
     * @param token IRCTokenised line to process
     * @throws ProcessorNotFoundException exception if no processors exists to handle the line
     */
    public void process(final String sParam, final String[] token) throws ProcessorNotFoundException {
        process(new Date(), sParam, token);
    }

    /**
     * Process a Line.
     *
     * @param date Date of line.
     * @param sParam Type of line to process ("005", "PRIVMSG" etc)
     * @param token IRCTokenised line to process
     * @throws ProcessorNotFoundException exception if no processors exists to handle the line
     */
    public void process(final Date date, final String sParam, final String[] token) throws ProcessorNotFoundException {
        IRCProcessor messageProcessor = null;
        try {
            messageProcessor = getProcessor(sParam);
            if (messageProcessor instanceof TimestampedIRCProcessor) {
                ((TimestampedIRCProcessor)messageProcessor).process(date, sParam, token);
            } else {
                messageProcessor.process(sParam, token);
            }
        } catch (ProcessorNotFoundException p) {
            throw p;
        } catch (Exception e) {
            final ParserError ei = new ParserError(ParserError.ERROR_ERROR,
                    "Exception in Processor. [" + messageProcessor + "]: "
                    + e.getMessage(), parser.getLastLine());
            ei.setException(e);
            parser.callErrorInfo(ei);
        } finally {
            // Try to call callNumeric. We don't want this to work if sParam is a non
            // integer param, hense the empty catch
            try {
                callNumeric(Integer.parseInt(sParam), token);
            } catch (NumberFormatException e) {
            }
        }
    }

    /**
     * Callback to all objects implementing the onNumeric Callback.
     *
     * @see com.dmdirc.parser.interfaces.callbacks.NumericListener
     * @param numeric What numeric is this for
     * @param token IRC Tokenised line
     * @return true if a method was called, false otherwise
     */
    protected boolean callNumeric(final int numeric, final String[] token) {
        return parser.getCallbackManager().getCallbackType(NumericListener.class).call(numeric, token);
    }
}