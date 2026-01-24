/*
 * Copyright (C) 2023 Lucas Nishimura < lucas at nishisan.dev >
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dev.nishisan.requests.common.response;


import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Abstract class representing a generic response.
 *
 * @param <T> the type of the payload
 * @author Lucas Nishimura
 */
public abstract class AbsResponse<T> implements IResponse<T> {

    private String sourceRequestId;
    private String responseId;
    private String traceId;
    private T payload;
    private int statusCode;
    private Map<String, String> responseHeaders = new ConcurrentHashMap<>();
    private long size;
    private long totalPages;

    /**
     * Default constructor that generates a unique response ID.
     */
    public AbsResponse() {
        this.responseId = UUID.randomUUID().toString();
    }

    /**
     * Constructor with source request ID and payload.
     *
     * @param sourceRequestId the ID of the source request
     * @param payload         the payload of the response
     */
    public AbsResponse(String sourceRequestId, T payload) {
        this(payload);
        this.sourceRequestId = sourceRequestId;
        this.payload = payload;
    }

    /**
     * Constructor with payload.
     *
     * @param payload the payload of the response
     */
    public AbsResponse(T payload) {
        this();
        this.payload = payload;
        if (this.payload instanceof List<?> list) {
            this.size = list.size();
        } else if (this.payload instanceof Map<?,?> map) {
            this.size = map.size();
        } else if (this.payload instanceof Page<?> p) {
            this.size = p.getSize();
            this.totalPages = p.getTotalPages();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceRequestId() {
        return this.sourceRequestId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSourceRequestId(String id) {
        this.sourceRequestId = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponseId(String id) {
        this.responseId = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseId() {
        return this.responseId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getPayload() {
        return this.payload;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPayload(T payload) {
        this.payload = payload;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addResponseHeader(String name, String value) {
        this.responseHeaders.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseHeader(String name) {
        return this.responseHeaders.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getResponseHeaders() {
        return this.responseHeaders;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders == null ? null : new ConcurrentHashMap<>(responseHeaders);
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
