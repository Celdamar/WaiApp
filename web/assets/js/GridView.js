(function() {
  var appendThumbnails, current, doc, onFail, picgrid, range, win;

  picgrid = $("#picgrid");

  current = 0;

  range = 24;

  appendThumbnails = function(data) {
    var i, incidentThumbnail, _i, _len, _ref, _results;
    _ref = data.incidents;
    _results = [];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      i = _ref[_i];
      incidentThumbnail = window.CreateIncidentThumbnail(i);
      picgrid.append(incidentThumbnail);
      _results.push(current++);
    }
    return _results;
  };

  onFail = function(data) {
    return console.log("Rest Call has failed");
  };

  window.RWCall(appendThumbnails, onFail, {}, "approved", "/start=" + current + "/number=" + range, "GET");

  win = $(window);

  doc = $(document);

  win.scroll(function() {
    if (win.scrollTop() + win.height() === doc.height()) {
      console.log("Reached bottom of page");
      return window.RWCall(appendThumbnails, onFail, {}, "approved", "/start=" + current + "/number=" + range, "GET");
    }
  });

}).call(this);
