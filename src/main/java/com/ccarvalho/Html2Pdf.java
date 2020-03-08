package com.ccarvalho;

import com.ccarvalho.handler.Converter;

public class Html2Pdf {
    public static void main(String[] args) throws Exception {
        String uriHtml = args[0];
        String destinationPath = args[1];
        new Converter(uriHtml, destinationPath).convert();
    }
}
