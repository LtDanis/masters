package ktu.masters.core.handlers;

import ktu.masters.dto.DbQueryResult;
import ktu.masters.dto.RunQueriesRequest;
import ktu.masters.dto.SessionResponse;

import static ktu.masters.core.SessionDatabase.saveTimeTaken;
import static ktu.masters.core.utils.HandlersHelper.findByType;

public class QueriesRunner implements Handler<RunQueriesRequest, SessionResponse> {
    @Override
    public SessionResponse handle(RunQueriesRequest sessionRequest) {
        String sessionId = sessionRequest.getSessionId();
        sessionRequest.getQuerySet().forEach(querySet -> querySet.getQueries().forEach(query -> {
            DbHandler handler = findByType(query.getDb());
            long timeTaken = handler.runQuery(sessionRequest.getColName(), query.getQuery());
            DbQueryResult dbQueryResult = new DbQueryResult(querySet.getName(), querySet.getType(), query.getDb(), timeTaken);
            saveTimeTaken(sessionId, dbQueryResult);
        }));
        return new SessionResponse(sessionId);
    }
}
