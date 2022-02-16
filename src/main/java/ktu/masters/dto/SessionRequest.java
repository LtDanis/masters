package ktu.masters.dto;

import lombok.Value;

import java.util.List;

@Value
public class SessionRequest {
    List<Database> types;
}
