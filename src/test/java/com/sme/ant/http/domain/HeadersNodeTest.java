package com.sme.ant.http.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link HeadersNode}.
 */
public class HeadersNodeTest extends Assertions
{
    @Test
    void testValid() throws Exception
    {
        HeadersNode headersNode = new HeadersNode();
        assertFalse(headersNode.isValid(), "Expects empty list");

        headersNode.addHeader(new HeaderNode());
        assertTrue(headersNode.isValid(), "Expects valid list");
    }
}
