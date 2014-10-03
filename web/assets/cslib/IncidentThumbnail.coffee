###
Create a Thumbnail For an Incident
###

Make = ( incident ) ->	
	
	# Create Cell
	console.log "Creating thumbmail for #{incident.id}, with url #{incident.thumbnail_url}"
	
	cell =  $ "<li class=\"span3\">
				<div href=\"\" data-toggle=\"modal\" class=\"thumbnail\" id=\"testThumbnail\">
					<img src=\"#{incident.thumbnail_url}\" alt=\"\">
					<div class = \"caption\">
						<h5>Incident #{incident.id}</h5>
						<p>#{incident.description}</p>
					</div>
				</div>
			</li>"	
		
	# Show Modal on Click
	cell.click ->
		window.CreateIncidentModal incident.id
	
	# Return Completed JQuery Element
	return cell
		
# Bind the method to the window so its global
window.CreateIncidentThumbnail = Make