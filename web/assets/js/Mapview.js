(function() {
  var RemoveFromMap, SetupMap, changeRange, createOverlay, current, d128Button, d16Button, d256Button, d32Button, d64Button, googleMap, mapCanvas, nextButton, onFail, onSuccess, overlaysOnMap, prevButton, range, size;

  overlaysOnMap = [];

  RemoveFromMap = function() {
    var o, _i, _len;
    for (_i = 0, _len = overlaysOnMap.length; _i < _len; _i++) {
      o = overlaysOnMap[_i];
      o.setMap(null);
    }
    return overlaysOnMap = [];
  };

  SetupMap = function() {
    var elem, mapCenter, mapOptions, style;
    mapCenter = new google.maps.LatLng(-41.288889, 174.777222);
    style = 
	[
		{
			"stylers": [
			{ "visibility": "off" }
			]
		},{
			"featureType": "water",
			"elementType": "geometry.fill",
			"stylers": [
			{ "visibility": "on" },
			{ "lightness": 1 },
			{ "color": "#19232F" }
			]
		},{
			"featureType": "landscape",
			"elementType": "geometry.fill",
			"stylers": [
			{ "visibility": "on" },
			{ "color": "#B4B577" }
			]
		},{
			"featureType": "poi",
			"elementType": "geometry",
			"stylers": [
			{ "visibility": "on" },
			{ "color": "#C8DE9B" }
			]
		}
	];
    mapOptions = {
      styles: style,
      zoom: 9,
      center: mapCenter,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      disableDefaultUI: true
    };
    elem = document.getElementById('map_canvas');
    return new google.maps.Map(elem, mapOptions);
  };

  googleMap = SetupMap();

  google.maps.event.addDomListener(window, "load", googleMap);

  mapCanvas = $("#map_canvas");

  mapCanvas.css({
    height: "100%"
  });

  size = 0.05;

  current = 0;

  range = 16;

  createOverlay = function(incident) {
    var geo, imageBounds, lat, lng, overlay,
      _this = this;
    geo = incident.geolocation;
    lat = new google.maps.LatLng(geo.lat - size * 0.125, geo.long - size * 0.25);
    lng = new google.maps.LatLng(geo.lat + size * 0.125, geo.long + size * 0.25);
    imageBounds = new google.maps.LatLngBounds(lat, lng);
    overlay = new google.maps.GroundOverlay("" + incident.thumbnail_url, imageBounds);
    google.maps.event.addListener(overlay, 'click', function() {
      return window.CreateIncidentModal(incident.id);
    });
    overlay.setMap(googleMap);
    return overlaysOnMap.push(overlay);
  };

  onSuccess = function(data) {
    var incident, _i, _len, _ref, _results;
    _ref = data.incidents;
    _results = [];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      incident = _ref[_i];
      console.log("creating incident " + current);
      current++;
      _results.push(createOverlay(incident));
    }
    return _results;
  };

  onFail = function(data) {
    return console.log("Rest Call failed");
  };

  window.RWCall(onSuccess, onFail, {}, "approved", "/start=" + current + "/number=" + range, "GET");

  nextButton = $("#nextBtn");

  prevButton = $("#prevBtn");

  d16Button = $("#d16");

  d32Button = $("#d32");

  d64Button = $("#d64");

  d128Button = $("#d128");

  d256Button = $("#d256");

  nextButton.click(function() {
    RemoveFromMap();
    console.log("Clicked Next");
    return window.RWCall(onSuccess, onFail, {}, "approved", "/start=" + current + "/number=" + range, "GET");
  });

  prevButton.click(function() {
    RemoveFromMap();
    console.log("Clicked Prev");
    current = current - range > 0 ? current - range : 0;
    return window.RWCall(onSuccess, onFail, {}, "approved", "/start=" + current + "/number=" + range, "GET");
  });

  changeRange = function(newRange) {
    RemoveFromMap();
    current = current - range > 0 ? current - range : 0;
    range = newRange;
    return window.RWCall(onSuccess, onFail, {}, "approved", "/start=" + current + "/number=" + range, "GET");
  };

  d16Button.click(function() {
    return changeRange(16);
  });

  d32Button.click(function() {
    return changeRange(32);
  });

  d64Button.click(function() {
    return changeRange(64);
  });

  d128Button.click(function() {
    return changeRange(128);
  });

  d256Button.click(function() {
    return changeRange(256);
  });

  /*
  for incident in incidentList.Incidents
  
  	newarkLat = new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
  	newarkLng = new google.maps.LatLng incident.Lat + size, incident.Lng + size * 2
  	
  	borderCoordinates = [
  		new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
  		new google.maps.LatLng incident.Lat - size, incident.Lng + size * 2
  		new google.maps.LatLng incident.Lat + size, incident.Lng + size * 2
  		new google.maps.LatLng incident.Lat + size, incident.Lng - size * 2
  		new google.maps.LatLng incident.Lat - size, incident.Lng - size * 2
  	]
  	
  	border = new google.maps.Polyline {
  			path: borderCoordinates,
  			strokeColor: "000000",
  			strokeOpacity: 1,
  			strokeWeight: 1
  	}
  
  		
  	border.setMap googleMap
  */

}).call(this);
