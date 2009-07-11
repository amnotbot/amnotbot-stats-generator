package org.bitbucket.gresco.amnotbot_stats_generator;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/**
 *
 * @author gpoppino
 */
@RunWith(value = Parameterized.class)
public class LogFileScannerTest
{
    final static String baseDir = "build/test/classes/";
    final static String dbFilename1 = "irclogs/oftc/#amnotbot.db";
    final static String dbFilename2 = "irclogs/freenode/#amnotbot.db";

    private static String backend;
    private static DbFileFilter filter;

    public LogFileScannerTest(String _backend)
    {
        backend = _backend;
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
        filter = new DbFileFilter();
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
        File dir = new File(baseDir + "irclogs");
        String[] dlist = dir.list();
        for (String d : dlist) {
            File subdir = new File(baseDir + "irclogs" + "/" + d);
            String[] flist = subdir.list(filter);
            for (String fn : flist) {
                File f = new File(baseDir + "irclogs" + "/" + d + "/" + fn);
                f.delete();
            }
        }
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Parameters
    public static Collection testParameters()
    {
        Object[][] data = 
                new Object[][] { {"sqlite"}, {"hsqldb"} };

        return Arrays.asList(data);
    }

    @Test
    public void testScanLogFiles() throws SQLException
    {
        System.out.println("scanLogFiles");
        Properties p = new Properties();

        p.setProperty("backend", backend);
        p.setProperty("ignorefile", baseDir + "ignore.words");
        p.setProperty("logdirectory", baseDir + "irclogs");
        p.setProperty("botnick", "amnotbot");
        p.setProperty("minwordlen", String.valueOf(3));
        p.setProperty("cmdtrigger", ".");
        
        LogFileScanner instance = new LogFileScanner(p);
        instance.scanLogFiles();
        instance = null;

        File f;
        f = new File(baseDir + dbFilename1);
        assertTrue( f.exists() );
        f = null;

        f = new File(baseDir + dbFilename2);
        assertTrue( f.exists() );
        f = null;
    }

    @Test
    public void testQueryWordsFormatOne()
            throws SQLException, ClassNotFoundException
    {
        System.out.println("testQueryWordsFormatOne");
        String query;
        query = "SELECT word, SUM(repetitions) AS rep FROM words " +
                 "GROUP BY word ORDER BY rep DESC LIMIT 5";

        Connection conn = null;
        conn = StatsFactory.instance().getConnection(backend,
                baseDir + dbFilename1);

        ResultSet rs = null;
        Statement smt = null;
        smt = conn.createStatement();        
        try {
            rs = smt.executeQuery(query);
            rs.next();
            assertEquals("developers", rs.getString(1));
            assertEquals(8, rs.getInt(2));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e);
        }
        rs.close();
        smt.close();
        StatsFactory.instance().closeConnection(backend, conn);
    }

    @Test
    public void testQueryWordsFormatTwo()
            throws SQLException, ClassNotFoundException
    {
        System.out.println("testQueryWordsFormatTwo");
        String query;
        query = "SELECT word, SUM(repetitions) AS rep FROM words " +
                 "GROUP BY word ORDER BY rep DESC LIMIT 5";

        Connection conn = null;
        conn = StatsFactory.instance().getConnection(backend,
                baseDir + dbFilename2);
        
        ResultSet rs = null;
        Statement smt = null;
        smt = conn.createStatement();
        try {
            rs = smt.executeQuery(query);
            rs.next();
            assertEquals("developers", rs.getString(1));
            assertEquals(8, rs.getInt(2));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e);
        }
        smt.close();
        rs.close();
        StatsFactory.instance().closeConnection(backend, conn);
    }

    @Test
    public void testQueryLinesFormatOne()
            throws SQLException, ClassNotFoundException
    {
        System.out.println("testQueryLinesFormatOne");
        String query;
        query = "SELECT nick, SUM(repetitions) AS rep FROM lines " +
                 "GROUP BY nick ORDER BY rep DESC LIMIT 5";

        Connection conn = null;
        conn = StatsFactory.instance().getConnection(backend,
                baseDir + dbFilename1);

        ResultSet rs = null;
        Statement smt = null;
        smt = conn.createStatement();
        try {
            rs = smt.executeQuery(query);
            rs.next();
            assertEquals("gresco", rs.getString(1));
            assertEquals(4, rs.getInt(2));
            rs.next();
            assertEquals("knix", rs.getString(1));
            assertEquals(3, rs.getInt(2));
            assertFalse(rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e);
        }
        smt.close();
        rs.close();
        StatsFactory.instance().closeConnection(backend, conn);
    }

    @Test
    public void testQueryLinesFormatTwo()
            throws SQLException, ClassNotFoundException
    {
        System.out.println("testQueryLinesFormatTwo");
        String query;
        query = "SELECT nick, SUM(repetitions) AS rep FROM lines " +
                 "GROUP BY nick ORDER BY rep DESC LIMIT 5";

        Connection conn = null;
        conn = StatsFactory.instance().getConnection(backend,
                baseDir + dbFilename2);

        ResultSet rs = null;
        Statement smt = null;
        smt = conn.createStatement();
        smt.setQueryTimeout(30);
        try {
            rs = smt.executeQuery(query);
            rs.next();
            assertEquals("gresco", rs.getString(1));
            assertEquals(4, rs.getInt(2));
            rs.next();
            assertEquals("knix", rs.getString(1));
            assertEquals(3, rs.getInt(2));
            assertFalse(rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e);
        }
        smt.close();
        rs.close();
        StatsFactory.instance().closeConnection(backend, conn);
    }
}