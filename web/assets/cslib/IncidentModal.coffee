###
Retreive, and create a incident modal dialog box
###


getMessage = () ->
	messageInput = $ "#messageInput"
	messageInput.val()
	
	
checkMessage = () ->
	if getMessage() == ""
	
		controlGroup = $ "#messageControlGroup"
		controlGroup.addClass "error"
	
		controlGroup.change ->
			if getMessage() != ""
				controlGroup.removeClass "error"
	
		# No Name
		return false
	
	# Name is ok
	return true

Make = (id) ->

	# Change somthing to the loading icon...
		
	# Reterive Infromation
	onFail = (data) ->
		console.log "Rest Call has failed"
	path = "incident"
	args = "/#{id}"
	calltype = "GET"
	
	onSuccess = (data) ->
	
		# Populate modal with correct information
		incidentModal = $ "<div class=\"modal hide fade in\" id=\"myModal\">
			<div class=\"modal-header\">
				<button type=\"button\" class=\"close\" data-dismiss=\"modal\"><i class=\"icon-remove icon-red\"></i></button>
				<h3>Incident Details</h3>
			</div>
			<div class=\"modal-body\">
				<div href=\"#\" class=\"thumbnail\" id=\"testThumbnail\">
					<img src=\"#{data.image_url}\" alt=\"\">	
				</div>
				<h2>Details</h2>
				<p> #{data.description}</p>
				<h2>Comments</h2>
				<p></p>
				<div id = \"comments\"></div>
				
				<h2>Post Comment</h2>
				<div class =\"control-group\" id = \"messageControlGroup\">
					<textarea class=\"input-xlarge\" id=\"messageInput\" rows=\"3\" style=\"margin: 0px; width: 690px; height: 114px; \"></textarea>
				</div>
				<p></p>
				<a href=\"#\" id= \"commentSubmitButton\"class=\"btn btn-primary\">
					<i class=\"icon-comment icon-white\"></i>
					Submit
				</a>	
			</div>
			<div class=\"modal-footer\">	
				<a href=\"#\" class=\"btn btn-danger\" data-dismiss=\"modal\">
					<i class=\"icon-remove icon-white\"></i>
					Close Incident
				</a>
			</div>
			
		</div>"
		
		incidentModal.modal {show: true }
		
		submitCommentButton = incidentModal.find "#commentSubmitButton"
		submitCommentButton.click ->
			
			# Get comment text
			if checkMessage()
				data = {
					
					comment: getMessage()
					name: "NA"
					email: "NA"
				}
				
				onSuccess = () ->
					submitCommentButton.removeClass "btn-primary"
					submitCommentButton.addClass "btn-success"
					console.log "Success"
					submitCommentButton.text "Comment Submitted Successfuly, Awaiting Approval"

				onFailure = () ->
					submitCommentButton.removeClass "btn-primary"
					submitCommentButton.addClass "btn-danger"
					console.log "Fail"
					submitCommentButton.text "Comment Submission Failed"
			
				submitCommentButton.text "Submitting..."
				submitCommentButton.click ->
			
				# Send no failure, as cross domain json posting with backend would not work
				window.RWCall onSuccess, onSuccess, data, "incident", "/#{id}/comment", "POST"
			
		
		# Get comments
		commentFailure = (data) ->
			console.log "problem for loaing comments"
		
		commentSuccess = (data) ->
			commentsSection = incidentModal.find "#comments"
			for c in data[ "comments" ]
			# for c in data[ "comments" ]
				console.log "adding coment #{c}"
				commentsSection.append $ "<p>#{c.comment}</p>"
				
		window.RWCall commentSuccess, commentFailure, {}, "incident", "/#{id}/comments/", "GET"
			
	window.RWCall onSuccess, onFail, {}, path, args, calltype
	
	
window.CreateIncidentModal = Make