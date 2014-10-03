adminMenu = $ "#adminMenu"
logoutButton = $ "#logoutButton"
loginMenu = $ "#loginMenu"
loginButton = $ "#loginButton"
usernameInput = $ "#usernameInput"
passwordInput = $ "#passwordInput"
form_login = $ "#form_login"




###
setCookie = (name, value) ->
	c_value = escape value
	c_name = escape name
	document.cookie = "#{c_name}=#{c_value};"
	console.log("Setting cookie to '#{c_name}=#{value};'")
	console.log "Cookies is '#{document.cookie}'"
###
###
getCookie = (name) ->
	cookies = document.cookie.split(";")
	console.log "cookies found #{cookies}"
	for c in cookies
	    console.log("Cookie is #{c}")
		cookie = c.split "="
		if name == cookie[0]
			return unescape cookie[1]

isAdmin = () ->
	return getCookie("status") == "Admin"
	
setLogInControl = () ->
    console.log("setLogInControl Called")
    if isAdmin()
	    console.log("Is an Admin")
	    adminMenu.css {display: "block"} 
	    loginMenu.css {display: "none"}
    else
        console.log("Is a user")
        adminMenu.css {display: "none"} 
        loginMenu.css {display: "block"} 
###
### Old Testing controls
logoutButton.click ->
	setCookie "status", "User"
	setLogInControl()
		
loginButton.click ->
	form_login.submit ->
	    alert "Done"
	
	setLogInControl()
###
	
# Check with the server to see if this session has admin priviliges
$ ->
    success = (data) ->
        status = JSON.parse(data).status
        console.log "Success #{data}"
        if status == "true"
	        console.log("Is an Admin")
	        adminMenu.css {display: "block"} 
	        loginMenu.css {display: "none"}
        else
            console.log("Is a user")
            adminMenu.css {display: "none"} 
            loginMenu.css {display: "block"}    
        
    fail = (data) ->
        console.log "Fail #{data}"    

    window.RWCall success, fail, {}, "status", "", "GET"

    ###
    setLogInControl()
    ###
    
   ### 
form_login.submit ->
    
    $.ajax {   
        type: "POST"
        data : $(this).serialize()
        cache: false
        url: "http://api.riverwatch.co.nz/wainz/login"   
        crossDomain: true
        dataType: "jsonp"
        success: () ->
            alert "Success"
    }
    
    loginMenu.removeClass "open"
        
    return false;   
###
###
setInterval setLogInControl, 1000
	###
		

		
		
