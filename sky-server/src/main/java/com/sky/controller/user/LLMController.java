package com.sky.controller.user;

import com.sky.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/llm")
@RequiredArgsConstructor
public class LLMController {

    private final RagService ragService;

    @GetMapping("/ask")
    public String ask(@RequestParam String q) {
        return ragService.ask(q);
    }
}
