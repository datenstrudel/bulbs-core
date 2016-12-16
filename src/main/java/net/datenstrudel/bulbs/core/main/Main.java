package net.datenstrudel.bulbs.core.main;

import net.datenstrudel.bulbs.core.application.ApplicationLayerConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.security.config.SecurityConfig;
import net.datenstrudel.bulbs.core.web.config.SwaggerConfig;
import net.datenstrudel.bulbs.core.web.config.WebConfig;
import net.datenstrudel.bulbs.core.websocket.WebSocketConfig;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAutoConfiguration( exclude = {DataSourceAutoConfiguration.class})
public class Main extends SpringBootServletInitializer {

    public static final Logger log = LoggerFactory.getLogger(Main.class);

    @Value("${sslKeystorePath:}")
    private String pathToKeystore;
    @Value("${sslKeystorePwd:}")
    private String sslKeystorePwd;
    @Value("${sslKeyAlias:}")
    private String sslKeyAlias;

    public static void main(String[] args) {
        ApplicationContext ctx = new SpringApplicationBuilder()
                .sources(
                        BulbsCoreConfig.class,
                        ApplicationLayerConfig.class,
                        SecurityConfig.class,
                        WebConfig.class,
                        WebSocketConfig.class,
                        SwaggerConfig.class,
                        Main.class
                )
                .web(true)
                .run(args);

        String[] beanNames = ctx.getBeanDefinitionNames();
        log.info("COUNT beans loaded: " + beanNames.length);
        beanNames = null;
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//            System.out.println(beanName);
//        }

    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.sources(Main.class);
        return super.configure(application);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
    }

    @Bean
    @Profile(value = "development")
    public EmbeddedServletContainerFactory servletContainerDevelopment() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(8084);
        factory.setSessionTimeout(10, TimeUnit.MINUTES);
        applyStageIndependendContainerConfig(factory);
        return factory;
    }
    @Bean
    @Profile(value = "production")
    public EmbeddedServletContainerFactory servletContainerProduction() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(80);
        factory.setSessionTimeout(10, TimeUnit.MINUTES);
        applyStageIndependendContainerConfig(factory);
        factory.addAdditionalTomcatConnectors(createSslConnector());
        return factory;
    }

    private void applyStageIndependendContainerConfig(TomcatEmbeddedServletContainerFactory factory){
        factory.setContextPath("");
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/errorpages/404.html"));

        //~ Enable compression
        factory.addConnectorCustomizers(connector -> {
            AbstractHttp11Protocol ph = (AbstractHttp11Protocol) connector.getProtocolHandler();
            ph.setCompression("on");
            ph.setCompressionMinSize(2048);
            String mimeTypes = ph.getCompressableMimeTypes();
            mimeTypes += "," + "application/javascript";
            mimeTypes += "," + "text/css";
            ph.setCompressableMimeTypes(mimeTypes);
            log.info("Compressable MimeTypes applied: {} ", mimeTypes);
        });
    }

    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        File keystore = null;
        File truststore = null;
        try {
            keystore = new DefaultResourceLoader().getResource(this.pathToKeystore).getFile(); //new ClassPathResource(pathToKeystore).getFile();
            truststore = new DefaultResourceLoader().getResource(pathToKeystore).getFile();
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(443);
            protocol.setSSLEnabled(true);
            protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass(this.sslKeystorePwd);
            protocol.setTruststoreFile(truststore.getAbsolutePath());
            protocol.setTruststorePass(this.sslKeystorePwd);
            protocol.setKeyAlias(this.sslKeyAlias);
            return connector;
        }catch (IOException ex) {
            throw new IllegalStateException("can't access keystore: [" + keystore
                    + "] or truststore: [" + truststore + "]", ex);
        }
    }

}
