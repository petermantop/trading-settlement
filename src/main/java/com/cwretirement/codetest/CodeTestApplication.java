package com.cwretirement.codetest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@SpringBootApplication
public class CodeTestApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(CodeTestApplication.class);

    private final ReconciliationService reconciliationService;
    private final ResourceLoader resourceLoader;
    private final String transactionFilePath;

    public CodeTestApplication(
            ReconciliationService reconciliationService,
            ResourceLoader resourceLoader,
            @Value("${transaction.file.path}") String transactionFilePath
    ) {
        this.reconciliationService = reconciliationService;
        this.resourceLoader = resourceLoader;
        this.transactionFilePath = transactionFilePath;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CodeTestApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        try (Stream<String> lines = Files.lines(resolveTransactionFile())) {
            AtomicLong lineNumber = new AtomicLong(1);
            AtomicLong contributionCount = new AtomicLong();
            AtomicLong settlementCount = new AtomicLong();

            lines.skip(1).forEach(line -> processLine(line, lineNumber, contributionCount, settlementCount));
            LOG.info("Processed {} contributions and {} trade settlements", contributionCount.get(), settlementCount.get());
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to read transaction file at '%s'", transactionFilePath), e);
        }
    }

    private void processLine(String line, AtomicLong lineNumber, AtomicLong contributionCount, AtomicLong settlementCount) {
        long currentLine = lineNumber.incrementAndGet();
        if (line == null || line.isBlank()) {
            LOG.debug("Skipping blank line {}", currentLine);
            return;
        }

        final String[] fields = line.split("\\|", -1);
        final Transaction transaction;
        try {
            transaction = new Transaction(fields);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Failed to parse transaction at line %d", currentLine), e);
        }

        LOG.debug("Parsed transaction line {} as {}", currentLine, transaction.getType());

        switch (transaction.getType()) {
            case CONTRIBUTION -> {
                reconciliationService.reconcileContribution(transaction.toContribution());
                contributionCount.incrementAndGet();
            }
            case PURCHASE_CASH_SETTLEMENT, SALE_CASH_SETTLEMENT -> {
                reconciliationService.reconcileTradeSettlement(transaction.toTradeSettlement());
                settlementCount.incrementAndGet();
            }
        }
    }

    private Path resolveTransactionFile() throws IOException {
        if (!transactionFilePath.startsWith("classpath:")) {
            return Path.of(transactionFilePath);
        }
        Resource resource = resourceLoader.getResource(transactionFilePath);
        return resource.getFile().toPath();
    }
}
