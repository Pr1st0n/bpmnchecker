'use strict';

var dropzone = function() {};

app.directive('dropzone', function() {
  return {
    restrict: 'C',
    link: function($scope, element) {
      var config = {
        url: 'http://localhost:8095/verify',
        maxFilesize: 100,
        paramName: 'uploadfile',
        maxThumbnailFilesize: 10,
        parallelUploads: 1,
        autoProcessQueue: false
      };

      var eventHandlers = {
        'addedfile': function(file) {
          $scope.file = file;
          if (this.files[1] !== null) {
            this.removeFile(this.files[0]);
          }
          $scope.$apply(function() {
            $scope.upload();
          });
        },

        'success': function () {
          $scope.resetDropzone();
        }

      };

      dropzone = new Dropzone(element[0], config);

      _.each(eventHandlers, function(handler, event) {
        dropzone.on(event, handler);
      });

      $scope.resetDropzone = function() {
        dropzone.removeAllFiles();
      };
    }
  };
});