App.File = Ember.Object.extend({
	id : 0,
	fileName : '',
	isSelected : false,
	classes : []
});

App.Class = Ember.Object.extend({
	className : '',
	methods : []
});

App.Method = Ember.Object.extend({
	methodName : '',
	
	isInstrumented : false, 
	
	parameters : [],
	
});

App.LogData = Ember.Object.extend({
	id : 0,
	logData : ""
});


//Helper classes help with the formatting of data
Ember.Handlebars.helper('format-method', function(method) {
	var result = "";
	if (method != null) {
		result += method.methodName + " (";
		for (var i=0; i < method.parameters.length; i++) {
			result += method.parameters[i];			
			if (i < method.parameters.length - 1) {
				result += ", "; 
			} 	
		}
		result += ")";
	}	
	return result;
});

var showdown = new Showdown.converter();

Ember.Handlebars.helper('format-markdown', function(input) {
	return new Handlebars.SafeString(showdown.makeHtml(input));
});

App.ConfigsModel = new Array(); 
App.LogsModel = new Array(); 


//App.LogsModel.push(App.LogData.create({
//	id : 0,
//	logData : "Log 1"
//}));
//
//App.LogsModel.push(App.LogData.create({
//	logData : "Log 2"
//}));
//
//var file = App.File.create({
//	id : 1,
//	fileName: 'Parser.java',
//	isSelected : false,
//	classes: []	
//});
//
//var classs = App.Class.create({
//	className : 'com.escala.Parser', 
//	methods : [{
//		methodName: 'init1',
//		isInstrumented: true,
//		parameters: ['String arg0', 'int arg1' ]
//	}, {
//		methodName: 'init2',
//		isInstrumented: false,
//		parameters: ['String arg0', 'int arg1' ]
//	}]
//});
//
//file.classes.push(classs);
//
//App.ConfigsModel.push(file);
//
//
//file = App.File.create({
//	id : 2,
//	fileName: 'Servlet.java',
//	isSelected : false,
//	classes: []	
//});
//
//classs = App.Class.create({
//	className : 'com.escala.Servlet', 
//	methods : [{
//		methodName: 'doGet',
//		isInstrumented: false,
//		parameters: ['String arg0', 'int arg1' ]
//	}, {
//		methodName: 'doPost',
//		isInstrumented: true,
//		parameters: ['String arg0', 'int arg1' ]
//	}]
//});
//
//file.classes.push(classs);
//
//App.ConfigsModel.push(file);
