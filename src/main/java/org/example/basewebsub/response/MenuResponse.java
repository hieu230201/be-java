package org.example.basewebsub.response;

import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuResponse {

    private Long id;

    private String name;

    private String path;

    private Integer parentId;

    private Boolean deleteFlag;

}
