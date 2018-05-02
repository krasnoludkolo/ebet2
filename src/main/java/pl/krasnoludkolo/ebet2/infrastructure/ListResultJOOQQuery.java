package pl.krasnoludkolo.ebet2.infrastructure;

import io.vavr.collection.List;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ListResultJOOQQuery<T> {


    private static final Logger LOGGER = Logger.getLogger(ListResultJOOQQuery.class.getName());

    private DatabaseConnectionInfo connectionInfo = new DatabaseConnectionInfo();

    public List<T> execute() {
        try (Connection connection = connectionInfo.createConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            Result<Record> result = query(create).fetch();
            return mapResult(result);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return List.empty();
    }


    protected abstract SelectConditionStep<Record> query(DSLContext create);

    protected abstract List<T> mapResult(Result<Record> result);

}
