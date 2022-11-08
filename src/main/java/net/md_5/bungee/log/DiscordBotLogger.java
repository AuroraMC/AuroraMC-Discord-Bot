/*
 * Copyright (c) 2021-2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.md_5.bungee.log;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jline.console.ConsoleReader;
import net.auroramc.discord.util.DiscordWebhook;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class DiscordBotLogger extends Logger
{

    private final LogDispatcher dispatcher = new LogDispatcher( this );

    // CHECKSTYLE:OFF
    @SuppressWarnings(
            {
                "CallToPrintStackTrace", "CallToThreadStartDuringObjectConstruction"
            })
    // CHECKSTYLE:ON
    @SuppressFBWarnings("SC_START_IN_CTOR")
    public DiscordBotLogger(String loggerName, String filePattern, ConsoleReader reader)
    {
        super( loggerName, null );
        setLevel( Level.ALL );

        try
        {
            FileHandler fileHandler = new FileHandler( filePattern, 1 << 24, 8, true );
            fileHandler.setLevel( Level.ALL );
            fileHandler.setFormatter( new ConciseFormatter( false ) );
            addHandler( fileHandler );

            ColouredWriter consoleHandler = new ColouredWriter( reader );
            consoleHandler.setLevel( Level.parse( System.getProperty( "net.md_5.bungee.console-log-level", "INFO" ) ) );
            consoleHandler.setFormatter( new ConciseFormatter( true ) );
            addHandler( consoleHandler );
        } catch ( IOException ex )
        {
            System.err.println( "Could not register logger!" );
            ex.printStackTrace();
        }

        dispatcher.start();
    }

    @Override
    public void log(LogRecord record)
    {
        dispatcher.queue( record );

    }

    void doLog(LogRecord record)
    {
        if (record.getLevel().intValue() >= 800) {
            DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/953678984791601153/IVUHQhpi7x6T-AAjkb6OGQ_GYFTrgnkIwKJlZLppY6Lhta3-XAobcpvEDiVNYTgtIhcl");
            webhook.setContent("**[" + record.getLevel().getName() + "]** " + record.getMessage()/* +  ((record.getThrown() != null)?"\n" +
                ExceptionUtils.getStackTrace(record.getThrown()) :"")*/ );
            try {
                webhook.execute();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        super.log( record );
    }
}
