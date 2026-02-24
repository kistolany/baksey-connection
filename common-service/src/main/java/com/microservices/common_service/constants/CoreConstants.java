package com.microservices.common_service.constants;

public class CoreConstants {

    public enum Status {
        Pending,
        Enabled,
        Disabled,
        Deleted
    }

    public enum OtpStrategy {
        Static,
        Dynamic
    }
    
    public enum TokenType {
        AccessToken,
        RefreshToken,
    }
}
