contentArea = $ "#approveCommentContent"

make = (c) ->

	incident =	$	"<ul class=\"thumbnails\">
						<li class = \"span3\">
							<div class=\"thumbnail\">
								<img src=\"#{c.thumbnail_url}\" alt=\"\">
							</div>
						</li>
						<li class = \"span5\">
							<div class = \"caption\">
								<h2>Description</h2>
								<p>#{c.description}</p>
							</div>
						</li>
						<li class = \"span4\">
							<div class = \"caption\">
								<h2>Comment</h2>
								<p>#{c.comment}</p>
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
	postDone = () ->
		incident.animate {height:0, opacity:0.25}, ->
			incident.remove()
		
	acceptButton.click ->
		window.RWCall postDone, postDone, {}, "comment/#{c.id}/approve", "", postCalltype
		
	rejectButton.click ->
		window.RWCall postDone, postDone, {}, "comment/#{c.id}/reject", "", postCalltype

onFail = (data) ->
		console.log "Rest Call has failed"
path = "comments/unapproved/"
args = ""
calltype = "GET"	

onSuccess = (data) ->
	for c in data[ "comments" ]
		make c

window.RWCall onSuccess, onFail, {}, path, args, calltype