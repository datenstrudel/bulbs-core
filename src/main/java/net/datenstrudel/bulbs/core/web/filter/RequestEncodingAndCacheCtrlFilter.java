package net.datenstrudel.bulbs.core.web.filter;


import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This filter obeys two requirements:
 * <ul>
 *    <li>Set character encoding to the one configured in <code>web.xml</code></li>
 *    <li>Set immediate cache expiration to all files served, that match the pattern
 *          <code>*.nocache.*</code></li>
 * </ul>
 * @author Thomas Wendzinski
 */
public class RequestEncodingAndCacheCtrlFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestEncodingAndCacheCtrlFilter.class);
    //~ (Bean) Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private String encoding = "utf-8";
    private FilterConfig filterConfig = null;
    private Set<String> nocacheUris = new HashSet<>();
    
    //~ (Bean) Setter(s), Getter(s) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //~ Constructor(s) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //~ Method(s) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public void init(FilterConfig filterConfig) {
//        log.info("1------------------------");
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            String encodingParam = filterConfig
                  .getInitParameter("encoding");
            if (encodingParam != null) {
                this.encoding = encodingParam;
            }
            
            String noCacheUris = filterConfig.getInitParameter("noCache");
            StringTokenizer nocUrisTk = new StringTokenizer(noCacheUris, ";");
            String tmpUri;
            while(nocUrisTk.hasMoreTokens()){
                tmpUri = nocUrisTk.nextToken();
                if(!tmpUri.isEmpty())this.nocacheUris.add(tmpUri);
            }
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)throws IOException, ServletException{
        HttpServletResponse resp;
        HttpServletRequest req = (HttpServletRequest)request;
        //~ Set correct Character Encoding
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        
        
        //~ Set caching policy
        if( reqUriMatchesNocacheUri(req.getRequestURI()) ){
            resp = (HttpServletResponse) response;
            resp.setDateHeader("Expires", 0);
            resp.setHeader("Pragma", "no-cache");
            resp.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        }
        
        try{
            chain.doFilter(request, response);
        }catch(IOException | ServletException ex){
            log.error("Exception raised to filter; Apply propper handling!!!!",ex);
            throw ex;
        }
    }
    
    private boolean reqUriMatchesNocacheUri(String uri){
        for (String el : nocacheUris) {
            if(uri.contains(el))return true;
        }
        return false;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}
