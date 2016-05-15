'use strict';

app.controller('UploadCtrl', function(UploadService, $scope) {
  $scope.upload = function() {
    UploadService.upload($scope.file);
    $scope.resetDropzone();
  };
});