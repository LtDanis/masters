package ktu.masters.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SessionData {
    private final List<DbQueryResult> results = new ArrayList<>();
}
