
/*
Create a Thumbnail For an Incident
*/

(function() {
  var Make;

  Make = function(incident) {
    var cell;
    console.log("Creating thumbmail for " + incident.id + ", with url " + incident.thumbnail_url);
    cell = $("<li class=\"span3\">				<div href=\"\" data-toggle=\"modal\" class=\"thumbnail\" id=\"testThumbnail\">					<img src=\"" + incident.thumbnail_url + "\" alt=\"\">					<div class = \"caption\">						<h5>Incident " + incident.id + "</h5>						<p>" + incident.description + "</p>					</div>				</div>			</li>");
    cell.click(function() {
      return window.CreateIncidentModal(incident.id);
    });
    return cell;
  };

  window.CreateIncidentThumbnail = Make;

}).call(this);
