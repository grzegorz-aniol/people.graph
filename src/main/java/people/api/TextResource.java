package people.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextResource {

    private String text;

    private String resourceTitle;

    private String sourceTypeName;

    private String resourceId;

}
