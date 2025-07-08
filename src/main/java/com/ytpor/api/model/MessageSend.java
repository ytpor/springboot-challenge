package com.ytpor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

@Data
public class MessageSend implements Serializable {

    private static final long serialVersionUID = -8934022390469476930L;

    @NotNull(message = "Title must not be null.")
    @Schema(description = "Title", example = "title")
    private String title;

    @NotNull(message = "Content must not be null.")
    @Schema(description = "Content", example = "cnotent")
    private String content;
}
