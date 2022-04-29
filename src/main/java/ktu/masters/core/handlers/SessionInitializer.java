package ktu.masters.core.handlers;

import ktu.masters.dto.DatabaseType;
import ktu.masters.dto.SessionRequest;
import ktu.masters.dto.SessionResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static ktu.masters.core.utils.HandlersHelper.findByType;

@RequiredArgsConstructor
public class SessionInitializer implements Handler<SessionRequest, SessionResponse> {
    public SessionResponse handle(SessionRequest request) {
        String sessionId = formatSessionId(request);
        SessionResponse sessionResponse = new SessionResponse(sessionId);
        if (request.isReloadDatabase())
            reloadDatabase(request.getTypes(), request.getFileName(), request.getDatabaseName(), sessionId);
        return sessionResponse;
    }

    private String formatSessionId(SessionRequest request) {
        return String.format("%s-%s", request.getUserId(), LocalDateTime.now());
    }

    private void reloadDatabase(List<DatabaseType> types, String fileName, String databaseName, String sessionId) {
        for (DatabaseType type : types)
            findByType(type).reset(databaseName, fileName, sessionId);
    }
}
