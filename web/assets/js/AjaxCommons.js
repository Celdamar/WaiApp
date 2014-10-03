
/*
A Wrapper around any Ajax Calls made by River Watch
Author Matthew Betts
*/

(function() {
  var BASE_URL, CONTENT_TYPE, DATA_TYPE, PROCESS_DATA, RWCall, TIMEOUT;

  BASE_URL = "http://api.riverwatch.co.nz/wainz/";

  CONTENT_TYPE = "contentType";

  PROCESS_DATA = false;

  TIMEOUT = 5000;

  DATA_TYPE = "jsonp";

  /* REST CALLS
  '/wainz/reset_db'
  '/wainz/?'
  '/wainz/submit/?'
  '/wainz/incident/:id/comment/?'
  '/wainz/all'
  
  Submit incidents
  { "description":"Blah", "tags":["cow", "poo"], "geolocation":{"lat":"1","long":"1"}, "physical_location":"NWENLAB" }
  '/wainz/submit/?'
  
  Submit comments
  { "comment": "This is a comment With a name and email", "name": "David Tredger", "email": "tredger@gmail.com" }
  '/wainz/incident/:id/comment/?'
  
  '/wainz/approved/:start/:number?'
  '/wainz/unapproved/:start/:number'
  '/wainz/unapproved_stub/:start?/:number'
  '/wainz/incident/:id/comments/?'
  '/wainz/incident/:id/?'
  '/wainz/image/:id/full/?'
  '/wainz/image/:id/?'
  '/wainz/image/:id/thumb/?'
  '/wainz/incident/:id/approve/?'
  '/wainz/incident/:id/approve/?'
  '/wainz/comment/:id/approve/?'
  '/wainz/comment/:id/approve/?'
  '/wainz/comments/unapproved/?'
  */

  /*
  Make an AJAX Put request to the River Watch Server
  
  	onSuccess (data) - function to execute on a successfull put, data is the responce from the server
  	onFailure (data) - function to execute on a failed put, data is the resonce from the server
  	data - Data the send to the server, this should be a map of maps\objects\primitives
  	path - the path to add onto the url for the requested service
  */

  RWCall = function(onSuccess, onFailure, data, path, args, callType) {
    var ajaxLoader, dataType, json;
    ajaxLoader = $("#ajaxLoader");
    ajaxLoader.css("display", "block");
    json = JSON.stringify(data);
    dataType = "jsonp";
    console.log("Call:" + path + "| callType:" + callType + "|json:" + json);
    if (callType === "GET") {
      return $.ajax({
        url: BASE_URL + path + args,
        dataType: dataType,
        type: callType,
        success: function(msg) {
          ajaxLoader.css("display", "none");
          return onSuccess(msg);
        },
        error: function(msg) {
          ajaxLoader.css("display", "none");
          return onFailure(msg);
        }
      });
    } else if (callType === "POST") {
      return $.ajax({
        url: BASE_URL + path + args,
        dataType: "json",
        type: "POST",
        data: json,
        success: function() {
          ajaxLoader.css("display", "none");
          return onSuccess();
        },
        error: function() {
          ajaxLoader.css("display", "none");
          return onFailure();
        }
      });
    }
  };

  /*
  Make an AJAX Gete request to the River Watch Server
  
  	onSuccess (data) - function to execute on a successfull get, data is the responce from the server
  	onFailure (data) - function to execute on a failed get, data is the resonce from the server
  	args - Arguments to send to the server, this should be a map of keys to string values
  	path - The path to add onto the url for the requested service
  */

  /*
  Save calls, so that can be accessed globaly across the website.
  */

  window.RWCall = RWCall;

}).call(this);
