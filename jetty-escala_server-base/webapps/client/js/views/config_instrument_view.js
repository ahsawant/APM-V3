
App.ConfigCheckbox = Ember.Checkbox.extend({
	className : null,
	method: null,
	attributeBindings: ['className', 'method'],
	
	click : function (evt) {
		// Check if the new state is checked or unchecked...
		var state = this.get('checked');
		// alert ("Checkbox Clicked " + this.get('checked') + " Class: " + this.get('className') + " Method: " + this.get('method').methodName);
		//this.set('checked', !state);
		this.get('controller').send('instrument', state, this.get('className'), this.get('method'));
	},
  	
});