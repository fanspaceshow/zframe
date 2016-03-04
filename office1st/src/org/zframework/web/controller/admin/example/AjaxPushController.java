package org.zframework.web.controller.admin.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.DateUtil;
import org.zframework.core.util.observer.Observable;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.controller.BaseController;
import org.zframework.web.controller.admin.example.observer.MessageQueueObserver;

@Controller
@RequestMapping("/admin/example/ajaxpush")
public class AjaxPushController extends BaseController<Object>{
	@RequestMapping(method=RequestMethod.GET)
	public String index(){
		MessageQueue mq = MessageQueue.newInstance(getCurrentUser().getId());
		mq.deleteObservers();
		MessageQueueObserver mqob = new MessageQueueObserver();
		mq.addObserver(mqob);
		return "admin/example/ajaxpush/index";
	}
	
	@RequestMapping(value="/push",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject push(int sid,String msg){
		MessageQueue mq = MessageQueue.newInstance(getCurrentUser().getId());
		mq.sendMessage(getCurrentUser().getId(),getCurrentUser().getRealName(),sid, msg);
		return WebResult.success();
	}
	
	@RequestMapping(value="/receive",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject receive(){
		try {
			synchronized (Thread.currentThread()) {
				MessageQueue mq = MessageQueue.newInstance(getCurrentUser().getId());
				List<Message> lstMsg = mq.receiveMessage();
				if(lstMsg.size()==0){//如果没有消息
					MessageQueueObserver.addThread(getCurrentUser().getId(), Thread.currentThread());
					Thread.currentThread().wait();
				}
				lstMsg = mq.receiveMessage();
				JSONArray jArrMsg = new JSONArray();
				for(Message msg : lstMsg)
					jArrMsg.add(new JSONObject().element("msg", msg.getMsg()).element("fromId", msg.getFromId()).element("fromName", msg.getFromName()).element("sendTime", msg.getSendTime()));
				mq.clearMessage();
				lstMsg.clear();
				return new JSONObject().element("messages", jArrMsg);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return WebResult.success();
	}
}
class Message{
	private int fromId;
	private String fromName;
	private int toId;
	private String msg;
	private String sendTime;
	public int getFromId() {
		return fromId;
	}
	public void setFromId(int fromId) {
		this.fromId = fromId;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public int getToId() {
		return toId;
	}
	public void setToId(int toId) {
		this.toId = toId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
}
class MessageQueue extends Observable{
	public int sid = -1;
	private static Map<Integer,MessageQueue> mqPool = new HashMap<Integer, MessageQueue>();
	private static Map<Integer,List<Message>> msq = new LinkedHashMap<Integer,List<Message>>();
	private MessageQueue(){
		
	}
	public static MessageQueue newInstance(int uid){
		MessageQueue mq = mqPool.get(uid);
		if(mq == null){
			mq = new MessageQueue();
			mqPool.put(uid, mq);
			mq.sid = uid;
		}
		return mq;
	}
	public void sendMessage(int fromId,String fromName,int toUid,String msg){
		List<Message> lstMsg = msq.get(toUid);
		if(lstMsg == null)
			lstMsg = new ArrayList<Message>();
		//设置消息实体
		Message message = new Message();
		message.setFromId(fromId);
		message.setFromName(fromName);
		message.setToId(toUid);
		message.setMsg(msg);
		message.setSendTime(DateUtil.getDateTime(new Date()));
		lstMsg.add(message);
		msq.put(toUid, lstMsg);
		this.setChanged();
		JSONObject jMsg = new JSONObject();
		jMsg.element("type", "send");
		jMsg.element("toUid", toUid);
		this.notifyObservers(jMsg);
	}
	/**
	 * 接受消息
	 * @return
	 */
	public List<Message> receiveMessage(){
		List<Message> msg = new ArrayList<Message>();
		Iterator<Integer> iter = msq.keySet().iterator();
		while(iter.hasNext()){
			Integer sid = iter.next();
			if(this.sid == sid){
				msg = msq.get(this.sid);
				break;
			}
		}
		return msg;
	}
	public void clearMessage(){
		msq.remove(this.sid);
	}
}
