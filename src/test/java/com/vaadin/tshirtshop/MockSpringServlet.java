package com.vaadin.tshirtshop;

import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import kotlin.jvm.functions.Function0;
import kotlin.reflect.KFunction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author mavi
 */
class MockSpringServlet extends SpringServlet {
    @NotNull
    public final Routes routes;
    @NotNull
    public final ApplicationContext ctx;
    @NotNull
    public final Function0<UI> uiFactory;

    public MockSpringServlet(@NotNull Routes routes, @NotNull ApplicationContext ctx, @NotNull Function0<UI> uiFactory) {
        super(ctx, false);
        this.ctx = ctx;
        this.routes = routes;
        this.uiFactory = uiFactory;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        routes.register(servletConfig.getServletContext());
        super.init(servletConfig);
    }


    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        final VaadinServletService service = new SpringVaadinServletService(this, deploymentConfiguration, ctx) {
            @Override
            protected boolean isAtmosphereAvailable() {
                return false;
            }

            @Override
            public String getMainDivId(VaadinSession session, VaadinRequest request) {
                return "ROOT-1";
            }

            @Override
            protected VaadinSession createVaadinSession(VaadinRequest request) {
                return new MockSpringVaadinSession(this, uiFactory);
            }
        };
        service.init();
        return service;
    }
}
