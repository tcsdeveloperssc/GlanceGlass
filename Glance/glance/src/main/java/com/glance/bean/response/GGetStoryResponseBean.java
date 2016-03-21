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

import java.util.ArrayList;
import java.util.List;

import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GGetStoryResponseBean extends GlanceBaseBean {

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
	
	@SerializedName("modifiedTime")
	private String modifiedTime;
	
	@SerializedName("nodeData")
	private List<NodeData> nodeData;
	
	@SerializedName("possibleCauses")
	private ArrayList<String> rootCause;
	
	
	public ArrayList<String> getRootCause() {
		return rootCause;
	}
	public void setRootCause(ArrayList<String> rootCause) {
		this.rootCause = rootCause;
	}
	
	
	
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


	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public List<NodeData> getNodeData() {
		return nodeData;
	}

	public void setNodeData(List<NodeData> nodeData) {
		this.nodeData = nodeData;
	}




	
	public class NodeData extends GlanceBaseBean{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8052072580959077310L;
		@SerializedName("nodeKey")
		private String nodeKey;
		@SerializedName("nodeTitle")
		private String nodeTitle;		
		@SerializedName("subNodes")
		private List<SubNodeBean> subNodes;
		@SerializedName("criteria")
		private  String criteria;
		
		public String getNodeKey() {
			return nodeKey;
		}
		public void setNodeKey(String nodeKey) {
			this.nodeKey = nodeKey;
		}
		public String getNodeTitle() {
			return nodeTitle;
		}
		public void setNodeTitle(String nodeTitle) {
			this.nodeTitle = nodeTitle;
		}
		public List<SubNodeBean> getSubNodes() {
			return subNodes;
		}
		public void setSubNodes(List<SubNodeBean> subNodes) {
			this.subNodes = subNodes;
		}
		public String getCriteria() {
			return criteria;
		}
		public void setCriteria(String criteria) {
			this.criteria = criteria;
		}
		public class SubNodeBean extends GlanceBaseBean{
			/**
			 * 
			 */
			private static final long serialVersionUID = 7100434727324965472L;
			@SerializedName("subNodeKey")
			private String subNodeKey;
			@SerializedName("subNodeTitle")
			private String subNodeTitle;
			@SerializedName("artifactId")
			private String artifactId;
			@SerializedName("hotSpots")
			private  List<HotSpotBean> hotSpots;
			
			@SerializedName("textSpots")
			private  List<TextSpotBean> textSpots;
			@SerializedName("checkList")
			private  String checkList;
			@SerializedName("selfHelp")
			private  String selfHelp;
			@SerializedName("colorCode")
			private  String color_code;
			
			
			
			
			
			public String getSubNodeKey() {
				return subNodeKey;
			}


			public void setSubNodeKey(String subNodeKey) {
				this.subNodeKey = subNodeKey;
			}


			public String getSubNodeTitle() {
				return subNodeTitle;
			}


			public void setSubNodeTitle(String subNodeTitle) {
				this.subNodeTitle = subNodeTitle;
			}


			public String getArtifactId() {
				return artifactId;
			}


			public void setArtifactId(String artifactId) {
				this.artifactId = artifactId;
			}


			public List<HotSpotBean> getHotSpots() {
				return hotSpots;
			}


			public void setHotSpots(List<HotSpotBean> hotSpots) {
				this.hotSpots = hotSpots;
			}




			public class HotSpotBean extends GlanceBaseBean{
				/**
				 * 
				 */
				private static final long serialVersionUID = -1108713225282584805L;
				@SerializedName("x")
				private String x;
				@SerializedName("y")
				private String y;
				@SerializedName("key")
				private String key;
				@SerializedName("radius")
				private String radius;
				@SerializedName("links")
				private  List<LinkBean> links;
				
				
				public String getX() {
					return x;
				}
				public void setX(String x) {
					this.x = x;
				}
				public String getY() {
					return y;
				}
				public void setY(String y) {
					this.y = y;
				}
				public String getKey() {
					return key;
				}
				public void setKey(String key) {
					this.key = key;
				}
				public String getRadius() {
					return radius;
				}
				public void setRadius(String radius) {
					this.radius = radius;
				}
				public List<LinkBean> getLinks() {
					return links;
				}
				public void setLinks(List<LinkBean> links) {
					this.links = links;
				}
				public class LinkBean extends GlanceBaseBean{
					
					/**
					 * 
					 */
					private static final long serialVersionUID = -7602080908861170375L;
					
					@SerializedName("artifactId")
					private String artifactId;
					@SerializedName("event")
					private String event;
					@SerializedName("type")
					private String type;
					@SerializedName("action")
					private String action;
					
					
					public String getArtifactId() {
						return artifactId;
					}
					public void setArtifactId(String artifactId) {
						this.artifactId = artifactId;
					}
					public String getEvent() {
						return event;
					}
					public void setEvent(String event) {
						this.event = event;
					}
					public String getType() {
						return type;
					}
					public void setType(String type) {
						this.type = type;
					}
					public String getAction() {
						return action;
					}
					public void setAction(String type) {
						this.action = action;
					}
					
				}
			}
			public class TextSpotBean extends GlanceBaseBean{
				/**
				 * 
				 */
				private static final long serialVersionUID = -1108713225282584805L;
				@SerializedName("x")
				private String x;
				@SerializedName("y")
				private String y;
				@SerializedName("key")
				private String key;
				@SerializedName("width")
				private String width;
				@SerializedName("height")
				private  String height;
				@SerializedName("content")
				private  String content;
				
				
				public String getX() {
					return x;
				}
				public void setX(String x) {
					this.x = x;
				}
				public String getY() {
					return y;
				}
				public void setY(String y) {
					this.y = y;
				}
				public String getKey() {
					return key;
				}
				public void setKey(String key) {
					this.key = key;
				}
				public String getWidth() {
					return width;
				}
				public void setWidth(String width) {
					this.width = width;
				}
				public String getHeight() {
					return height;
				}
				public void setHeight(String height) {
					this.height = height;
				}
				public String getContent() {
					return content;
				}
				public void setContent(String content) {
					this.content = content;
				}
				
				
			}
			public String getCheckList() {
				return checkList;
			}


			public void setCheckList(String checkList) {
				this.checkList = checkList;
			}


			public List<TextSpotBean> getTextSpots() {
				return textSpots;
			}


			public void setTextSpots(List<TextSpotBean> textSpots) {
				this.textSpots = textSpots;
			}


			public String getSelfHelp() {
				return selfHelp;
			}


			public void setSelfHelp(String selfHelp) {
				this.selfHelp = selfHelp;
			}


			public String getColor_code() {
				return color_code;
			}


			public void setColor_code(String color_code) {
				this.color_code = color_code;
			}


			
		}
		
		
	}
	

}
 