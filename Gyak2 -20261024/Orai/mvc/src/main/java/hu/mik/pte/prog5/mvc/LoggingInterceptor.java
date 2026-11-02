package hu.mik.pte.prog5.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        log.info("[1] DispatchServlet received: "+ request.getMethod() + "" + request.getRequestURI());
        log.info("[2] HandlerMapping resolved to: "+ handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        log.info("[3] Controller finished, view name:"+ modelAndView.getViewName());
    }
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex){
        log.info("[4] View rendered, response sent. Status"+response.getStatus());
    }
}
