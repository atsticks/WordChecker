package com.aws.codestar.projecttemplates.handler;

import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;

import com.aws.codestar.projecttemplates.GatewayResponse;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link HelloWorldHandler}. Modify the tests in order to support your use case as you build your project.
 */
@DisplayName("Tests for HelloWorldHandler")
public class HelloWorldHandlerTest {

    private static final String EXPECTED_CONTENT_TYPE = "application/json";
    private static final String EXPECTED_RESPONSE_START = "Hello World at";
    private static final String EXPECTED_RESPONSE_END = "!";
    private static final int EXPECTED_STATUS_CODE_SUCCESS = 200;
    private static final String POST_INPUT = "          a, bb, cc, dddd, a, bb";


    // A mock class for com.amazonaws.services.lambda.runtime.Context
    private final MockLambdaContext mockLambdaContext = new MockLambdaContext();

    /**
     * Initializing variables before we run the tests.
     * Use @BeforeAll for initializing static variables at the start of the test class execution.
     * Use @BeforeEach for initializing variables before each test is run.
     */
    @BeforeAll
    static void setup() {
        // Use as needed.
    }

    /**
     * De-initializing variables after we run the tests.
     * Use @AfterAll for de-initializing static variables at the end of the test class execution.
     * Use @AfterEach for de-initializing variables at the end of each test.
     */
    @AfterAll
    static void tearDown() {
        // Use as needed.
    }

    /**
     * Basic test to verify the result obtained when calling {@link HelloWorldHandler} successfully.
     */
    @Test
    @DisplayName("Basic test for request handler (GET)")
    void testHandleGetRequest() {
        final JSONObject input = new JSONObject();
        input.put("httpMethod", "GET");
        input.put("queryStringParameters", new JSONObject().put("url", "http://www.google.com"));
        input.put("headers", new JSONObject());
        GatewayResponse response = (GatewayResponse) new HelloWorldHandler().handleRequest(input.toString(), mockLambdaContext);

        assertEquals(response.getStatusCode(), 200);
        JSONObject result = new JSONObject(response.getBody());
        assertEquals("http://www.google.com", result.getString("url"));
        assertNotNull(result.getString("content-type"));
//        assertNotNull(result.getString("encoding"));
        assertTrue(result.getInt("content-length")>0);
        assertTrue(result.getInt("trimmed-length")>0);
        assertTrue(result.getInt("total-word-chars")>0);
        assertTrue(result.getInt("total-word-count")>0);
        assertTrue(result.getInt("different-word-count")>0);
        assertNotSame("", result.getString("min-word"));
        assertNotSame("", result.getString("max-word"));
        assertNotNull(result.getDouble("average-word-length")>0.0d);
    }

    @Test
    @DisplayName("Basic test for request handler (POST)")
    void testHandlePostRequest() {
        final JSONObject input = new JSONObject();
        input.put("headers", new JSONObject());
        input.put("httpMethod", "POST");
        input.put("body", POST_INPUT);
        GatewayResponse response = (GatewayResponse) new HelloWorldHandler().handleRequest(input, mockLambdaContext);

        assertEquals(response.getStatusCode(), 200);
        JSONObject result = new JSONObject(response.getBody());
        assertEquals("N/A", result.getString("url"));
        assertEquals("N/A", result.getString("content-type"));
        assertEquals("N/A", result.getString("content-encoding"));
        assertEquals(POST_INPUT.trim().length(), result.getInt("trimmed-length"));
        assertEquals(POST_INPUT.length(), result.getInt("content-length"));
        assertEquals(12, result.getInt("total-word-chars"));
        assertEquals(6, result.getInt("total-word-count"));
        assertEquals(4, result.getInt("different-word-count"));
        assertEquals("a", result.getString("min-word"));
        assertEquals("dddd", result.getString("max-word"));
        assertNotNull(result.getDouble("average-word-length")>0.0d);
    }
}
