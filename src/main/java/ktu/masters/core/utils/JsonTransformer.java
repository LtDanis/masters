package ktu.masters.core.utils;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
    public static final Gson GSON = new Gson();

    @Override
    public String render(Object model) {
        return GSON.toJson(model);
    }
}
