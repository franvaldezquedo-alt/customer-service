package com.nttdata.customer_service.domain.error;

import com.mongodb.MongoException;
import com.mongodb.MongoTimeoutException;
import com.nttdata.customer_service.infrastructure.utils.Constantes;
import reactor.core.Exceptions;

public class CustomerErrorFactory {

    public static RuntimeException createException(Throwable error) {

        // Timeout al intentar conectar o esperar respuesta
        if (error instanceof MongoTimeoutException) {
            return new GatewayTimeOutExceptions(Constantes.DATABASE_TIMEOUT);
        }

        // Cualquier otro error de MongoDB (incluye socket, comando, etc.)
        if (error instanceof MongoException) {
            return new ServiceUnavailableExceptions(Constantes.DATABASE_UNAVAILABLE);
        }

        // Error de reintentos agotados (propio de Reactor)
        if (Exceptions.isRetryExhausted(error)) {
            return new GatewayTimeOutExceptions(Constantes.DATABASE_TIMEOUT);
        }

        // Error de negocio propio
        if (error instanceof CustomerNotFoundExeptions) {
            return new CustomerNotFoundExeptions(error.getMessage());
        }

        // Cualquier otro error inesperado
        return new CustomerException(Constantes.DATABASE_USER_EXCEPTIONS);
    }
}
