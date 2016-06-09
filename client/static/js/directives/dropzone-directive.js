'use strict';

var dropzone = function() {};

app.directive('dropzone', function() {
  return {
    restrict: 'C',
    link: function($scope, element) {
      var config = {
        url: 'http://52.58.134.150/rest-api/verify',
        maxFilesize: 100,
        paramName: 'uploadfile',
        maxThumbnailFilesize: 10,
        parallelUploads: 1,
        autoProcessQueue: false,
        acceptedFiles: '.bpmn'
      };

      var eventHandlers = {
        'addedfile': function(file) {
          var fileName = file.name.split('.');
          //Validate file format
          if (fileName[fileName.length - 1] !== 'bpmn') {
            var $resultForm = $('.result-form');

            $resultForm.empty();

            $resultForm.append('<li>' + 'Unsupported file format' + '</li>');
            $resultForm.find('li').css({ color: 'red' });
            
            return;
          }

          var fileName = file.name.split('.');
          //Validate file format
          if (fileName[fileName.length - 1] !== 'bpmn') {
            var $resultForm = $('.result-form');

            $resultForm.empty();

            $resultForm.append('<li>' + 'Unsupported file format' + '</li>');
            $resultForm.find('li').css({ color: 'red' });

            return;
          }

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