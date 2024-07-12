package com.h2tg.ysogate.payloads;

/**
 * @author mbechler
 */
public interface ReleaseableObjectPayload<T> extends ObjectPayload<T> {

    void release(T obj) throws Exception;
}