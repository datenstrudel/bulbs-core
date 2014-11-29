package net.datenstrudel.bulbs.core.util;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
* Created by Thomas Wendzinski.
*/
@Configuration
@ComponentScan(basePackages = {
        "net.datenstrudel.bulbs.core.util"
        }, excludeFilters = @ComponentScan.Filter(Configuration.class))
public class IdentityUtlConfig {
}
