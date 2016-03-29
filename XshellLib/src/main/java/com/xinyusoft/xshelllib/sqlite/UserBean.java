package com.xinyusoft.xshelllib.sqlite;

public class UserBean {
	private Integer userId;      
    private String taskGoal ;      
    private String type ;
    private String time ;
    private Integer totalNum ;
    private String lastMessage ;
    private String lastSender ;
    private Integer unReadMessageNum ;
    
    @Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{taskGoal:\""+taskGoal+"\",type:\""+type+"\",time:\""+time+"\",totalNum:\""+totalNum+"\","
				+ "lastMessage:\""+lastMessage+"\",lastSender:\""+lastSender+"\",unReadMeassageNum:\""+unReadMessageNum+"\"}";
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getTaskGoal() {
		return taskGoal;
	}
	public void setTaskGoal(String taskGoal) {
		this.taskGoal = taskGoal;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Integer getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}
	public String getLastMessage() {
		return lastMessage;
	}
	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}
	public String getLastSender() {
		return lastSender;
	}
	public void setLastSender(String lastSender) {
		this.lastSender = lastSender;
	}
	public Integer getUnReadMessageNum() {
		return unReadMessageNum;
	}
	public void setUnReadMessageNum(Integer unReadMessageNum) {
		this.unReadMessageNum = unReadMessageNum;
	}
	
	
	
	
	
    
    
    
    
}
