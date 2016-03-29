package com.xinyusoft.xshell.luckview.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zzy on 2016/2/21. 奖品股票
 */
public class PrizeStock implements Parcelable {

	private String name;
	/**
	 * 涨跌幅
	 */
	private double zdf;
	/**
	 * 是否涨停
	 */
	private boolean isLimitUp;
	private String symbol;
	private String time;

	public PrizeStock(String name, double zdf, boolean isLimitUp, String time, String symbol) {
		this.name = name;
		this.zdf = zdf;
		this.isLimitUp = isLimitUp;
		this.time = time;
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getZdf() {
		return zdf;
	}

	public void setZdf(double zdf) {
		this.zdf = zdf;
	}

	public boolean isLimitUp() {
		return isLimitUp;
	}

	public void setLimitUp(boolean isLimitUp) {
		this.isLimitUp = isLimitUp;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// 只写三个可以？
		dest.writeString(name);
		dest.writeString(symbol);
		dest.writeString(time);
		dest.writeDouble(zdf);
		dest.writeByte((byte)(isLimitUp ?1:0));
	}

	public static final Parcelable.Creator<PrizeStock> CREATOR = new Creator<PrizeStock>() {
		@Override
		public PrizeStock[] newArray(int size) {
			return new PrizeStock[size];
		}

		@Override
		public PrizeStock createFromParcel(Parcel in) {
			return new PrizeStock(in);
		}
	};

	public PrizeStock(Parcel in) {
		name = in.readString();
		symbol = in.readString();
		time = in.readString();
		zdf = in.readDouble();
		isLimitUp =in.readByte()!=0;
	}

}
