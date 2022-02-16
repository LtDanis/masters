package ktu.masters.core;

import ktu.masters.dto.SessionRequest;
import ktu.masters.dto.SessionResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Getter
public class Session {
    private static SessionRequest request;
    private static SessionResponse response;

    public static void reset() {
        request = null;
        response = null;
    }

    public static void setInitialData(SessionRequest req, SessionResponse res) {
        request = req;
        response = res;
    }
}
