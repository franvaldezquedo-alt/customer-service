package com.nttdata.customer_service.infrastructure.utils;

public class Constantes {
    // 🔹 Errores de base de datos
    public static final String DATABASE_UNAVAILABLE = "Servicio de base de datos no disponible. Verifique la conexión con MongoDB.";
    public static final String DATABASE_TIMEOUT = "Tiempo de espera agotado al intentar conectar o ejecutar operación en MongoDB.";
    public static final String DATABASE_USER_EXCEPTIONS = "Error desconocido durante la operación con la base de datos.";

    // 🔹 Errores de negocio
    public static final String CUSTOMER_NOT_FOUND = "El cliente solicitado no fue encontrado.";
    public static final String INVALID_CUSTOMER_DATA = "Los datos del cliente proporcionados no son válidos.";
    public static final String CUSTOMER_ALREADY_EXISTS = "Ya existe un cliente con el mismo documento.";

    // 🔹 Mensajes genéricos
    public static final String INTERNAL_SERVER_ERROR = "Ocurrió un error interno en el servidor.";
    public static final String BAD_REQUEST = "Solicitud inválida. Por favor verifique los datos enviados.";
    public static final String SUCCESS_OPERATION = "Operación realizada con éxito.";

    // 🔹 Información del servicio
    public static final String SERVICE_NAME = "Customer Service";
    public static final String VERSION = "1.0.0";


    public static final Integer COD_RESPONSE = 0;
    public static final String USER_SAVED = "User saved successfully";
    public static final String CUSTOMER_DELETED = "Customer delete successfully";
    public static final String ERROR_DELETE = "Error Delete: ";
    private Constantes() {
        // Evita la instanciación
    }
}
