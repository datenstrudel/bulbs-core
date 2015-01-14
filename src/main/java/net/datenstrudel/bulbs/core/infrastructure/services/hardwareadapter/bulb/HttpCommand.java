package net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Objects;

/**
 * Wrapper that contains all parameters necessary to describe a HTTP request.
 * @author Thomas Wendzinski
 */
public class HttpCommand {

    //~ Member(s) //////////////////////////////////////////////////////////////
    /** Can contain placeholders like http://bridgeHost/{esourceParam}*/
    private String url;
    private HttpMethod httpMethod;
    private HttpEntity<String> entity;
    /** Variables that are put into url as resolved placeholders */
    private Map<String, Object> urlVariables;
    
    
    //~ Construction ///////////////////////////////////////////////////////////
    private HttpCommand(){}
    public HttpCommand(
            String url, 
            HttpMethod httpMethod, 
            HttpEntity<?> entity, 
            Map<String, Object> urlVariables) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.entity = (HttpEntity<String>) entity;
        this.urlVariables = urlVariables;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public String getUrl() {
        return url;
    }
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    public HttpEntity<String> getEntity() {
        return entity;
    }
    public Map<String, Object> getUrlVariables() {
        return urlVariables;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.url);
        hash = 17 * hash + Objects.hashCode(this.httpMethod);
        hash = 17 * hash + Objects.hashCode(this.entity);
        hash = 17 * hash + Objects.hashCode(this.urlVariables);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HttpCommand other = (HttpCommand) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        if (this.httpMethod != other.httpMethod) {
            return false;
        }
        if (!Objects.equals(this.entity, other.entity)) {
            return false;
        }
        if (!Objects.equals(this.urlVariables, other.urlVariables)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "HttpCommand{" 
                + "url=" + url 
                + ", httpMethod=" + httpMethod 
                + ", entity=" + entity 
                + ", urlVariables=" + urlVariables + '}';
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

   

}
