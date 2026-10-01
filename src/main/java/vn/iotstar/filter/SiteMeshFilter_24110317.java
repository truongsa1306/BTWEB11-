package vn.iotstar.filter;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

/**
 * Cau hinh Sitemesh Decorators cho 02 vai tro:
 *  - /admin/*  -> /WEB-INF/decorators/admin.jsp (giao dien quan tri)
 *  - con lai   -> /WEB-INF/decorators/user.jsp  (giao dien nguoi dung)
 */
public class SiteMeshFilter_24110317 extends ConfigurableSiteMeshFilter {
    @Override
    protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
        builder.addDecoratorPath("/admin", "admin.jsp")
               .addDecoratorPath("/admin/*", "admin.jsp")
               .addDecoratorPath("/*", "user.jsp")
               .addExcludedPath("/assets/*");
    }
}
