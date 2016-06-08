'use strict';

app.service('UploadService', function($http) {
  return {
    upload: function(file) {
      var formData = new FormData();
      
      formData.append('file', file);
      
      $http.post('http://52.58.134.150/rest-api/verify', formData, {
          transformRequest: angular.identity,
          headers: {'Content-Type': undefined}
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
          $('.result').text(err);
        });
    }
  };
});