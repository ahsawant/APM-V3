
App.Polster = Ember.Object.extend({
  controller : null,
  
  start: function(){
    this.timer = setInterval(this.onPoll.bind(this), 2000);
  },
  stop: function(){
    clearInterval(this.timer);
  },
  onPoll: function(){
    // Issue JSON request and add data to the store
  }
});