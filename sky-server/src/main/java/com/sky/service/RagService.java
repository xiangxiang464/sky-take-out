package com.sky.service;
import com.sky.context.BaseContext;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.query.Metadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RagService {

    @Resource
    private ChatModel qwenChatModel;            // 从 starter 注入的 bean，名字随 starter 配置

    private final RetrievalAugmentor retrievalAugmentor;
    private final Map<String, ChatMemory> chatMemoryStore = new ConcurrentHashMap<>();
    // 如果你用的是 @RequiredArgsConstructor 注入 RetrievalAugmentor，可改成构造注入
    public RagService(RetrievalAugmentor retrievalAugmentor) {
        this.retrievalAugmentor = retrievalAugmentor;
    }

    public String ask(String question) {
        ChatMessage questionMessage =UserMessage.from(
                TextContent.from(question)
        );// 构造一个最简单的 ChatMessage
//        ChatMessage chatMessage = new UserMessage("init"); // 可以是空内容或者占位符

        // chatMemoryId 可以为是用户userid
        Long userId = BaseContext.getCurrentId();
        System.out.println("chatMemoryId: " + userId);
// --- 第一步：获取或创建该用户的记忆 ---
        ChatMemory chatMemory = chatMemoryStore.computeIfAbsent(String.valueOf(userId), k ->
                MessageWindowChatMemory.withMaxMessages(10)); // 记住最近10条

        // --- 第二步：准备 RAG 检索 ---
        // 获取历史记录用于辅助检索（可选，但推荐）
        List<ChatMessage> history = chatMemory.messages();
//        ChatMessage questionMessage = UserMessage.from(question);

        // 构造 Metadata，传入历史记录
        Metadata metadata = Metadata.from(questionMessage, userId, history);
        AugmentationRequest request = new AugmentationRequest(questionMessage, metadata);

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

//        // 3) 调用注入的 ChatModel（Qwen）
//        // prompt 是 String
//        ChatMessage userMessage = new UserMessage(prompt);
//        ChatResponse response = qwenChatModel.chat(userMessage);
//
//        // 4) 返回结果（根据版本可能是 outputText() 或 getOutput()，这里用 outputText）
//        String answer = response.toString();
        // 2. 【关键修正1】组装消息列表：历史记录 + 当前带上下文的问题
        List<ChatMessage> messagesToSend = new ArrayList<>();
        messagesToSend.addAll(history);             // 先放入之前的对话历史
        messagesToSend.add(UserMessage.from(prompt)); // 再放入当前这一句（带RAG上下文）

        // --- 第四步：调用 LLM ---
        // 【关键修正2】这里传入的是列表，而不是单条消息
        ChatResponse response = qwenChatModel.chat(messagesToSend);
        String answer = response.aiMessage().text(); // 获取纯文本回答

        // --- 第五步：【关键修正3】更新记忆 ---
        // 注意：存入记忆时，存“原始短问题”而不是“带一大堆RAG上下文的长Prompt”
        // 这样可以省钱（Token），也能防止历史记录膨胀太快
        chatMemory.add(UserMessage.from(question));
        chatMemory.add(response.aiMessage());

        log.info("问：{}\n答：{}", question, answer);
        return answer;
    }
}
