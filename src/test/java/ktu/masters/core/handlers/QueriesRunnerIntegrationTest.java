package ktu.masters.core.handlers;

import ktu.masters.dto.DatabaseType;
import ktu.masters.dto.QueryType;
import ktu.masters.dto.RunQueriesRequest;
import ktu.masters.dto.RunQueriesRequest.Query;
import ktu.masters.dto.RunQueriesRequest.QuerySet;
import ktu.masters.dto.SessionResponse;
import ktu.masters.exception.ApiException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ktu.masters.core.SessionDatabase.printByDb;
import static ktu.masters.core.SessionDatabase.printByQuery;
import static ktu.masters.core.handlers.CouchDBHandlerTest.couchQuery;
import static ktu.masters.core.handlers.QueriesHelper.initKeys;
import static ktu.masters.core.utils.HandlersHelper.AEROSPIKE_HANDLER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueriesRunnerIntegrationTest {
    private final QueriesRunner runner = new QueriesRunner();

    @Test
    void aerospikeError() {
        String sessId = "NOT_FOUND";
        RunQueriesRequest req = new RunQueriesRequest(sessId, "test", 2, List.of(new QuerySet("test1", QueryType.SEARCH, List.of(
                new Query(DatabaseType.MONGO, List.of("{ ord_qty: 501 }")),
                new Query(DatabaseType.COUCH_DB, List.of(couchQuery())),
                new Query(DatabaseType.AEROSPIKE, List.of("$.*"))
        ))));

        assertThatThrownBy(() -> runner.handle(req))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Failed to fetch Aerospike query");
    }

    @Test
    void happyPath() {
        String sessId = "ABC";
        initKeys(AEROSPIKE_HANDLER, sessId);
        RunQueriesRequest req = new RunQueriesRequest(sessId, "test", 2, List.of(new QuerySet("test1", QueryType.SEARCH, List.of(
                new Query(DatabaseType.MONGO, List.of("{ ord_qty: 501 }")),
                new Query(DatabaseType.COUCH_DB, List.of(couchQuery())),
                new Query(DatabaseType.AEROSPIKE, List.of("$.*"))
        ))));

        SessionResponse resp = runner.handle(req);

        assertThat(resp.getSessionId()).isEqualTo(sessId);
        printByDb(sessId);
        printByQuery(sessId);
    }
}
