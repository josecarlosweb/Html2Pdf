package com.ccarvalho.handler;

import com.openhtmltopdf.extend.FSUriResolver;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.swing.NaiveUserAgent;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Converter {

    private String htmlFileUri;

    private String destinationFilePath;

    public Converter(String htmlFileUri, String destinationFilePath) {
        this.htmlFileUri = "file://" + htmlFileUri;
        this.destinationFilePath = destinationFilePath;
    }

    public void convert() throws Exception {
        System.out.println("Converting file " + this.htmlFileUri + " to pdf " + this.destinationFilePath);
        try (OutputStream os = new FileOutputStream(this.destinationFilePath)) {
            final NaiveUserAgent.DefaultUriResolver defaultUriResolver = new NaiveUserAgent.DefaultUriResolver();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useUriResolver(getResolver(defaultUriResolver));
            builder.useHttpStreamImplementation(new OkHttpStreamFactory());
            org.w3c.dom.Document document = html5ParseDocument(this.htmlFileUri, 10000);
            builder.withW3cDocument(document, this.htmlFileUri);
            builder.toStream(os);
            builder.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private FSUriResolver getResolver(NaiveUserAgent.DefaultUriResolver defaultUriResolver) {
        return (baseUri, uri) -> {
            String supResolved = defaultUriResolver.resolveURI(baseUri, uri);
            if (supResolved == null || supResolved.isEmpty())
                return null;

            try {
                URI uriObj = new URI(supResolved);
                return uriObj.toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    public org.w3c.dom.Document html5ParseDocument(String urlStr, int timeoutMs) throws IOException {
        URL url = new URL(urlStr);
        org.jsoup.nodes.Document doc;

        if (url.getProtocol().equalsIgnoreCase("file")) {
            doc = Jsoup.parse(new File(url.getPath()), "UTF-8");
        } else {
            doc = Jsoup.parse(url, timeoutMs);
        }
        // Should reuse W3CDom instance if converting multiple documents.
        return new W3CDom().fromJsoup(doc);
    }

}
