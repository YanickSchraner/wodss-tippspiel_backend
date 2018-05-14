package ch.fhnw.wodss.tippspiel.configuration;

import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Requires a header "Origin" header to be present for every request to enforce CORS for every request.
 */

public class EnforceCorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        if (!CorsUtils.isCorsRequest(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Not a CORS request (Origin header is missing)");
        } else {
            filterChain.doFilter(request, response);
        }
    }
}