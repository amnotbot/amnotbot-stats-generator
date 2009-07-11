package org.bitbucket.gresco.amnotbot_stats_generator;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author gpoppino
 */
public class DbFileFilter implements FilenameFilter
{

    @Override
    public boolean accept(File dir, String name)
    {
        return (name.startsWith("#") && name.contains(".db"));
    }

}
