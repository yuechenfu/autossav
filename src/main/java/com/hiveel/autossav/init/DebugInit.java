package com.hiveel.autossav.init;

import com.hiveel.core.debug.DebugSetting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebugInit {
    @Value("${core.debug:false}")
    public void setDebug(boolean debug){
        DebugSetting.debug = debug;
    }
}
