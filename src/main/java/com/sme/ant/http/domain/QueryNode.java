package com.sme.ant.http.domain;

import org.apache.tools.ant.types.DataType;

/**
 * Query parameter http node.
 */
public class QueryNode extends DataType
{
    private String name;
    private String value;

    public QueryNode()
    {
    }

    public QueryNode(String name, String value)
    {
        this();
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
