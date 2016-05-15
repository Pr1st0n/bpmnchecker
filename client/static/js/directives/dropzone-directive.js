'use strict';

function dropzone() {
}

app.directive('dropzone', function() {
  return {
    restrict: 'C',
    link: function($scope, element, attrs) {
      var config = {
        url: 'http://52.58.134.150/rest-api/verify',
        maxFilesize: 100,
        paramName: "uploadfile",
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

      angular.forEach(eventHandlers, function(handler, event) {
        dropzone.on(event, handler);
      });

      $scope.processDropzone = function() {
        dropzone.processQueue();
      };

      $scope.resetDropzone = function() {
        dropzone.removeAllFiles();
      };
    }
  };
});