package vn.iotstar.filter;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import vn.iotstar.service.CartService_24110317;

/** Dat cac hang so dung chung cho JSP (applicationScope) de khong lap lai con so trong view. */
@WebListener
public class AppInitListener_24110317 implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("cartMaxPerItem", CartService_24110317.MAX_PER_ITEM);
    }
}
