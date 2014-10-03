
###
A Wrapper around any Ajax Calls made by River Watch
Author Matthew Betts
###


# Globals
BASE_URL = "http://api.riverwatch.co.nz/wainz/"
CONTENT_TYPE = "contentType"
PROCESS_DATA = false
TIMEOUT = 5000
DATA_TYPE = "jsonp"


### REST CALLS
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
###

		
###
Make an AJAX Put request to the River Watch Server

	onSuccess (data) - function to execute on a successfull put, data is the responce from the server
	onFailure (data) - function to execute on a failed put, data is the resonce from the server
	data - Data the send to the server, this should be a map of maps\objects\primitives
	path - the path to add onto the url for the requested service

###

RWCall = (onSuccess, onFailure, data, path, args, callType) ->

	ajaxLoader = $ "#ajaxLoader"
	ajaxLoader.css "display", "block"

	# Format the data
	json = JSON.stringify data 
	
	dataType = "jsonp"
	
	console.log "Call:#{path}| callType:#{callType}|json:#{json}"
	
	if(callType == "GET")
	
		$.ajax
			# type: callType,
			url: BASE_URL + path + args,
			# data: json,
			dataType: dataType,
			type: callType,
			# jsonp: "jsonp",
			# processData: false ,
			# contentType: "application/json;charset=UTF-8",
			# timeout: TIMEOUT,
			success: (msg) ->
			
			    #Sanatise
				ajaxLoader.css "display", "none"
				onSuccess msg
				
			error: (msg) -> 
                ajaxLoader.css "display", "none"
                onFailure msg
				
	else if (callType == "POST")
		# $.post BASE_URL + path + args, "{ \"comment\": \"hello comment\"}"

		$.ajax
			url: BASE_URL + path + args,
			# data: json,
			dataType: "json",
			type: "POST",
			data: json,
			# processData	: false ,
			# contentType: "application/json;charset=UTF-8",
			# timeout: TIMEOUT,
			success: () ->
				ajaxLoader.css "display", "none"
				onSuccess() 
				
			error: () -> 
				ajaxLoader.css "display", "none"
				onFailure()
				

###
Make an AJAX Gete request to the River Watch Server

	onSuccess (data) - function to execute on a successfull get, data is the responce from the server
	onFailure (data) - function to execute on a failed get, data is the resonce from the server
	args - Arguments to send to the server, this should be a map of keys to string values
	path - The path to add onto the url for the requested service

###

###
Save calls, so that can be accessed globaly across the website.
###
# `var global = {};
# global.RWCall = RWCall`

window.RWCall = RWCall
