// Maps the URIs to resources
App.Router.map(function() {
	this.resource('configs', function() {
		this.resource('config', {
			path : ':fileName'
		});
	});
	this.route('logs');
	//	this.resource('about');
	//	this.resource('posts', function() {
	//		this.resource('post', {  path: ':post_id' });
	//	});	
});

App.ConfigsRoute = Ember.Route.extend({
	
	setupController : function(controller, model) {
		// Call _super for default behavior
		this._super(controller, model);
		// Now check if any of the files in the model is selected
		for (var i=0; i < model.length; i++) {
			var file = model[i];
			if (file.get('isSelected')) {
				// alert('File already selected: ' + file.get("fileName"));
				this.transitionTo('config', file);
			}
		}
	},
	
	model : function() {
		return App.ConfigsModel;
	}
});

App.ConfigRoute = Ember.Route.extend({
	// Add the logic to handle the transition to a new File selection
	setupController : function(controller, model) {
		// Call _super for default behavior
		this._super(controller, model);
		this.controllerFor('configs').send('clearSelections');
		controller.set('isSelected', true);
	},

	//activate: function() {
	// Clear all selected files
	//this.controllerFor('configs').clearSelections();
	// And set the current file with the new state
	//this.controllerFor('config').set('selected', true);
	//}, 

	// For the route, we actually pass the file_id via the URL 
	model : function(params) {
		// Returns the object for which the id matches the file_id specified in the URL
		var result = App.ConfigsModel.filter(function(element) {
			return element.fileName === params.fileName;
		});
		return result;
	}
});

App.LogsRoute = Ember.Route.extend({

	// This is called upon exiting the Route
	deactivate : function() {
		this.get('pollster').stop();
	},

	setupController : function(controller, model) {
		this._super(controller, model);
		if (Ember.isNone(this.get('pollster'))) {
			this.set('pollster', App.Polster.create({
				controller : controller, 
				onPoll : function() {
					// alert("Polling...");
					controller.send('getLogs');
				}
			}));
		}
		this.get('pollster').start();
	},

	model : function() {
		//alert("Getting model for logs " + App.LogsModel.length);
		return App.LogsModel;
		//return App.ConfigsModel;
	}
});