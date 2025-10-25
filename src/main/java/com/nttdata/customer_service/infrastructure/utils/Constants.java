package com.nttdata.customer_service.infrastructure.utils;

public class    Constants {

  // Errores de Base de Datos
  public static final String DATABASE_UNAVAILABLE = "Servicio de base de datos no disponible. Verifique la conexión con MongoDB.";
  public static final String DATABASE_TIMEOUT = "Tiempo de espera excedido al intentar conectar o ejecutar una operación en MongoDB.";
  public static final String DATABASE_EXCEPTION = "Se produjo un error desconocido durante la operación en la base de datos.";

  // Errores de Negocio
  public static final String CUSTOMER_NOT_FOUND = "El cliente solicitado no fue encontrado.";
  public static final String INVALID_CUSTOMER_DATA = "Los datos proporcionados del cliente no son válidos.";
  public static final String CUSTOMER_ALREADY_EXISTS = "Ya existe un cliente con el mismo documento.";
  public static final String DOCUMENT_ALREADY_REGISTERED = "El número de documento ya está registrado para otro cliente.";

  // Mensajes Genéricos
  public static final String INTERNAL_SERVER_ERROR = "Se produjo un error interno en el servidor.";
  public static final String BAD_REQUEST = "Solicitud inválida. Por favor, verifique los datos enviados.";
  public static final String SUCCESS_OPERATION = "Operación completada exitosamente.";

  // Mensajes de Operaciones
  public static final String CUSTOMER_CREATED = "Cliente registrado exitosamente.";
  public static final String CUSTOMER_UPDATED = "Cliente actualizado exitosamente.";
  public static final String CUSTOMER_DELETED = "Cliente eliminado exitosamente.";
  public static final String CUSTOMER_SAVED = "Cliente guardado exitosamente.";
  public static final String ERROR_PROCESSING = "Error al procesar el cliente: ";
  public static final String ERROR_DELETE = "Error al eliminar el cliente: ";
  public static final String ERROR_UPDATE = "Error al actualizar el cliente: ";

  // Códigos de Estado HTTP
  public static final int HTTP_OK = 200;
  public static final int HTTP_INTERNAL_ERROR = 500;

  // Códigos de Respuesta
  public static final Integer SUCCESS_CODE = 0;

  // Información del Servicio
  public static final String SERVICE_NAME = "Servicio de Clientes";
  public static final String VERSION = "1.0.0";

  // Error de respuesta genérica
  public static final String ID_NOT_NULL = "El ID del cliente no puede ser nulo.";

  private Constants() {
    // Evita la instanciación
  }
}