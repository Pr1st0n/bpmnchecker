'use strict';

app.service('UploadService', function($http) {
  return {
    upload: function(file, uploadUrl) {
      var formData = new FormData();
      
      formData.append('file', file);
      
      $http.post('http://localhost:8095/verify', formData, {
          transformRequest: angular.identity,
          headers: {'Content-Type': undefined}
        })
        .success(function(data, status, response) {
          var $result = $('.result');

          $result.text(data.result);

          if (data.valid) {
            $result.css('color', 'green');
          }
          else {
            $result.css('color', 'red');
          }
        })
        .error(function(err) {
          $('.result').text(err);
        });
    }
  };
});