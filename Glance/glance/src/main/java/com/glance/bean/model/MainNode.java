package com.glance.bean.model;

import java.util.ArrayList;


public class MainNode extends GlanceBaseBean{//implements Parcelable
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5418712475615659884L;
	private String nodeKey;
	private String nodeTitle;
	private String criteria;
	private ArrayList<SubNodes> subNodes;
	
	
	public MainNode()
	{
		
	}
	
	public void setNodeKey(String nodeKey){
		this.nodeKey=nodeKey;
	}
	
    public String getNode()
    {
    	return nodeKey;
    }
    
    public void setnodeTitle(String nodeTitle){
		this.nodeTitle=nodeTitle;
	}
    
    public String getNodeTitle()
    {
    	return nodeTitle;
    }
    public void setCriteria(String criteria){
		this.criteria=criteria;
	}
    
    public String getCriteria()
    {
    	return criteria;
    }
    public void setSubNodes(ArrayList<SubNodes> subNodes){
		this.subNodes=subNodes;
	}
    
    public ArrayList<SubNodes> getSubNodes()
    {
    	return subNodes;
    }

	
	/*@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(nodeKey);
		dest.writeString(nodeTitle);
		Bundle b = new Bundle();
		b.putParcelableArrayList("subNodes", subNodes);
		dest.writeBundle(b);
	}
	public static final Parcelable.Creator<MainNode> CREATOR = 
			new Parcelable.Creator<MainNode>() { 
			public MainNode createFromParcel(Parcel in) { 
				MainNode mn = new MainNode();
				mn.nodeKey = in.readString();
				mn.nodeTitle = in.readString();
				Bundle b = in.readBundle(SubNodes.class.getClassLoader());        
				mn.subNodes = b.getParcelableArrayList("subNodes");

			return mn;
			}

			@Override
			public MainNode[] newArray(int size) {
			return new MainNode[size];
			}
			};*/
	
}
