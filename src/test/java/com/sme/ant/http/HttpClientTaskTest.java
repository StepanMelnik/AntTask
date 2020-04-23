package com.sme.ant.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sme.ant.http.domain.HeaderNode;
import com.sme.ant.http.domain.HeadersNode;
import com.sme.ant.http.domain.HttpMethod;
import com.sme.ant.http.domain.QueriesNode;
import com.sme.ant.http.domain.QueryNode;

/**
 * Unit tests of {@link HttpClientTask}.
 */
public class HttpClientTaskTest extends Assertions
{
    private static Server server;
    private static String url;

    private HttpClientTask httpClientTask;

    @BeforeAll
    public static void startServer() throws Exception
    {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
        server.addConnector(connector);

        RewriteHandler rewrite = new RewriteHandler();
        rewrite.setRewriteRequestURI(true);
        rewrite.setRewritePathInfo(false);
        rewrite.setOriginalPathAttribute("requestedPath");
        rewrite.setHandler(new AbstractHandler()
        {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
            {
                response.setCharacterEncoding("UTF-8");
                ServletOutputStream output = response.getOutputStream();
                output.write("Test Response".getBytes(StandardCharsets.UTF_8));
                baseRequest.setHandled(true);
            }
        });

        RedirectPatternRule redirect = new RedirectPatternRule();
        redirect.setPattern("/redirect/*");
        redirect.setLocation("/redirected");
        rewrite.addRule(redirect);

        server.setHandler(rewrite);

        server.start();

        // Determine Base URI for Server
        url = new StringBuilder("http://")
                .append(Optional.ofNullable(connector.getHost()).orElse("localhost"))
                .append(":")
                .append(connector.getLocalPort())
                .append("/")
                .toString();
    }

    @AfterAll
    public static void stopSer() throws Exception
    {
        server.stop();
    }

    @BeforeEach
    public void setUp()
    {
        httpClientTask = new HttpClientTask();
    }

    @Test
    void testValid() throws Exception
    {
        assertThrows(NullPointerException.class, () -> httpClientTask.validate(), "Expects NullPointerException error");

        httpClientTask.setUrl("http://repository.sme.com:8081/repository/redirect");
        httpClientTask.setMethod(HttpMethod.GET);

        assertThrows(BuildException.class, () -> httpClientTask.validate(), "Expects BuildException error");

        HeadersNode headersNode = new HeadersNode();
        headersNode.addHeader(new HeaderNode());
        httpClientTask.addHeaders(headersNode); // no errors here
    }

    /**
     * {@link LaxRedirectStrategy} should be performed while applying a redirected request in Jetty.
     */
    @Test
    void testGet()
    {
        HeadersNode headersNode = new HeadersNode();
        headersNode.addHeader(new HeaderNode("Accept", "text/html"));
        headersNode.addHeader(new HeaderNode("Content-Type", "text/html;charset=UTF-8"));

        QueriesNode queriesNode = new QueriesNode();
        queriesNode.addQuery(new QueryNode("id", "1234"));

        httpClientTask = new HttpClientTask();
        httpClientTask.addHeaders(headersNode);
        httpClientTask.addQueries(queriesNode);
        httpClientTask.setProject(new Project());

        String entityProperty = "ENTITY";
        String statusProperty = "STATUS";
        httpClientTask.setEntityProperty(entityProperty);
        httpClientTask.setStatusProperty(statusProperty);

        httpClientTask.setUrl(url + "/redirect/test");
        httpClientTask.setMethod(HttpMethod.GET);
        httpClientTask.execute();

        String status = httpClientTask.getProject().getProperty(statusProperty);
        assertEquals("200", status, "Response should be successfull");

        String content = httpClientTask.getProject().getProperty(entityProperty);
        assertEquals("Test Response", content, "Expects a proper response from redirected request");
    }
}
