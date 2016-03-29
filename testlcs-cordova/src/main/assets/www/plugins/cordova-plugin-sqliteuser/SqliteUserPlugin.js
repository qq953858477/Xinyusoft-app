cordova.define("cordova-plugin-sqliteuser.SqliteUserPlugin", function(require,
		exports, module) {

	var exec = require('cordova/exec');
	
	module.exports = {

		update : function(callBack, userJson) {
			exec(callBack, null, "SqliteUserPlugin", "Update", [ userJson ]);
		},
		deleteFramTaskGoal : function(callBack, taskGoal) {
			exec(callBack, null, "SqliteUserPlugin", "DeleteFramTaskGoal", [ {
				'taskGoal' : taskGoal
			} ]);
		},
		selectFramTaskGoalToUrm : function(callBack, taskGoal) {
			exec(callBack, null, "SqliteUserPlugin", "SelectFramTaskGoalToUrm",
					[ {
						'taskGoal' : taskGoal
					} ]);
		},
		selectToLastMessage : function(callBack, taskGoal) {
			exec(callBack, null, "SqliteUserPlugin", "SelectToLastMessage", [ {
				'taskGoal' : taskGoal
			} ]);
		},
		selectToSomeMessage: function(callBack,userJson) {
            exec(callBack, null, "SqliteUserPlugin", "SelectToSomeMessage", [ {'taskGoalList':userJson}]);
            }
	};

});
