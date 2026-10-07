package com.vrmart.service;

import com.vrmart.dao.ProductDAO;
import com.vrmart.model.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides catalog-aware shopping assistant functionality for VR Mart.
 */
public final class AIShoppingService {

    /** Maximum number of products returned by the assistant. */
    private static final int MAX_RESULTS = 8;

    /** Minimum stock required for an in-stock recommendation. */
    private static final int MIN_STOCK = 1;

    /** Default Ollama generate endpoint. */
    private static final String DEFAULT_OLLAMA_URL =
            "http://localhost:11434/api/generate";

    /** Default local Ollama model. */
    private static final String DEFAULT_OLLAMA_MODEL = "mistral";

    /** Ollama connection timeout in milliseconds. */
    private static final int OLLAMA_CONNECT_TIMEOUT = 2000;

    /** Ollama read timeout in milliseconds. */
    private static final int OLLAMA_READ_TIMEOUT = 15000;

    /** Minimum HTTP status code considered successful. */
    private static final int HTTP_SUCCESS_MIN = 200;

    /** First HTTP status code outside the successful range. */
    private static final int HTTP_SUCCESS_MAX = 300;

    /** Pattern used to extract product IDs from the AI response. */
    private static final Pattern PRODUCT_IDS_PATTERN = Pattern.compile(
            "\"productIds\"\\s*:\\s*\\[([^]]*)\\]");

    /** Pattern used to extract numeric product IDs. */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    /** Minimum keyword length used for singular/plural matching. */
    private static final int MIN_KEYWORD_LENGTH = 3;

    /** Product DAO used to access the VR Mart catalog. */
    private final ProductDAO productDAO;

    /** Ollama endpoint used for natural-language processing. */
    private final String ollamaUrl;

    /** Local Ollama model used by the shopping assistant. */
    private final String ollamaModel;

    /**
     * Creates the shopping assistant service.
     *
     * @param dao product data access object
     */
    public AIShoppingService(final ProductDAO dao) {
        productDAO = dao;
        ollamaUrl = readConfig("OLLAMA_URL", DEFAULT_OLLAMA_URL);
        ollamaModel = readConfig("OLLAMA_MODEL", DEFAULT_OLLAMA_MODEL);
    }

    /**
     * Processes a natural-language shopping query.
     *
     * @param query customer query
     * @return assistant response
     * @throws Exception when catalog access fails
     */
    public AssistantResponse processQuery(final String query)
            throws Exception {
        final String normalizedQuery = normalize(query);

        if (normalizedQuery.isEmpty()) {
            return new AssistantResponse(
                    "Tell me what you are looking for. "
                            + "For example: phones under 30000.",
                    new ArrayList<>());
        }

        if (isGreeting(normalizedQuery)) {
            return new AssistantResponse(
                    "Hi! 👋 I am the VR Mart Shopping Assistant. "
                            + "You can ask me to find products, compare "
                            + "options, or recommend something from VR Mart.",
                    new ArrayList<>());
        }

        if (isGeneralVRMartQuestion(normalizedQuery)) {
            return new AssistantResponse(
                    "VR Mart is an online marketplace where you can "
                            + "discover products from different categories "
                            + "and shop from sellers on the platform.",
                    new ArrayList<>());
        }

        if (isAssistantConversation(normalizedQuery)) {
            return new AssistantResponse(
                    "Sure! 👋 I can help you find products, compare "
                            + "options, check prices and recommend products "
                            + "available in VR Mart.",
                    new ArrayList<>());
        }

        if (isClearlyOutsideVRMart(normalizedQuery)) {
            return new AssistantResponse(
                    "I can help only with VR Mart. Ask me about products, "
                            + "prices, categories, availability or "
                            + "recommendations within this mart.",
                    new ArrayList<>());
        }

        final List<Product> products = productDAO.findAll();
        final AssistantResponse aiResponse =
                askOllama(normalizedQuery, products);

        if (aiResponse != null) {
            return aiResponse;
        }

        final QueryCriteria criteria = buildCriteria(normalizedQuery);
        final List<Product> matches = findMatches(products, criteria);

        if (matches.isEmpty()) {
            return buildEmptyResponse(criteria);
        }

        sortProducts(matches, criteria);

        if (matches.size() > MAX_RESULTS) {
            return new AssistantResponse(
                    buildSuccessMessage(criteria, matches.size()),
                    new ArrayList<>(matches.subList(0, MAX_RESULTS)));
        }

        return new AssistantResponse(
                buildSuccessMessage(criteria, matches.size()), matches);
    }

    private AssistantResponse askOllama(
            final String query, final List<Product> products) {
        try {
            final String request = buildOllamaRequest(
                    buildOllamaPrompt(query, products));
            final String response = callOllama(request);

            if (response == null || response.isEmpty()) {
                return null;
            }

            final String modelResponse = extractOllamaResponse(response);
            final String intent = extractJsonString(modelResponse, "intent");
            final String message =
                    extractJsonString(modelResponse, "message");

            if (message.isEmpty() || intent.isEmpty()) {
                return null;
            }

            if ("OUT_OF_SCOPE".equalsIgnoreCase(intent)) {
                return new AssistantResponse(
                        "I can help only with VR Mart. Ask me about "
                                + "products, prices, categories, availability "
                                + "or recommendations within this mart.",
                        new ArrayList<>());
            }

            if ("CHAT".equalsIgnoreCase(intent)) {
                if (isVRMartChatQuery(query)) {
                    return new AssistantResponse(message, new ArrayList<>());
                }
                if (isLikelyShoppingQuery(query)) {
                    return null;
                }
                return new AssistantResponse(
                        "I can help only with VR Mart. Ask me about "
                                + "products, prices, categories, availability "
                                + "or recommendations within this mart.",
                        new ArrayList<>());
            }

            if (!"SHOPPING".equalsIgnoreCase(intent)) {
                return null;
            }

            final Map<Long, Product> productMap = new HashMap<>();
            for (Product product : products) {
                productMap.put(product.getId(), product);
            }

            final List<Product> selected = new ArrayList<>();
            final Set<Long> selectedIds = new LinkedHashSet<>();
            final Matcher ids = PRODUCT_IDS_PATTERN.matcher(modelResponse);

            if (ids.find()) {
                final Matcher numbers = NUMBER_PATTERN.matcher(ids.group(1));
                while (numbers.find() && selected.size() < MAX_RESULTS) {
                    final long id = Long.parseLong(numbers.group());
                    final Product product = productMap.get(id);

                    if (product != null
                            && product.getStockQty() >= MIN_STOCK
                            && selectedIds.add(id)) {
                        selected.add(product);
                    }
                }
            }

            final QueryCriteria criteria = buildCriteria(query);
            final List<Product> validated = validateAIProducts(
                    selected, criteria);

            if (validated.isEmpty()) {
                return null;
            }

            return new AssistantResponse(message, validated);
        } catch (IOException | RuntimeException exception) {
            return null;
        }
    }

    private List<Product> validateAIProducts(
            final List<Product> products, final QueryCriteria criteria) {
        final List<Product> validated = new ArrayList<>();

        for (Product product : products) {
            if (matchesCriteria(product, criteria)) {
                validated.add(product);
            }
        }

        return validated;
    }

    private String buildOllamaPrompt(
            final String query, final List<Product> products) {
        final StringBuilder catalog = new StringBuilder();

        for (Product product : products) {
            catalog.append("ID=").append(product.getId())
                    .append(" | NAME=").append(safe(product.getName()))
                    .append(" | CATEGORY=")
                    .append(safe(product.getCategory()))
                    .append(" | PRICE=").append(product.getPrice())
                    .append(" | STOCK=").append(product.getStockQty())
                    .append(" | DESCRIPTION=")
                    .append(safe(product.getDescription()))
                    .append('\n');
        }

        return "You are the VR Mart Shopping Assistant. "
                + "Understand the buyer's natural-language request. "
                + "Use ONLY the VR Mart catalog below. For greetings or "
                + "general VR Mart questions use CHAT and no product IDs. "
                + "For shopping requests use SHOPPING and return up to 8 "
                + "relevant in-stock product IDs. Understand synonyms such "
                + "as phone/mobile/smartphone, laptop/notebook, "
                + "t-shirt/tshirt/shirt, dress/dresses, "
                + "headphone/headphones, shoe/shoes, "
                + "budget/cheap, college/student, gaming and home. "
                + "Do not classify a general VR Mart question as SHOPPING. "
                + "Never invent product facts. "
                + "Return ONLY JSON: {\"intent\":\"SHOPPING|CHAT|"
                + "OUT_OF_SCOPE\", "
                + "\"message\":\"short natural response\", "
                + "\"productIds\":[1,2]}\n\n"
                + "BUYER QUERY: " + query
                + "\n\nVR MART CATALOG:\n" + catalog;
    }

    private String buildOllamaRequest(final String prompt) {
        return "{\"model\":\"" + escapeJson(ollamaModel)
                + "\",\"prompt\":\"" + escapeJson(prompt)
                + "\",\"stream\":false}";
    }

    private String callOllama(final String requestBody) throws IOException {
        final HttpURLConnection connection =
                (HttpURLConnection) new URL(ollamaUrl).openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(OLLAMA_CONNECT_TIMEOUT);
        connection.setReadTimeout(OLLAMA_READ_TIMEOUT);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        try {
            try (OutputStream output = connection.getOutputStream()) {
                output.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            final int status = connection.getResponseCode();
            if (status < HTTP_SUCCESS_MIN || status >= HTTP_SUCCESS_MAX) {
                return null;
            }

            try (InputStream stream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    stream, StandardCharsets.UTF_8))) {
                final StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            }
        } finally {
            connection.disconnect();
        }
    }

    private String extractOllamaResponse(final String body) {
        final String value = extractJsonString(body, "response");
        return value.isEmpty() ? body : value;
    }

    private String extractJsonString(final String body, final String field) {
        final String marker = "\"" + field + "\"";
        final int fieldIndex = body.indexOf(marker);

        if (fieldIndex < 0) {
            return "";
        }

        final int colonIndex =
                body.indexOf(':', fieldIndex + marker.length());

        if (colonIndex < 0) {
            return "";
        }

        int startIndex = colonIndex + 1;

        while (startIndex < body.length()
                && Character.isWhitespace(body.charAt(startIndex))) {
            startIndex++;
        }

        if (startIndex >= body.length()
                || body.charAt(startIndex) != '"') {
            return "";
        }

        final StringBuilder value = new StringBuilder();
        boolean escaped = false;

        for (int index = startIndex + 1; index < body.length(); index++) {
            final char character = body.charAt(index);

            if (escaped) {
                value.append('\\').append(character);
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '"') {
                return unescapeJson(value.toString());
            } else {
                value.append(character);
            }
        }

        return "";
    }

    private boolean isGreeting(final String query) {
        return query.equals("hi")
                || query.equals("hello")
                || query.equals("hey")
                || query.equals("hai")
                || query.equals("good morning")
                || query.equals("good afternoon")
                || query.equals("good evening")
                || query.startsWith("hi ")
                || query.startsWith("hello ")
                || query.startsWith("hey ")
                || query.startsWith("hai ");
    }

    private boolean isGeneralVRMartQuestion(final String query) {
        return query.equals("what is vr mart")
                || query.equals("what is vrmart")
                || query.equals("what's vr mart")
                || query.equals("what's vrmart")
                || query.equals("about vr mart")
                || query.equals("about vrmart")
                || query.contains("tell me about vr mart")
                || query.contains("tell me about vrmart")
                || query.contains("what does vr mart do")
                || query.contains("what does vrmart do")
                || query.contains("how does vr mart work")
                || query.contains("how does vrmart work");
    }

    private boolean isAssistantConversation(final String query) {
        return query.equals("ok")
                || query.equals("okay")
                || query.equals("thanks")
                || query.equals("thank you")
                || query.equals("who are you")
                || query.equals("what can you do")
                || query.equals("help")
                || query.equals("what is your name")
                || query.equals("bye")
                || query.equals("goodbye");
    }

    private boolean isVRMartChatQuery(final String query) {
        return isGeneralVRMartQuestion(query)
                || query.contains("vr mart")
                || query.contains("vrmart")
                || query.contains("shopping assistant")
                || query.contains("your products")
                || query.contains("your catalog");
    }

    private boolean isLikelyShoppingQuery(final String query) {
        final String[] shoppingTerms = {
            "buy", "shop", "find", "show", "looking for", "want",
            "need", "recommend", "suggest", "compare", "price",
            "budget", "cheap", "phone", "mobile", "laptop",
            "computer", "tablet", "shirt", "shirts", "tshirt",
            "t shirt", "shoe", "shoes", "book", "books", "home",
            "kitchen", "beauty", "sports", "electronics",
            "accessories", "fashion", "clothing", "in stock",
            "available", "under ", "below ", "above ", "over "
        };

        for (String term : shoppingTerms) {
            if (query.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private boolean isClearlyOutsideVRMart(final String query) {
        final String[] phrases = {
            "tell me a joke",
            "make me laugh",
            "write a poem",
            "write a story",
            "solve my homework",
            "write code",
            "programming help",
            "weather today",
            "latest news",
            "politics",
            "movie review",
            "song lyrics"
        };

        for (String phrase : phrases) {
            if (query.contains(phrase)) {
                return true;
            }
        }
        return false;
    }

    private String readConfig(final String name, final String defaultValue) {
        final String environment = System.getenv(name);

        if (environment != null && !environment.trim().isEmpty()) {
            return environment.trim();
        }

        final String property = System.getProperty(name);
        return property == null || property.trim().isEmpty()
                ? defaultValue : property.trim();
    }

    private String escapeJson(final String value) {
        return safe(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private String unescapeJson(final String value) {
        return safe(value)
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    private String safe(final String value) {
        return value == null ? "" : value;
    }

    /**
     * Normalizes a customer query.
     *
     * @param query original query
     * @return normalized query
     */
    private String normalize(final String query) {
        if (query == null) {
            return "";
        }

        return query.trim()
                .toLowerCase(Locale.ROOT)
                .replace("t-shirts", "t shirt")
                .replace("tshirts", "t shirt")
                .replace("tee-shirts", "tee shirt")
                .replace("tee shirts", "shirt");
    }

    /**
     * Builds search criteria from the customer query.
     *
     * @param query normalized query
     * @return query criteria
     */
    private QueryCriteria buildCriteria(final String query) {
        final QueryCriteria criteria = new QueryCriteria();
        criteria.setCategory(findCategory(query));
        criteria.setProductType(findProductType(query));
        criteria.setMaxPrice(findMaxPrice(query));
        criteria.setMinPrice(findMinPrice(query));
        criteria.setInStock(isInStockQuery(query));
        criteria.setKeywords(findKeywords(query));
        return criteria;
    }

    /**
     * Finds a specific product type mentioned in the query.
     *
     * @param query normalized query
     * @return detected product type or null
     */
    private String findProductType(final String query) {
        final String[][] productTypes = {
            {"smartphone", "phone"},
            {"smartphones", "phone"},
            {"mobile phone", "phone"},
            {"mobile", "phone"},
            {"mobiles", "phone"},
            {"phones", "phone"},
            {"phone", "phone"},
            {"laptops", "laptop"},
            {"laptop", "laptop"},
            {"notebook", "laptop"},
            {"notebooks", "laptop"},
            {"t shirt", "shirt"},
            {"tshirt", "shirt"},
            {"shirts", "shirt"},
            {"shirt", "shirt"},
            {"dresses", "dress"},
            {"dress", "dress"},
            {"headphones", "headphone"},
            {"headphone", "headphone"},
            {"earphones", "earphone"},
            {"earphone", "earphone"},
            {"shoes", "shoe"},
            {"shoe", "shoe"},
            {"footwear", "shoe"},
            {"tablets", "tablet"},
            {"tablet", "tablet"},
            {"watches", "watch"},
            {"watch", "watch"}
        };

        for (String[] productType : productTypes) {
            if (containsPhrase(query, productType[0])) {
                return productType[1];
            }
        }

        return null;
    }

    /**
     * Finds a product category mentioned in the query.
     *
     * @param query normalized query
     * @return detected category or null
     */
    private String findCategory(final String query) {
        final String[] categories = {
            "electronics",
            "mobile",
            "mobiles",
            "phone",
            "phones",
            "laptop",
            "laptops",
            "computer",
            "computers",
            "fashion",
            "clothing",
            "shoes",
            "books",
            "book",
            "home",
            "kitchen",
            "beauty",
            "sports",
            "accessories"
        };

        for (String category : categories) {
            if (containsPhrase(query, category)) {
                return normalizeCategory(category);
            }
        }

        return null;
    }

    private boolean containsPhrase(
            final String text, final String phrase) {
        final String paddedText = " " + text + " ";
        final String paddedPhrase = " " + phrase + " ";
        return paddedText.contains(paddedPhrase);
    }

    /**
     * Normalizes detected category names.
     *
     * @param category detected category
     * @return normalized category
     */
    private String normalizeCategory(final String category) {
        if ("mobile".equals(category)
                || "mobiles".equals(category)
                || "phone".equals(category)
                || "phones".equals(category)) {
            return "electronics";
        }

        if ("laptop".equals(category)
                || "laptops".equals(category)
                || "computer".equals(category)
                || "computers".equals(category)) {
            return "electronics";
        }

        if ("book".equals(category)) {
            return "books";
        }

        return category;
    }

    /**
     * Finds a maximum price from the query.
     *
     * @param query normalized query
     * @return maximum price or null
     */
    private BigDecimal findMaxPrice(final String query) {
        final String[] markers = {
            "under ",
            "below ",
            "less than ",
            "within ",
            "upto ",
            "up to "
        };

        for (String marker : markers) {
            final int markerIndex = query.indexOf(marker);

            if (markerIndex >= 0) {
                final String value = extractNumber(
                        query.substring(markerIndex + marker.length()));

                if (!value.isEmpty()) {
                    return parsePrice(value);
                }
            }
        }

        return null;
    }

    /**
     * Finds a minimum price from the query.
     *
     * @param query normalized query
     * @return minimum price or null
     */
    private BigDecimal findMinPrice(final String query) {
        final String[] markers = {
            "above ",
            "over ",
            "more than "
        };

        for (String marker : markers) {
            final int markerIndex = query.indexOf(marker);

            if (markerIndex >= 0) {
                final String value = extractNumber(
                        query.substring(markerIndex + marker.length()));

                if (!value.isEmpty()) {
                    return parsePrice(value);
                }
            }
        }

        return null;
    }

    /**
     * Extracts the first numeric value from text.
     *
     * @param text source text
     * @return numeric text
     */
    private String extractNumber(final String text) {
        final StringBuilder value = new StringBuilder();

        for (int index = 0; index < text.length(); index++) {
            final char character = text.charAt(index);

            if (Character.isDigit(character)
                    || character == ','
                    || character == '.') {
                value.append(character);
            } else if (value.length() > 0) {
                break;
            }
        }

        return value.toString().replace(",", "");
    }

    /**
     * Parses a price value.
     *
     * @param value numeric price
     * @return decimal price
     */
    private BigDecimal parsePrice(final String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Determines whether the customer requested available products.
     *
     * @param query normalized query
     * @return true when stock is requested
     */
    private boolean isInStockQuery(final String query) {
        return query.contains("in stock")
                || query.contains("available")
                || query.contains("availability")
                || query.contains("buy now");
    }

    /**
     * Extracts useful product keywords from the query.
     *
     * @param query normalized query
     * @return keyword set
     */
    private Set<String> findKeywords(final String query) {
        final Set<String> keywords = new LinkedHashSet<>();
        final String[] words = query.split("\\s+");
        final String[] ignoredWords = {
            "i", "me", "my", "want", "need", "find", "show",
            "search", "give", "get", "looking", "for", "a",
            "an", "the", "with", "under", "below", "less",
            "than", "above", "over", "more", "within", "upto",
            "up", "to", "in", "stock", "available", "availability",
            "product", "products", "please", "good", "best"
        };

        for (String word : words) {
            final String cleaned = word
                    .replaceAll("[^a-z0-9]", "");

            if (cleaned.length() >= 2
                    && !contains(ignoredWords, cleaned)
                    && !isNumeric(cleaned)) {
                keywords.add(cleaned);
            }
        }

        return keywords;
    }

    /**
     * Checks whether a value exists in an array.
     *
     * @param values source array
     * @param value value to find
     * @return true when found
     */
    private boolean contains(final String[] values, final String value) {
        for (String item : values) {
            if (item.equals(value)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether text contains only numeric characters.
     *
     * @param value text
     * @return true when numeric
     */
    private boolean isNumeric(final String value) {
        for (int index = 0; index < value.length(); index++) {
            if (!Character.isDigit(value.charAt(index))) {
                return false;
            }
        }

        return !value.isEmpty();
    }

    /**
     * Finds products matching the query criteria.
     *
     * @param products catalog products
     * @param criteria query criteria
     * @return matching products
     */
    private List<Product> findMatches(
            final List<Product> products, final QueryCriteria criteria) {
        final List<Product> matches = new ArrayList<>();

        for (Product product : products) {
            if (matchesCriteria(product, criteria)) {
                matches.add(product);
            }
        }

        return matches;
    }

    /**
     * Checks whether a product matches all requested filters.
     *
     * @param product product
     * @param criteria criteria
     * @return true when product matches
     */
    private boolean matchesCriteria(
            final Product product, final QueryCriteria criteria) {
        if (criteria.isInStock() && product.getStockQty() < MIN_STOCK) {
            return false;
        }

        if (!matchesPrice(product, criteria)) {
            return false;
        }

        if (!matchesCategory(product, criteria)) {
            return false;
        }

        if (!matchesProductType(product, criteria.getProductType())) {
            return false;
        }

        return matchesKeywords(product, criteria.getKeywords());
    }

    /**
     * Checks product price constraints.
     *
     * @param product product
     * @param criteria criteria
     * @return true when price matches
     */
    private boolean matchesPrice(
            final Product product, final QueryCriteria criteria) {
        final BigDecimal price = product.getPrice();

        if (price == null) {
            return false;
        }

        if (criteria.getMinPrice() != null
                && price.compareTo(criteria.getMinPrice()) < 0) {
            return false;
        }

        return criteria.getMaxPrice() == null
                || price.compareTo(criteria.getMaxPrice()) <= 0;
    }

    /**
     * Checks product category.
     *
     * @param product product
     * @param criteria criteria
     * @return true when category matches
     */
    private boolean matchesCategory(
            final Product product, final QueryCriteria criteria) {
        if (criteria.getCategory() == null) {
            return true;
        }

        final String category = normalize(product.getCategory());

        if (category.contains(criteria.getCategory())) {
            return true;
        }

        return matchesElectronicsProduct(product, criteria.getCategory());
    }

    /**
     * Checks common electronics product names.
     *
     * @param product product
     * @param category category
     * @return true when it belongs to electronics
     */
    private boolean matchesElectronicsProduct(
            final Product product, final String category) {
        if (!"electronics".equals(category)) {
            return false;
        }

        final String name = normalize(product.getName());
        final String description = normalize(product.getDescription());

        return name.contains("phone")
                || name.contains("mobile")
                || name.contains("laptop")
                || name.contains("computer")
                || name.contains("tablet")
                || name.contains("watch")
                || name.contains("headphone")
                || name.contains("earphone")
                || description.contains("electronics");
    }

    /**
     * Checks a specific requested product type.
     *
     * @param product product
     * @param productType requested product type
     * @return true when the product type matches
     */
    private boolean matchesProductType(
            final Product product, final String productType) {
        if (productType == null) {
            return true;
        }

        final String name = normalize(product.getName());
        final String description = normalize(product.getDescription());

        if ("phone".equals(productType)) {
            return containsAnyPhrase(
                    name, "phone", "smartphone", "mobile phone")
                    || containsAnyPhrase(
                            description, "phone", "smartphone", "mobile phone");
        }

        if ("laptop".equals(productType)) {
            return containsAnyPhrase(
                    name, "laptop", "notebook")
                    || containsAnyPhrase(
                            description, "laptop", "notebook");
        }

        if ("shirt".equals(productType)) {
            return containsAnyPhrase(
                    name, "shirt", "t shirt", "tshirt")
                    || containsAnyPhrase(
                            description, "shirt", "t shirt", "tshirt");
        }

        if ("dress".equals(productType)) {
            return containsAnyPhrase(name, "dress")
                    || containsAnyPhrase(description, "dress");
        }

        if ("headphone".equals(productType)) {
            return containsAnyPhrase(name, "headphone")
                    || containsAnyPhrase(description, "headphone");
        }

        if ("earphone".equals(productType)) {
            return containsAnyPhrase(name, "earphone")
                    || containsAnyPhrase(description, "earphone");
        }

        if ("shoe".equals(productType)) {
            return containsAnyPhrase(
                    name, "shoe", "footwear")
                    || containsAnyPhrase(
                            description, "shoe", "footwear");
        }

        if ("tablet".equals(productType)) {
            return containsAnyPhrase(name, "tablet")
                    || containsAnyPhrase(description, "tablet");
        }

        if ("watch".equals(productType)) {
            return containsAnyPhrase(name, "watch")
                    || containsAnyPhrase(description, "watch");
        }

        return true;
    }

    private boolean containsAnyPhrase(
            final String text, final String... phrases) {
        for (String phrase : phrases) {
            if (containsPhrase(text, phrase)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks product keyword matching.
     *
     * @param product product
     * @param keywords requested keywords
     * @return true when keywords match
     */
    private boolean matchesKeywords(
            final Product product, final Set<String> keywords) {
        if (keywords.isEmpty()) {
            return true;
        }

        final String searchableText =
                normalize(product.getName()) + " "
                        + normalize(product.getDescription()) + " "
                        + normalize(product.getCategory());

        for (String keyword : keywords) {
            if (containsPhrase(searchableText, keyword)
                    || matchesKeywordStem(searchableText, keyword)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesKeywordStem(
            final String searchableText, final String keyword) {
        if (keyword.length() <= MIN_KEYWORD_LENGTH) {
            return false;
        }

        if (keyword.endsWith("s")) {
            return containsPhrase(
                    searchableText, keyword.substring(0, keyword.length() - 1));
        }

        return containsPhrase(searchableText, keyword + "s");
    }

    /**
     * Sorts matched products for recommendation.
     *
     * @param products matched products
     * @param criteria query criteria
     */
    private void sortProducts(
            final List<Product> products, final QueryCriteria criteria) {
        if (criteria.getMaxPrice() != null) {
            products.sort(
                    Comparator.comparing(
                            Product::getPrice,
                            Comparator.nullsLast(Comparator.naturalOrder())));
        } else {
            products.sort(
                    Comparator.comparingInt(Product::getStockQty)
                            .reversed()
                            .thenComparing(
                                    Product::getName,
                                    String.CASE_INSENSITIVE_ORDER));
        }
    }

    /**
     * Builds a successful response message.
     *
     * @param criteria search criteria
     * @param count match count
     * @return response message
     */
    private String buildSuccessMessage(
            final QueryCriteria criteria, final int count) {
        if (criteria.getProductType() != null) {
            return "I found " + count + " "
                    + criteria.getProductType()
                    + " product(s) matching your request.";
        }

        if (criteria.getCategory() != null) {
            return "I found " + count + " product(s) matching your "
                    + criteria.getCategory() + " request.";
        }

        if (criteria.getMaxPrice() != null) {
            return "I found " + count
                    + " product(s) within your budget.";
        }

        return "I found " + count
                + " product(s) that match your request.";
    }

    /**
     * Builds a response when no product matches.
     *
     * @param criteria search criteria
     * @return assistant response
     */
    private AssistantResponse buildEmptyResponse(
            final QueryCriteria criteria) {
        final String message;

        if (criteria.getMaxPrice() != null) {
            message = "I couldn't find a matching product within that "
                    + "budget. Try increasing the price range.";
        } else if (criteria.getCategory() != null) {
            message = "I couldn't find a matching product in "
                    + criteria.getCategory() + ". Try another category.";
        } else {
            message = "I couldn't find a matching product in the "
                    + "current VR Mart catalog.";
        }

        return new AssistantResponse(message, new ArrayList<>());
    }

    /**
     * Represents the parsed shopping criteria.
     */
    private static final class QueryCriteria {

        /** Requested category. */
        private String category;

        /** Requested specific product type. */
        private String productType;

        /** Maximum requested price. */
        private BigDecimal maxPrice;

        /** Minimum requested price. */
        private BigDecimal minPrice;

        /** Whether only available products are requested. */
        private boolean inStock;

        /** Requested product keywords. */
        private Set<String> keywords;

        /**
         * Returns the category.
         *
         * @return category
         */
        public String getCategory() {
            return category;
        }

        /**
         * Sets the category.
         *
         * @param value category
         */
        public void setCategory(final String value) {
            category = value;
        }

        /**
         * Returns the product type.
         *
         * @return product type
         */
        public String getProductType() {
            return productType;
        }

        /**
         * Sets the product type.
         *
         * @param value product type
         */
        public void setProductType(final String value) {
            productType = value;
        }

        /**
         * Returns maximum price.
         *
         * @return maximum price
         */
        public BigDecimal getMaxPrice() {
            return maxPrice;
        }

        /**
         * Sets maximum price.
         *
         * @param value maximum price
         */
        public void setMaxPrice(final BigDecimal value) {
            maxPrice = value;
        }

        /**
         * Returns minimum price.
         *
         * @return minimum price
         */
        public BigDecimal getMinPrice() {
            return minPrice;
        }

        /**
         * Sets minimum price.
         *
         * @param value minimum price
         */
        public void setMinPrice(final BigDecimal value) {
            minPrice = value;
        }

        /**
         * Returns stock requirement.
         *
         * @return true when stock is required
         */
        public boolean isInStock() {
            return inStock;
        }

        /**
         * Sets stock requirement.
         *
         * @param value stock requirement
         */
        public void setInStock(final boolean value) {
            inStock = value;
        }

        /**
         * Returns keywords.
         *
         * @return keywords
         */
        public Set<String> getKeywords() {
            return keywords;
        }

        /**
         * Sets keywords.
         *
         * @param value keywords
         */
        public void setKeywords(final Set<String> value) {
            keywords = value;
        }
    }

    /**
     * Represents the assistant response.
     */
    public static final class AssistantResponse {

        /** Response message. */
        private final String message;

        /** Matching products. */
        private final List<Product> products;

        /**
         * Creates an assistant response.
         *
         * @param responseMessage response message
         * @param matchingProducts matching products
         */
        public AssistantResponse(
                final String responseMessage,
                final List<Product> matchingProducts) {
            message = responseMessage;
            products = matchingProducts;
        }

        /**
         * Returns the response message.
         *
         * @return response message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Returns matching products.
         *
         * @return matching products
         */
        public List<Product> getProducts() {
            return products;
        }
    }
}
