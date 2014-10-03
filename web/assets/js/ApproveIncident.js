(function() {
  var calltype, contentArea, make, onFail, onSuccess;

  contentArea = $("#approveIncidentContent");

  make = function(i) {
    var acceptButton, btnGroup, incident, outcome, postCalltype, postDone, rejectButton;
    incident = $("<ul class=\"thumbnails\">						<li class = \"span3\">							<div class=\"thumbnail\">								<img src=\"" + i.thumbnail_url + "\" alt=\"\">							</div>						</li>						<li class = \"span9\">							<div class = \"caption\">								<h2>Description</h2>								<p>" + i.description + "</p>							</div>						</li>						<li class=\"span12\"> 							<div class=\"btn-group\" id =\"btns\"> 							</div>						</li>					</ul>				</div>");
    outcome = "reject";
    acceptButton = $("<a href=\"#\" class=\"btn btn-success btn-long\"><i class=\"icon-ok icon-white\"></i> Accept</a>");
    rejectButton = $("<a href=\"#\" class=\"btn btn-danger btn-long\"><i class=\"icon-remove icon-white\"></i> Reject</a>");
    btnGroup = incident.find("#btns");
    btnGroup.append(acceptButton);
    btnGroup.append(rejectButton);
    contentArea.append(incident);
    postCalltype = "POST";
    postDone = function(data) {
      return incident.animate({
        height: 0,
        opacity: 0.25
      }, function() {
        return incident.remove();
      });
    };
    acceptButton.click(function() {
      return window.RWCall(postDone, postDone, {}, "incident/" + i.id + "/approve", "", postCalltype);
    });
    return rejectButton.click(function() {
      return window.RWCall(postDone, postDone, {}, "incident/" + i.id + "/reject", "", postCalltype);
    });
  };

  onFail = function(data) {
    return console.log("Rest Call has failed");
  };

  calltype = "GET";

  onSuccess = function(data) {
    var i, _i, _len, _ref, _results;
    _ref = data["incidents"];
    _results = [];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      i = _ref[_i];
      _results.push(make(i));
    }
    return _results;
  };

  window.RWCall(onSuccess, onFail, {}, "/unapproved/start=0/number=100", "", calltype);

}).call(this);
