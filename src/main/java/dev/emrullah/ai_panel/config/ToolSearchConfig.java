package dev.emrullah.ai_panel.config;

import org.springframework.ai.chat.client.advisor.toolsearch.ToolSearchToolCallingAdvisor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.toolsearch.ToolIndex;
import org.springframework.ai.tool.toolsearch.index.vectorstore.VectorToolIndex;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class ToolSearchConfig {

    @Bean(name = "toolVectorStore")
    public VectorStore toolVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public ToolIndex toolIndex(@Qualifier("toolVectorStore") VectorStore toolVectorStore) {
        return new VectorToolIndex(toolVectorStore);
    }

    @Bean
    public ToolSearchToolCallingAdvisor toolSearchAdvisor(ToolIndex toolIndex) {

        return ToolSearchToolCallingAdvisor.builder()
                .toolIndex(toolIndex)
                .maxResults(5)
                .build();
    }

}
