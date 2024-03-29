/*
 * Copyright (c) 2021-2024 Ethan P-B. All Rights Reserved.
 */

package net.md_5.bungee.log;

import com.google.common.base.Charsets;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class LoggingOutputStream extends ByteArrayOutputStream
{

    private static final String separator = System.getProperty( "line.separator" );
    /*========================================================================*/
    private final Logger logger;
    private final Level level;

    @Override
    public void flush() throws IOException
    {
        String contents = toString( Charsets.UTF_8.name() );
        super.reset();
        if ( !contents.isEmpty() && !contents.equals( separator ) )
        {
            logger.logp( level, "", "", contents );
        }
    }
}
