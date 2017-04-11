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

import java.math.BigDecimal;

public class TrackerPOSTTrack {
    @com.google.gson.annotations.SerializedName("userId")
    private String userId = null;
    @com.google.gson.annotations.SerializedName("trackTimestamp")
    private String trackTimestamp = null;
    @com.google.gson.annotations.SerializedName("trackStatus")
    private Integer trackStatus = null;
    @com.google.gson.annotations.SerializedName("trackLat")
    private BigDecimal trackLat = null;
    @com.google.gson.annotations.SerializedName("trackLong")
    private BigDecimal trackLong = null;

    /**
     * Gets userId
     *
     * @return userId
     **/
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the value of userId.
     *
     * @param userId the new value
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets trackTimestamp
     *
     * @return trackTimestamp
     **/
    public String getTrackTimestamp() {
        return trackTimestamp;
    }

    /**
     * Sets the value of trackTimestamp.
     *
     * @param trackTimestamp the new value
     */
    public void setTrackTimestamp(String trackTimestamp) {
        this.trackTimestamp = trackTimestamp;
    }

    /**
     * Gets trackStatus
     *
     * @return trackStatus
     **/
    public Integer getTrackStatus() {
        return trackStatus;
    }

    /**
     * Sets the value of trackStatus.
     *
     * @param trackStatus the new value
     */
    public void setTrackStatus(Integer trackStatus) {
        this.trackStatus = trackStatus;
    }

    /**
     * Gets trackLat
     *
     * @return trackLat
     **/
    public BigDecimal getTrackLat() {
        return trackLat;
    }

    /**
     * Sets the value of trackLat.
     *
     * @param trackLat the new value
     */
    public void setTrackLat(BigDecimal trackLat) {
        this.trackLat = trackLat;
    }

    /**
     * Gets trackLong
     *
     * @return trackLong
     **/
    public BigDecimal getTrackLong() {
        return trackLong;
    }

    /**
     * Sets the value of trackLong.
     *
     * @param trackLong the new value
     */
    public void setTrackLong(BigDecimal trackLong) {
        this.trackLong = trackLong;
    }

}
