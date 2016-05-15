'use strict';

app.service('UploadService', function($http) {
  return {
    upload: function(file, uploadUrl) {
      var formData = new FormData();
      
      formData.append('file', file);
      
      $http.post('http://localhost:8095/rest-api/verify', formData, {
          transformRequest: angular.identity,
          headers: {'Content-Type': undefined}
        })
        .success(function(data, status, response) {
          console.log(data);
          console.log(status);
          console.log(response);

          $('.result').text('Result: ' + data.result);
        })
        .error(function(err) {
          console.log(err);
        });
    }
  };
});