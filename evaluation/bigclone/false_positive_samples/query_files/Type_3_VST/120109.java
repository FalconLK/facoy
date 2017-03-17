package edu.clemson.cs.nestbed.server.adaptation.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.clemson.cs.nestbed.common.model.ProgramSymbol;
import edu.clemson.cs.nestbed.server.adaptation.AdaptationException;
import edu.clemson.cs.nestbed.server.adaptation.ProgramSymbolAdapter;

public class ProgramSymbolSqlAdapter extends SqlAdapter implements ProgramSymbolAdapter {

    private static final Log log = LogFactory.getLog(ProgramSymbolSqlAdapter.class);

    private enum Index {

        ID, PROGID, MODULE, SYMBOL, ADDRESS, SIZE, TIMESTAMP;

        public int index() {
            return ordinal() + 1;
        }
    }

    public Map<Integer, ProgramSymbol> readProgramSymbols() throws AdaptationException {
        Map<Integer, ProgramSymbol> programSymbols = new HashMap<Integer, ProgramSymbol>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM ProgramSymbols";
            connection = DriverManager.getConnection(CONN_STR);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                ProgramSymbol programSymbol = getProgramSymbol(resultSet);
                programSymbols.put(programSymbol.getID(), programSymbol);
            }
        } catch (SQLException ex) {
            String msg = "SQLException in readProgramSymbols";
            log.error(msg, ex);
            throw new AdaptationException(msg, ex);
        } finally {
            try {
                resultSet.close();
            } catch (Exception ex) {
            }
            try {
                statement.close();
            } catch (Exception ex) {
            }
            try {
                connection.close();
            } catch (Exception ex) {
            }
        }
        return programSymbols;
    }

    public ProgramSymbol createNewProgramSymbol(int programID, String module, String symbol, int address, int size) throws AdaptationException {
        ProgramSymbol programSymbol = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String query = "INSERT INTO ProgramSymbols " + "(programID, module, symbol, address, size)" + " VALUES (" + programID + ", '" + module + "',  '" + symbol + "', " + address + ", " + size + ")";
            connection = DriverManager.getConnection(CONN_STR);
            statement = connection.createStatement();
            statement.executeUpdate(query);
            query = "SELECT * FROM ProgramSymbols WHERE  " + "programID =  " + programID + "  AND " + "module    = '" + module + "' AND " + "symbol    = '" + symbol + "'";
            resultSet = statement.executeQuery(query);
            if (!resultSet.next()) {
                connection.rollback();
                String msg = "Attempt to create program symbol failed.";
                log.error(msg);
                throw new AdaptationException(msg);
            }
            programSymbol = getProgramSymbol(resultSet);
            connection.commit();
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (Exception e) {
            }
            String msg = "SQLException in createNewProgramSymbol";
            log.error(msg, ex);
            throw new AdaptationException(msg, ex);
        } finally {
            try {
                resultSet.close();
            } catch (Exception ex) {
            }
            try {
                statement.close();
            } catch (Exception ex) {
            }
            try {
                connection.close();
            } catch (Exception ex) {
            }
        }
        return programSymbol;
    }

    public ProgramSymbol deleteProgramSymbol(int id) throws AdaptationException {
        ProgramSymbol programSymbol = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM ProgramSymbols " + "WHERE id = " + id;
            connection = DriverManager.getConnection(CONN_STR);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (!resultSet.next()) {
                connection.rollback();
                String msg = "Attempt to delete program symbol failed.";
                log.error(msg);
                throw new AdaptationException(msg);
            }
            programSymbol = getProgramSymbol(resultSet);
            query = "DELETE FROM ProgramSymbols " + "WHERE id = " + id;
            statement.executeUpdate(query);
            connection.commit();
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (Exception e) {
            }
            String msg = "SQLException in deleteProgramSymbol";
            log.error(msg, ex);
            throw new AdaptationException(msg, ex);
        } finally {
            try {
                resultSet.close();
            } catch (Exception ex) {
            }
            try {
                statement.close();
            } catch (Exception ex) {
            }
            try {
                connection.close();
            } catch (Exception ex) {
            }
        }
        return programSymbol;
    }

    private final ProgramSymbol getProgramSymbol(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(Index.ID.index());
        int programID = resultSet.getInt(Index.PROGID.index());
        String module = resultSet.getString(Index.MODULE.index());
        String symbol = resultSet.getString(Index.SYMBOL.index());
        int address = resultSet.getInt(Index.ADDRESS.index());
        short size = resultSet.getShort(Index.SIZE.index());
        Date timestamp = resultSet.getDate(Index.TIMESTAMP.index());
        return new ProgramSymbol(id, programID, module, symbol, address, size, timestamp);
    }
}
