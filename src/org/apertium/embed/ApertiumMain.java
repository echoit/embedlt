/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apertium.echo;
/*
 * Copyright (C) 2010 Stephen Tigner
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

import java.io.*;
import static org.apertium.utils.IOUtils.getStdoutWriter;
import static org.apertium.utils.IOUtils.listFilesInDir;
import static org.apertium.utils.IOUtils.addTrailingSlash;
import static org.apertium.utils.MiscUtils.getLineSeparator;


import org.apertium.pipeline.Dispatcher;
import org.apertium.pipeline.Mode;
import org.apertium.pipeline.Program;
import org.apertium.utils.StringTable;

/**
 * @author Stephen Tigner
 *
 */
class ApertiumMain {
    public static final String DATA_DIRECTORY = "/home/dx/gsoc2012/_apertium/apertium-eo-en/";
    public static final String TARGET_PATTERN = "eo-en";

    private static final boolean DEBUG = false;

    private static Reader _getFileReader() throws UnsupportedEncodingException, FileNotFoundException {
        return _getFileReader("UTF-8");
    }

    private static Reader _getFileReader(String encoding) throws UnsupportedEncodingException, FileNotFoundException {
        return new InputStreamReader(ApertiumMain.class.getResourceAsStream("eotest.input"), encoding);
    }

    private static class CommandLineParams {
        //Directory to look for modes files in

        String dataDir = null;
        //What mode to use
        String direction;
        //Display ambiguity, defaults to false
        boolean dispAmb = false;
        //Display marks '*' for unknown words, defaults to true
        boolean dispMarks = true;
        /*
         * List translation directions in the given data dir. Done as a flag
         * because we need to make sure the datadir has been parsed from the
         * command line before we try and read the files in it.
         */
        boolean listModes = false;
        Program deformatter;
        Program reformatter;
        Reader extInput = null;
        Writer extOutput = null;
    }

    private static void _setFormatter(String formatterName,
            ApertiumMain.CommandLineParams clp) {
        String deFormatProgName = "apertium-des" + formatterName;
        String reFormatProgName = "apertium-re" + formatterName;

        clp.deformatter = new Program(deFormatProgName);
        clp.reformatter = new Program(reFormatProgName);
    }

    private static boolean _parseCommandLine(ApertiumMain.CommandLineParams clp) throws Exception {
        clp.dataDir = DATA_DIRECTORY;
        clp.dataDir = addTrailingSlash(clp.dataDir);

        if (clp.deformatter == null || clp.reformatter == null) {
            //Formatters weren't set on command-line, set default of txt
            _setFormatter("txt", clp);
        }

        clp.direction = TARGET_PATTERN;
        if (clp.extInput == null) {
            clp.extInput = _getFileReader();
        }
        if (clp.extOutput == null) {
            clp.extOutput = getStdoutWriter();
        }
        return true;
    }

    private static void _dispatchPipeline(Mode mode,
            ApertiumMain.CommandLineParams clp) throws Exception {
        StringReader input = null;
        StringWriter output = new StringWriter();

        Dispatcher.dispatch(clp.deformatter, clp.extInput, output, clp.dispAmb,
                clp.dispMarks);
        if (DEBUG) {
            System.err.println("*** DEBUG: deformatter run");
        }
        int pipelineLength = mode.getPipelineLength();
        try {
            for (int i = 0; i < pipelineLength; i++) {
                Program currProg = mode.getProgramByIndex(i);
                if (DEBUG) {
                    System.err.println("*** DEBUG: output size: "
                            + output.toString().length());
                    System.err.println("*** DEBUG: output -- "
                            + output.toString());
                    System.err.println("*** DEBUG: dispatching "
                            + currProg.getCommandName());
                }
                switch (currProg.getProgram()) {
                    case UNKNOWN:
                        /*
                         * Okay, this probably needs some explanation. When
                         * executing an external program, as the UNKNOWN program
                         * type does, we can't use Readers and Writers, we have
                         * to use byte streams instead. Using a
                         * StringBufferInputStream is not recommended and is, in
                         * fact, deprecated because of encoding issues. To
                         * hopefully avoid those issues, byte array streams are
                         * used instead. The input is converted to a byte array
                         * using String.getBytes() with "UTF-8" as the charset
                         * name.
                         */
                        byte[] inputBytes = output.toString().getBytes("UTF-8");
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Dispatcher.dispatchUnknown(currProg, inputBytes, outputStream);
                        /*
                         * When finished with the external program, we need to
                         * convert it back to a UTF-8 string and put the result
                         * in the output writer for the next iteration of the
                         * loop.
                         */
                        output = new StringWriter();
                        output.write(outputStream.toString("UTF-8"));
                        break;
                    default:
                        input = new StringReader(output.toString());
                        output = new StringWriter();
                        Dispatcher.dispatch(currProg, input, output,
                                clp.dispAmb, clp.dispMarks);
                        break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            String errorString = "Apertium (pipeline) -- "
                    + StringTable.UNSUPPORTED_ENCODING;
            errorString += getLineSeparator() + e.getLocalizedMessage();
            throw new Exception(errorString, e);
        }

        input = new StringReader(output.toString());
//        input = new StringReader("Saluton, mi estas Jacob");
        Dispatcher.dispatch(clp.reformatter, input, clp.extOutput, clp.dispAmb,
                clp.dispMarks);
    }

    private static void _listModes(ApertiumMain.CommandLineParams clp) {
        String[] modeList = listFilesInDir(clp.dataDir + "modes/", "mode");

        if (modeList == null) {
            System.out.println("No translation directions in the specified directory.");
        } else {
            for (String mode : modeList) {
                System.out.println(mode.replaceAll("\\.mode", ""));
            }
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static int run()
            throws Exception {
        //Ensure we are using UTF-8 encoding by default
        System.setProperty("file.encoding", "UTF-8");

        ApertiumMain.CommandLineParams clp = new ApertiumMain.CommandLineParams();

        if (!_parseCommandLine(clp)) {
            return 1;
        }

        if (clp.listModes) {
            _listModes(clp);
        }

        Mode mode = null;

        try {
            mode = new Mode(clp.dataDir + "modes/" + clp.direction + ".mode");
        } catch (IOException e) {
            String errorString = "Apertium (mode parsing) -- "
                    + StringTable.IO_EXCEPTION;
            errorString += getLineSeparator() + e.getLocalizedMessage();
            throw new IOException(errorString, e);
        }

        _dispatchPipeline(mode, clp);

        try {
            clp.extOutput.flush(); //Just to make sure it gets flushed.
        } catch (IOException e) {
            String errorString = "Apertium (flushing output) -- "
                    + StringTable.IO_EXCEPTION;
            errorString += getLineSeparator() + e.getLocalizedMessage();
            throw new IOException(errorString, e);
        }

        return 0;
    }
}
