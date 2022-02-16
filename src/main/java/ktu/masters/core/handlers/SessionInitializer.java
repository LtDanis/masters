package ktu.masters.core.handlers;

import ktu.masters.dto.SessionRequest;
import ktu.masters.dto.SessionResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionInitializer implements Handler<SessionRequest, SessionResponse> {
    public SessionResponse handle(SessionRequest request) {
        return null; //TODO
    }
}
