App = Ember.Application.create();

// Maps the URIs to resources
App.Router.map(function () {
	this.resource('config');
	this.resource('logs');
	this.resource('about');
	this.resource('posts', function() {
		this.resource('post', {  path: ':post_id' });
	});	
});


// Creates a map between the model variable and the view
App.PostsRoute = Ember.Route.extend ({
	model: function() {
		return posts;
	}
});

App.PostRoute = Ember.Route.extend ({
	// For the route, we actually pass the post_id via the URL 
	model: function(params) {
		// Returns the object for which the id matches the post_id specified in the URL
		return posts.findBy('id', params.post_id);
	}
});

App.ConfigRoute = Ember.Route.extend({
	model: function() {
		return configModel;
	}
});

App.LogsRoute = Ember.Route.extend({
	model: function() {
		var url = 'http://localhost:8080/escala/apis/logentries';
		return Ember.$.getJSON(url).then(function(data) {
			
			alert("Log entries received: " + JSON.stringify(data, null, 2));
			return data;
		});
	}
});

// Create a controller to handle the application's actions and state
App.PostController = Ember.ObjectController.extend({
	isEditing: false,
	
	actions: {
		edit: function() {
			this.set('isEditing', true);
		},
		
		doneEditing: function() {
			this.set('isEditing', false);
		}
	}
});


App.ConfigController = Ember.ObjectController.extend({
	loaded: false,
	
	actions: {
		openFile: function() {
			$("#fileSelector").click();
		}, 
		
		submitConfigs: function() {
			//this.set('model', configModel);
		}, 
		
		loadFile: function() {
			alert("Reloading model");
			this.set('model', null);
			this.set('model', configModel);
			this.set('loaded', true);
		}
	}
});

//Helper classes help with the formatting of data
Ember.Handlebars.helper('format-method', function(method) {
	var result = "";
	if (method != null) {
		result += method.name + " (";
		for (var i=0; i < method.params.length; i++) {
			result += method.params[i];
			if (i != method.params.length - 1) {
				result += ", "; 
			} 	
		}
		result += ")";
	}	
	return result;
});

// Helper classes help with the formatting of data
Ember.Handlebars.helper('format-date', function(date) {
	return moment(date).fromNow();
});

var showdown = new Showdown.converter();

Ember.Handlebars.helper('format-markdown', function(input) {
	return new Handlebars.SafeString(showdown.makeHtml(input));
});

function uploadFile(fileName) {
	//alert("upload " + fileName);
	if (fileName === "") {
		return;
	} else {
		// Create a new FormData object.
		var formData = new FormData();
		var selected_file = $('#fileSelector').get(0).files[0];

		//alert("New file being uploaded: " + selected_file.name);

		// Add the file to the request.
		formData.append('file', selected_file);

		// Set up the request.
		var xhr = new XMLHttpRequest();
		// Open the connection. Asynchronously = true
		xhr.open('POST', '../escala-util/FileParser', true);

		xhr.onload = function() {
			if (xhr.status === 200) {
				// File(s) uploaded.
				//alert('File uploaded successfully ' + xhr.responseText);
				var obj = jQuery.parseJSON(xhr.responseText);
				
				configModel = obj;
				
				if (configModel != null && configModel.classes.length > 0) {
					var classCount = configModel.classes.length;
					for (var i = 0; i < classCount; i++) {
						var myClass = configModel.classes[i];
						if (myClass != null && myClass.methods != null) {
							for (var j = 0; j < myClass.methods.length; j++) {
								myClass.methods[j].instrumented = false;
							}							
						}
					}
				}
				
				$('#loadFile').click();
				
				// Now check the model to see which methods are being instrumented
				getInstrumentationSettings(configModel);
				
			} else {
				alert('An error occurred!');
			}
		};

		// Send the Data.
		xhr.send(formData);
		// Reset the file selector value to always trigger a change event
		$("#fileSelector").val("");
	}
	
}

function getInstrumentationSettings(config) {
	if (config != null && config.classes.length > 0) {
		var classCount = config.classes.length;
		for (var i = 0; i < classCount; i++) {
			var myClass = config.classes[i];
			if (myClass != null && myClass.methods != null && myClass.methods.length > 0) {
				var className = myClass.name;
				var requestURL = '../escala/apis/logconfigs/' + className;
				$.ajax({
					type:'GET',
					url: requestURL,
					dataType: 'json',
					context: myClass
				}).done (function(data) {
										
					if (data != null && data.logConfigs != null) {
						alert('Iterating over methode names');
						var methodNames = new Array();
						// First read all the method names into the methodNames array
						if (data.logConfigs instanceof Array) {
							var configCount = data.logConfigs.length;
							alert('Config Count: ' + configCount);
							for (var j = 0; j < configCount; j++) {
								methodNames.push(data.logConfigs[j].methodInfo); 
							}
						} else {
							alert('Config Count: 1');
							methodNames.push(data.logConfigs.methodInfo);
						}
						
						// Then iterate over the class methods and see if they are listed in the array
						if (this.methods != null) {
							alert('Iterating over class methods');
							var methodCount = this.methods.length;
							alert('Method Count: ' + methodCount);
							for (var j = 0; j < methodCount; j++) {
								var method = this.methods[j];
								if (methodNames.indexOf(method.name) != -1) {
									// If the method name is part of the config data
									// Update the model with this info
									method.set('instrumented', true);
									alert("Found instrumented method: " + method.name);
								} else {
									method.instrumented = false;
								}
							}	
						}
						
						// After model is updated, refresh the view
						$('#loadFile').click();
					}
				});

			}				
		}
	}
}



var configModel = {
	name: 'My File',
	classes: [{
		name: 'My Class',
		methods: [{
			className: 'My Class',
			instrumented: false,
			name: 'My Method',
			params: ['String arg0', 'String arg1']
		}]
	}]
};

var posts = [{
	  id: '1',
	  title: "Rails is Omakase",
	  author: { name: "d2h" },
	  date: new Date('12-27-2012'),
	  excerpt: "There are lots of à la carte software environments in this world. Places where in order to eat, you must first carefully look over the menu of options to order exactly what you want.",
	  body: "I want this for my ORM, I want that for my template language, and let's finish it off with this routing library. Of course, you're going to have to know what you want, and you'll rarely have your horizon expanded if you always order the same thing, but there it is. It's a very popular way of consuming software.\n\nRails is not that. Rails is omakase."
	}, {
	  id: '2',
	  title: "The Parley Letter",
	  author: { name: "d2h" },
	  date: new Date('12-24-2012'),
	  excerpt: "My [appearance on the Ruby Rogues podcast](http://rubyrogues.com/056-rr-david-heinemeier-hansson/) recently came up for discussion again on the private Parley mailing list.",
	  body: "A long list of topics were raised and I took a time to ramble at large about all of them at once. Apologies for not taking the time to be more succinct, but at least each topic has a header so you can skip stuff you don't care about.\n\n### Maintainability\n\nIt's simply not true to say that I don't care about maintainability. I still work on the oldest Rails app in the world."
}];
