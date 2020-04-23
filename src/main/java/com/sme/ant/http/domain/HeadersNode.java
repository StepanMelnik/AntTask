package com.sme.ant.http.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.types.DataType;

/**
 * Contains a list of {@link HeaderNode}.
 */
public class HeadersNode extends DataType
{
    private final List<HeaderNode> headers = new ArrayList<>();

    /**
     * Add {@link HeaderNode}.
     * 
     * @param header The instance of {@link HeaderNode}.
     */
    public void addHeader(HeaderNode header)
    {
        headers.add(header);
    }

    public List<HeaderNode> getHeaders()
    {
        return headers;
    }

    public boolean isValid()
    {
        return headers.size() > 0;
    }
}
