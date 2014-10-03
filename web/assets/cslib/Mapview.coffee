# Set up the Google map 

overlaysOnMap = []

RemoveFromMap = () ->
	for o in overlaysOnMap
		o.setMap null
		
	overlaysOnMap = []

SetupMap = () ->
	
	# Map set up
	mapCenter = new google.maps.LatLng -41.288889, 174.777222

	
	style = `
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
	]`
	
	mapOptions = { 
		styles: style
		zoom: 9,
		center: mapCenter,
		mapTypeId: google.maps.MapTypeId.ROADMAP,
		disableDefaultUI: true
	}

	elem = document.getElementById 'map_canvas'
	
	new google.maps.Map elem, mapOptions
	
		
googleMap = SetupMap()
google.maps.event.addDomListener(window, "load", googleMap);

# Fix the height of the map, twitter bootstrap fucks it up, it probbly fucks up some other map things aswell
mapCanvas = $ "#map_canvas"
mapCanvas.css {height: "100%" }
# mapCanvasHeight = mapCanvas.css "height"
# mapCanvas.css {height: "#{mapCanvasHeight - 50}px"}

# Zoom listner
# google.maps.event.addListener googleMap, "zoom_changed", ->
# console.log "Zoom #{googleMap.getZoom()} "
size = 0.05
current = 0;
range = 16;
	
createOverlay = (incident) ->

	geo = incident.geolocation
	
	lat = new google.maps.LatLng geo.lat - size * 0.125, geo.long - size * 0.25
	lng = new google.maps.LatLng geo.lat + size * 0.125, geo.long + size * 0.25

	imageBounds = new google.maps.LatLngBounds lat, lng
	overlay = new google.maps.GroundOverlay "#{incident.thumbnail_url}", imageBounds

	google.maps.event.addListener overlay, 'click', () => 
		window.CreateIncidentModal incident.id

	overlay.setMap googleMap
	overlaysOnMap.push overlay

onSuccess = (data) ->
	for incident in data.incidents
		console.log "creating incident #{current}"
		current++
		createOverlay incident
		
onFail = (data) ->
	console.log "Rest Call failed"
		
window.RWCall onSuccess, onFail, {}, "approved", "/start=#{current}/number=#{range}", "GET"


# Controls
nextButton = $ "#nextBtn"
prevButton = $ "#prevBtn"
d16Button = $ "#d16"
d32Button = $ "#d32"
d64Button = $ "#d64"
d128Button = $ "#d128"
d256Button = $ "#d256"

nextButton.click ->
	RemoveFromMap()
	console.log "Clicked Next"
	window.RWCall onSuccess, onFail, {}, "approved", "/start=#{current}/number=#{range}", "GET"

prevButton.click ->
	RemoveFromMap()
	console.log "Clicked Prev"
	current = if current - range > 0 then current - range else 0;
	window.RWCall onSuccess, onFail, {}, "approved", "/start=#{current}/number=#{range}", "GET"
	
changeRange = (newRange) ->
	RemoveFromMap()
	current = if current - range > 0 then current - range else 0;
	range = newRange;
	window.RWCall onSuccess, onFail, {}, "approved", "/start=#{current}/number=#{range}", "GET"

d16Button.click -> changeRange 16
d32Button.click -> changeRange 32
d64Button.click -> changeRange 64
d128Button.click -> changeRange 128
d256Button.click -> changeRange 256

###
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
###	
