
/*
A Wrapper around any Ajax Calls made by River Watch, Mocks calls to a web service
Author Matthew Betts
*/

/*
This is a class for producing mock data, returns from methods in this class should be formatted as json, as specified by the API
*/

(function() {
  var CommentsForIncident, GPSCoordinatesNZ, IncidentDetail, IncidentList, RWCall, UnapprovedComments;

  GPSCoordinatesNZ = [
    {
      lat: -40.405995,
      lng: 172.408714
    }, {
      lat: -38.402627,
      lng: 178.011583
    }, {
      lat: -43.529559,
      lng: 169.026211
    }, {
      lat: -46.017497,
      lng: 167.429489
    }, {
      lat: -42.353075,
      lng: 172.270042
    }, {
      lat: -41.021948,
      lng: 173.009794
    }, {
      lat: -36.414556,
      lng: 175.324181
    }, {
      lat: -35.488086,
      lng: 173.385537
    }, {
      lat: -41.021419,
      lng: 173.009429
    }, {
      lat: -35.021999,
      lng: 173.556997
    }, {
      lat: -38.59082,
      lng: 177.474014
    }, {
      lat: -41.476564,
      lng: 172.203613
    }
  ];

  /*
  Test Stub for retreiving a specific incident
  */

  IncidentDetail = function(id) {
    var data, gps;
    gps = Math.floor(Math.random() * 12);
    return data = {
      Incident_ID: id,
      Full_URL: "http://placehold.it/1600x900",
      Description: "Description for " + id,
      Tags: ["Cow", "Poo", "River", "Pond"],
      Lat: GPSCoordinatesNZ[0].lat,
      Lng: GPSCoordinatesNZ[0].lng
    };
  };

  UnapprovedComments = function(start, range) {
    var comments, data, i, _ref;
    data = {};
    data["start"] = start;
    data["range"] = range;
    console.log("start " + start);
    console.log("range " + range);
    comments = [];
    for (i = 0, _ref = range - 1; 0 <= _ref ? i <= _ref : i >= _ref; 0 <= _ref ? i++ : i--) {
      comments[i] = {
        incident_id: start + i,
        comment_id: start + i,
        thumbnail_url: "http://placehold.it/320x180",
        description: "Description for " + (start + i),
        comment: "I like the way the light reflects of these cows, love mablye"
      };
    }
    data.comments = comments;
    return data;
  };

  /*
  Test Stub for reteriving multiple incidents
  */

  IncidentList = function(start, range) {
    var data, gps, i, incidents, _ref;
    data = {};
    data["start"] = start;
    data["range"] = range;
    console.log("start " + start);
    console.log("range " + range);
    incidents = [];
    for (i = 0, _ref = range - 1; 0 <= _ref ? i <= _ref : i >= _ref; 0 <= _ref ? i++ : i--) {
      gps = Math.floor(Math.random() * 12);
      incidents[i] = {
        id: start + i,
        thumbnail_url: "http://placehold.it/320x180",
        description: "Description for " + (start + i),
        Tags: ["Cow", "Poo", "River", "Pond"],
        geolocation: {
          lat: GPSCoordinatesNZ[gps].lat + (Math.floor(Math.random() * 100) - 50) / 50,
          long: GPSCoordinatesNZ[gps].lng + (Math.floor(Math.random() * 100) - 50) / 50
        }
      };
      console.log(GPSCoordinatesNZ[gps].lat);
    }
    data["incidents"] = incidents;
    return data;
  };

  /*
  Test Stub for retreiving comments on an incident
  */

  CommentsForIncident = function(id, start, range) {
    var data;
    data = {};
    data["Incident_ID"] = id;
    data["Start"] = start;
    data["Given"] = 2;
    console.log("id " + id);
    console.log("start " + start);
    console.log("range " + range);
    data["Comments"] = [
      {
        Incident_ID: id,
        Comment_ID: start,
        Comment_Text: "This is a comment"
      }, {
        Incident_ID: id,
        Comment_ID: start + 1,
        Comment_Text: "This is another comment"
      }
    ];
    return data;
  };

  window.IncidentList = IncidentList;

  window.IncidentDetail = IncidentDetail;

  window.CommentsForIncident = CommentsForIncident;

  RWCall = function(onSuccess, onFailure, data, path, args, callType) {
    var a, aSplit, argsSplit, parsedArgs, result, _i, _len;
    parsedArgs = [];
    argsSplit = args.split("/");
    for (_i = 0, _len = argsSplit.length; _i < _len; _i++) {
      a = argsSplit[_i];
      aSplit = a.split("=");
      parsedArgs.push(aSplit[1]);
    }
    if (path === "unapproved_detail_stub") result = IncidentDetail(parsedArgs[1]);
    if (path === "unapproved_stub") {
      result = IncidentList(parsedArgs[1], parsedArgs[2]);
    }
    if (path === "comment_stub") {
      result = CommentsForIncident(parsedArgs[1], parsedArgs[2], parsedArgs[3]);
    }
    if (path === "unapproved_comments") {
      result = UnapprovedComments(parsedArgs[1], parsedArgs[2]);
    }
    return onSuccess(result);
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
