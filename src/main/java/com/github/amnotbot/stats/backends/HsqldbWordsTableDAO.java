package com.github.amnotbot.stats.backends;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import com.github.amnotbot.stats.StatsRecordDAO;
import com.github.amnotbot.stats.StatsTableDAO;

/**
 *
 * @author gpoppino
 */
public class HsqldbWordsTableDAO implements StatsTableDAO
{

    @Override
    public void create(Connection conn) throws SQLException
    {
        Statement statement;
        statement = conn.createStatement();

        statement.executeUpdate("CREATE CACHED TABLE words " +
                "(d DATE, nick VARCHAR(50), word VARCHAR(50), repetitions REAL)");
        statement.executeUpdate("CREATE UNIQUE INDEX wdnw ON words" +
                " (d, nick, word)");
        statement.executeUpdate("CREATE INDEX wn ON words (nick)");

        statement.close();
    }

    @Override
    public void drop(Connection conn) throws SQLException
    {
        Statement statement;
        statement = conn.createStatement();

        statement.executeUpdate("DROP TABLE IF EXISTS words");

        statement.close();
    }

    @Override
    public void update(Connection conn, StatsRecordDAO r)
            throws SQLException
    {
        if (r.getWord() == null) return;

        PreparedStatement updateWords = conn.prepareStatement(
                "UPDATE words SET d = ?, nick = ?, word = ?, " +
                "repetitions = (repetitions+1) WHERE d = ? AND nick = ? " +
                "AND word = ?");

        updateWords.setDate(1, new java.sql.Date(r.getDate().getTime()));
        updateWords.setString(2, r.getNick());
        updateWords.setString(3, r.getWord());
        updateWords.setDate(4, new java.sql.Date(r.getDate().getTime()));
        updateWords.setString(5, r.getNick());
        updateWords.setString(6, r.getWord());

        int ret = updateWords.executeUpdate();
        updateWords.close();
        if (ret == 0) {
            PreparedStatement insertWords = conn.prepareStatement(
                    "INSERT INTO words values(?, ?, ?, 1)");

            insertWords.setDate(1, new java.sql.Date(r.getDate().getTime()));
            insertWords.setString(2, r.getNick());
            insertWords.setString(3, r.getWord());

            insertWords.executeUpdate();
            insertWords.close();
        }
    }
}
