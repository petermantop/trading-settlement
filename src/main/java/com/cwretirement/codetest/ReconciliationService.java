package com.cwretirement.codetest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class ReconciliationService {
    private static final Logger LOG = LoggerFactory.getLogger(ReconciliationService.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String contributionReconciliationURL;
    private final String tradeSettlementReconciliationURL;

    public ReconciliationService(
            @Value("${contribution.reconciliation.url}") String contributionReconciliationURL,
            @Value("${trade.settlement.reconciliation.url}") String tradeSettlementReconciliationURL
    ) {
        this.contributionReconciliationURL = contributionReconciliationURL;
        this.tradeSettlementReconciliationURL = tradeSettlementReconciliationURL;
    }

    public void reconcileContribution(Contribution contribution) {
        reconcile(contribution, contributionReconciliationURL, "contribution");
    }

    public void reconcileTradeSettlement(TradeSettlement tradeSettlement) {
        reconcile(tradeSettlement, tradeSettlementReconciliationURL, "trade settlement");
    }

    private void reconcile(Object payload, String urlString, String payloadType) {
        HttpURLConnection connection = null;
        try {
            URL url = URI.create(urlString).toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000 * 5);
            connection.setReadTimeout(1000 * 5);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");

            final String jsonString = serialize(payload);
            byte[] representation = jsonString.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(representation.length);

            LOG.debug("Posting {} reconciliation payload to {}", payloadType, urlString);
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(representation);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new ReconciliationException(
                        ReconciliationException.Type.CANNOT_RECONCILE,
                        String.format("%s reconciliation service returned HTTP code %d", payloadType, responseCode)
                );
            }
        } catch (ReconciliationException e) {
            throw e;
        } catch (java.net.SocketTimeoutException e) {
            throw new ReconciliationException(ReconciliationException.Type.SERVICE_TIMEOUT, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(String.format("Configured URL for %s reconciliation is invalid: %s", payloadType, urlString), e);
        } catch (IOException e) {
            throw new ReconciliationException(ReconciliationException.Type.SERVICE_UNAVAILABLE, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String serialize(Object payload) {
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize reconciliation payload", e);
        }
    }
}
