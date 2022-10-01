package com.phuocnguyen.app.ngxblobsadvice.config;


import com.phuocnguyen.app.ngxblobsadvice.service.NgxGlobalExceptionService;
import com.phuocnguyen.app.ngxblobsadvice.service.serviceImpl.NgxGlobalExceptionServiceImpl;
import com.sivaos.Service.GlobalExceptionService;
import com.sivaos.Service.SIVAOSServiceImplement.GlobalExceptionServiceImplement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

@SuppressWarnings({"All"})
@Configuration
public class ServicesGlobalConfig {

    @Bean
    @Primary
    @Resource(name = "globalExceptionService")
    public GlobalExceptionService globalExceptionService() {
        return new GlobalExceptionServiceImplement();
    }

    @Bean
    @Primary
    @Resource(name = "ngxGlobalExceptionService")
    public NgxGlobalExceptionService ngxGlobalExceptionService() {
        return new NgxGlobalExceptionServiceImpl();
    }
}
