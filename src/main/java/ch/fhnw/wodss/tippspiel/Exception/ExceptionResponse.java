package ch.fhnw.wodss.tippspiel.Exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExceptionResponse {
    private String errorCode;
    private String errorMessage;
}
