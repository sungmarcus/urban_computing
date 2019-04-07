package com.example.sungm.urbanapp;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import retrofit2.Retrofit;

import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class ApiRetroFit {

    private static Retrofit retrofitXml = null;

    public static Retrofit getRetrofitInstanceXML(String url) {
        if (retrofitXml == null) {
            retrofitXml = new retrofit2.Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(SimpleXmlConverterFactory
                            .createNonStrict(new Persister(new AnnotationStrategy())))
                    .build();
        }
        return retrofitXml;
    }

}