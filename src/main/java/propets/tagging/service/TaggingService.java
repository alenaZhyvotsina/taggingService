package propets.tagging.service;

import propets.tagging.dto.ColorsOfPicDto;
import propets.tagging.dto.TagsAndColorsOfPictureDto;
import propets.tagging.dto.TagsOfPicDto;

public interface TaggingService {
	
	String getUploadsId(String imageUrl);
	
	TagsOfPicDto getTags(String uploadId);
	
	ColorsOfPicDto getColors(String uploadId);
	
	TagsAndColorsOfPictureDto getTagsAndColors(String imageUrl);

}
