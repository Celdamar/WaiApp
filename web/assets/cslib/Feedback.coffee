sendButton = $ "#sendButton"

getName = () ->
	nameInput = $ "#nameInput"
	nameInput.val()
	
getSubject = () ->
	subjectInput = $ "#subjectInput"
	subjectInput.val()
	
getMessage = () ->
	messageInput = $ "#messageInput"
	messageInput.val()
	
getEmail = () ->
	emailInput = $ "#emailInput"
	emailInput.val()
	
	
checkName = () ->
	if getName() == ""
		
		nameControlGroup = $ "#nameControlGroup"
		nameControlGroup.addClass "error"
		
		nameControlGroup.change ->
			if getName() != ""
				nameControlGroup.removeClass "error"
		
		# No Name
		return false
	
	# Name is ok
	return true

	
checkSubject = () ->
	if getSubject() == ""
	
		controlGroup = $ "#subjectControlGroup"
		controlGroup.addClass "error"
	
		controlGroup.change ->
			if getSubject() != ""
				controlGroup.removeClass "error"
	
		# No Name
		return false
	
	# Name is ok
	return true
	
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
	
checkEmail = () ->
	if getEmail() == ""
	
		controlGroup = $ "#emailControlGroup"
		controlGroup.addClass "error"
	
		controlGroup.change ->
			if getEmail() != ""
				controlGroup.removeClass "error"
	
		# No Name
		return false
	
	# Name is ok
	return true

sendButton.click ->

	# Check Fields
	allCorrect = true
	allCorrect = checkName() && allCorrect
	allCorrect = checkSubject() && allCorrect
	allCorrect = checkEmail() && allCorrect
	allCorrect = checkMessage() && allCorrect

	if allCorrect
		console.log "All fields are fine"
		
		# Build Json
		data = {
			name: getName()
			subject: getSubject()
			email: getEmail()
			message: getMessage()
		}
		
		onSuccess = (msg) ->
			sendButton.removeClass "btn-primary"
			sendButton.addClass "btn-success"
			console.log "Success"
			sendButton.text "Feedback Submitted Successfuly"

		onFailure = (msg) ->
			sendButton.removeClass "btn-primary"
			sendButton.addClass "btn-danger"
			console.log "Fail"
			sendButton.text "Feedback Submission Failed"
		
		path = "/Submit/Feedback"
		args = []
		callType = "POST"
		
		sendButton.text "Submitting..."
		
		# Send
		window.RWCall onSuccess, onFailure, data, path, args, callType
		
		# Don't Refresh
		return false
		
	return false