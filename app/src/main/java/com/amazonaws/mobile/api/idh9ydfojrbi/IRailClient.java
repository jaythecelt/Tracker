/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.mobile.api.idh9ydfojrbi;

import java.util.*;

import com.amazonaws.mobile.api.idh9ydfojrbi.model.Empty;
import com.amazonaws.mobile.api.idh9ydfojrbi.model.TrackerPOST;


@com.amazonaws.mobileconnectors.apigateway.annotation.Service(endpoint = "https://h9ydfojrbi.execute-api.us-east-1.amazonaws.com/prod")
public interface IRailClient {


    /**
     * A generic invoker to invoke any API Gateway endpoint.
     * @param request
     * @return ApiResponse
     */
    com.amazonaws.mobileconnectors.apigateway.ApiResponse execute(com.amazonaws.mobileconnectors.apigateway.ApiRequest request);
    
    /**
     * 
     * 
     * @param start 
     * @param user 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/tracker", method = "GET")
    Empty trackerGet(
            @com.amazonaws.mobileconnectors.apigateway.annotation.Parameter(name = "start", location = "query")
                    String start,
            @com.amazonaws.mobileconnectors.apigateway.annotation.Parameter(name = "user", location = "query")
                    String user);
    
    /**
     * 
     * 
     * @param body 
     * @param contentType 
     * @return Empty
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/tracker", method = "POST")
    Empty trackerPost(
            TrackerPOST body,
            @com.amazonaws.mobileconnectors.apigateway.annotation.Parameter(name = "Content-Type", location = "header")
                    String contentType);
    
}

