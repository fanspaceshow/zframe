package org.zframework.web.controller.admin.example.observer;

import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import org.zframework.core.util.observer.Observable;
import org.zframework.core.util.observer.Observer;

public class MessageQueueObserver implements Observer{
	private static Map<Integer,Thread> threadPool = new HashMap<Integer, Thread>();
	public static void addThread(int uid,Thread thread){
		Thread t = threadPool.get(uid);
		if(t != null && !t.isAlive()){
			synchronized (t) {
				t.notify();
			}
		}
		threadPool.put(uid, thread);
	}
	@Override
	public void update(Observable obs, Object arg) {
		JSONObject obj = (JSONObject) arg;
		if(obj.get("type").equals("send")){
			int uid = obj.getInt("toUid");
			Thread thread = threadPool.get(uid);
			if(thread == null)
				return;
			synchronized (thread) {
				threadPool.get(uid).notify();
			}
		}
	}

}
