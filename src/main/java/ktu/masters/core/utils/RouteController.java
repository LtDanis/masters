package ktu.masters.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import ktu.masters.core.RouteFactory;
import ktu.masters.core.SessionDatabase;
import ktu.masters.core.handlers.DbHandler;
import ktu.masters.core.handlers.QueriesRunner;
import ktu.masters.core.handlers.SessionInitializer;
import ktu.masters.dto.*;
import ktu.masters.exception.ApiException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import spark.Request;
import spark.Response;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.List;

import static ktu.masters.core.SessionDatabase.getRequest;
import static ktu.masters.core.SessionDatabase.saveRequest;
import static ktu.masters.core.utils.HandlersHelper.findByType;
import static ktu.masters.core.utils.JsonTransformer.GSON;

@UtilityClass
@SuppressWarnings("unused")
public class RouteController {
    @Getter
    @Setter
    private static RouteFactory factory = new RouteFactory(
            new SessionInitializer(),
            new QueriesRunner()
    );

    public static Object handleSessionInit(Request request, Response response) {
        SessionRequest obj = GSON.fromJson(request.body(), SessionRequest.class);
        return getFactory().getSessionInitializer().handle(obj);
    }

    public static Object prepareQueries(Request request, Response response) {
        RunQueriesRequest obj = GSON.fromJson(request.body(), RunQueriesRequest.class);
        saveRequest(obj);
        return new SessionResponse(obj.getSessionId());
    }

    public static Object runQueries(Request request, Response response) {
        String sessionId = request.params(":sessionId");
        return getFactory().getQueriesRunner().handle(getRequest(sessionId));
    }

    public static Object runQuery(Request request, Response response) {
        String colName = request.params(":colName");
        String dbType = request.params(":dbType");
        String sessionId = request.queryParams(":sessionId");
        DbHandler handler = findByType(DatabaseType.parse(dbType));
        return handler.runQuery(colName, List.of(request.body()), 1, sessionId).get(0);
    }

    public static Object readResults(Request request, Response response) {
        return SessionDatabase.getResultsList(request.params(":sessionId"));
    }

    public static Object generateCsv(Request request, Response response) {
        String sessionId = request.params(":sessionId");
        response.header("Content-Disposition", String.format("attachment;filename=%s.csv", sessionId));
        response.raw().setContentType("application/octet-stream");
        return toCsv(SessionDatabase.getResultsList(sessionId), response);
    }

    private static Object toCsv(List<DbQueryResult> resultsList, Response response) {
        try {
            ServletOutputStream out = response.raw().getOutputStream();
            new CsvMapper()
                    .writerFor(DbQueryResult[].class)
                    .with(new CsvMapper().schemaFor(DbQueryResult.class))
                    .writeValue(out, resultsList.toArray(DbQueryResult[]::new));
            out.flush();
            out.close();
            return out;
        } catch (IOException e) {
            throw new ApiException(500, e, "Failed to write csv file");
        }
    }

    private static CsvSchema createSchemaWithHeaders(JsonNode jsonTree) {
        Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
        return csvSchemaBuilder.build();
    }
}
