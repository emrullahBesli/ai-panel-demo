package dev.emrullah.ai_panel.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class DomainSchemaRagConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainSchemaRagConfig.class);

    private static final String VECTOR_STORE_NAME = "vectorstore.json";

    @Value("classpath:/data/userEntityText.txt")
    private Resource userEntityText;

    @Value("classpath:/data/orderEntityText.txt")
    private Resource orderEntityText;

    @Value("classpath:/data/orderUsageEntityText.txt")
    private Resource orderUsageEntityText;

    @Value("classpath:/data/jpqlRulesText.txt")
    private Resource jpqlRulesText;

    @Value("classpath:/data/jpqlExamplesText.txt")
    private Resource jpqlExamplesText;

    @Bean(name = "ragVectorStore")
    public SimpleVectorStore ragVectorStore(EmbeddingModel embeddingModel) throws IOException {

        var vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        File vectorStoreFile = getVectorStoreFile();

        if (vectorStoreFile.exists()) {

            LOGGER.info("Loading vector store from {}", vectorStoreFile.getAbsolutePath());
            vectorStore.load(vectorStoreFile);

        } else {

            LOGGER.info("Creating vector store {}", vectorStoreFile.getAbsolutePath());

            List<Document> documents = List.of(
                    new Document(read(userEntityText)),
                    new Document(read(orderEntityText)),
                    new Document(read(orderUsageEntityText)),
                    new Document(read(jpqlRulesText)),
                    new Document(read(jpqlExamplesText))
            );

            vectorStore.add(documents);
            vectorStore.save(vectorStoreFile);
        }

        return vectorStore;
    }

    private String read(Resource resource) throws IOException {
        return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
    }

    private File getVectorStoreFile() {
        Path path = Paths.get("src", "main", "resources", VECTOR_STORE_NAME);
        return path.toFile();
    }
}