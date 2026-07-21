package dev.emrullah.ai_panel.service;

import dev.emrullah.ai_panel.advisors.AvailableToolsLoggingAdvisor;
import dev.emrullah.ai_panel.model.AiPanelChatResponse;
import dev.emrullah.ai_panel.tools.CurrentDateTimeTool;
import dev.emrullah.ai_panel.tools.EntityManagementTool;
import dev.emrullah.ai_panel.tools.ExcelExportTool;
import org.springaicommunity.agent.tools.SmartWebFetchTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.toolsearch.ToolSearchToolCallingAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient,
                       ToolSearchToolCallingAdvisor toolSearchToolCallingAdvisor,
                       AvailableToolsLoggingAdvisor availableToolsLoggingAdvisor,
                       @Qualifier("ragVectorStore") VectorStore ragVectorStore,
                       CurrentDateTimeTool currentDateTimeTool,
                       ExcelExportTool excelExportTool,
                       EntityManagementTool entityManagementTool) {

        this.chatClient = chatClient.mutate()

                // System prompt to enforce native tool calling and prevent text-based JSON hallucinations
                .defaultSystem("""
                    You are a highly capable AI assistant that actively queries databases and generates files.
                    
                    HOW TO USE YOUR KNOWLEDGE BASE (RAG):
                    You are provided with documents that describe the database schema (Entities, properties, etc.).
                    This is ONLY the schema. It does not contain actual records.
                    To get actual records or generate files, you MUST use tools.
                    
                    CRITICAL INSTRUCTION - DO NOT REFUSE TO ACT:
                    Never say "I don't have access to a database or user data".
                    You DO have access! Your access is through the 'toolSearchTool'.
                    
                    AGENTIC WORKFLOW (MANDATORY):
                    1. Read the user's request.
                    2. Use the provided RAG context to understand the database schema (e.g., what fields the User entity has).
                    3. IMMEDIATELY call 'toolSearchTool' with queries like "database query" or "excel export".
                    4. Wait for the toolSearchTool to return the available tools (e.g. 'generate_tabular_excel_with_jpql').
                    5. Call those returned tools using the schema knowledge you got from the RAG context.
                    6. AFTER receiving tool results, YOU MUST synthesize them into the final JSON response format. Do not stop without producing the final JSON.
                    
                    Retrieved context is for reference only. It must NEVER be used as a substitute
                    for calling toolSearchTool when the user requests database queries, data
                    retrieval, or file exports.
           
                        IMPORTANT:
                    
                        Your final response MUST consist ONLY of a single valid JSON object.
           
                        Do not wrap it in markdown.
                  
                        Do not use ```json.
                   
                        Do not write explanations before the JSON.
                    
                        Do not write explanations after the JSON.
                    
                        Output exactly one JSON object.
                    
                        Example:
                    
                        {
                          "chatResponse":"...",
                          "downloadableContentUrl":null
                        }
          
                    """)
                // tool search advisor
                .defaultAdvisors(toolSearchToolCallingAdvisor)

                // tool name logging advisor
                .defaultAdvisors(availableToolsLoggingAdvisor)

                //vector store advisor
                .defaultAdvisors(QuestionAnswerAdvisor.builder(ragVectorStore)
                        .searchRequest(SearchRequest.builder()
                                .similarityThreshold(0.7)
                                .topK(3)
                                .build())
                        .build())

                // web search agent
                .defaultTools(SmartWebFetchTool.builder(chatClient).build())

                // custom tools
                .defaultTools(currentDateTimeTool, excelExportTool, entityManagementTool)

                .build();
    }

    public AiPanelChatResponse chat(String userMessage) {
        try {
            return chatClient.prompt()
                    .user(userMessage)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "default"))
                    .call()
                    .entity(AiPanelChatResponse.class);
        } catch (Exception e) {
            logger.error("Error processing chat request", e);
            return new AiPanelChatResponse("An error occurred while generating the response: " + e.getMessage(), null);
        }
    }
}