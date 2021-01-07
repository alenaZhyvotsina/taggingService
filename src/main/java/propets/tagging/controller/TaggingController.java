package propets.tagging.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import propets.tagging.dto.ImageUrlRequestDto;
import propets.tagging.dto.TagsAndColorsOfPictureDto;
import propets.tagging.service.TaggingService;

@RestController
@RequestMapping("/tagging")
public class TaggingController {
	
	@Autowired
	TaggingService taggingService;
	
	@PostMapping("/tags")
	public TagsAndColorsOfPictureDto getTagsColors(@RequestBody ImageUrlRequestDto imageUrlRequestDto){
		return taggingService.getTagsAndColors(imageUrlRequestDto.getImageUrl());
	}
	
}
