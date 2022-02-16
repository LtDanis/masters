package ktu.masters.core.handlers;

public interface Handler<REQ, RES> {
    RES handle(REQ req);
}
