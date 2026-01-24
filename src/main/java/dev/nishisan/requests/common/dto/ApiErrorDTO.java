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
package dev.nishisan.requests.common.dto;

import org.springframework.http.ProblemDetail;
import java.net.URI;

/**
 *
 * @author Lucas Nishimura < lucas at nishisan.dev >
 * @created 11.07.2023
 */
public class ApiErrorDTO extends ProblemDetail {

    public ApiErrorDTO() {
        super(500); // Default status
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return getDetail();
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        setDetail(msg);
    }

    /**
     * @return the statusCode
     */
    public Integer getStatusCode() {
        return getStatus();
    }

    /**
     * @param statusCode the statusCode to set
     */
    public void setStatusCode(Integer statusCode) {
        setStatus(statusCode != null ? statusCode : 500);
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return (String) (getProperties() != null ? getProperties().get("className") : null);
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        setProperty("className", className);
    }

    /**
     * @param details the details to set
     */
    public void setDetails(Object details) {
        setProperty("details", details);
    }

    /**
     * @return the details
     */
    public Object getDetails() {
        return getProperties() != null ? getProperties().get("details") : null;
    }

    /**
     * @return the request
     */
    public Object getRequest() {
        return getProperties() != null ? getProperties().get("request") : null;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(Object request) {
        setProperty("request", request);
    }

}