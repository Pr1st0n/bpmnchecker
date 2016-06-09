'use strict';

app.service('UploadService', function($http) {
  return {
    upload: function(file) {
      var formData = new FormData();
      
      formData.append('file', file);
      
      $http.post('http://localhost:8095/verify', formData, {
          transformRequest: angular.identity,
          headers: {'Content-Type': undefined},
          timeout: 10 * 1000
        })
        .success(function(data) {
          var $resultForm = $('.result-form');

          $resultForm.empty();

          if (data.length === 0) {
            $resultForm.append('<li>Model is valid!</li>');
            $resultForm.find('li').css({ color: 'green' });
          }
          else {
            _.each(data, res => {
              $resultForm.append('<li>Id: ' + res.entityId + '</li>');
              $resultForm.append('<li>Error type: ' + res.type + '</li>');
              $resultForm.append('<li>Discription: ' + res.message + '\n</li>');
              $resultForm.append('<hr />');

              $resultForm.css('color', 'red');
            });
          }
        })
        .error(function(err) {
          var errMessage = err ? err.message : 'Timeout occurred during waiting for the request';
          var $resultForm = $('.result-form');

          $resultForm.empty();

          $resultForm.append('<li>Server error: ' + errMessage + '</li>');
          $resultForm.find('li').css({ color: 'red' });
        });
    }
  };
});