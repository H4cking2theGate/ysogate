package com.h2tg.ysogate.payloads;

/**
 * @author mbechler
 */
public interface ReleaseableObjectPayload<T> extends CommandObjectPayload<T>
{

    void release(T obj) throws Exception;
}