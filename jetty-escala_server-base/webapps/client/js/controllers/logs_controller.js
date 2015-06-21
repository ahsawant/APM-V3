
App.LogsController = Ember.ArrayController.extend({
	logId : 0,
	firstTime : true,
	sortProperties : ['id'],
	sortAscending : false,
	searchQuery : "",
	
	actions : {
		getLogs : function() {
			var firstRun = this.get('firstTime');
			if (firstRun) {
				// Fetch all the logs from the server and add it to the model
				// Let's update the server with the new method instrumentation
				var getUrl = baseLogsUrl;
				
				// Issue the ajax request to the server
				$.ajax({
					  url: getUrl,
					  type: "GET",
					  data: "",
					  processData: false,  // tell jQuery not to process the data
					  contentType: false,  // tell jQuery not to set contentType
					  dataType: 'json',
					  context: this,
					  success: function (data) {	
						 this.set('firstTime', false);
						 processLogEntries(this, data);
					  },		  
					  error: function(XMLHttpRequest, textStatus, errorThrown) {						  
						  alert("Failed receive logs!");  
					  }    
					});
				
			} else {
				// Fetch only the new logs...
				var getUrl = baseLogsModifiedUrl;
				
				// Issue the ajax request to the server
				$.ajax({
					  url: getUrl,
					  type: "GET",
					  data: "",
					  processData: false,  // tell jQuery not to process the data
					  contentType: false,  // tell jQuery not to set contentType
					  dataType: 'json',
					  context: this,
					  success: function (data) {	
						  processLogEntries(this, data);
					  },		  
					  error: function(XMLHttpRequest, textStatus, errorThrown) {						  
						  alert("Failed to receive logs!");  
					  }    
					});
			}
		}		
	}
});

function processLogEntries(controller, data) {
	if (data != null && data.logEntries != null) {
		  var logEntries = data.logEntries;
		  var logId = controller.get('logId');
		  //alert("Received " + count + " logs");
		  var model = controller.get('model');
		  
		  if (logEntries instanceof Array) {
			  var count = logEntries.length;
			  for (var i = 0; i < count; i++) {
				  model.pushObject(App.LogData.create({
					  id : logId,
					  logData : logEntries[i].logData
				  }));
				  logId++;
			  }
		  } else {
			  model.pushObject(App.LogData.create({
					id : logId,
					logData : logEntries.logData
				}));
			  logId++;
		  }
		  controller.set('logId', logId);
	  } 
}
