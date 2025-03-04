package de.gedoplan.showcase.springaidemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai/rag")
public class RagAiResource {

    @Autowired
    ChatClient chatClient;

    @Autowired
    VectorStore vectorStore;

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String rag(@RequestBody String question) {
        String RAG_PROMPT = """
                
                To answer this question you can use the following information if applicable:
                {question_answer_context}\
                """;
        return chatClient
                .prompt()
                .user(question)
//                .advisors(new SimpleLoggerAdvisor())
                .advisors(new QuestionAnswerAdvisor(
                        vectorStore,
                        SearchRequest.builder().similarityThreshold(0.6).topK(3).build(),
                        RAG_PROMPT))
                .call()
                .content();
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE, value = "/checkEmbeddingScore")
    public String checkEmbeddingScore(@RequestBody String question) {
        List<Document> results = this.vectorStore.similaritySearch(SearchRequest.builder().similarityThresholdAll().topK(10).query(question).build());
        return results.stream().map(document -> {
            Double score = document.getScore();
            return score + ": " + document.getText();
        }).collect(Collectors.joining("\n\n"));
    }

}
