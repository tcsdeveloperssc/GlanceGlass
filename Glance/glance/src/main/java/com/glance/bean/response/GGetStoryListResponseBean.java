/**
Copyright (c) 2013, TATA Consultancy Services Limited (TCSL)
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

+ Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
+ Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
+ Neither the name of TCSL nor the names of its contributors may be 
  used to endorse or promote products derived from this software without 
  specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */

/**
22-Nov-2013
528937vnkm
Class Description
**/
package com.glance.bean.response;

import java.util.List;

import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GGetStoryListResponseBean extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1930785814151724646L;

	/**
	 * 
	 */
	
	
	@SerializedName("message")
	private String message;
	
	@SerializedName("status")
	private String status;
	
	
	
	@SerializedName("stories")
	private List<Story> stories;
	
	
	
	public String getMessage() {
		return message;
	}

	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<Story> getStories() {
		return stories;
	}


	public void setStories(List<Story> stories) {
		this.stories = stories;
	}

	public class Story extends GlanceBaseBean{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1108713225282584805L;
		@SerializedName("averageTime")
		private String averageTime;
		@SerializedName("creator")
		private String creator;
		@SerializedName("mode")
		private String mode;
		@SerializedName("modifiedTime")
		private String modifiedTime;
		@SerializedName("publishedTime")
		private String publishedTime;
		@SerializedName("storyId")
		private String storyId;
		@SerializedName("storyName")
		private String storyName;
		public String getAverageTime() {
			return averageTime;
		}
		public void setAverageTime(String averageTime) {
			this.averageTime = averageTime;
		}
		public String getCreator() {
			return creator;
		}
		public void setCreator(String creator) {
			this.creator = creator;
		}
		public String getMode() {
			return mode;
		}
		public void setMode(String mode) {
			this.mode = mode;
		}
		public String getModifiedTime() {
			return modifiedTime;
		}
		public void setModifiedTime(String modifiedTime) {
			this.modifiedTime = modifiedTime;
		}
		public String getPublishedTime() {
			return publishedTime;
		}
		public void setPublishedTime(String publishedTime) {
			this.publishedTime = publishedTime;
		}
		public String getStoryId() {
			return storyId;
		}
		public void setStoryId(String storyId) {
			this.storyId = storyId;
		}
		public String getStoryName() {
			return storyName;
		}
		public void setStoryName(String storyName) {
			this.storyName = storyName;
		}
	}




}
 