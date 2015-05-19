package net.datenstrudel.bulbs.core.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Controller
public class BulbsRootCtrl {

    private static final Logger log = LoggerFactory.getLogger(BulbsRootCtrl.class);
    public static final String TARGET_APP_MINIFIED = "/bulbsCore/index.html";
    public static final String TARGET_APP_NOT_MINIFIED = "/bulbsCore/index-devel.html";

    private static Boolean isDevelopment = null;

    @Value("${webapp.forceMinifiedVersion:false}")
    private Boolean forceminifiedVersion;

    @Autowired
    private Environment environment;

    @RequestMapping("/")
    public void index( HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(isDevelopment == null) isDevelopment = determineStage();
        if(isDevelopment){
            log.debug("Minfied version enforced: " + forceminifiedVersion);
        }
        if(!isDevelopment || forceminifiedVersion){
            req.getRequestDispatcher(TARGET_APP_MINIFIED).forward(req, resp);
            return;
        }else{
            req.getRequestDispatcher(TARGET_APP_NOT_MINIFIED).forward(req, resp);
            return;
        }
    }

    private boolean determineStage(){
        String[] activeProfiles = environment.getActiveProfiles();
        log.info("Profiles detected: " + Arrays.toString(activeProfiles));

        if(activeProfiles == null) throw new IllegalStateException("Aplication stated without any profile specified");
        for(String el : activeProfiles){
            if("development".equals(el)){
                return true;
            }
        }
        return false;
    }
}
