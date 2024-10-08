package de.gedoplan.showcase.springaidemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class VectorStoreConfig {

    Logger logger = LoggerFactory.getLogger(VectorStoreConfig.class);

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }

    @Bean
    CommandLineRunner load(VectorStore vectorStore,
                           @Value("classpath:${de.gedoplan.showcase.springaidemo.rag.resourcepath}") String resourcePath) throws IOException {
        File ragResourceDir = new DefaultResourceLoader().getResource(resourcePath).getFile();
        if (!ragResourceDir.exists() && !ragResourceDir.isDirectory()) {
            throw new IllegalArgumentException("Resource path '" + resourcePath + "' does not exist or is not a directory");
        }
        File[] files = ragResourceDir.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files found in resource path '" + resourcePath + "'");
        }
        List<Document> documents = new ArrayList<>();
        Arrays.stream(files).forEach(file -> {
            if (!file.isDirectory()) {
                documents.addAll(
                        new TokenTextSplitter().transform(
                                new TextReader(resourcePath + File.separator + file.getName()).get()
                        )
                );
            } else {
                throw new IllegalArgumentException("Nested RAG resource directories not supported yet.");
            }
        });
        return ignored -> {
            vectorStore.add(documents);
            logger.info("Loaded " + documents.size() + " documents.");
        };
    }
}
