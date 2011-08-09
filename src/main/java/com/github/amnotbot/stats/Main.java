package com.github.amnotbot.stats;

import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author gpoppino
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        
        Options opts = new Options();
        opts.addOption("b", "backend", true, "Backend to use. Eg. hsqldb");
        opts.addOption("i", "ignorefile", true,
                "File that contains words that should be ignored.");
        opts.addOption("d", "logdirectory", true,
                "Directory that contains the log files.");
        opts.addOption("n", "botnick", true,
                "Nickname of the bot.");
        opts.addOption("l", "minwordlen", true, "Minimum word lenght");
        opts.addOption("t", "cmdtrigger", true,
                "Command trigger used by the bot.");

        CommandLine cmd = null;
        CommandLineParser parser = new PosixParser();
        try {
            cmd = parser.parse(opts, args);
        } catch (ParseException e) {
            System.err.println(e);
            System.exit(1);
        }
       
        if (!cmd.hasOption("b") || !cmd.hasOption("i") ||
                !cmd.hasOption("d") || !cmd.hasOption("n")) {
            HelpFormatter help = new HelpFormatter();
            
            help.printHelp("genstats", opts);
            System.exit(0);
        }

        int minWordLength = 3;
        if (cmd.hasOption("l")) {
            minWordLength = Integer.parseInt( cmd.getOptionValue("l") );
        }

        String cmdTrigger = new String("!");
        if (cmd.hasOption("t")) {
            cmdTrigger = cmd.getOptionValue("t");
        }

        Properties p = new Properties();

        p.setProperty("backend", cmd.getOptionValue("b"));
        p.setProperty("ignorefile",  cmd.getOptionValue("i"));
        p.setProperty("logdirectory", cmd.getOptionValue("d"));
        p.setProperty("botnick", cmd.getOptionValue("n").trim());
        p.setProperty("cmdtrigger", cmdTrigger.trim());
        p.setProperty("minwordlen", String.valueOf(minWordLength));

        LogFileScanner scanner;
        scanner = new LogFileScanner(p);
        
        scanner.scanLogFiles();
    }
}
