package com.jbb.library_common.basemvp;

import android.app.Activity;

import com.jbb.library_common.comfig.KeyContacts;
import com.jbb.library_common.utils.log.LogUtil;

import java.util.Stack;

/**
 * activity管理类，防止activity跳转混乱
 *
 */
public class ActivityManager {
	/**
	 * 接收activity的Stack
	 */
	private static Stack<Activity> activityStack = null;
	private static ActivityManager activityManager = null;

	//记录是从u3d进入习惯养成还是从原生进入1说明是从原生进入,2说明是从u3d进入
	private int platformSource = 1;

	private int appStatus = KeyContacts.STATUS_FORCE_KILLED; //默认为被后台回收了

	private ActivityManager() {
	}

	/**
	 * 单实�?
	 * 
	 * @return
	 */
	public static ActivityManager getInstance() {
		if (activityManager == null) {
			synchronized (ActivityManager.class){
				if(activityManager == null){
					activityManager = new ActivityManager();
				}
			}
		}
		return activityManager;
	}

	public int getPlatformSource() {
		return platformSource;
	}

	public void setPlatformSource(int platformSource) {
		this.platformSource = platformSource;
	}

	/**
	 * 将activity移出�?
	 * 
	 * @param activity
	 */
	public void popActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
		}
	}

	/**
	 * 结束指定activity
	 * 
	 * @param activity
	 */
	public void endActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}

	/**
	 * 获得当前的activity(即最上层)
	 * 
	 * @return
	 */
	public Activity currentActivity() {
		Activity activity = null;
		if (!activityStack.empty())
			activity = activityStack.lastElement();
		return activity;
	}
	/**
	 * 获得当前的activity(即当前activity的上一层)
	 *
	 * @return
	 */
	public Activity lastActivity() {
		Activity activity = null;
		if (!activityStack.empty())
			if(activityStack.size()==1){
				return null;
			}
//			System.out.println("pure name "+activityStack.size());
//			System.out.println("pure name "+activityStack.get(activityStack.size()-2).getLocalClassName());
			activity = activityStack.get(activityStack.size()-2);
		return activity;
	}

	/**
	 * 将activity推入栈内
	 * 
	 * @param activity
	 */
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 弹出除cls外的�?有activity
	 * 
	 * @param cls
	 */
	public void popAllActivityExceptOne(Class<? extends Activity> cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}

	/**
	 * 结束除cls之外的所有activity,执行结果都会清空Stack
	 * 
	 * @param cls
	 */
	public void finishAllActivityExceptOne(Class<? extends Activity> cls) {
		while (!activityStack.empty()) {
			Activity activity = currentActivity();
			if (activity.getClass().equals(cls)) {
				popActivity(activity);//这个是只移出队列
			} else {
				endActivity(activity);//这个是既移出队列又销毁实例
			}
		}
	}

	/**
	 * 结束�?有activity
	 */
	public void finishAllActivity() {
		while (!activityStack.empty()) {
			Activity activity = currentActivity();
			endActivity(activity);
		}
	}

	public int getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(int appStatus) {
		this.appStatus = appStatus;
	}
}