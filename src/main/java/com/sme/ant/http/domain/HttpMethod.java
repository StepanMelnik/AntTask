package com.sme.ant.http.domain;

import org.apache.http.impl.client.LaxRedirectStrategy;

/**
 * A list of supported http methods.
 * <ul>
 * {@link LaxRedirectStrategy} supports the following Http methods only:
 * <li>HttpGet.METHOD_NAME</li>
 * <li>HttpPost.METHOD_NAME</li>
 * <li>HttpHead.METHOD_NAME</li>
 * <li>HttpDelete.METHOD_NAME</li>
 * </ul>
 */
public enum HttpMethod
{
    GET,
    POST,
    HEAD,
    DELETE
}
