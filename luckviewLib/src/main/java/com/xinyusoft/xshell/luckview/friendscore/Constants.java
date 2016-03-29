/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.xinyusoft.xshell.luckview.friendscore;
/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * 常量类
 */
public final class Constants {

	// 一堆图片链接
	public static final String[] IMAGES = new String[] {
			// Heavy images
			"http://b.hiphotos.baidu.com/image/pic/item/fc1f4134970a304e9ce8639bd6c8a786c8175c8d.jpg",
			"http://www.bz55.com/uploads/allimg/120627/1-12062G00Q9.jpg",
			"http://pic10.nipic.com/20101018/1412106_135131406000_2.jpg",
			"http://pic12.nipic.com/20101231/1412106_093907138000_2.jpg",
			"http://pic12.nipic.com/20101231/1412106_093844410000_2.jpg",
			"http://wx.qlogo.cn/mmopen/ajNVdqHZLLBXAW6GbpLiaNsNa4NTWnOwSUsC0j2bov7Jd5atGtYqvlBzUdP6UrlR4kFQKLHqwKD8v14iaLGJialdg/0"
	};

	private Constants() {
	}

	// 配置
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	// 额外类
	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}
}