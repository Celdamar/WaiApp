contentArea = $ "#approveIncidentContent"

make = (i) ->

	incident =	$	"<ul class=\"thumbnails\">
						<li class = \"span3\">
							<div class=\"thumbnail\">
								<img src=\"#{i.thumbnail_url}\" alt=\"\">
							</div>
						</li>
						<li class = \"span9\">
							<div class = \"caption\">
								<h2>Description</h2>
								<p>#{i.description}</p>
							</div>
						</li>
						<li class=\"span12\"> 
							<div class=\"btn-group\" id =\"btns\"> 
							</div>
						</li>
					</ul>
				</div>"
				
	outcome = "reject";
	acceptButton = $ "<a href=\"#\" class=\"btn btn-success btn-long\"><i class=\"icon-ok icon-white\"></i> Accept</a>"
	rejectButton = $ "<a href=\"#\" class=\"btn btn-danger btn-long\"><i class=\"icon-remove icon-white\"></i> Reject</a>"	
	
	btnGroup = incident.find "#btns"
	btnGroup.append acceptButton 
	btnGroup.append rejectButton
	
	contentArea.append incident	
	
	postCalltype = "POST"
	postDone = (data) ->
		incident.animate {height:0, opacity:0.25}, ->
			incident.remove()
		
	acceptButton.click ->
		window.RWCall postDone, postDone, {}, "incident/#{i.id}/approve", "", postCalltype
		
	rejectButton.click ->
		window.RWCall postDone, postDone, {}, "incident/#{i.id}/reject", "", postCalltype

onFail = (data) ->
		console.log "Rest Call has failed"
calltype = "GET"	

onSuccess = (data) ->
	for i in data[ "incidents" ]
		make i
	
window.RWCall onSuccess, onFail, {}, "/unapproved/start=0/number=100", "", calltype