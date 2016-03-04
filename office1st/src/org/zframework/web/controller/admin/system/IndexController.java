package org.zframework.web.controller.admin.system;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.URLName;

import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.DateUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Resource;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.UserService;
@Controller
@RequestMapping("/admin/index")
public class IndexController extends BaseController<Object>{
	@Autowired
	private UserService userService;
	@Autowired
	private LogService logService;
	/**
	 * 加载用户资源
	 * @param model
	 * @param theme
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model,@RequestParam(required=false,value="theme")String theme){
		User user = getCurrentUser();
		if(ObjectUtil.isNotNull(user)){
			Map<Integer,List<Resource>> userResMap = new HashMap<Integer, List<Resource>>();
			List<Resource> resources = user.getResources();
			List<Resource> firstRes = new ArrayList<Resource>();
			for(Resource res : resources){
				if(res.getParentId()==0){
					firstRes.add(res);
					userResMap.put(res.getId(), getChildRes(resources, res.getId()));
				}
			}
			model.addAttribute("firstRes",firstRes);
			model.addAttribute("resMap",userResMap);
		}
		return "admin/system/index";
	}
	/**
	 * 加载用户资源
	 * @param model
	 * @param theme
	 * @return
	 */
	@RequestMapping(value="/v",method={RequestMethod.GET})
	public String index_v(Model model,@RequestParam(required=false,value="theme")String theme){
		User user = getCurrentUser();
		if(ObjectUtil.isNotNull(user)){
			Map<Integer,List<Resource>> userResMap = new HashMap<Integer, List<Resource>>();
			List<Resource> resources = user.getResources();
			List<Resource> firstRes = new ArrayList<Resource>();
			for(Resource res : resources){
				if(res.getParentId()==0){
					firstRes.add(res);
					userResMap.put(res.getId(), getChildRes(resources, res.getId()));
				}
			}
			model.addAttribute("firstRes",firstRes);
			model.addAttribute("resMap",userResMap);
			model.addAttribute("layout","-v");
		}
		return "admin/system/index-v";
	}
	/**
	 * 获取子资源
	 * @param resources
	 * @param parentId
	 * @return
	 */
	private List<Resource> getChildRes(List<Resource> resources,Integer parentId){
		List<Resource> childRes = new ArrayList<Resource>();
		for(Resource res : resources){
			if(res.getParentId() == parentId){
				childRes.add(res);
			}
		}
		return childRes;
	}
	@RequestMapping(value="/welcome",method={RequestMethod.GET})
	public String welcome(Model model){
		return "admin/system/welcome";
	}
	@RequestMapping(value="/changeThemes",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doChangeThemes(String themeName){
		JSONObject jResult = new JSONObject();
		User user = getCurrentUser();
		user.setPageStyle(themeName);
		userService.update(user);
		jResult.element("result", "success");
		return jResult;
	}
	@RequestMapping(value="/frameOut",method={RequestMethod.GET})
	public String frameOut(Model model,String url,String width,String height){
		model.addAttribute("url", url);
		model.addAttribute("height", height);
		return "admin/system/jump";
	}
	/**
	 * 获取天气信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/weather",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> weather(){
		Map<String,Object> map = new HashMap<String, Object>();
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		String url = "http://www.weather.com.cn/html/weather/101190101.shtml";
		try {
			Document root = Jsoup.connect(url).get();
			Elements lstYubao = root.getElementsByClass("yuBaoTable");
			for(int i=0;i<lstYubao.size();i++){
				JSONObject obj = new JSONObject();
				obj.element("city","合肥");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				Date date = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.DATE, i);
				obj.element("time", sdf.format(cal.getTime()));
				Element yubao = lstYubao.get(i);
				Elements tr = yubao.getElementsByTag("tr");
				if(tr.size() == 2){//白天和晚上
					Elements td1 =  tr.get(0).getElementsByTag("td");
					Elements td2 =  tr.get(1).getElementsByTag("td");
					obj.element("weather", td1.get(3).text().trim()+"转"+td2.get(2).text().trim());
					obj.element("c", td1.get(4).text().trim().substring(2)+"~"+td2.get(3).text().substring(2));
					obj.element("wind", td1.get(5).text().trim()+td1.get(6).text().trim()+"转"+td2.get(4).text().trim()+td2.get(5).text().trim());
				}else{//全天
					Elements td =  tr.get(0).getElementsByTag("td");
					obj.element("weather", td.get(3).text().trim());
					obj.element("c", td.get(4).text().trim().substring(2));
					obj.element("wind", td.get(5).text().trim());
				}
				lstJson.add(obj);
			}
			map.put("rows", lstJson);
			map.put("total", lstJson.size());
			getApplicationCache().put("MapWeatherInfo", map);
			logService.recordInfo("首页 - 生活方式", "获取天气信息 成功", getCurrentUser().getLoginName(), getRequestAddr());
		} catch (IOException e) {
			e.printStackTrace();
			logService.recordError("首页 - 生活方式", "获取天气信息失败 "+e.getMessage()+"，使用缓存数据。", getCurrentUser().getLoginName(), getRequestAddr());
			Object weatherCache = getApplicationCache().get("MapWeatherInfo");
			if(ObjectUtil.isNotNull(weatherCache))
				map = (Map<String, Object>) weatherCache;
		}
		return map;
	}
	/**
	 * 翻译中英互译
	 * @param type zh为中文->英文 en为英文->中文
	 * @return
	 */
	@RequestMapping(value="/translate",method={RequestMethod.POST})
	@ResponseBody
	public String translate(String type,String val){
		String result = "";
		if(type.equals("zh")){
			try {
				Document root = Jsoup.connect("http://openapi.baidu.com/public/2.0/bmt/translate?from=zh&to=en&q={0}&client_id=3rkcOR0IoYNCQ4x0jynVDYC0".replace("{0}", URLEncoder.encode(val,"UTF-8"))).ignoreContentType(true).get();
				JSONObject json = JSONObject.fromObject(root.text());
				if(json.get("error_code")!=null){
					result = "中译英 翻译错误!";
				}
				else
					result = json.getJSONArray("trans_result").getJSONObject(0).getString("dst");
			} catch (IOException e) {
				e.printStackTrace();
				result = "中译英 翻译错误!";
			}
		}else{
			try {
				Document root = Jsoup.connect("http://openapi.baidu.com/public/2.0/bmt/translate?from=en&to=zh&q={0}&client_id=3rkcOR0IoYNCQ4x0jynVDYC0".replace("{0}", URLEncoder.encode(val,"UTF-8"))).ignoreContentType(true).get();
				JSONObject json = JSONObject.fromObject(root.text());
				if(json.get("error_code")!=null)
					result = "英译中 翻译错误!";
				else
					result = json.getJSONArray("trans_result").getJSONObject(0).getString("dst");
			} catch (IOException e) {
				e.printStackTrace();
				result = "英译中 翻译错误!";
			}
		}
		return result;
	}
	@RequestMapping(value="/email/receive",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> email_receive(){
		Map<String,Object> map = new HashMap<String, Object>();
		List<JSONObject> list = new ArrayList<JSONObject>();
		// 准备连接服务器的会话信息 
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";  
		// Get a Properties object  
		Properties props = System.getProperties();  
		props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);  
		props.setProperty("mail.pop3.socketFactory.fallback", "false");  
		props.setProperty("mail.pop3.port", "995");  
		props.setProperty("mail.pop3.socketFactory.port", "995");  
		props.setProperty("mail.pop3.disabletop", "true");  
		props.setProperty("mail.pop3.ssl.enable", "true");  
		props.setProperty("mail.pop3.useStartTLS", "true"); 
		Store store = null;
		Folder inbox = null;
        try {
        	//连接服务器
        	Session session = Session.getInstance(props);
        	URLName urlName = new URLName("pop3", "pop.qq.com", 995, null, "1077020759@qq.com", "15855239335");
			store = session.getStore(urlName);
			System.out.println("-----------------连接服务器-------------------");
			store.connect();
			System.out.println("-----------------接受邮件---------------------");
			//获得收件箱
			inbox = store.getFolder("INBOX");
			//打开收件箱
			inbox.open(Folder.READ_ONLY);
			FetchProfile profile = new FetchProfile();  
            profile.add(FetchProfile.Item.ENVELOPE);  
//          profile.add(FetchProfile.Item.CONTENT_INFO);  
//          profile.add(FetchProfile.Item.FLAGS);  
            profile.add(UIDFolder.FetchProfileItem.UID);  
            Message[] messages = inbox.getMessages();
            for(int i=0;i<messages.length;i++){
            	Message msg = messages[i];
            	JSONObject jsonMsg = new JSONObject();
            	//发送者
            	String from = msg.getFrom()[0].toString();
            	if(from.indexOf("<")>=0){
            		from = from.substring(from.indexOf("<")+1,from.indexOf(">"));
            	}
            	jsonMsg.element("from", from);
            	//标题
            	jsonMsg.element("subject", msg.getSubject());
            	//发送时间
            	jsonMsg.element("time", DateUtil.format(msg.getSentDate(), "yyyy-MM-dd HH:mm:ss"));
            	list.add(jsonMsg);
            }
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}finally{
			try {
				inbox.close(true);
				store.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
        map.put("rows", list);
        map.put("total", list.size());
		return map;
	}
}
