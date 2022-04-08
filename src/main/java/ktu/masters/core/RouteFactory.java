package ktu.masters.core;

import ktu.masters.core.handlers.Handler;
import ktu.masters.dto.RunQueriesRequest;
import ktu.masters.dto.SessionRequest;
import ktu.masters.dto.SessionResponse;
import lombok.Value;

@Value
public class RouteFactory {
    Handler<SessionRequest, SessionResponse> sessionInitializer;
    Handler<RunQueriesRequest, SessionResponse> queriesRunner;
}
