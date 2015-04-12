package com.droidcon.it.hackaton.cooltivate;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(
        rootUrl = "http://192.168.3.7:5000/",
        converters = {MappingJacksonHttpMessageConverter.class, StringHttpMessageConverter.class}
)
public interface EdisonRestClient {

    @Get("led/on")
    Status setLedOn();

    @Get("led/off")
    Status setLedOff();

    @Get("light/{size}")
    Status setLight(float size);

    @Get("fan/on")
    Status setFanOn();

    @Get("fan/off")
    Status setFanOff();
}
