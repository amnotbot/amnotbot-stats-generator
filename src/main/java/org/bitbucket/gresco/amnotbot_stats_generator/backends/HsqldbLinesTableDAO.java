package org.bitbucket.gresco.amnotbot_stats_generator.backends;

import org.bitbucket.gresco.amnotbot_stats_generator.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gpoppino
 */
public class HsqldbLinesTableDAO implements StatsTableDAO
{

    @Override
    public void create(Connection conn) throws SQLException
    {
        Statement statement;
        statement = conn.createStatement();

        statement.execute("SET WRITE_DELAY 10 MILLIS");
        
        statement.executeUpdate("CREATE TABLE lines " +
                "(d DATE, nick VARCHAR, repetitions REAL)");

        statement.executeUpdate("CREATE UNIQUE INDEX ldn ON lines (d, nick)");
        statement.executeUpdate("CREATE INDEX ln ON lines (nick)");

        statement.close();
    }

    @Override
    public void drop(Connection conn) throws SQLException
    {
        Statement statement;
        statement = conn.createStatement();

        statement.executeUpdate("DROP TABLE IF EXISTS lines");

        statement.close();
    }

    @Override
    public void update(Connection conn, StatsRecordDAO r)
            throws SQLException
    {
        if (r.getWord() != null) return;

        PreparedStatement selectLines = conn.prepareStatement(
                "SELECT * FROM lines WHERE d = ? AND nick = ?");

        selectLines.setDate(1, new java.sql.Date(r.getDate().getTime()));
        selectLines.setString(2, r.getNick());

        ResultSet rs = selectLines.executeQuery();
        if (rs.next()) {
            PreparedStatement updateLines = conn.prepareStatement(
                    "UPDATE lines SET d = ?, nick = ?, " +
                    "repetitions = (repetitions+1) WHERE d = ? AND nick = ?");

            updateLines.setDate(1, new java.sql.Date(r.getDate().getTime()));
            updateLines.setString(2, r.getNick());
            updateLines.setDate(3, new java.sql.Date(r.getDate().getTime()));
            updateLines.setString(4, r.getNick());

            updateLines.executeUpdate();
            updateLines.close();
        } else {
            PreparedStatement insertLines = conn.prepareStatement(
                    "INSERT INTO lines values(?, ?, 1)");

            insertLines.setDate(1, new java.sql.Date(r.getDate().getTime()));
            insertLines.setString(2, r.getNick());

            insertLines.executeUpdate();
            insertLines.close();
        }

        rs.close();
        selectLines.close();
    }
}
