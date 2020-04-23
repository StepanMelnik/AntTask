package com.sme.ant.http.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link QueriesNode}.
 */
public class QueriesNodeTest extends Assertions
{
    @Test
    void testList() throws Exception
    {
        QueriesNode queriesNode = new QueriesNode();
        assertFalse(queriesNode.isValid(), "Expects empty list");

        queriesNode.addQuery(new QueryNode());
        assertTrue(queriesNode.isValid(), "Expects valid list");
    }
}
