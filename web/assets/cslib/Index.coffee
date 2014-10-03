carouselContainer = $ "#carouselContainer"

createItem = (i, active) ->
	return $ 	"<div class=\"item #{active}\">
					<img src=\"#{i.thumbnail_url}\" width=\"800\" height=\"450\" alt=\"\">
					<div class=\"carousel-caption\">
						<h4>Description</h4>
						<p>#{i.description}</p>
					</div>
				</div>"


# Create and Append each incident as a thumbnail
createCarousel = (data) ->
	
	carousel = $ 	"<div id=\"myCarousel\" class=\"carousel slide\">
					<!-- Carousel items -->
						<div class=\"carousel-inner\" id=\"carouselInner\">
						
						</div>
						<!-- Carousel nav -->
						<a class=\"carousel-control left\" href=\"#myCarousel\" data-slide=\"prev\">&lsaquo;</a>
						<a class=\"carousel-control right\" href=\"#myCarousel\" data-slide=\"next\">&rsaquo;</a>
					</div>"
		

	carouselInner = $ carousel.find "#carouselInner"
	first = "active"
	for i in data.incidents
		item = createItem i, first
		carouselInner.append item
		first = " "
		
	carouselContainer.append carousel
	carousel.carousel()
		
onFail = (data) ->
	console.log "Rest Call has failed"

window.RWCall createCarousel, onFail, {}, "unapproved_stub", "/start=0/number=4", "GET"


			
		