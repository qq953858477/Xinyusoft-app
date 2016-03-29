package com.xinyusoft.xshell.luckview.bean;

/**
 * 大转盘的好友列表bean
 * 
 * @author zzy
 *
 */
public class Friend {
	String name;
	String url;
	// 抽奖次数
	int drawCount;
	// 最后抽奖时间
	String lastTime;
	// 好友的总积分
	double allScore;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDrawCount() {
		return drawCount;
	}

	public void setDrawCount(int drawCount) {
		this.drawCount = drawCount;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public double getAllScore() {
		return allScore;
	}

	public void setAllScore(double allCount) {
		this.allScore = allCount;
	}

	public Friend(String name, String url, int drawCount, String lastTime, double allScore) {
		super();
		this.name = name;
		this.url = url;
		this.drawCount = drawCount;
		this.lastTime = lastTime;
		this.allScore = allScore;
	}

	public Friend() {
	}
}
