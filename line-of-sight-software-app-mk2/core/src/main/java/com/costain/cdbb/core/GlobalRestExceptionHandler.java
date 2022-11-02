/*
 * Copyright © 2022 Costain Ltd
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.costain.cdbb.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

    private class ErrorMessage {
        @JsonProperty
        private String item;
        @JsonProperty
        private String error;
        @JsonProperty
        private boolean forUi;

        public ErrorMessage(String item, String error, boolean forUi) {
            this.item = item;
            this.error = error;
            this.forUi = forUi;
        }

        public String toString() {
            return "Error {"
                + "item="
                + item
                + ", error='" + error + '\''
                + ", forUi=" + forUi
                + '}';
        }
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected void handleAuthenticationException(AuthenticationException e) {
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected void handleNotFoundException(EmptyResultDataAccessException e) {
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected List<ErrorMessage> handleWebBindException(Errors e) {
        return e.getAllErrors().stream()
            .map(error -> new ErrorMessage(error.getObjectName(), error.getDefaultMessage(), false)).toList();
    }

    @ExceptionHandler(CdbbValidationError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleNameError(Throwable ex) {
        return new ErrorMessage("Error", ex.getMessage(), true);
    }

    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleJpaNotFound(Throwable ex) {
        String message = ex.getMessage();
        return new ErrorMessage("Error", "Unable to find object with id"
            + message.substring(message.lastIndexOf(' ')), false);
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Throwable.class})
    protected ErrorMessage handleOthers(Throwable ex) {
        logger.info("Unhandled exception", ex);
        return new ErrorMessage("Error", "An unexpected error has occurred, please try again later", true);
    }
}
