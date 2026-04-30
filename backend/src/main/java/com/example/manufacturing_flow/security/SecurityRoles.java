package com.example.manufacturing_flow.security;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

public class SecurityRoles {
    public static final String ADMIN = "ADMIN";
    public static final String GUDANG = "GUDANG";
    public static final String PRODUKSI = "PRODUKSI";
    public static final String FINANCE = "FINANCE";

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('ADMIN')")
    public @interface IsAdmin {}

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('ADMIN', 'GUDANG', 'PRODUKSI', 'FINANCE')")
    public @interface HasAnyDashboardRole {}
}
