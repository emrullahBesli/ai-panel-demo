package dev.emrullah.ai_panel.advisors;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailableToolsLoggingAdvisor implements BaseAdvisor {

    private final static Logger LOGGER = LoggerFactory.getLogger(AvailableToolsLoggingAdvisor.class);

    public static final int DEFAULT_ORDER = 1000;

    private static final String CYAN = "\u001B[36m";

    private static final String GREEN = "\u001B[32m";

    private static final String DIM = "\u001B[2m";

    private static final String RESET = "\u001B[0m";

    private final int order;
    
    public AvailableToolsLoggingAdvisor() {
        this(DEFAULT_ORDER);
    }
    
    public AvailableToolsLoggingAdvisor(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
        List<String> toolNames = List.of();
        if (request.prompt().getOptions() instanceof ToolCallingChatOptions options
                && options.getToolCallbacks() != null) {
            toolNames = options.getToolCallbacks()
                    .stream()
                    .map(tc -> tc.getToolDefinition().name())
                    .sorted()
                    .toList();
        }

        LOGGER.info("\n{}>>> LLM call - {} tool(s) visible to the model: {}{}", CYAN,
                toolNames.size(), toolNames, RESET);

        // If the previous step was a tool response (e.g. tool-search results), show it.
        Message last = request.prompt().getLastUserOrToolResponseMessage();
        if (last instanceof ToolResponseMessage toolResponse) {
            toolResponse.getResponses()
                    .forEach(r -> LOGGER.info("{}      tool result [{}]: {}{}", DIM, r.name(),
                            truncate(r.responseData(), 260), RESET));
        }
        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse response, AdvisorChain chain) {
        var chatResponse = response.chatResponse();
        if (chatResponse == null) {
            return response;
        }
        chatResponse.getResults()
                .stream()
                .map(Generation::getOutput)
                .filter(message -> !message.getToolCalls().isEmpty())
                .flatMap(message -> message.getToolCalls().stream())
                .forEach(toolCall -> LOGGER.info("{}      model -> calls {}({}){}", GREEN, toolCall.name(),
                        truncate(toolCall.arguments(), 160), RESET));
        return response;
    }

    private static String truncate(@Nullable String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max) + "…";
    }

    
}
