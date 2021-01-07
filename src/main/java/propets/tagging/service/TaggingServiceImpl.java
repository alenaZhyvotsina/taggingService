package propets.tagging.service;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import propets.tagging.dto.ColorsOfPicDto;
import propets.tagging.dto.ResponseColorDto;
import propets.tagging.dto.ResponseTagDto;
import propets.tagging.dto.ResponseUploadDto;
import propets.tagging.dto.TagsAndColorsOfPictureDto;
import propets.tagging.dto.TagsOfPicDto;

@Service
@ManagedResource
public class TaggingServiceImpl implements TaggingService {
	
	@Autowired
	RestTemplate restTemplate;

	private String imaggaUrl = "https://api.imagga.com/v2/";
	
	@Value("${imagga.header}")
	private String imaggaHeader;
	
	@Value("${imagga.auth.type}")
	private String imaggaAuthType;
	
	@Value("${imagga.auth.value}")
	private String imaggaAuthValue;
	/*
	@ManagedAttribute
	public String getImaggaAuthType() {
		return imaggaAuthType;
	}

	@ManagedAttribute
	public void setImaggaAuthType(String imaggaAuthType) {
		this.imaggaAuthType = imaggaAuthType;
	}

	@ManagedAttribute
	public String getImaggaAuthValue() {
		return imaggaAuthValue;
	}

	@ManagedAttribute
	public void setImaggaAuthValue(String imaggaAuthValue) {
		this.imaggaAuthValue = imaggaAuthValue;
	}
	
	@ManagedAttribute
	public String getImaggaHeader() {
		return imaggaHeader;
	}

	@ManagedAttribute
	public void setImaggaHeader(String imaggaHeader) {
		this.imaggaHeader = imaggaHeader;
	}
	*/
	
	@Override
	public TagsAndColorsOfPictureDto getTagsAndColors(String imageUrl) {
		/*
		String uploadId = getUploadsId(imageUrl);
		if(uploadId == null || uploadId.isEmpty()) {
			throw new TaggingServiceException();
		}
			
		TagsOfPicDto tagsOfPicDto = getTags(uploadId);
		ColorsOfPicDto colorsOfPicDto = getColors(uploadId);
		*/
				
		TagsOfPicDto tagsOfPicDto = getTags(imageUrl);
		ColorsOfPicDto colorsOfPicDto = getColors(imageUrl);
		
		Set<String> tagsColors = new LinkedHashSet<>();
		
		if(tagsOfPicDto != null) {
			tagsOfPicDto.getTags().forEach(e -> {
				if(e.getConfidence() >= 30.) {
					tagsColors.add(e.getTag().getEn());
				}
			});			
		}
		if(colorsOfPicDto != null) {
			colorsOfPicDto.getForegroundColors().forEach(e -> {
				tagsColors.add(e.getClosestPaletteColor());
				tagsColors.add(e.getClosestPaletteColorParent());
			});
		}
				
		return new TagsAndColorsOfPictureDto(tagsColors);
	}
	

	@Override
	public String getUploadsId(String imageUrl) {
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(imaggaUrl + "uploads");
				
		HttpHeaders headers = new HttpHeaders();
		headers.add(imaggaHeader, imaggaAuthType + " " + imaggaAuthValue);
		
		Map<String, File> body = new HashMap<>();
		body.put("image", new File(imageUrl));
		/*
		RequestEntity<Map<String, File>> requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, builder.build().toUri());
		System.out.println("!!!! req = " + requestEntity.getHeaders());
				
		ResponseEntity<ResponseUploadDto> responseEntity = restTemplate.exchange(requestEntity, ResponseUploadDto.class);
		*/
		
		HttpEntity<Map<String, File>> requestEntity = new HttpEntity<>(body, headers);
				
		ResponseEntity<ResponseUploadDto> responseEntity = restTemplate.exchange(builder.build().toUri(), 
																				 HttpMethod.POST, 
																				 requestEntity, 
																				 ResponseUploadDto.class);
		
		return responseEntity.getBody().getUploadId();
		
	}

	@Override
	public TagsOfPicDto getTags(/*String uploadId*/ String imageUrl) {

		RestTemplate restTemplate = new RestTemplate();
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(imaggaUrl + "tags")
				.queryParam("image_url", imageUrl);
				//.queryParam("image_upload_id", uploadId);
				
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(imaggaHeader, imaggaAuthType + " " + imaggaAuthValue);
		
		RequestEntity<String> request = 
				new RequestEntity<String>(headers, HttpMethod.GET, builder.build().toUri());
		
		ResponseEntity<ResponseTagDto> response = 
				restTemplate.exchange(request, ResponseTagDto.class);
		
		TagsOfPicDto tagsOfPicDto = response.getBody().getResult();
		if(tagsOfPicDto != null) tagsOfPicDto.getTags().forEach(e -> System.out.println(e.getTag().getEn() + " - "  + e.getConfidence()));
		
		return tagsOfPicDto;
		

	}

	@Override
	public ColorsOfPicDto getColors(/*String uploadId*/String imageUrl) {
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(imaggaUrl + "colors")
				.queryParam("image_url", imageUrl)
				//.queryParam("image_upload_id", uploadId)
				.queryParam("extract_object_colors", "1");
		
		HttpHeaders headers = new HttpHeaders();
		headers.add(imaggaHeader, imaggaAuthType + " " + imaggaAuthValue);
		
		RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET, builder.build().toUri());
			
		ResponseEntity<ResponseColorDto> response = restTemplate.exchange(request, ResponseColorDto.class);
		
		ColorsOfPicDto imageColors = response.getBody().getResult().getColors();
		if(imageColors != null) {
			System.out.println("objectPercentage = " + imageColors.getObjectPercentage());
			imageColors.getForegroundColors().forEach(e -> System.out.println(e.getClosestPaletteColor() + " " + e.getClosestPaletteColorParent()));
		}
		
		return imageColors;
	}
	
}
