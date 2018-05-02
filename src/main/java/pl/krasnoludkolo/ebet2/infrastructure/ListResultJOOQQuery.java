package pl.krasnoludkolo.ebet2.infrastructure;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ListResultJOOQQuery<T> {


    private static final Logger LOGGER = Logger.getLogger(ListResultJOOQQuery.class.getName());
    private DatabaseConnectionInfo connectionInfo = new DatabaseConnectionInfo();

    public List<T> execute() {
        try (Connection connection = connectionInfo.createConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            Result<Record> result = query(create);
            return mapResult(result);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return Collections.emptyList();
    }


    protected abstract Result<Record> query(DSLContext create);

    protected abstract List<T> mapResult(Result<Record> result);

}
