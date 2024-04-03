package backendapp.myPizza.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.access.AccessDeniedException;



    @ControllerAdvice
    public class AppExceptionHandler extends ResponseEntityExceptionHandler {
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<HttpErrorRes> badRequestHandler(BadRequestException e) {
            return new ResponseEntity<>(new HttpErrorRes(HttpStatus.BAD_REQUEST,
                    "Bad request", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<HttpErrorRes> notFoundHandler(NotFoundException e) {
            return new ResponseEntity<>(new HttpErrorRes(HttpStatus.NOT_FOUND,
                    "Not found", e.getMessage()), HttpStatus.NOT_FOUND);
        }


        @ExceptionHandler(InternalServerErrorException.class)
        public ResponseEntity<HttpErrorRes> internalServerErrorHandler(InternalServerErrorException e) {
            return genericExceptionHandler(e);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<HttpErrorRes> genericExceptionHandler(Exception e) {
            return new ResponseEntity<>(new HttpErrorRes(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<HttpErrorRes> accessDeniedHandler(AccessDeniedException e) {
            return new ResponseEntity<>(new HttpErrorRes(HttpStatus.UNAUTHORIZED,
                    "Unauthorized", "You don't have permissions to access this resource"), HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<HttpErrorRes> accessDeniedHandler(UnauthorizedException e) {
            return new ResponseEntity<>(new HttpErrorRes(HttpStatus.UNAUTHORIZED,
                    "Unauthorized", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }


    }

