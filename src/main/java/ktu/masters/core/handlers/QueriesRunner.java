package ktu.masters.core.handlers;

import ktu.masters.core.SessionDatabase;
import ktu.masters.dto.DbQueryResult;
import ktu.masters.dto.RunQueriesRequest;
import ktu.masters.dto.SessionResponse;

import java.util.List;

import static ktu.masters.core.SessionDatabase.*;
import static ktu.masters.core.utils.HandlersHelper.findByType;

public class QueriesRunner implements Handler<RunQueriesRequest, SessionResponse> {
    @Override
    public SessionResponse handle(RunQueriesRequest sessionRequest) {
        String sessionId = sessionRequest.getSessionId();
        SessionDatabase.reset(sessionId);
        sessionRequest.getQuerySet().forEach(querySet -> querySet.getQueries().forEach(query -> {
            DbHandler handler = findByType(query.getDb());
            List<Long> timesTaken = handler.runQuery(sessionRequest.getColName(), query.getQuery(), sessionRequest.getNumberOfRuns(), sessionId);
            long avg = timesTaken.stream()
                    .reduce(0L, Long::sum) / timesTaken.size();
            DbQueryResult dbQueryResult = new DbQueryResult(querySet.getName(), querySet.getType(), query.getDb(), reformatTimes(timesTaken), avg);
            saveTimeTaken(sessionId, dbQueryResult);
        }));
        printAverageTimeTaken(sessionId);
        return new SessionResponse(sessionId);
    }
}
