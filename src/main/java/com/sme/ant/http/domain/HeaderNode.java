package com.sme.ant.http.domain;

import org.apache.tools.ant.types.DataType;

/**
 * Header node to create an http header.
 */
public class HeaderNode extends DataType
{
    private String name;
    private String value;

    public HeaderNode()
    {
    }

    public HeaderNode(String name, String value)
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
