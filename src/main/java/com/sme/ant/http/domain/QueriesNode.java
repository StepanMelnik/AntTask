package com.sme.ant.http.domain;

import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.types.DataType;

/**
 * Contains a list of {@link HeaderNode}.
 */
public class QueriesNode extends DataType
{
    private final List<QueryNode> queries = new LinkedList<>();

    /**
     * Add {@link QueriesNode}.
     * 
     * @param query The instance of {@link QueriesNode}.
     */
    public void addQuery(QueryNode query)
    {
        queries.add(query);
    }

    public List<QueryNode> getQueries()
    {
        return queries;
    }

    public boolean isValid()
    {
        return queries.size() > 0;
    }
}
