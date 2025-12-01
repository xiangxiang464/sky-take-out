package com.sky.service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.query.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class RagService {

    @Resource
    private ChatModel qwenChatModel;            // 从 starter 注入的 bean，名字随 starter 配置

    private final RetrievalAugmentor retrievalAugmentor;

    // 如果你用的是 @RequiredArgsConstructor 注入 RetrievalAugmentor，可改成构造注入
    public RagService(RetrievalAugmentor retrievalAugmentor) {
        this.retrievalAugmentor = retrievalAugmentor;
    }

    public String ask(String question) {
        ChatMessage questionMessage =UserMessage.from(
                TextContent.from(question)
        );// 构造一个最简单的 ChatMessage
        ChatMessage chatMessage = new UserMessage("init"); // 可以是空内容或者占位符

// chatMemoryId 可以为空
        Object chatMemoryId = null;

// chatMemory 可以为空列表
        List<ChatMessage> chatMemory = java.util.Collections.emptyList();

// 创建 Metadata
        Metadata metadata = Metadata.from(chatMessage, chatMemoryId, chatMemory);
        // 构造请求
        AugmentationRequest request = new AugmentationRequest(questionMessage,metadata);  // 直接用构造函数
        // 1) 向量检索增强
        AugmentationResult augment = retrievalAugmentor.augment(request);

        // 2) 拼 prompt（把检索到的上下文放入）
        String prompt = String.format(
                "你是苍穹外卖的智能客服，请结合下列知识回答用户的问题，回答时简单明了：\n" +
                        "%s\n" +
                        "用户问题：%s",
                augment.contents(),
                question
        );

        // 3) 调用注入的 ChatModel（Qwen）
        // prompt 是 String
        ChatMessage userMessage = new UserMessage(prompt);
        ChatResponse response = qwenChatModel.chat(userMessage);

        // 4) 返回结果（根据版本可能是 outputText() 或 getOutput()，这里用 outputText）
        String answer = response.toString();

        log.info("问：{}\n答：{}", question, answer);
        return answer;
    }
}
