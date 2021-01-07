package propets.tagging.dto;

import java.util.Set;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ColorsOfPicDto {
	
	Set<ColorOfPicDto> foregroundColors;
	double objectPercentage;

}
