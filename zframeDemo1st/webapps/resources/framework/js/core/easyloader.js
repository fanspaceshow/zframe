(function(){
	var modules = {
		draggable:{
			js:'zframe.draggable.js'
		},
		droppable:{
			js:'zframe.droppable.js'
		},
		resizable:{
			js:'zframe.resizable.js'
		},
		linkbutton:{
			js:'zframe.linkbutton.js',
			css:'linkbutton.css'
		},
		progressbar:{
			js:'zframe.progressbar.js',
			css:'progressbar.css'
		},
		pagination:{
			js:'zframe.pagination.js',
			css:'pagination.css',
			dependencies:['linkbutton']
		},
		datagrid:{
			js:'zframe.datagrid.js',
			css:'datagrid.css',
			dependencies:['panel','resizable','linkbutton','pagination']
		},
		treegrid:{
			js:'zframe.treegrid.js',
			css:'tree.css',
			dependencies:['datagrid']
		},
		propertygrid:{
			js:'zframe.propertygrid.js',
			css:'propertygrid.css',
			dependencies:['datagrid']
		},
		panel: {
			js:'zframe.panel.js',
			css:'panel.css'
		},
		window:{
			js:'zframe.window.js',
			css:'window.css',
			dependencies:['resizable','draggable','panel']
		},
		dialog:{
			js:'zframe.dialog.js',
			css:'dialog.css',
			dependencies:['linkbutton','window']
		},
		messager:{
			js:'zframe.messager.js',
			css:'messager.css',
			dependencies:['linkbutton','window','progressbar']
		},
		layout:{
			js:'zframe.layout.js',
			css:'layout.css',
			dependencies:['resizable','panel']
		},
		form:{
			js:'zframe.form.js'
		},
		menu:{
			js:'zframe.menu.js',
			css:'menu.css'
		},
		tabs:{
			js:'zframe.tabs.js',
			css:'tabs.css',
			dependencies:['panel','linkbutton']
		},
		splitbutton:{
			js:'zframe.splitbutton.js',
			css:'splitbutton.css',
			dependencies:['linkbutton','menu']
		},
		menubutton:{
			js:'zframe.menubutton.js',
			css:'menubutton.css',
			dependencies:['linkbutton','menu']
		},
		accordion:{
			js:'zframe.accordion.js',
			css:'accordion.css',
			dependencies:['panel']
		},
		calendar:{
			js:'zframe.calendar.js',
			css:'calendar.css'
		},
		combo:{
			js:'zframe.combo.js',
			css:'combo.css',
			dependencies:['panel','validatebox']
		},
		combobox:{
			js:'zframe.combobox.js',
			css:'combobox.css',
			dependencies:['combo']
		},
		combotree:{
			js:'zframe.combotree.js',
			dependencies:['combo','tree']
		},
		combogrid:{
			js:'zframe.combogrid.js',
			dependencies:['combo','datagrid']
		},
		validatebox:{
			js:'zframe.validatebox.js',
			css:'validatebox.css'
		},
		numberbox:{
			js:'zframe.numberbox.js',
			dependencies:['validatebox']
		},
		searchbox:{
			js:'zframe.searchbox.js',
			css:'searchbox.css',
			dependencies:['menubutton']
		},
		spinner:{
			js:'zframe.spinner.js',
			css:'spinner.css',
			dependencies:['validatebox']
		},
		numberspinner:{
			js:'zframe.numberspinner.js',
			dependencies:['spinner','numberbox']
		},
		timespinner:{
			js:'zframe.timespinner.js',
			dependencies:['spinner']
		},
		tree:{
			js:'zframe.tree.js',
			css:'tree.css',
			dependencies:['draggable','droppable']
		},
		datebox:{
			js:'zframe.datebox.js',
			css:'datebox.css',
			dependencies:['calendar','combo']
		},
		datetimebox:{
			js:'zframe.datetimebox.js',
			dependencies:['datebox','timespinner']
		},
		slider:{
			js:'zframe.slider.js',
			dependencies:['draggable']
		},
		parser:{
			js:'zframe.parser.js'
		}
	};
	
	var locales = {
		'en':'zframe.ui-lang-en.js',
		'zh_CN':'zframe.ui-lang-zh_CN.js',
		'zh_TW':'zframe.ui-lang-zh_TW.js'
	};
	
	var queues = {};
	
	function loadJs(url, callback){
		var done = false;
		var script = document.createElement('script');
		script.type = 'text/javascript';
		script.language = 'javascript';
		script.src = url;
		script.onload = script.onreadystatechange = function(){
			if (!done && (!script.readyState || script.readyState == 'loaded' || script.readyState == 'complete')){
				done = true;
				script.onload = script.onreadystatechange = null;
				if (callback){
					callback.call(script);
				}
			}
		}
		document.getElementsByTagName("head")[0].appendChild(script);
	}
	
	function runJs(url, callback){
		loadJs(url, function(){
			document.getElementsByTagName("head")[0].removeChild(this);
			if (callback){
				callback();
			}
		});
	}
	
	function loadCss(url, callback){
		var link = document.createElement('link');
		link.rel = 'stylesheet';
		link.type = 'text/css';
		link.media = 'screen';
		link.href = url;
		document.getElementsByTagName('head')[0].appendChild(link);
		if (callback){
			callback.call(link);
		}
	}
	
	function loadSingle(name, callback){
		queues[name] = 'loading';
		
		var module = modules[name];
		var jsStatus = 'loading';
		var cssStatus = (easyloader.css && module['css']) ? 'loading' : 'loaded';
		
		if (easyloader.css && module['css']){
			if (/^http/i.test(module['css'])){
				var url = module['css'];
			} else {
				var url = easyloader.base + 'themes/' + easyloader.theme + '/' + module['css'];
			}
			loadCss(url, function(){
				cssStatus = 'loaded';
				if (jsStatus == 'loaded' && cssStatus == 'loaded'){
					finish();
				}
			});
		}
		
		if (/^http/i.test(module['js'])){
			var url = module['js'];
		} else {
			var url = easyloader.base + 'plugins/' + module['js'];
		}
		loadJs(url, function(){
			jsStatus = 'loaded';
			if (jsStatus == 'loaded' && cssStatus == 'loaded'){
				finish();
			}
		});
		
		function finish(){
			queues[name] = 'loaded';
			easyloader.onProgress(name);
			if (callback){
				callback();
			}
		}
	}
	
	function loadModule(name, callback){
		var mm = [];
		var doLoad = false;
		
		if (typeof name == 'string'){
			add(name);
		} else {
			for(var i=0; i<name.length; i++){
				add(name[i]);
			}
		}
		
		function add(name){
			if (!modules[name]) return;
			var d = modules[name]['dependencies'];
			if (d){
				for(var i=0; i<d.length; i++){
					add(d[i]);
				}
			}
			mm.push(name);
		}
		
		function finish(){
			if (callback){
				callback();
			}
			easyloader.onLoad(name);
		}
		
		var time = 0;
		function loadMm(){
			if (mm.length){
				var m = mm[0];	// the first module
				if (!queues[m]){
					doLoad = true;
					loadSingle(m, function(){
						mm.shift();
						loadMm();
					});
				} else if (queues[m] == 'loaded'){
					mm.shift();
					loadMm();
				} else {
					if (time < easyloader.timeout){
						time += 10;
						setTimeout(arguments.callee, 10);
					}
				}
			} else {
				if (easyloader.locale && doLoad == true && locales[easyloader.locale]){
					var url = easyloader.base + 'locale/' + locales[easyloader.locale];
					runJs(url, function(){
						finish();
					});
				} else {
					finish();
				}
			}
		}
		
		loadMm();
	}
	
	easyloader = {
		modules:modules,
		locales:locales,
		
		base:'.',
		theme:'default',
		css:true,
		locale:null,
		timeout:2000,
	
		load: function(name, callback){
			if (/\.css$/i.test(name)){
				if (/^http/i.test(name)){
					loadCss(name, callback);
				} else {
					loadCss(easyloader.base + name, callback);
				}
			} else if (/\.js$/i.test(name)){
				if (/^http/i.test(name)){
					loadJs(name, callback);
				} else {
					loadJs(easyloader.base + name, callback);
				}
			} else {
				loadModule(name, callback);
			}
		},
		
		onProgress: function(name){},
		onLoad: function(name){}
	};

	var scripts = document.getElementsByTagName('script');
	for(var i=0; i<scripts.length; i++){
		var src = scripts[i].src;
		if (!src) continue;
		var m = src.match(/easyloader\.js(\W|$)/i);
		if (m){
			easyloader.base = src.substring(0, m.index);
		}
	}

	window.using = easyloader.load;
	
	if (window.jQuery){
		jQuery(function(){
			easyloader.load('parser', function(){
				jQuery.parser.parse();
			});
		});
	}
	
})();
