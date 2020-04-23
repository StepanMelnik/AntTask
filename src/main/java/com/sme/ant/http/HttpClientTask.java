package com.sme.ant.http;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.sme.ant.http.domain.HeadersNode;
import com.sme.ant.http.domain.HttpMethod;
import com.sme.ant.http.domain.QueriesNode;

/**
 * Ant target to work with HTTP request that supports a redirect strategy to a new location.
 */
public class HttpClientTask extends Task
{
    private String url;
    private String statusProperty;
    private String entityProperty;
    private String outFile;
    private boolean followRedirects = true;
    private HttpMethod method;
    private boolean printRequest = true;
    private boolean printResponse = true;
    private boolean printRequestHeaders = true;
    private boolean printResponseHeaders = true;

    private boolean failOnUnexpected = true;
    private int expectedCode = HttpStatus.SC_OK;

    private HeadersNode headers = new HeadersNode();
    private QueriesNode queries;

    @Override
    public void execute() throws BuildException
    {
        validate();

        CloseableHttpClient httpClient = createHttpClient();
        HttpRequestBase httpMethod = createHttpMethod();
        httpMethod.setHeaders(headers.getHeaders().stream().map(h -> new BasicHeader(h.getName(), h.getValue())).collect(toList()).toArray(new BasicHeader[0]));

        printRequest(httpMethod);

        try (CloseableHttpResponse httpResponse = httpClient.execute(httpMethod);)
        {
            final int responseCode = httpResponse.getStatusLine().getStatusCode();

            if (failOnUnexpected && responseCode != expectedCode)
            {
                throw new BuildException("Http response does not return a porper code: " + responseCode);
            }

            Optional.ofNullable(statusProperty).ifPresent(s -> getProject().setProperty(statusProperty, String.valueOf(responseCode)));

            byte[] content = EntityUtils.toByteArray(httpResponse.getEntity());
            String contentAsString = new String(content, "UTF-8");
            Optional.ofNullable(entityProperty).ifPresent(s -> getProject().setProperty(entityProperty, contentAsString));

            Optional.ofNullable(outFile).ifPresent(o ->
            {
                saveFile(content);
            });

            printResponse(httpResponse, contentAsString);
        }
        catch (Exception e)
        {
            throw new BuildException("Cannot perfrom http request", e);
        }
    }

    private void saveFile(byte[] content)
    {
        try
        {
            try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(outFile)))
            {
                fos.write(content);
                fos.flush();

                File file = new File(outFile);
                log("Entity written to file: " + file.getAbsolutePath());
            }
        }
        catch (Exception e)
        {
            throw new BuildException("Cannot save file", e);
        }
    }

    private void printResponse(CloseableHttpResponse httpResponse, String content)
    {
        printResponse(printResponseHeaders, r -> asList(r.getAllHeaders()).stream().forEach(h -> log("Response Header: " + h)), httpResponse);

        if (printResponse)
        {
            log("Response: " + content);
        }
    }

    private void printResponse(boolean print, Consumer<CloseableHttpResponse> consumer, CloseableHttpResponse httpResponse)
    {
        if (print)
        {
            consumer.accept(httpResponse);
        }
    }

    private void printRequest(HttpRequestBase httpMethod)
    {
        printRequest(printRequest, m -> log("Requests: " + m), httpMethod);
        printRequest(printRequestHeaders, m -> asList(m.getAllHeaders()).stream().forEach(h -> log("Request Header: " + h)), httpMethod);
    }

    private void printRequest(boolean print, Consumer<HttpRequestBase> consumer, HttpRequestBase httpMethod)
    {
        if (print)
        {
            consumer.accept(httpMethod);
        }
    }

    private HttpRequestBase createHttpMethod()
    {
        try
        {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameters(queries.getQueries().stream().map(q -> new BasicNameValuePair(q.getName(), q.getValue())).collect(toList()));

            switch (method)
            {
                case GET:
                    return new HttpGet(uriBuilder.build());

                default:
                    throw new IllegalArgumentException(method + " has not supported yet");
            }
        }
        catch (URISyntaxException e)
        {
            throw new BuildException("Url parameters cannot be parsed", e);
        }
    }

    private CloseableHttpClient createHttpClient()
    {
        return followRedirects ? HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build() : HttpClients.custom().build();
    }

    /**
     * Validate input parameters.
     */
    void validate()
    {
        requireNonNull(url, "Requires 'url' parameter");
        requireNonNull(method, "Requires 'method' parameter");

        Optional.of(headers.isValid()).filter(v -> v).orElseThrow(() -> new BuildException("Headers should be predefined to perfrom http query"));
    }

    /**
     * Add headers.
     * 
     * @param headers The list of headers.
     */
    public void addHeaders(HeadersNode headers)
    {
        this.headers = headers;
    }

    /**
     * Add queries.
     * 
     * @param queries The list of queries.
     */
    public void addQueries(QueriesNode queries)
    {
        this.queries = queries;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setStatusProperty(String statusProperty)
    {
        this.statusProperty = statusProperty;
    }

    public void setEntityProperty(String entityProperty)
    {
        this.entityProperty = entityProperty;
    }

    public void setOutFile(String outFile)
    {
        this.outFile = outFile;
    }

    public void setPrintRequest(boolean printRequest)
    {
        this.printRequest = printRequest;
    }

    public void setPrintResponse(boolean printResponse)
    {
        this.printResponse = printResponse;
    }

    public void setFollowRedirects(boolean followRedirects)
    {
        this.followRedirects = followRedirects;
    }

    public void setHeaders(HeadersNode headers)
    {
        this.headers = headers;
    }

    public void setQueries(QueriesNode queries)
    {
        this.queries = queries;
    }

    public void setMethod(final HttpMethod method)
    {
        this.method = method;
    }

    public void setPrintRequestHeaders(boolean printRequestHeaders)
    {
        this.printRequestHeaders = printRequestHeaders;
    }

    public void setPrintResponseHeaders(boolean printResponseHeaders)
    {
        this.printResponseHeaders = printResponseHeaders;
    }

    public void setFailOnUnexpected(boolean failOnUnexpected)
    {
        this.failOnUnexpected = failOnUnexpected;
    }

    public void setExpectedCode(int expectedCode)
    {
        this.expectedCode = expectedCode;
    }
}
