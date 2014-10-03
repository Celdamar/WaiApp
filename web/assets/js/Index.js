(function() {
  var carouselContainer, createCarousel, createItem, onFail;

  carouselContainer = $("#carouselContainer");

  createItem = function(i, active) {
    return $("<div class=\"item " + active + "\">					<img src=\"" + i.thumbnail_url + "\" width=\"800\" height=\"450\" alt=\"\">					<div class=\"carousel-caption\">						<h4>Description</h4>						<p>" + i.description + "</p>					</div>				</div>");
  };

  createCarousel = function(data) {
    var carousel, carouselInner, first, i, item, _i, _len, _ref;
    carousel = $("<div id=\"myCarousel\" class=\"carousel slide\">					<!-- Carousel items -->						<div class=\"carousel-inner\" id=\"carouselInner\">												</div>						<!-- Carousel nav -->						<a class=\"carousel-control left\" href=\"#myCarousel\" data-slide=\"prev\">&lsaquo;</a>						<a class=\"carousel-control right\" href=\"#myCarousel\" data-slide=\"next\">&rsaquo;</a>					</div>");
    carouselInner = $(carousel.find("#carouselInner"));
    first = "active";
    _ref = data.incidents;
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      i = _ref[_i];
      item = createItem(i, first);
      carouselInner.append(item);
      first = " ";
    }
    carouselContainer.append(carousel);
    return carousel.carousel();
  };

  onFail = function(data) {
    return console.log("Rest Call has failed");
  };

  window.RWCall(createCarousel, onFail, {}, "unapproved_stub", "/start=0/number=4", "GET");

}).call(this);
