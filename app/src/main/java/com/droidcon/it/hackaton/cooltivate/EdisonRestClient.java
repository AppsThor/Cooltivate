package com.droidcon.it.hackaton.cooltivate;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

@Rest(
        rootUrl = "http://192.168.43.66:5000/",
        converters = {MappingJacksonHttpMessageConverter.class, StringHttpMessageConverter.class}
)
public interface EdisonRestClient {

    @Get("irrigation/{status}")
    Status changeIrrigationStatus(int status);

    @Get("irrigation")
    Status getIrrigationStatus();

    @Get("light/{status}")
    Status changeLightStatus(int status);

    @Get("light")
    Status getLightStatus();

    @Get("fan/{status}")
    Status changeFanStatus(int status);

    @Get("fan")
    Status getFanStatus();

}
