package nz.co.android.cowseye2.database;


public class IncidentBuilder{
	private Incident i;
	public IncidentBuilder() {
		i = new Incident();
	}
	public Incident build(){
		return i;
	}
	public IncidentBuilder setThumbnailUrl(String thumbnailUrl){
		i.setThumbnailUrl(thumbnailUrl);
		return this;
	}
	public IncidentBuilder setImageUrl(String imageUrl){
		i.setImageUrl(imageUrl);
		return this;
	}
	public IncidentBuilder setDescription(String description){
		i.setDescription(description);
		return this;
	}
	public IncidentBuilder setLatitude(float latitude){
		i.setLatitude(latitude);
		return this;
	}
	public IncidentBuilder setLongitude(float longitude){
		i.setLongitude(longitude);
		return this;
	}
	public IncidentBuilder setPhysicalLocation(String physicalLocation){
		i.setPhysicalLocation(physicalLocation);
		return this;
	}
	public IncidentBuilder setLocalThumbnailUrl(String localThumbnailUrl){
		i.setLocalThumbnailUrl(localThumbnailUrl);
		return this;
	}
	public IncidentBuilder setLocalImageUrl(String localImageUrl){
		i.setLocalImageUrl(localImageUrl);
		return this;
	}
	public IncidentBuilder setId(int id){
		i.setId(id);
		return this;
	}
}