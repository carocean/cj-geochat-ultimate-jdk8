package cj.geochat.ability.oauth2;

import cj.geochat.ability.api.R;
import cj.geochat.ability.api.ResultCode;
import cj.geochat.ability.oauth2.properties.AuthPageAddress;
import cj.geochat.ability.oauth2.properties.SecurityProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类拦截所有请求。<br>
 * 用于拦截系统起发的用户授权确认请求
 * <pre>
 * 当合法访问了(即在访问授权端点前已成功登录)/oauth/authorize授权码端点，且用户没有确认过授权，则：
 * oauth2会自动重定向（前端作用机制）发起到/oauth/confirm_access的请求。
 * 在此拦截请求，协议响应给调用者，由调用者决定用户授权确认页面。
 *
 * 如果系统判断该步不需要用户授权确认，则：
 * oauth2会自动根据client的redirect_uri地址重定向，并附上code，
 * 因此开发者只需要在redirect_uri等待并以code换取token即可。
 * </pre>
 */
public class DefaultWebRequestInterceptor implements HandlerInterceptor {
    SecurityProperties securityProperties;

    public DefaultWebRequestInterceptor(SecurityProperties securityProperties) {
        this.securityProperties=securityProperties;
    }

    //自定义用户确认页到认证前端web，也可以拦截认证失败地址到认证前端web处理
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().startsWith("/oauth/confirm_access")) {
            Map<String, String[]> paramMap = request.getParameterMap();
            StringBuilder param = new StringBuilder();
            paramMap.forEach((k, v) -> {
                param.append("&").append(k).append("=").append(v[0]);
            });
            param.deleteCharAt(0);
            ResultCode rc = ResultCode.CONFIRM_ACCESS;
            Map<String, Object> map = new HashMap<>();
            map.put("web_auth", securityProperties.getWeb_auth());
            map.put("app_auth", securityProperties.getApp_auth());
            if (!paramMap.isEmpty()) {
                map.put("queryString", param);
            }
            Object obj = R.of(rc, map);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(obj));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
