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
package dev.nishisan.requests.common.exception;

import dev.nishisan.requests.common.request.IRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class BasicException extends Exception implements Serializable, IBasicException {
    protected IRequest<?> request;
    protected Integer statusCode = 500;
    private Map<String, Object> details = new HashMap<>();


    public BasicException(){

    }

    public BasicException(IRequest<?> request) {
        this.request = request;
    }

    public BasicException(String message){
        super(message);
    }

    public BasicException(String message, Throwable th){
        super(message,th);
    }

    public BasicException(String message, IRequest<?> request){
        super(message);
        this.request = request;
    }

    public BasicException(IRequest<?> request, Integer statusCode, Map<String, Object> details) {
        this.request = request;
        this.statusCode = statusCode;
        this.details = details;
    }

    public BasicException(String message, IRequest<?> request, Integer statusCode, Map<String, Object> details) {
        super(message);
        this.request = request;
        this.statusCode = statusCode;
        this.details = details;
    }

    public BasicException(String message, Throwable cause, IRequest<?> request, Integer statusCode, Map<String, Object> details) {
        super(message, cause);
        this.request = request;
        this.statusCode = statusCode;
        this.details = details;
    }

    public BasicException(Throwable cause, IRequest<?> request, Integer statusCode, Map<String, Object> details) {
        super(cause);
        this.request = request;
        this.statusCode = statusCode;
        this.details = details;
    }

    public BasicException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, IRequest<?> request, Integer statusCode, Map<String, Object> details) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.request = request;
        this.statusCode = statusCode;
        this.details = details;
    }

    public IRequest<?> getRequest() {
        return request;
    }

    public void setRequest(IRequest<?> request) {
        this.request = request;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }



    protected void addDetail(String key, Object value) {
        this.details.put(key, value);
    }

    public abstract <T extends IBasicException> T details(String key,Object value);

    public Map<String, Object> details(){
        return  this.details;
    }
}
