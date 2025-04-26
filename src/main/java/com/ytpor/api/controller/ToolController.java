package com.ytpor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Random;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ytpor.api.model.SerialVersionUID;

@RestController
@RequestMapping("/tool")
@Tag(name = "Tool", description = "Tools used for specific tasks")
public class ToolController {

    // Declare Random as a class-level field
    private final Random random = new Random();

    @GetMapping("/random-uid")
    @Operation(summary = "Generate UID", description = "Generate Random Serial Version UID")
    public ResponseEntity<SerialVersionUID> generateSerialVersionUID() {
        SerialVersionUID serialVersionUID = new SerialVersionUID();
        serialVersionUID.setUid(random.nextLong() + "L");

        return ResponseEntity.ok(serialVersionUID);
    }
}
