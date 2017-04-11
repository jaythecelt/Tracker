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

package com.amazonaws.mobile.api.idh9ydfojrbi.model;

import com.amazonaws.mobile.api.idh9ydfojrbi.model.TrackerPOSTTrack;

public class TrackerPOST {
    @com.google.gson.annotations.SerializedName("msgType")
    private String msgType = null;
    @com.google.gson.annotations.SerializedName("msgVersion")
    private String msgVersion = null;
    @com.google.gson.annotations.SerializedName("track")
    private TrackerPOSTTrack track = null;

    /**
     * Gets msgType
     *
     * @return msgType
     **/
    public String getMsgType() {
        return msgType;
    }

    /**
     * Sets the value of msgType.
     *
     * @param msgType the new value
     */
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    /**
     * Gets msgVersion
     *
     * @return msgVersion
     **/
    public String getMsgVersion() {
        return msgVersion;
    }

    /**
     * Sets the value of msgVersion.
     *
     * @param msgVersion the new value
     */
    public void setMsgVersion(String msgVersion) {
        this.msgVersion = msgVersion;
    }

    /**
     * Gets track
     *
     * @return track
     **/
    public TrackerPOSTTrack getTrack() {
        return track;
    }

    /**
     * Sets the value of track.
     *
     * @param track the new value
     */
    public void setTrack(TrackerPOSTTrack track) {
        this.track = track;
    }

}
