package org.example.springpilot.Service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.example.springpilot.Sentiment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SentimentAnalysisService {

    @Value("${anthropic.api-key}")
    private String apiKey;


    public Sentiment getSentiment(String text){
        try{
            AnthropicClient client = AnthropicOkHttpClient.builder().
                    apiKey(apiKey)
                    .build();
            String prompt =
                    "Analyze the sentiment of this journal entry and respond and respond with only one word- " +
                            "either HAPPY, SAD, ANXIOUS or ANGRY. Nothing else.\n\nJournal entry: "+text;

            MessageCreateParams params = MessageCreateParams.builder().
                    model(Model.CLAUDE_HAIKU_4_5)
                    .maxTokens(10)
                    .addUserMessage(prompt)
                    .build();
            Message message = client.messages().create(params);
            String result = message.content().stream()
                    .filter(block -> block.isText())
                    .map(block -> block.asText().text().trim().toUpperCase())
                    .findFirst()
                    .orElse("NEUTRAL");

            return Sentiment.valueOf(result);
        }
        catch(Exception e){
            log.error("Error calling Claude API for sentiment analysis",e);
            return Sentiment.SAD;
        }
    }
}
