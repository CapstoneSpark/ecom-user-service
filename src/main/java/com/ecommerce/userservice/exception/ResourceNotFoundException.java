
package com.ecommerce.userservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Generic message-only constructor
     * Example: throw new ResourceNotFoundException("User not found");
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Detailed constructor used across the service layer
     * Example:
     *  throw new ResourceNotFoundException("User", "email", email)
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
