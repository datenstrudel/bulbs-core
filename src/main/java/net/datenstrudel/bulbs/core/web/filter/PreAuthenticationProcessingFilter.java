package net.datenstrudel.bulbs.core.web.filter;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Thomas Wendzinski
 */
public class PreAuthenticationProcessingFilter extends AbstractPreAuthenticatedProcessingFilter{

    //~ Member(s) //////////////////////////////////////////////////////////////
    //~ Construction ///////////////////////////////////////////////////////////
    public PreAuthenticationProcessingFilter() {
    }

    //~ Method(s) //////////////////////////////////////////////////////////////
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String principal = request.getHeader("Auth");
//        if (principal == null ){ 
//&& exceptionIfHeaderMissing) {
//            throw new PreAuthenticatedCredentialsNotFoundException("'Auth' header not found in request.");
//        }

        return principal;
    }
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
//         if (credentialsRequestHeader != null) {
//            return request.getHeader(credentialsRequestHeader);
//        }

        return "N/A";
    }

    //~ Private Artifact(s) ////////////////////////////////////////////////////

}
