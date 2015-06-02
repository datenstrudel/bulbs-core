package net.datenstrudel.bulbs.core.web.config;

import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.*;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Wendzinski.
 */
@Configuration
@EnableSwagger
@Profile("swagger")
public class SwaggerConfig {

    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;
//
    @Bean
    public SwaggerSpringMvcPlugin customImplementation(){
        SwaggerSpringMvcPlugin mvcPlugin = new SwaggerSpringMvcPlugin(this.springSwaggerConfig);
        mvcPlugin
                .apiInfo(apiInfo())
                .ignoredParameterTypes(Authentication.class, Principal.class)
                .excludeAnnotations(new Class[]{Controller.class})
                .authorizationTypes(authTypes())
                .authorizationContext(authContext()) ;
        return mvcPlugin;
    }
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Bulbs_Core Light control API",
                "Bulbs_Core enables you to abstract control of light bulbs from (potentially) different hardware vendors by giving you a unified API.",
                "",
                "twx1@arcor.de",
                "No license yet",
                "No license yet"
        );
    }

    private List<AuthorizationType> authTypes(){
        List<AuthorizationType> res = new ArrayList<>(1);

//        List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
//        authorizationScopeList.add(new AuthorizationScope("global", "access all"));

        ApiKey apiKeyAuthType = new ApiKey("Auth", "Auth");
        res.add(apiKeyAuthType);
        return res;
    }
    private AuthorizationContext authContext(){
        List<Authorization> auths = new ArrayList<>(1);
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "Access everything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{authorizationScope};
        auths.add(new Authorization("Auth", authorizationScopes));

        return new AuthorizationContext.AuthorizationContextBuilder(auths).build();

    }
}
