package ktu.masters.core;

import ktu.masters.core.handlers.Handler;
import ktu.masters.dto.SessionRequest;
import ktu.masters.dto.SessionResponse;
import lombok.Value;

@Value
public class RouteFactory {
    Handler<SessionRequest, SessionResponse> sessionInitializer;
}
