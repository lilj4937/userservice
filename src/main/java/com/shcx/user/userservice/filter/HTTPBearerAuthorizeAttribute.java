package com.shcx.user.userservice.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shcx.user.userservice.jwt.JwtHelper;
import com.shcx.user.userservice.util.StringUtils;

public class HTTPBearerAuthorizeAttribute implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String url = httpRequest.getRequestURI();
		if(url.equals("/auth")){//不验证登录接口
			chain.doFilter(request, response);
			return;
		}if(url.equals("/user/register")){//不验证注册接口
			chain.doFilter(request, response);
			return;
		}
		
		String auth = httpRequest.getHeader("Authorization");
		if (StringUtils.isNotEmpty(auth)) {
			if (JwtHelper.parseJWT(auth) != null) {
				chain.doFilter(request, response);
				return;
			}
		}

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json; charset=utf-8");
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		ObjectMapper mapper = new ObjectMapper();
		JSONObject resultMsg = new JSONObject();
		resultMsg.put("code", "403");
		resultMsg.put("message", "用户验证失败！");
		httpResponse.getWriter().write(mapper.writeValueAsString(resultMsg));
		return;

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				filterConfig.getServletContext());
	}

}
