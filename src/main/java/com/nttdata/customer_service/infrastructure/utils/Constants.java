package com.nttdata.customer_service.infrastructure.utils;

public class Constants {

    // Database Errors
    public static final String DATABASE_UNAVAILABLE = "Database service unavailable. Please verify MongoDB connection.";
    public static final String DATABASE_TIMEOUT = "Timeout exceeded while attempting to connect or execute operation in MongoDB.";
    public static final String DATABASE_EXCEPTION = "Unknown error occurred during database operation.";

    // Business Errors
    public static final String CUSTOMER_NOT_FOUND = "The requested customer was not found.";
    public static final String INVALID_CUSTOMER_DATA = "The provided customer data is invalid.";
    public static final String CUSTOMER_ALREADY_EXISTS = "A customer with the same document already exists.";

    // Generic Messages
    public static final String INTERNAL_SERVER_ERROR = "An internal server error occurred.";
    public static final String BAD_REQUEST = "Invalid request. Please verify the data sent.";
    public static final String SUCCESS_OPERATION = "Operation completed successfully.";

    // Operation Messages
    public static final String CUSTOMER_CREATED = "Customer registered successfully.";
    public static final String CUSTOMER_UPDATED = "Customer updated successfully.";
    public static final String CUSTOMER_DELETED = "Customer deleted successfully.";
    public static final String CUSTOMER_SAVED = "Customer saved successfully.";
    public static final String ERROR_PROCESSING = "Error processing customer: ";
    public static final String ERROR_DELETE = "Error deleting customer: ";
    public static final String ERROR_UPDATE = "Error updating customer: ";

    // HTTP Status Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_INTERNAL_ERROR = 500;

    // Response Codes
    public static final Integer SUCCESS_CODE = 0;

    // Service Information
    public static final String SERVICE_NAME = "Customer Service";
    public static final String VERSION = "1.0.0";

    private Constants() {
        // Prevents instantiation
    }
}