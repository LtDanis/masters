package ktu.masters.core.utils;

import ktu.masters.core.RouteFactory;
import ktu.masters.core.handlers.SessionInitializer;
import ktu.masters.dto.SessionRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import spark.Request;
import spark.Response;

import static ktu.masters.core.utils.JsonTransformer.GSON;

@UtilityClass
public class RouteController {
    @Getter
    @Setter
    private static RouteFactory factory = new RouteFactory(
            new SessionInitializer()
    );

    public static Object handleSessionInit(Request request, Response response) {
        SessionRequest obj = GSON.fromJson(request.body(), SessionRequest.class);
        return getFactory().getSessionInitializer().handle(obj);
    }
}
