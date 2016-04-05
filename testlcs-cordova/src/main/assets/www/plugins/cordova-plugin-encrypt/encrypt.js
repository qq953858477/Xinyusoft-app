cordova.define("cordova-plugin-encrypt.encrypt", function(require, exports, module) { /*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

var exec = require('cordova/exec');

/**
 * Provides access to notifications on the device.
 */
module.exports = {
    
	encrypt: function(callback,encryptContent,password) {
        exec(callback, null, "EncryptPlugin", "encrypt", [encryptContent,password]);
    },
    decrypt: function(callback,ciphertext,password) {
        exec(callback, null, "EncryptPlugin", "decrypt", [ciphertext,password]);
    }
};

});