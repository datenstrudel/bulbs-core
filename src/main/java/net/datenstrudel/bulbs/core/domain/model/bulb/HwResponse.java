package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.shared.domain.model.ValueObject;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Thomas Wendzinski
 * @deprecated - use {@link InvocationResponse instead!}
 */
public class HwResponse implements ValueObject<HwResponse>{

    //~ Member(s) //////////////////////////////////////////////////////////////
    private List<Map<String, Object>> responseBody;
    private HttpStatus httpStatusCode;

    //~ Construction ///////////////////////////////////////////////////////////
    public HwResponse(List<Map<String, Object>> responseBody, HttpStatus httpStatusCode) {
        this.responseBody = responseBody;
        this.httpStatusCode = httpStatusCode;
    }
    
    //~ Method(s) //////////////////////////////////////////////////////////////
    public List<Map<String, Object>> getResponseBody() {
        return responseBody;
    }
    private void setResponseBody(List<Map<String, Object>> responseBody) {
        this.responseBody = responseBody;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }
    private void setHttpStatusCode(HttpStatus httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.responseBody);
        hash = 71 * hash + Objects.hashCode(this.httpStatusCode);
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
        final HwResponse other = (HwResponse) obj;
        if (!Objects.equals(this.responseBody, other.responseBody)) {
            return false;
        }
        if (this.httpStatusCode != other.httpStatusCode) {
            return false;
        }
        return true;
    }
    @Override
    public boolean sameValueAs(HwResponse other) {
        if(other == null) return false;
        return this.equals(other);
    }
    
    
    //~ Private Artifact(s) ////////////////////////////////////////////////////

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("HwResponse{")
                .append("httpStatusCode=")
                .append(httpStatusCode)
                .append(" | responseBody={");
        if(responseBody != null ){
            for (Map<String, Object> map : responseBody) {
                res.append(objToString(map)).append(" | ");
            }
        }else res.append("{ null }");
        res.append("} }");
        return res.toString();
    }
    private Object objToString(Object o){
        StringBuilder res = new StringBuilder();
        if(o instanceof Map){
            res.append("{");
            for (Object el : ((Map)o).keySet()) {
                res.append(el).append(" => ").append( ((Map)o).get(el) );
            }
            res.append("}");
        }else{
            res.append(o);
        }
        return res.toString();
    }

}
